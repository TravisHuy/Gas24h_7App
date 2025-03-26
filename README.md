<h1 align="center"><img src="app/src/main/res/drawable/logo.png" alt="Screenshot" width="50" height="50"/> Gas24h_7App</h1>
<p align="center">
  <b>Gas24h_7App</b> is an online gas ordering application that allows users to easily search, place orders, and manage their orders directly from their mobile devices. The app provides a user-friendly interface, real-time order tracking, and various modern features to enhance the shopping experience.
</p>

<h2>🌟 Key Features</h2>
<ul>
  <li>🚀 Place gas orders with just a few simple steps.</li>
  <li>📦 Track orders in real-time.</li>
  <li>🛒 Manage your shopping cart easily.</li>
  <li>🔔 Receive instant notifications via WebSocket when order status updates.</li>
  <li>🗨 Review and rate products with images/videos.</li>
  <li>📞 Chat support with the admin for quick assistance.</li>
</ul>

## 🚀 Main Features
<ul>
  <li>🛒 <b>Cart Management:</b> Add/remove products from the cart.</li>
  <li>🛍 <b>Quick Ordering:</b> Multiple options for ordering gas.</li>
  <li>📦 <b>Order Tracking:</b> View order status in real-time.</li>
  <li>🗨 <b>Review & Rating System:</b> Users can submit reviews with images/videos.</li>
  <li>🔔 <b>Real-time Notifications:</b> WebSocket-based notifications from the admin.</li>
  <li>📞 <b>Chat with Admin:</b> One-on-one chat for each order.</li>
</ul>

## 🏗 Technologies Used
<ul>
  <li><b>Language:</b> Kotlin</li>
  <li><b>UI:</b> Jetpack Compose & XML</li>
  <li><b>Backend:</b> Spring Boot + MongoDB</li>
  <li><b>Key Libraries:</b>
    <ul>
      <li>Retrofit (API calls)</li>
      <li>Glide (Image processing)</li>
      <li>WebSocket (Real-time notifications)</li>
      <li>Dagger 2 (Dependency injection)</li>
      <li>Navigation Component (Screen navigation)</li>
      <li>Zing QR (QR Code generation)</li>
      <li>TravisHuyProgressBar (Enhanced UI progress tracking)</li>
      <li>Firebase Authentication (User login and registration)</li>
      <li>Firestore (Cloud database for user and order data)</li>
    </ul>
  </li>
</ul>

## 📥 Installation & Running the App
<h3>1️⃣ Clone the repository:</h3>
<pre><code>git clone https://github.com/yourusername/gas24h_7app.git
cd gas24h_7app</code></pre>

<h3>2️⃣ Configure Firebase:</h3>
<ul>
  <li>Download <code>google-services.json</code> from Firebase Console and place it in the <code>app/</code> directory.</li>
</ul>

<h3>3️⃣ Run the App:</h3>
<ul>
  <li>Open with <b>Android Studio</b></li>
  <li>Select a virtual or physical device</li>
  <li>Click <b>Run</b> (<code>Shift + F10</code>)</li>
</ul>

## 📖 Backend API
<p>The app connects to the backend deployed on Render:</p>
<ul>
  <li><b>Base URL:</b> <code>https://mongodb-csvv.onrender.com/</code></li>
  <li><b>Main Endpoints:</b>
    <ul>
      <li><code>GET travishuy/notifications/all</code> - Get all notifications</li>
      <li><code>GET travishuy/notifications/{id}</code> - Get detail notification by Id</li>
      <li><code>POST travishuy/notifications/add </code> - Create a new notification</li>
      <li><code>PUT travishuy/notifications/edit/{id}</code> - Edit notification</li>
      <li><code>DELETE travishuy/notifications/{id}</code> - Delete notification</li>
    </ul>
  </li>
</ul>

## 🎯 Main Screens
<ul>
  <li>
    🏠 <b>Dashboard</b><br>
    <img src="https://github.com/user-attachments/assets/edbadc2e-e5f6-4f44-9983-6c6ae28f9c50" width="150" height="300"/>
  </li>
  <li>
    🏠 <b>Home</b><br>
    <img src="screenshots/home.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🛒 <b>Shopping Cart</b><br>
    <img src="screenshots/add_cart1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_cart2.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_cart3.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    📦 <b>Order</b><br>
    <img src="screenshots/order_1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/voucher.png" alt="Screenshot" width="300" height="300"/>
  </li>
  <li>
    🔍 <b>Search Products</b><br>
    <img src="screenshots/search_1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/search_2.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    📷 <b>Scanner</b><br>
    <img src="screenshots/scan_qr_1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/scan_qr_2.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    ⭐ <b>Review Products</b><br>
    <img src="screenshots/review_2.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/review_3.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/review_1.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🛠 <b>Admin Home</b><br>
    <img src="screenshots/admin_screen.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    ➕ <b>Add Product</b><br>
    <img src="screenshots/add_product.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    ✏ <b>Edit Product</b><br>
    <img src="screenshots/edit_product.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🗑 <b>Remove Product</b><br>
    <img src="screenshots/remove_product.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🎟 <b>Add Voucher</b><br>
    <img src="screenshots/add_voucher.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_voucher_2.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_voucher_3.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_vouche_4.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_voucher_5.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    📑 <b>All QR Voucher</b><br>
    <img src="screenshots/all_qr_voucher.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🆕 <b>Create Voucher</b><br>
    <img src="screenshots/create_voucher.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    🔔 <b>Add Notification</b><br>
    <img src="screenshots/add_notification.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_notification_1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/add_notification_2.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    📊 <b>Revenue Statistics</b><br>
    <img src="screenshots/revene_statistics.png" alt="Screenshot" width="150" height="300"/>
  </li>
  <li>
    💬 <b>Chat</b><br>
    <img src="screenshots/chat_1.png" alt="Screenshot" width="150" height="300"/>
    <img src="screenshots/chat_2.png" alt="Screenshot" width="150" height="300"/>
  </li>
</ul>

## 🛠 Contributing
<p>You can contribute by following these steps:</p>
<ol>
  <li>Fork the repository 🍴</li>
  <li>Create a new branch (<code>git checkout -b feature-x</code>)</li>
  <li>Commit your changes (<code>git commit -m "Add feature x"</code>)</li>
  <li>Submit a pull request 📩</li>
</ol>

## 📄 License
<p>
  This project is licensed under the <a href="LICENSE">MIT License</a>.  
  You are free to use, modify, and distribute the code for personal or commercial purposes.  
  However, the software is provided "as-is" without any warranty, and the author (<b>TravisHuy</b>) is not responsible for any issues that may arise from its use.  
  For more details, please refer to the <a href="LICENSE">full license text</a>.
</p>
