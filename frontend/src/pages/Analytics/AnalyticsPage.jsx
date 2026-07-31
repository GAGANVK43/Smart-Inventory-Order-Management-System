import React, { useEffect, useState } from 'react';
import { Box, Paper, Typography, Grid } from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
  LineChart,
  Line,
} from 'recharts';
import { orderApi } from '../../services/orderApi';
import { productApi } from '../../services/productApi';
import { formatCurrency } from '../../utils/formatters';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';

const AnalyticsPage = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [ordersData, setOrdersData] = useState([]);
  const [productsData, setProductsData] = useState([]);

  const loadAnalytics = async () => {
    setLoading(true);
    setError(null);
    try {
      const [orders, products] = await Promise.all([
        orderApi.getAll(),
        productApi.getAll(),
      ]);

      // Format orders for line chart
      const orderChartData = orders.map((o) => ({
        id: `#${o.id}`,
        amount: o.totalAmount,
        customer: o.customerName,
      }));

      // Format top products by stock & price
      const productChartData = products.map((p) => ({
        name: p.name.length > 15 ? p.name.substring(0, 15) + '...' : p.name,
        price: p.price,
        stock: p.quantity,
      }));

      setOrdersData(orderChartData);
      setProductsData(productChartData);
    } catch (err) {
      setError(err.message || 'Failed to load analytics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAnalytics();
  }, []);

  if (loading) return <LoadingSpinner message="Generating analytical reports..." />;
  if (error) return <ErrorAlert message={error} onRetry={loadAnalytics} />;

  return (
    <Box sx={{ pb: 4 }}>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
          Inventory & Sales Analytics
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Deep-dive visual reports on order transactions and product stock levels.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Revenue Trend per Transaction ($)
            </Typography>
            <Box sx={{ height: 300, width: '100%' }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={ordersData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#E2E8F0" />
                  <XAxis dataKey="id" stroke="#64748B" />
                  <YAxis stroke="#64748B" />
                  <RechartsTooltip formatter={(value) => formatCurrency(value)} />
                  <Line type="monotone" dataKey="amount" stroke="#10B981" strokeWidth={3} dot={{ r: 6 }} />
                </LineChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              Stock Quantities by Product
            </Typography>
            <Box sx={{ height: 300, width: '100%' }}>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={productsData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#E2E8F0" />
                  <XAxis dataKey="name" stroke="#64748B" />
                  <YAxis stroke="#64748B" />
                  <RechartsTooltip />
                  <Bar dataKey="stock" fill="#3B82F6" radius={[6, 6, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default AnalyticsPage;
