document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadProducts();

    document.getElementById('categoryFilter').addEventListener('change', (e) => {
        const categoryId = e.target.value;
        if (!categoryId) {
            loadProducts();
        } else {
            loadProductsByCategory(categoryId);
        }
    });
});

function showAlert(message, isError = true) {
    const alertBox = document.getElementById('alertBox');
    alertBox.className = 'alert ' + (isError ? 'alert-error' : 'alert-success');
    alertBox.textContent = message;
    alertBox.style.display = 'block';
}

function clearAlert() {
    const alertBox = document.getElementById('alertBox');
    alertBox.style.display = 'none';
}

async function loadCategories() {
    const query = `
        query GetCategories {
            categories {
                id
                name
            }
        }
    `;
    try {
        const data = await graphqlRequest(query);
        const select = document.getElementById('categoryFilter');
        select.innerHTML = '<option value="">All Categories (Price Ascending)</option>';
        data.categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.name;
            select.appendChild(option);
        });
    } catch (err) {
        showAlert('Failed to load categories: ' + err.message);
    }
}

async function loadProducts() {
    clearAlert();
    const query = `
        query GetProductsByPriceAsc {
            productsByPriceAsc {
                id
                title
                price
                quantity
                categoryName
                userFullname
                description
            }
        }
    `;
    try {
        const data = await graphqlRequest(query);
        renderProducts(data.productsByPriceAsc);
    } catch (err) {
        showAlert('Failed to load products: ' + err.message);
    }
}

async function loadProductsByCategory(categoryId) {
    clearAlert();
    const query = `
        query GetProductsByCategory($categoryId: ID!) {
            productsByCategory(categoryId: $categoryId) {
                id
                title
                price
                quantity
                categoryName
                userFullname
                description
            }
        }
    `;
    try {
        const data = await graphqlRequest(query, { categoryId });
        renderProducts(data.productsByCategory);
    } catch (err) {
        showAlert('Failed to load products by category: ' + err.message);
    }
}

function renderProducts(products) {
    const tbody = document.getElementById('productTableBody');
    tbody.innerHTML = '';
    if (!products || products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7">No products found.</td></tr>';
        return;
    }

    products.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.title}</td>
            <td>${p.price}</td>
            <td>${p.quantity}</td>
            <td>${p.categoryName || 'N/A'}</td>
            <td>${p.userFullname || 'N/A'}</td>
            <td>${p.description || ''}</td>
        `;
        tbody.appendChild(tr);
    });
}
