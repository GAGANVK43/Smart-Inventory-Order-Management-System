import React, { useEffect, useState } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Chip,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  IconButton,
  Tooltip,
  Avatar,
} from '@mui/material';
import CategoryIcon from '@mui/icons-material/Category';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import PeopleIcon from '@mui/icons-material/People';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import RefreshIcon from '@mui/icons-material/Refresh';

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';

import { categoryApi } from '../../services/categoryApi';
import { supplierApi } from '../../services/supplierApi';
import { productApi } from '../../services/productApi';
import { customerApi } from '../../services/customerApi';
import { orderApi } from '../../services/orderApi';
import { formatCurrency, formatDate, getStockStatus } from '../../utils/formatters';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';
import StatusBadge from '../../components/common/StatusBadge';
import { Link as RouterLink } from 'react-router-dom';

const COLORS = ['#0F172A', '#10B981', '#3B82F6', '#F59E0B', '#EF4444', '#8B5CF6'];

const DashboardPage = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [stats, setStats] = useState({
    categoriesCount: 0,
    suppliersCount: 0,
    productsCount: 0,
    customersCount: 0,
    ordersCount: 0,
    totalRevenue: 0,
  });

  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [categoriesData, setCategoriesData] = useState([]);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [catData, supData, prodData, custData, ordData] = await Promise.all([
        categoryApi.getAll(),
        supplierApi.getAll(),
        productApi.getAll(),
        customerApi.getAll(),
        orderApi.getAll(),
      ]);

      const extractList = (res) => (Array.isArray(res) ? res : (res?.data && Array.isArray(res.data) ? res.data : []));

      const catList = extractList(catData);
      const supList = extractList(supData);
      const prodList = extractList(prodData);
      const custList = extractList(custData);
      const ordList = extractList(ordData);

      const revenue = ordList.reduce((sum, order) => sum + (order?.totalAmount || 0), 0);

      setStats({
        categoriesCount: catList.length,
        suppliersCount: supList.length,
        productsCount: prodList.length,
        customersCount: custList.length,
        ordersCount: ordList.length,
        totalRevenue: revenue,
      });

      setProducts(prodList);
      setOrders(ordList);

      // Group products by category for chart
      const categoryMap = {};
      catList.forEach((c) => {
        if (c?.name) categoryMap[c.name] = 0;
      });
      prodList.forEach((p) => {
        const catName = p?.categoryName || 'Uncategorized';
        categoryMap[catName] = (categoryMap[catName] || 0) + 1;
      });

      const catChartData = Object.keys(categoryMap).map((key) => ({
        name: key,
        products: categoryMap[key],
      }));

      setCategoriesData(catChartData);
    } catch (err) {
      setError(err.message || 'Failed to load dashboard statistics.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  if (loading) return <LoadingSpinner message="Fetching inventory analytics..." />;
  if (error) return <ErrorAlert message={error} onRetry={fetchDashboardData} />;

  // Filter low stock products (qty <= 5)
  const lowStockProducts = products.filter((p) => p.quantity <= 5);

  // Calculate stock breakdown
  const inStockCount = products.filter((p) => p.quantity > 5).length;
  const lowStockCount = lowStockProducts.length;
  const outOfStockCount = products.filter((p) => p.quantity <= 0).length;

  const stockPieData = [
    { name: 'In Stock', value: inStockCount },
    { name: 'Low Stock', value: lowStockCount },
    { name: 'Out of Stock', value: outOfStockCount },
  ];

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header Bar */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            System Dashboard
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Real-time inventory metrics, order summaries, and stock alerts.
          </Typography>
        </Box>
        <Tooltip title="Refresh Dashboard">
          <IconButton onClick={fetchDashboardData} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {/* Top 5 Metric Cards */}
      <Grid container spacing={2.5} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={2.4}>
          <Card sx={{ height: '100%', borderLeft: '4px solid #0F172A' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
                    Categories
                  </Typography>

                  <Typography variant="h4" sx={{ fontWeight: 700, my: 0.5 }}>
                    {stats.categoriesCount}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: '#F1F5F9', color: '#0F172A', width: 40, height: 40 }}>
                  <CategoryIcon fontSize="small" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <Card sx={{ height: '100%', borderLeft: '4px solid #3B82F6' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
                    Suppliers
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700, my: 0.5 }}>
                    {stats.suppliersCount}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: '#EFF6FF', color: '#3B82F6', width: 40, height: 40 }}>
                  <LocalShippingIcon fontSize="small" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <Card sx={{ height: '100%', borderLeft: '4px solid #10B981' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
                    Total Products
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700, my: 0.5 }}>
                    {stats.productsCount}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: '#ECFDF5', color: '#10B981', width: 40, height: 40 }}>
                  <ShoppingBagIcon fontSize="small" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <Card sx={{ height: '100%', borderLeft: '4px solid #8B5CF6' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
                    Customers
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700, my: 0.5 }}>
                    {stats.customersCount}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: '#F5F3FF', color: '#8B5CF6', width: 40, height: 40 }}>
                  <PeopleIcon fontSize="small" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={2.4}>
          <Card sx={{ height: '100%', borderLeft: '4px solid #F59E0B' }}>
            <CardContent sx={{ p: 2.5 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                <Box>
                  <Typography variant="caption" color="text.secondary" sx={{ fontWeight: 600, textTransform: 'uppercase' }}>
                    Total Orders
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 700, my: 0.5 }}>
                    {stats.ordersCount}
                  </Typography>
                  <Typography variant="caption" sx={{ color: '#10B981', fontWeight: 600 }}>
                    {formatCurrency(stats.totalRevenue)}
                  </Typography>
                </Box>
                <Avatar sx={{ bgcolor: '#FFFBEB', color: '#F59E0B', width: 40, height: 40 }}>
                  <ReceiptLongIcon fontSize="small" />
                </Avatar>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Analytics Charts Section */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {/* Products by Category Bar Chart */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Products Distribution by Category
            </Typography>
            <Box sx={{ height: 280, width: '100%' }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={categoriesData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                  <XAxis dataKey="name" stroke="#64748B" fontSize={12} tickLine={false} />
                  <YAxis stroke="#64748B" fontSize={12} tickLine={false} axisLine={false} />
                  <RechartsTooltip cursor={{ fill: '#F1F5F9' }} />
                  <Bar dataKey="products" fill="#0F172A" radius={[6, 6, 0, 0]} barSize={40} />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>

        {/* Inventory Status Breakdown Pie Chart */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0', height: '100%' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Inventory Health Status
            </Typography>
            <Box sx={{ height: 240, width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={stockPieData} cx="50%" cy="50%" innerRadius={60} outerRadius={85} paddingAngle={4} dataKey="value">
                    <Cell fill="#10B981" />
                    <Cell fill="#F59E0B" />
                    <Cell fill="#EF4444" />
                  </Pie>
                  <RechartsTooltip />
                  <Legend verticalAlign="bottom" height={36} />
                </PieChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Tables Section: Low Stock Warnings & Recent Orders */}
      <Grid container spacing={3}>
        {/* Low Stock Alerts */}
        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <WarningAmberIcon color="warning" />
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  Low Stock Products ({lowStockProducts.length})
                </Typography>
              </Box>
              <Button component={RouterLink} to="/products" size="small" endIcon={<ArrowForwardIcon />}>
                View All
              </Button>
            </Box>

            {lowStockProducts.length === 0 ? (
              <Box sx={{ p: 3, textAlign: 'center', bgcolor: '#F8FAFC', borderRadius: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  ✅ All product inventory levels are healthy!
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Product</TableCell>
                      <TableCell>Category</TableCell>
                      <TableCell align="right">Qty</TableCell>
                      <TableCell align="center">Status</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {lowStockProducts.slice(0, 5).map((row) => (
                      <TableRow key={row.id} hover>
                        <TableCell sx={{ fontWeight: 600 }}>{row.name}</TableCell>
                        <TableCell>{row.categoryName}</TableCell>
                        <TableCell align="right" sx={{ fontWeight: 700, color: row.quantity === 0 ? '#EF4444' : '#F59E0B' }}>
                          {row.quantity}
                        </TableCell>
                        <TableCell align="center">
                          <StatusBadge quantity={row.quantity} />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        </Grid>

        {/* Recent Orders */}
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                Recent Customer Orders
              </Typography>
              <Button component={RouterLink} to="/orders" size="small" endIcon={<ArrowForwardIcon />}>
                Manage Orders
              </Button>
            </Box>

            {orders.length === 0 ? (
              <Box sx={{ p: 3, textAlign: 'center', bgcolor: '#F8FAFC', borderRadius: 2 }}>
                <Typography variant="body2" color="text.secondary">
                  No orders placed yet.
                </Typography>
              </Box>
            ) : (
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Order ID</TableCell>
                      <TableCell>Customer</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell align="right">Total</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {orders.slice(0, 5).map((ord) => (
                      <TableRow key={ord.id} hover>
                        <TableCell sx={{ fontWeight: 700 }}>#{ord.id}</TableCell>
                        <TableCell>{ord.customerName}</TableCell>
                        <TableCell>{formatDate(ord.orderDate)}</TableCell>
                        <TableCell align="right" sx={{ fontWeight: 700, color: '#10B981' }}>
                          {formatCurrency(ord.totalAmount)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default DashboardPage;
