# Smart Inventory & Order Management System - React Frontend

Modern, responsive admin dashboard web application built with **React 19**, **Vite**, **Material UI (MUI v6)**, **React Router DOM v7**, **Axios**, and **Recharts**. Consumes the Spring Boot backend REST APIs (`http://localhost:8080/api/v1`).

---

## 🎨 UI Theme & Design System

Designed following modern SaaS admin dashboard standards (Microsoft Admin, Linear, Atlassian aesthetic):
* **Background**: Clean Light Slate Gray (`#F8FAFC`)
* **Primary / Headers**: Dark Slate Blue (`#0F172A`)
* **Accents / Success**: Spring Green (`#10B981`)
* **Card Borders**: Subtle Divider Borders (`#E2E8F0`)

---

## 📂 Folder Structure

```text
frontend/
 ├── package.json
 ├── vite.config.js
 ├── .env
 ├── index.html
 └── src/
      ├── components/
      │    └── common/
      │         ├── LoadingSpinner.jsx
      │         ├── ConfirmationDialog.jsx
      │         ├── ErrorAlert.jsx
      │         ├── StatusBadge.jsx
      │         └── BreadcrumbsNav.jsx
      ├── context/
      │    └── AppContext.jsx          # Toast Notifications & Global Drawer State
      ├── layout/
      │    ├── Navbar.jsx              # Top Navigation Bar & Status Indicator
      │    ├── Sidebar.jsx             # Left Navigation Drawer
      │    ├── Footer.jsx              # Application Footer
      │    └── MainLayout.jsx          # Master Layout Wrapper
      ├── pages/
      │    ├── Dashboard/
      │    │    └── DashboardPage.jsx  # Recharts Analytics & Metric Cards
      │    ├── Category/
      │    │    └── CategoryPage.jsx   # Category CRUD & Search
      │    ├── Supplier/
      │    │    └── SupplierPage.jsx   # Supplier CRUD & Validations
      │    ├── Product/
      │    │    └── ProductPage.jsx    # Product Catalog, Stock Badges & Category Filters
      │    ├── Customer/
      │    │    └── CustomerPage.jsx   # Customer Account Management
      │    ├── Order/
      │    │    ├── OrderPage.jsx      # Order History & Line Breakdown
      │    │    └── CreateOrderPage.jsx # Multi-Item Order Placement Basket
      │    ├── Analytics/
      │    │    └── AnalyticsPage.jsx  # Revenue & Stock Recharts Graphs
      │    └── Settings/
      │         └── SettingsPage.jsx   # API Parameters & Profile
      ├── services/
      │    ├── api.js                  # Axios Instance & Response Interceptors
      │    ├── categoryApi.js
      │    ├── supplierApi.js
      │    ├── productApi.js
      │    ├── customerApi.js
      │    └── orderApi.js
      ├── theme/
      │    └── theme.js                # Custom Material UI Palette & Typography
      ├── utils/
      │    └── formatters.js           # Currency ($), Date, & Stock Status Helpers
      ├── routes/
      │    └── AppRoutes.jsx           # React Router Route Mappings
      ├── App.jsx
      └── main.jsx
```

---

## ⚙️ Environment Variables

The frontend connects to the Spring Boot backend using `.env`:
```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

---

## ⚡ How to Run Locally

### 1. Install Dependencies
```bash
cd frontend
npm install
```

### 2. Start Development Server
```bash
node node_modules/vite/bin/vite.js
```
* Access dashboard in browser: `http://localhost:3000`

### 3. Build for Production
```bash
node node_modules/vite/bin/vite.js build
```

---

## 📡 REST API Integration Reference

| Module | Frontend Component | Backend Endpoint |
| :--- | :--- | :--- |
| **Dashboard** | `DashboardPage.jsx` | `GET /categories`, `GET /suppliers`, `GET /products`, `GET /customers`, `GET /orders` |
| **Category** | `CategoryPage.jsx` | `GET/POST/PUT/DELETE /categories` |
| **Supplier** | `SupplierPage.jsx` | `GET/POST/PUT/DELETE /suppliers` |
| **Product** | `ProductPage.jsx` | `GET/POST/PUT/DELETE /products`, `GET /products/search` |
| **Customer** | `CustomerPage.jsx` | `GET/POST/PUT/DELETE /customers` |
| **Order** | `CreateOrderPage.jsx`, `OrderPage.jsx` | `GET/POST /orders` |
