let categoriesData = [];

document.addEventListener('DOMContentLoaded', () => {
    loadCategories();

    const form = document.getElementById('categoryForm');
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
        query GetAllCategories {
            categories {
                id
                name
                images
            }
        }
    `;
    try {
        const data = await graphqlRequest(query);
        categoriesData = data.categories || [];
        renderCategories(categoriesData);
    } catch (err) {
        showAlert('Failed to load categories: ' + err.message);
    }
}

function renderCategories(categories) {
    const tbody = document.getElementById('categoryTableBody');
    tbody.innerHTML = '';
    if (!categories || categories.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No categories found.</td></tr>';
        return;
    }

    categories.forEach(cat => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cat.id}</td>
            <td>${cat.name}</td>
            <td>${cat.images || ''}</td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="editCategory('${cat.id}')">Edit</button>
                <button class="btn btn-danger btn-sm" onclick="deleteCategory('${cat.id}')">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function editCategory(id) {
    clearAlert();
    const cat = categoriesData.find(c => String(c.id) === String(id));
    if (!cat) return;

    document.getElementById('categoryId').value = cat.id;
    document.getElementById('name').value = cat.name;
    document.getElementById('images').value = cat.images || '';

    document.getElementById('formTitle').textContent = 'Edit Category (ID: ' + cat.id + ')';
    document.getElementById('submitBtn').textContent = 'Update Category';
    document.getElementById('cancelBtn').style.display = 'inline-block';
}

function resetForm() {
    document.getElementById('categoryForm').reset();
    document.getElementById('categoryId').value = '';
    document.getElementById('formTitle').textContent = 'Add New Category';
    document.getElementById('submitBtn').textContent = 'Create Category';
    document.getElementById('cancelBtn').style.display = 'none';
}

async function handleFormSubmit(e) {
    e.preventDefault();
    clearAlert();

    const categoryId = document.getElementById('categoryId').value;
    const input = {
        name: document.getElementById('name').value.trim(),
        images: document.getElementById('images').value.trim() || null
    };

    if (categoryId) {
        const mutation = `
            mutation UpdateCategory($id: ID!, $input: CategoryInput!) {
                updateCategory(id: $id, input: $input) {
                    id
                    name
                    images
                }
            }
        `;
        try {
            await graphqlRequest(mutation, { id: categoryId, input });
            showAlert('Category updated successfully!', false);
            resetForm();
            loadCategories();
        } catch (err) {
            showAlert('Failed to update category: ' + err.message);
        }
    } else {
        const mutation = `
            mutation CreateCategory($input: CategoryInput!) {
                createCategory(input: $input) {
                    id
                    name
                    images
                }
            }
        `;
        try {
            await graphqlRequest(mutation, { input });
            showAlert('Category created successfully!', false);
            resetForm();
            loadCategories();
        } catch (err) {
            showAlert('Failed to create category: ' + err.message);
        }
    }
}

async function deleteCategory(id) {
    if (!confirm('Are you sure you want to delete Category #' + id + '?')) {
        return;
    }
    clearAlert();

    const mutation = `
        mutation DeleteCategory($id: ID!) {
            deleteCategory(id: $id)
        }
    `;
    try {
        const data = await graphqlRequest(mutation, { id });
        if (data.deleteCategory) {
            showAlert('Category deleted successfully!', false);
            if (document.getElementById('categoryId').value === String(id)) {
                resetForm();
            }
            loadCategories();
        } else {
            showAlert('Category delete returned false.');
        }
    } catch (err) {
        showAlert('Failed to delete category: ' + err.message);
    }
}
