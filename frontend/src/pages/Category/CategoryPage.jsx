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
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useForm } from 'react-hook-form';

import { categoryApi } from '../../services/categoryApi';
import { useApp } from '../../context/AppContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';

const CategoryPage = () => {
  const { showNotification } = useApp();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // Delete Dialog State
  const [deleteId, setDeleteId] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm();

  const fetchCategories = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await categoryApi.getAll();
      setCategories(data);
    } catch (err) {
      setError(err.message || 'Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  const handleOpenAddModal = () => {
    setEditingCategory(null);
    reset({ name: '', description: '' });
    setOpenModal(true);
  };

  const handleOpenEditModal = (cat) => {
    setEditingCategory(cat);
    setValue('name', cat.name);
    setValue('description', cat.description || '');
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setEditingCategory(null);
  };

  const onSubmitForm = async (formData) => {
    setSubmitting(true);
    try {
      if (editingCategory) {
        await categoryApi.update(editingCategory.id, formData);
        showNotification('Category updated successfully!', 'success');
      } else {
        await categoryApi.create(formData);
        showNotification('Category created successfully!', 'success');
      }
      handleCloseModal();
      fetchCategories();
    } catch (err) {
      showNotification(err.message || 'Failed to save category', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteId) return;
    setDeleting(true);
    try {
      await categoryApi.delete(deleteId);
      showNotification('Category deleted successfully!', 'success');
      setDeleteId(null);
      fetchCategories();
    } catch (err) {
      showNotification(err.message || 'Failed to delete category', 'error');
    } finally {
      setDeleting(false);
    }
  };

  // Filter Categories by Search Term
  const filteredCategories = categories.filter(
    (c) =>
      c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (c.description && c.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  return (
    <Box sx={{ pb: 4 }}>
      {/* Page Title & Actions */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            Category Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Organize products into logical category structures.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={fetchCategories} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button variant="contained" color="secondary" startIcon={<AddIcon />} onClick={handleOpenAddModal}>
            Add Category
          </Button>
        </Box>
      </Box>

      {/* Main Content Paper */}
      <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
        {/* Search Bar */}
        <Box sx={{ mb: 3, maxWidth: 360 }}>
          <TextField
            fullWidth
            size="small"
            placeholder="Search categories..."
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

        {loading && <LoadingSpinner message="Loading categories..." />}
        {error && <ErrorAlert message={error} onRetry={fetchCategories} />}

        {!loading && !error && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Category Name</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredCategories.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={4} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No categories found. Click "Add Category" to create one.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredCategories.map((cat) => (
                    <TableRow key={cat.id} hover>
                      <TableCell sx={{ fontWeight: 700 }}>#{cat.id}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{cat.name}</TableCell>
                      <TableCell color="text.secondary">{cat.description || 'N/A'}</TableCell>
                      <TableCell align="right">
                        <Tooltip title="Edit">
                          <IconButton size="small" color="primary" onClick={() => handleOpenEditModal(cat)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => setDeleteId(cat.id)}>
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

      {/* Add / Edit Category Modal */}
      <Dialog open={openModal} onClose={handleCloseModal} maxWidth="sm" fullWidth>
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <DialogTitle sx={{ fontWeight: 700 }}>
            {editingCategory ? 'Edit Category' : 'Add New Category'}
          </DialogTitle>
          <DialogContent divider>
            <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <TextField
                label="Category Name"
                fullWidth
                size="small"
                {...register('name', {
                  required: 'Category name is required',
                  minLength: { value: 2, message: 'Name must be at least 2 characters' },
                })}
                error={!!errors.name}
                helperText={errors.name?.message}
              />
              <TextField
                label="Description"
                fullWidth
                multiline
                rows={3}
                size="small"
                {...register('description', {
                  maxLength: { value: 500, message: 'Description cannot exceed 500 characters' },
                })}
                error={!!errors.description}
                helperText={errors.description?.message}
              />
            </Box>
          </DialogContent>
          <DialogActions sx={{ px: 3, py: 2 }}>
            <Button onClick={handleCloseModal} color="inherit" disabled={submitting}>
              Cancel
            </Button>
            <Button type="submit" variant="contained" color="secondary" disabled={submitting}>
              {submitting ? 'Saving...' : editingCategory ? 'Update Category' : 'Create Category'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Confirmation Dialog */}
      <ConfirmationDialog
        open={Boolean(deleteId)}
        title="Delete Category?"
        message="Are you sure you want to delete this category? This action cannot be undone."
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleting}
      />
    </Box>
  );
};

export default CategoryPage;
