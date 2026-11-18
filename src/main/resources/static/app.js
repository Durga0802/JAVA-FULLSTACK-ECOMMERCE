const API = "https://ecommerce-fullstack-dz04.onrender.com";

window.onload = function() {
    const path = window.location.pathname;
    const token = localStorage.getItem("token");

    // 1. Force Show Body (Worse case fix)
    const body = document.getElementById("mainBody");
    if(body) {
        body.classList.remove("hidden-body");
        body.style.display = "block";
    }

    // 2. Auth Protection
    // Inme se koi bhi word URL mein ho toh login check karo
    const adminWords = ["admin", "products", "orders", "add-product"];
    const isAdminPage = adminWords.some(word => path.includes(word));

    if (isAdminPage && !token) {
        console.warn("No token found! Redirecting...");
        window.location.href = "login.html";
        return;
    }

    // 3. Smart Routing (Element existence par based)
    // Agar page par totalProducts dikhane ki jagah hai, toh stats load karo
    if (document.getElementById("totalProducts")) loadDashboardStats();

    // Agar page par product table (productList) hai, toh load karo
    if (document.getElementById("productList")) loadProducts();

    // Agar page par order table (orderList) hai, toh load karo
    if (document.getElementById("orderList")) loadOrders();

    // Agar page par store grid (productDisplay) hai, toh load karo
    if (document.getElementById("productDisplay")) loadStoreProducts();

    // 4. Edit Mode Check (add-product.html)
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (document.getElementById("pageTitle") && productId) {
        document.getElementById('pageTitle').innerText = "Edit Product";
        document.getElementById('saveBtn').innerText = "Update Product";
        fetchProductDetails(productId);
    }
};
// Function ko alag se define karo taaki direct call ho sake
function routeAndLoad() {
    const path = window.location.pathname;
    const token = localStorage.getItem("token");

    console.log("Routing for path:", path); // Console mein check karo kya aa raha hai

    // 1. Force Show Body
    const body = document.getElementById("mainBody");
    if(body) {
        body.classList.remove("hidden-body");
        body.style.display = "block";
    }

    // 2. Auth Check
    const adminPages = ["admin", "products", "orders", "add-product"];
    if (adminPages.some(p => path.includes(p)) && !token) {
        window.location.href = "login.html";
        return;
    }

    // 3. Execution (Manual ID Check)
    if (document.getElementById("productList")) {
        console.log("Product List table found, loading...");
        loadProducts();
    }
    if (document.getElementById("orderList")) {
        console.log("Order List table found, loading...");
        loadOrders();
    }
    if (document.getElementById("productDisplay")) {
        console.log("Store display found, loading...");
        loadStoreProducts();
    }
    if (document.getElementById("totalProducts")) loadDashboardStats();
}

// Dono tariko se call karo taaki fail na ho
window.addEventListener('load', routeAndLoad);
document.addEventListener('DOMContentLoaded', routeAndLoad);
// --- STORE LOGIC ---
async function loadStoreProducts() {
    try {
        const res = await fetch(`${API}/api/products`);
        if (res.ok) {
            const products = await res.json();
            const display = document.getElementById("productDisplay");
            if (!display) return;

            let html = "";
            products.forEach(p => {
                const finalImgPath = getProductImagePath(p.imageUrl);
                html += `
                    <div class="product-card">
                        <img src="${finalImgPath}" alt="${p.name}" onerror="this.src='https://placehold.co/300x220?text=Image+Not+Found'">
                        <h3>${p.name}</h3>
                        <p class="product-desc" style="font-size: 0.9rem; color: #666; margin: 10px 0;">
                            ${p.description || 'No description available'}
                        </p>
                        <div class="price-tag">₹${p.price}</div>
                        <button class="btn-buy" onclick="buyProduct(${p.id})">Buy Now</button>
                    </div>`;
            });
            display.innerHTML = html;
        }
    } catch (err) { console.error("Store Load Fail", err); }
}

