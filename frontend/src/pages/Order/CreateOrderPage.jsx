import React, { useEffect, useState } from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Grid,
  MenuItem,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Card,
  CardContent,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
} from '@mui/material';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import DeleteIcon from '@mui/icons-material/Delete';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import { useNavigate, Link as RouterLink } from 'react-router-dom';

import { customerApi } from '../../services/customerApi';
import { productApi } from '../../services/productApi';
import { orderApi } from '../../services/orderApi';
import { useApp } from '../../context/AppContext';
import { formatCurrency } from '../../utils/formatters';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';

const CreateOrderPage = () => {
  const navigate = useNavigate();
  const { showNotification } = useApp();

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Form State
  const [selectedCustomerId, setSelectedCustomerId] = useState('');
  const [selectedProductId, setSelectedProductId] = useState('');
  const [quantity, setQuantity] = useState(1);

  // Order Items Basket
  const [cartItems, setCartItems] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  // Success Modal
  const [placedOrder, setPlacedOrder] = useState(null);

  const loadFormData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [custList, prodList] = await Promise.all([
        customerApi.getAll(),
        productApi.getAll(),
      ]);
      setCustomers(custList);
      setProducts(prodList);
      if (custList.length > 0) setSelectedCustomerId(custList[0].id);
      if (prodList.length > 0) setSelectedProductId(prodList[0].id);
    } catch (err) {
      setError(err.message || 'Failed to load customers/products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadFormData();
  }, []);

  const handleAddToCart = () => {
    if (!selectedProductId) return;

    const targetProduct = products.find((p) => p.id === Number(selectedProductId));
    if (!targetProduct) return;

    if (quantity > targetProduct.quantity) {
      showNotification(`Cannot add ${quantity} units. Only ${targetProduct.quantity} available in stock.`, 'warning');
      return;
    }

    const existingIndex = cartItems.findIndex((item) => item.productId === targetProduct.id);

    if (existingIndex > -1) {
      const updated = [...cartItems];
      const newQty = updated[existingIndex].quantity + Number(quantity);
      if (newQty > targetProduct.quantity) {
        showNotification(`Cannot exceed total stock of ${targetProduct.quantity}.`, 'warning');
        return;
      }
      updated[existingIndex].quantity = newQty;
      updated[existingIndex].subtotal = newQty * targetProduct.price;
      setCartItems(updated);
    } else {
      setCartItems([
        ...cartItems,
        {
          productId: targetProduct.id,
          productName: targetProduct.name,
          price: targetProduct.price,
          quantity: Number(quantity),
          subtotal: Number(quantity) * targetProduct.price,
          stockAvailable: targetProduct.quantity,
        },
      ]);
    }

    setQuantity(1);
  };

  const handleRemoveFromCart = (productId) => {
    setCartItems(cartItems.filter((item) => item.productId !== productId));
  };

  const calculateTotal = () => {
    return cartItems.reduce((sum, item) => sum + item.subtotal, 0);
  };

  const handlePlaceOrder = async () => {
    if (!selectedCustomerId) {
      showNotification('Please select a customer', 'error');
      return;
    }
    if (cartItems.length === 0) {
      showNotification('Please add at least one product to the order', 'error');
      return;
    }

    setSubmitting(true);
    try {
      const payload = {
        customerId: Number(selectedCustomerId),
        items: cartItems.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      };

      const result = await orderApi.create(payload);
      setPlacedOrder(result);
      showNotification('Order placed successfully!', 'success');
      setCartItems([]);
    } catch (err) {
      showNotification(err.message || 'Failed to place order', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading customer & product data..." />;
  if (error) return <ErrorAlert message={error} onRetry={loadFormData} />;

  const currentSelectedProduct = products.find((p) => p.id === Number(selectedProductId));

  return (
    <Box sx={{ pb: 4 }}>
      {/* Top Action Bar */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button component={RouterLink} to="/orders" startIcon={<ArrowBackIcon />} color="inherit">
          Back to Orders
        </Button>
        <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
          Create New Customer Order
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* Left Column: Form Controls & Product Selector */}
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0', mb: 3 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              1. Select Customer
            </Typography>
            <TextField
              select
              fullWidth
              size="small"
              label="Customer Account"
              value={selectedCustomerId}
              onChange={(e) => setSelectedCustomerId(e.target.value)}
            >
              {customers.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.name} ({c.email})
                </MenuItem>
              ))}
            </TextField>
          </Paper>

          <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
            <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
              2. Add Products to Order
            </Typography>

            <Grid container spacing={2} sx={{ mb: 2 }}>
              <Grid item xs={12} sm={7}>
                <TextField
                  select
                  fullWidth
                  size="small"
                  label="Select Product"
                  value={selectedProductId}
                  onChange={(e) => setSelectedProductId(e.target.value)}
                >
                  {products.map((p) => (
                    <MenuItem key={p.id} value={p.id} disabled={p.quantity <= 0}>
                      {p.name} - {formatCurrency(p.price)} (Stock: {p.quantity})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item xs={6} sm={3}>
                <TextField
                  label="Quantity"
                  type="number"
                  fullWidth
                  size="small"
                  value={quantity}
                  onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value, 10) || 1))}
                  inputProps={{ min: 1, max: currentSelectedProduct?.quantity || 1 }}
                />
              </Grid>
              <Grid item xs={6} sm={2}>
                <Button
                  fullWidth
                  variant="contained"
                  color="secondary"
                  sx={{ height: 40 }}
                  onClick={handleAddToCart}
                  startIcon={<AddShoppingCartIcon />}
                >
                  Add
                </Button>
              </Grid>
            </Grid>

            {currentSelectedProduct && (
              <Box sx={{ p: 2, bgcolor: '#F8FAFC', borderRadius: 2, display: 'flex', gap: 2, alignItems: 'center' }}>
                <ShoppingBagIcon color="primary" />
                <Box>
                  <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                    {currentSelectedProduct.name}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Unit Price: {formatCurrency(currentSelectedProduct.price)} | Available Stock: {currentSelectedProduct.quantity} units
                  </Typography>
                </Box>
              </Box>
            )}
          </Paper>
        </Grid>

        {/* Right Column: Order Summary Basket */}
        <Grid item xs={12} md={5}>
          <Card sx={{ borderRadius: 3, border: '1px solid #E2E8F0', height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ p: 3, flexGrow: 1 }}>
              <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
                Order Summary Basket ({cartItems.length} items)
              </Typography>

              {cartItems.length === 0 ? (
                <Box sx={{ p: 4, textAlign: 'center', bgcolor: '#F8FAFC', borderRadius: 2, my: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Your order basket is currently empty. Select products from the left to populate the basket.
                  </Typography>
                </Box>
              ) : (
                <TableContainer sx={{ mb: 2 }}>
                  <Table size="small">
                    <TableHead>
                      <TableRow>
                        <TableCell>Item</TableCell>
                        <TableCell align="center">Qty</TableCell>
                        <TableCell align="right">Subtotal</TableCell>
                        <TableCell align="center"></TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {cartItems.map((item) => (
                        <TableRow key={item.productId}>
                          <TableCell sx={{ fontWeight: 600 }}>{item.productName}</TableCell>
                          <TableCell align="center">{item.quantity}</TableCell>
                          <TableCell align="right" sx={{ fontWeight: 600 }}>
                            {formatCurrency(item.subtotal)}
                          </TableCell>
                          <TableCell align="center">
                            <IconButton size="small" color="error" onClick={() => handleRemoveFromCart(item.productId)}>
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}

              <Divider sx={{ my: 2 }} />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  Total Order Amount:
                </Typography>
                <Typography variant="h4" sx={{ fontWeight: 700, color: '#10B981' }}>
                  {formatCurrency(calculateTotal())}
                </Typography>
              </Box>

              <Button
                fullWidth
                variant="contained"
                color="secondary"
                size="large"
                disabled={cartItems.length === 0 || submitting}
                onClick={handlePlaceOrder}
                sx={{ py: 1.5, fontSize: '1rem', fontWeight: 700 }}
              >
                {submitting ? 'Processing Transaction...' : 'Confirm & Place Order'}
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Success Confirmation Modal */}
      <Dialog open={Boolean(placedOrder)} onClose={() => setPlacedOrder(null)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ textAlign: 'center', pt: 3 }}>
          <CheckCircleOutlineIcon sx={{ fontSize: 60, color: '#10B981', mb: 1 }} />
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            Order Placed Successfully!
          </Typography>
          <Chip label={`Order #${placedOrder?.id}`} color="primary" size="small" sx={{ mt: 1, fontWeight: 700 }} />
        </DialogTitle>
        <DialogContent dividers>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5, py: 1 }}>
            <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
              Customer: {placedOrder?.customerName} ({placedOrder?.customerEmail})
            </Typography>
            <Typography variant="subtitle2" sx={{ fontWeight: 600, color: '#10B981' }}>
              Total Paid: {formatCurrency(placedOrder?.totalAmount)}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Date: {placedOrder?.orderDate}
            </Typography>

            <Typography variant="body2" sx={{ fontWeight: 700, mt: 1 }}>
              Ordered Line Items:
            </Typography>
            {placedOrder?.orderItems?.map((item) => (
              <Box key={item.id} sx={{ display: 'flex', justifyContent: 'space-between', bgcolor: '#F8FAFC', p: 1.5, borderRadius: 1.5 }}>
                <Typography variant="body2" sx={{ fontWeight: 600 }}>
                  {item.productName} (x{item.quantity})
                </Typography>
                <Typography variant="body2" sx={{ fontWeight: 700 }}>
                  {formatCurrency(item.subTotal)}
                </Typography>
              </Box>
            ))}
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2.5, justifyContent: 'center' }}>
          <Button variant="contained" color="primary" onClick={() => navigate('/orders')}>
            View All Orders
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default CreateOrderPage;
