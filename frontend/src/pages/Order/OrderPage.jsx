import React, { useEffect, useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  TextField,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Tooltip,
  Chip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import VisibilityIcon from '@mui/icons-material/Visibility';
import RefreshIcon from '@mui/icons-material/Refresh';
import { Link as RouterLink } from 'react-router-dom';

import { orderApi } from '../../services/orderApi';
import { formatCurrency, formatDate } from '../../utils/formatters';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';

const OrderPage = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Selected Order Modal
  const [selectedOrder, setSelectedOrder] = useState(null);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await orderApi.getAll();
      setOrders(data);
    } catch (err) {
      setError(err.message || 'Failed to load order history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  // Filter Orders
  const filteredOrders = orders.filter(
    (o) =>
      String(o.id).includes(searchTerm) ||
      o.customerName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      o.customerEmail.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header Bar */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            Order Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            View order transactions, line item details, and customer purchases.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={fetchOrders} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button component={RouterLink} to="/orders/new" variant="contained" color="secondary" startIcon={<AddIcon />}>
            Create New Order
          </Button>
        </Box>
      </Box>

      {/* Main Table Paper */}
      <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
        <Box sx={{ mb: 3, maxWidth: 360 }}>
          <TextField
            fullWidth
            size="small"
            placeholder="Search by Order ID or Customer..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon fontSize="small" color="action" />
                </InputAdornment>
              ),
            }}
          />
        </Box>

        {loading && <LoadingSpinner message="Loading orders..." />}
        {error && <ErrorAlert message={error} onRetry={fetchOrders} />}

        {!loading && !error && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Order ID</TableCell>
                  <TableCell>Customer Name</TableCell>
                  <TableCell>Customer Email</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell align="right">Items Count</TableCell>
                  <TableCell align="right">Total Amount</TableCell>
                  <TableCell align="center">Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredOrders.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No orders found. Click "Create New Order" to place one.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredOrders.map((ord) => (
                    <TableRow key={ord.id} hover>
                      <TableCell sx={{ fontWeight: 700 }}>#{ord.id}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{ord.customerName}</TableCell>
                      <TableCell color="text.secondary">{ord.customerEmail}</TableCell>
                      <TableCell color="text.secondary">{formatDate(ord.orderDate)}</TableCell>
                      <TableCell align="right">{ord.orderItems ? ord.orderItems.length : 0}</TableCell>
                      <TableCell align="right" sx={{ fontWeight: 700, color: '#10B981' }}>
                        {formatCurrency(ord.totalAmount)}
                      </TableCell>
                      <TableCell align="center">
                        <Tooltip title="View Order Details">
                          <IconButton size="small" color="primary" onClick={() => setSelectedOrder(ord)}>
                            <VisibilityIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Paper>

      {/* Order Details Dialog */}
      <Dialog open={Boolean(selectedOrder)} onClose={() => setSelectedOrder(null)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              Order Breakdown #{selectedOrder?.id}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Date: {formatDate(selectedOrder?.orderDate)}
            </Typography>
          </Box>
          <Chip label="COMPLETED" color="success" size="small" sx={{ fontWeight: 700 }} />
        </DialogTitle>
        <DialogContent dividers>
          <Box sx={{ mb: 2 }}>
            <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
              Customer Details:
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Name: {selectedOrder?.customerName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Email: {selectedOrder?.customerEmail}
            </Typography>
          </Box>

          <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 1 }}>
            Purchased Line Items:
          </Typography>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Product</TableCell>
                  <TableCell align="center">Qty</TableCell>
                  <TableCell align="right">Price</TableCell>
                  <TableCell align="right">Subtotal</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {selectedOrder?.orderItems?.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell sx={{ fontWeight: 600 }}>{item.productName}</TableCell>
                    <TableCell align="center">{item.quantity}</TableCell>
                    <TableCell align="right">{formatCurrency(item.price)}</TableCell>
                    <TableCell align="right" sx={{ fontWeight: 700 }}>
                      {formatCurrency(item.subTotal)}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 3, pt: 2, borderTop: '1px solid #E2E8F0' }}>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              Grand Total:
            </Typography>
            <Typography variant="h5" sx={{ fontWeight: 700, color: '#10B981' }}>
              {formatCurrency(selectedOrder?.totalAmount)}
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setSelectedOrder(null)} color="primary" variant="contained">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default OrderPage;