// Order Placing Logic (Buy Now Button)
async function buyProduct(productId) {
    // Agar order place karne ke liye login zaroori hai toh token bhejna padega
    const token = localStorage.getItem("token");
    try {
        const res = await fetch(`${API}/api/orders/place/${productId}`, {
            method: "POST",
            headers: {
                "Authorization": token ? "Bearer " + token : ""
            }
        });
        if (res.ok) {
            alert("Order Placed Successfully!");
            if(window.location.pathname.includes("index.html")) loadStoreProducts();
        } else {
            const errData = await res.text();
            console.error("Order Error:", errData);
            alert("Order failed! Check console for backend error.");
        }
    } catch (err) {
        console.error("Network Error:", err);
        alert("Server connection failed!");
    }
}

// --- ADMIN LOGIC ---
async function loadDashboardStats() {
    const token = localStorage.getItem("token");
    try {
        // 1. Fetch Products Count
        const pRes = await fetch(`${API}/api/products/count`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (pRes.ok) {
            const count = await pRes.json();
            document.getElementById("totalProducts").innerText = count;
        }

        // 2. Fetch Orders for Count & Revenue
        const oRes = await fetch(`${API}/api/orders`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (oRes.ok) {
            let orders = await oRes.json();

            // Agar backend Page object bhej raha hai toh data nikalo
            if (orders.content) orders = orders.content;

            // Total Orders Count update karo (HTML ki id "totalOrders" se match karega)
            if(document.getElementById("totalOrders")) {
                document.getElementById("totalOrders").innerText = orders.length;
            }

            // Total Revenue Calculate karo
            const totalRevenue = orders.reduce((sum, o) => {
                const amount = o.totalAmount || o.totalPrice || 0;
                return sum + amount;
            }, 0);

            // Revenue update karo (HTML ki id "totalSales" se match karega)
            if(document.getElementById("totalSales")) {
                document.getElementById("totalSales").innerText = `₹${totalRevenue.toLocaleString('en-IN')}`;
            }
        }

    } catch (err) {
        console.error("Dashboard Stats Update Fail:", err);
    }
}
async function loadProducts() {
    const token = localStorage.getItem("token");
    try {
        const res = await fetch(`${API}/api/products`, {
            headers: { "Authorization": "Bearer " + token }
        });

        if (res.ok) {
            let products = await res.json();
            if (products.content) products = products.content;
            if (products.data) products = products.data;

            const listBody = document.getElementById("productList");
            if (!listBody) return;

            if (!Array.isArray(products) || products.length === 0) {
                listBody.innerHTML = "<tr><td colspan='5' style='text-align:center;'>No products found.</td></tr>";
                return;
            }

            let rows = "";
            products.forEach(p => {
                const finalImgPath = getProductImagePath(p.imageUrl);
                rows += `<tr>
                    <td><img src="${finalImgPath}" width="40" class="product-img" onerror="this.src='https://placehold.co/40x40'"></td>
                    <td>${p.name}</td>
                    <td>₹${p.price}</td>
                    <td>${p.stock}</td>
                    <td>
                        <button class="btn-edit" onclick="location.href='add-product.html?id=${p.id}'">Edit</button>
                        <button class="btn-delete" onclick="deleteProduct(${p.id})">Delete</button>
                    </td>
                </tr>`;
            });
            listBody.innerHTML = rows;
        }
    } catch (err) { console.error("Admin Load Error:", err); }
}

async function loadOrders() {
    const token = localStorage.getItem("token");
    try {
        const res = await fetch(`${API}/api/orders`, {
            headers: { "Authorization": "Bearer " + token }
        });
        if (res.ok) {
            let orders = await res.json();
            console.log("Orders Received:", orders);

            const orderBody = document.getElementById("orderList");
            if (!orderBody) return;

            if (orders.content) orders = orders.content;
            if (!Array.isArray(orders) || orders.length === 0) {
                orderBody.innerHTML = "<tr><td colspan='6' style='text-align:center;'>No orders placed yet.</td></tr>";
                return;
            }

            let rows = "";
            orders.forEach(o => {
                // Agar o.totalAmount undefined hai toh o.totalPrice check karo
                const displayAmount = o.totalAmount || o.totalPrice || o.amount || 0;
                const displayName = o.customerName || o.userName || "Guest";

                rows += `<tr>
                    <td>#${o.id}</td>
                    <td>${displayName}</td>
                    <td>₹${displayAmount}</td>
                    <td>${o.orderDate ? new Date(o.orderDate).toLocaleDateString() : 'N/A'}</td>
                    <td><span class="status-badge status-completed">${o.status || 'COMPLETED'}</span></td>
                    <td><button class="btn-view">Details</button></td>
                </tr>`;
            });
            orderBody.innerHTML = rows;
        }
    } catch (err) { console.error("Orders Load Fail", err); }
}

// --- SAVE & EDIT FUNCTIONS ---
async function saveProduct() {
    const token = localStorage.getItem("token");
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');

    const formData = new FormData();
    formData.append("name", document.getElementById("name").value);
    formData.append("description", document.getElementById("description").value);
    formData.append("price", document.getElementById("price").value);
    formData.append("stock", document.getElementById("stock").value);
    formData.append("categoryId", document.getElementById("catId").value);

    // Elements ko pehle pakdo
    const urlInput = document.getElementById("externalImageUrl");
    const fileInput = document.getElementById("image");

    const externalUrl = urlInput ? urlInput.value.trim() : "";
    // Safe Check: Agar fileInput exist karta hai aur usme files hain
    const file = (fileInput && fileInput.files) ? fileInput.files[0] : null;

    // Logic: URL ko priority do, agar URL nahi hai toh file bhej do
    if (externalUrl !== "") {
        formData.append("imageUrl", externalUrl);
        console.log("Using External URL:", externalUrl);
    } else if (file) {
        formData.append("image", file);
        console.log("Using Uploaded File:", file.name);
    }

    const url = productId ? `${API}/api/products/upload/${productId}` : `${API}/api/products/upload`;

    try {
        const res = await fetch(url, {
            method: "POST",
            headers: { "Authorization": "Bearer " + token },
            body: formData
        });

        if (res.ok) {
            alert("Success!");
            window.location.href = "products.html";
        } else {
            const errorText = await res.text();
            alert("Error from Server: " + errorText);
        }
    } catch (err) {
        console.error("Save Error:", err);
        alert("Network Error! Check Console.");
    }
}
function getProductImagePath(imageUrl) {
    if (!imageUrl) return 'https://placehold.co/300x220?text=No+Image';

    // Check if it's an external link
    if (imageUrl.startsWith("http")) {
        return imageUrl;
    }

    // Otherwise, use backend images folder
    return `${API}/images/${imageUrl}`;
}


async function fetchProductDetails(id) {
    const token = localStorage.getItem("token");
    try {
        const res = await fetch(`${API}/api/products/${id}`, { headers: { "Authorization": "Bearer " + token } });
        if (res.ok) {
            const p = await res.json();
            document.getElementById("name").value = p.name;
            document.getElementById("price").value = p.price;
            document.getElementById("stock").value = p.stock;
            document.getElementById("description").value = p.description || "";
            document.getElementById("catId").value = p.categoryId || 1;

            // Preview existing image
            if (p.imageUrl) {
                const preview = document.getElementById('imgPreview');
                preview.src = `${API}/images/${p.imageUrl}`;
                preview.style.display = 'block';
            }
        }
    } catch (err) { console.error("Details fetch error", err); }
}

async function deleteProduct(id) {
    if(!confirm("Are you sure you want to delete this?")) return;
    const token = localStorage.getItem("token");
    await fetch(`${API}/api/products/${id}`, { method: "DELETE", headers: { "Authorization": "Bearer " + token } });
    loadProducts();
}

async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const res = await fetch(`${API}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });
    if (res.ok) {
        const data = await res.json();
        localStorage.setItem("token", data.token);
        window.location.href = "admin-dashboard.html";
    } else { alert("Login Failed!"); }
}

function logout() { localStorage.removeItem("token"); window.location.href = "login.html"; }