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
  MenuItem,
  Tooltip,
  Grid,
  Avatar,
  Chip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import FilterAltIcon from '@mui/icons-material/FilterAlt';
import { useForm } from 'react-hook-form';

import { productApi } from '../../services/productApi';
import { categoryApi } from '../../services/categoryApi';
import { supplierApi } from '../../services/supplierApi';
import { useApp } from '../../context/AppContext';
import { formatCurrency } from '../../utils/formatters';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';
import StatusBadge from '../../components/common/StatusBadge';

const ProductPage = () => {
  const { showNotification } = useApp();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [selectedSupplier, setSelectedSupplier] = useState('ALL');

  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Delete State
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm();

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [prodData, catData, supData] = await Promise.all([
        productApi.getAll(),
        categoryApi.getAll(),
        supplierApi.getAll(),
      ]);
      setProducts(prodData);
      setCategories(catData);
      setSuppliers(supData);
    } catch (err) {
      setError(err.message || 'Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleOpenAddModal = () => {
    setEditingProduct(null);
    reset({
      name: '',
      description: '',
      price: '',
      quantity: '',
      categoryId: categories[0]?.id || '',
      supplierId: suppliers[0]?.id || '',
    });
    setOpenModal(true);
  };

  const handleOpenEditModal = (prod) => {
    setEditingProduct(prod);
    setValue('name', prod.name);
    setValue('description', prod.description || '');
    setValue('price', prod.price);
    setValue('quantity', prod.quantity);
    setValue('categoryId', prod.categoryId);
    setValue('supplierId', prod.supplierId);
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setEditingProduct(null);
  };

  const onSubmitForm = async (formData) => {
    setSubmitting(true);
    try {
      const payload = {
        name: formData.name,
        description: formData.description,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.quantity, 10),
        categoryId: parseInt(formData.categoryId, 10),
        supplierId: parseInt(formData.supplierId, 10),
      };

      if (editingProduct) {
        await productApi.update(editingProduct.id, payload);
        showNotification('Product updated successfully!', 'success');
      } else {
        await productApi.create(payload);
        showNotification('Product added successfully!', 'success');
      }
      handleCloseModal();
      loadData();
    } catch (err) {
      showNotification(err.message || 'Failed to save product', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteId) return;
    setDeleting(true);
    try {
      await productApi.delete(deleteId);
      showNotification('Product deleted successfully!', 'success');
      setDeleteId(null);
      loadData();
    } catch (err) {
      showNotification(err.message || 'Failed to delete product', 'error');
    } finally {
      setDeleting(false);
    }
  };

  // Filter Products
  const filteredProducts = products.filter((p) => {
    const matchesSearch =
      p.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (p.description && p.description.toLowerCase().includes(searchTerm.toLowerCase()));
    const matchesCategory = selectedCategory === 'ALL' || p.categoryId === Number(selectedCategory);
    const matchesSupplier = selectedSupplier === 'ALL' || p.supplierId === Number(selectedSupplier);

    return matchesSearch && matchesCategory && matchesSupplier;
  });

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header Bar */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            Product Catalog
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage inventory items, pricing, categories, and stock quantities.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={loadData} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button variant="contained" color="secondary" startIcon={<AddIcon />} onClick={handleOpenAddModal}>
            Add Product
          </Button>
        </Box>
      </Box>

      {/* Main Content Paper */}
      <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
        {/* Search & Filter Bar */}
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={4}>
            <TextField
              fullWidth
              size="small"
              placeholder="Search products by keyword..."
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
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              select
              fullWidth
              size="small"
              label="Filter by Category"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              <MenuItem value="ALL">All Categories</MenuItem>
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>
                  {c.name}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              select
              fullWidth
              size="small"
              label="Filter by Supplier"
              value={selectedSupplier}
              onChange={(e) => setSelectedSupplier(e.target.value)}
            >
              <MenuItem value="ALL">All Suppliers</MenuItem>
              {suppliers.map((s) => (
                <MenuItem key={s.id} value={s.id}>
                  {s.name}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
        </Grid>

        {loading && <LoadingSpinner message="Loading product catalog..." />}
        {error && <ErrorAlert message={error} onRetry={loadData} />}

        {!loading && !error && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Product</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell>Supplier</TableCell>
                  <TableCell align="right">Price</TableCell>
                  <TableCell align="right">Stock</TableCell>
                  <TableCell align="center">Status</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredProducts.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No products match your criteria. Click "Add Product" to create one.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredProducts.map((prod) => (
                    <TableRow key={prod.id} hover>
                      <TableCell>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                          <Avatar sx={{ bgcolor: '#F1F5F9', color: '#0F172A', width: 36, height: 36, borderRadius: 1.5 }}>
                            <ShoppingBagIcon fontSize="small" />
                          </Avatar>
                          <Box>
                            <Typography variant="body2" sx={{ fontWeight: 600 }}>
                              {prod.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary" noWrap sx={{ maxWidth: 200, display: 'block' }}>
                              {prod.description || 'No description'}
                            </Typography>
                          </Box>
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip label={prod.categoryName} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell color="text.secondary">{prod.supplierName}</TableCell>
                      <TableCell align="right" sx={{ fontWeight: 700, color: '#10B981' }}>
                        {formatCurrency(prod.price)}
                      </TableCell>
                      <TableCell align="right" sx={{ fontWeight: 700 }}>
                        {prod.quantity}
                      </TableCell>
                      <TableCell align="center">
                        <StatusBadge quantity={prod.quantity} />
                      </TableCell>
                      <TableCell align="right">
                        <Tooltip title="Edit">
                          <IconButton size="small" color="primary" onClick={() => handleOpenEditModal(prod)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => setDeleteId(prod.id)}>
                            <DeleteIcon fontSize="small" />
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

      {/* Add / Edit Product Modal */}
      <Dialog open={openModal} onClose={handleCloseModal} maxWidth="sm" fullWidth>
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <DialogTitle sx={{ fontWeight: 700 }}>
            {editingProduct ? 'Edit Product' : 'Add New Product'}
          </DialogTitle>
          <DialogContent divider>
            <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
              <TextField
                label="Product Name"
                fullWidth
                size="small"
                {...register('name', { required: 'Product name is required' })}
                error={!!errors.name}
                helperText={errors.name?.message}
              />
              <TextField
                label="Description"
                fullWidth
                multiline
                rows={2}
                size="small"
                {...register('description')}
              />
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    label="Price ($)"
                    type="number"
                    step="0.01"
                    fullWidth
                    size="small"
                    {...register('price', {
                      required: 'Price is required',
                      min: { value: 0.01, message: 'Price must be greater than zero' },
                    })}
                    error={!!errors.price}
                    helperText={errors.price?.message}
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    label="Stock Quantity"
                    type="number"
                    fullWidth
                    size="small"
                    {...register('quantity', {
                      required: 'Quantity is required',
                      min: { value: 0, message: 'Quantity cannot be negative' },
                    })}
                    error={!!errors.quantity}
                    helperText={errors.quantity?.message}
                  />
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <TextField
                    select
                    label="Category"
                    fullWidth
                    size="small"
                    {...register('categoryId', { required: 'Category selection is required' })}
                    error={!!errors.categoryId}
                    helperText={errors.categoryId?.message}
                  >
                    {categories.map((c) => (
                      <MenuItem key={c.id} value={c.id}>
                        {c.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    select
                    label="Supplier"
                    fullWidth
                    size="small"
                    {...register('supplierId', { required: 'Supplier selection is required' })}
                    error={!!errors.supplierId}
                    helperText={errors.supplierId?.message}
                  >
                    {suppliers.map((s) => (
                      <MenuItem key={s.id} value={s.id}>
                        {s.name}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
              </Grid>
            </Box>
          </DialogContent>
          <DialogActions sx={{ px: 3, py: 2 }}>
            <Button onClick={handleCloseModal} color="inherit" disabled={submitting}>
              Cancel
            </Button>
            <Button type="submit" variant="contained" color="secondary" disabled={submitting}>
              {submitting ? 'Saving...' : editingProduct ? 'Update Product' : 'Add Product'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Delete Dialog */}
      <ConfirmationDialog
        open={Boolean(deleteId)}
        title="Delete Product?"
        message="Are you sure you want to remove this product from inventory? This action cannot be undone."
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleting}
      />
    </Box>
  );
};

export default ProductPage;
