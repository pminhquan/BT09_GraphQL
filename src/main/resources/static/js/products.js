let productsData = [];

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();
    loadProducts();

    const form = document.getElementById('productForm');
    form.addEventListener('submit', handleFormSubmit);

    document.getElementById('cancelBtn').addEventListener('click', resetForm);
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
        const select = document.getElementById('categoryId');
        select.innerHTML = '<option value="">-- Select Category --</option>';
        data.categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = cat.name + ' (ID: ' + cat.id + ')';
            select.appendChild(option);
        });
    } catch (err) {
        showAlert('Failed to load categories: ' + err.message);
    }
}

async function loadProducts() {
    const query = `
        query GetAllProducts {
            products {
                id
                title
                price
                quantity
                description
                userId
                categoryId
                categoryName
                userFullname
            }
        }
    `;
    try {
        const data = await graphqlRequest(query);
        productsData = data.products || [];
        renderProducts(productsData);
    } catch (err) {
        showAlert('Failed to load products: ' + err.message);
    }
}

function renderProducts(products) {
    const tbody = document.getElementById('productTableBody');
    tbody.innerHTML = '';
    if (!products || products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9">No products found.</td></tr>';
        return;
    }

    products.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.title}</td>
            <td>${p.price}</td>
            <td>${p.quantity}</td>
            <td>${p.categoryName || p.categoryId || 'N/A'}</td>
            <td>${p.userId || 'N/A'}</td>
            <td>${p.userFullname || 'N/A'}</td>
            <td>${p.description || ''}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editProduct('${p.id}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteProduct('${p.id}')">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function editProduct(id) {
    clearAlert();
    const product = productsData.find(p => String(p.id) === String(id));
    if (!product) return;

    document.getElementById('productId').value = product.id;
    document.getElementById('title').value = product.title;
    document.getElementById('quantity').value = product.quantity;
    document.getElementById('price').value = product.price;
    document.getElementById('userId').value = product.userId || '';
    document.getElementById('categoryId').value = product.categoryId || '';
    document.getElementById('description').value = product.description || '';

    document.getElementById('formTitle').textContent = 'Edit Product (ID: ' + product.id + ')';
    document.getElementById('submitBtn').textContent = 'Update Product';
    document.getElementById('cancelBtn').style.display = 'inline-block';
}

function resetForm() {
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';
    document.getElementById('formTitle').textContent = 'Add New Product';
    document.getElementById('submitBtn').textContent = 'Create Product';
    document.getElementById('cancelBtn').style.display = 'none';
}

async function handleFormSubmit(e) {
    e.preventDefault();
    clearAlert();

    const productId = document.getElementById('productId').value;
    const input = {
        title: document.getElementById('title').value.trim(),
        quantity: parseInt(document.getElementById('quantity').value, 10),
        price: document.getElementById('price').value,
        userId: document.getElementById('userId').value,
        categoryId: document.getElementById('categoryId').value,
        description: document.getElementById('description').value.trim() || null
    };

    if (productId) {
        const mutation = `
            mutation UpdateProduct($id: ID!, $input: ProductInput!) {
                updateProduct(id: $id, input: $input) {
                    id
                    title
                    price
                }
            }
        `;
        try {
            await graphqlRequest(mutation, { id: productId, input });
            showAlert('Product updated successfully!', false);
            resetForm();
            loadProducts();
        } catch (err) {
            showAlert('Failed to update product: ' + err.message);
        }
    } else {
        const mutation = `
            mutation CreateProduct($input: ProductInput!) {
                createProduct(input: $input) {
                    id
                    title
                    price
                }
            }
        `;
        try {
            await graphqlRequest(mutation, { input });
            showAlert('Product created successfully!', false);
            resetForm();
            loadProducts();
        } catch (err) {
            showAlert('Failed to create product: ' + err.message);
        }
    }
}

async function deleteProduct(id) {
    if (!confirm('Are you sure you want to delete Product #' + id + '?')) {
        return;
    }
    clearAlert();

    const mutation = `
        mutation DeleteProduct($id: ID!) {
            deleteProduct(id: $id)
        }
    `;
    try {
        const data = await graphqlRequest(mutation, { id });
        if (data.deleteProduct) {
            showAlert('Product deleted successfully!', false);
            if (document.getElementById('productId').value === String(id)) {
                resetForm();
            }
            loadProducts();
        } else {
            showAlert('Product delete returned false.');
        }
    } catch (err) {
        showAlert('Failed to delete product: ' + err.message);
    }
}
