import React from 'react';
import { Routes, Route } from 'react-router-dom';
import MainLayout from '../layout/MainLayout';
import DashboardPage from '../pages/Dashboard/DashboardPage';
import CategoryPage from '../pages/Category/CategoryPage';
import SupplierPage from '../pages/Supplier/SupplierPage';
import ProductPage from '../pages/Product/ProductPage';
import CustomerPage from '../pages/Customer/CustomerPage';
import OrderPage from '../pages/Order/OrderPage';
import CreateOrderPage from '../pages/Order/CreateOrderPage';
import AnalyticsPage from '../pages/Analytics/AnalyticsPage';
import SettingsPage from '../pages/Settings/SettingsPage';

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<DashboardPage />} />
        <Route path="categories" element={<CategoryPage />} />
        <Route path="suppliers" element={<SupplierPage />} />
        <Route path="products" element={<ProductPage />} />
        <Route path="customers" element={<CustomerPage />} />
        <Route path="orders" element={<OrderPage />} />
        <Route path="orders/new" element={<CreateOrderPage />} />
        <Route path="analytics" element={<AnalyticsPage />} />
        <Route path="settings" element={<SettingsPage />} />
        <Route path="*" element={<DashboardPage />} />
      </Route>
    </Routes>
  );
};

export default AppRoutes;
