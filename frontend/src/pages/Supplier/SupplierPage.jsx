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

import { supplierApi } from '../../services/supplierApi';
import { useApp } from '../../context/AppContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';

const SupplierPage = () => {
  const { showNotification } = useApp();
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState(null);
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

  const fetchSuppliers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await supplierApi.getAll();
      setSuppliers(data);
    } catch (err) {
      setError(err.message || 'Failed to load suppliers');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const handleOpenAddModal = () => {
    setEditingSupplier(null);
    reset({ name: '', email: '', phone: '' });
    setOpenModal(true);
  };

  const handleOpenEditModal = (sup) => {
    setEditingSupplier(sup);
    setValue('name', sup.name);
    setValue('email', sup.email);
    setValue('phone', sup.phone);
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setEditingSupplier(null);
  };

  const onSubmitForm = async (formData) => {
    setSubmitting(true);
    try {
      if (editingSupplier) {
        await supplierApi.update(editingSupplier.id, formData);
        showNotification('Supplier updated successfully!', 'success');
      } else {
        await supplierApi.create(formData);
        showNotification('Supplier created successfully!', 'success');
      }
      handleCloseModal();
      fetchSuppliers();
    } catch (err) {
      showNotification(err.message || 'Failed to save supplier', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteId) return;
    setDeleting(true);
    try {
      await supplierApi.delete(deleteId);
      showNotification('Supplier deleted successfully!', 'success');
      setDeleteId(null);
      fetchSuppliers();
    } catch (err) {
      showNotification(err.message || 'Failed to delete supplier', 'error');
    } finally {
      setDeleting(false);
    }
  };

  // Filter Suppliers
  const filteredSuppliers = suppliers.filter(
    (s) =>
      s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      s.phone.includes(searchTerm)
  );

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header Bar */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            Supplier Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage vendor contact records, emails, and supply channels.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={fetchSuppliers} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button variant="contained" color="secondary" startIcon={<AddIcon />} onClick={handleOpenAddModal}>
            Add Supplier
          </Button>
        </Box>
      </Box>

      {/* Main Table Paper */}
      <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
        <Box sx={{ mb: 3, maxWidth: 360 }}>
          <TextField
            fullWidth
            size="small"
            placeholder="Search suppliers..."
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

        {loading && <LoadingSpinner message="Loading suppliers..." />}
        {error && <ErrorAlert message={error} onRetry={fetchSuppliers} />}

        {!loading && !error && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Supplier Name</TableCell>
                  <TableCell>Email Address</TableCell>
                  <TableCell>Phone Number</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredSuppliers.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No suppliers found. Click "Add Supplier" to create one.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredSuppliers.map((sup) => (
                    <TableRow key={sup.id} hover>
                      <TableCell sx={{ fontWeight: 700 }}>#{sup.id}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{sup.name}</TableCell>
                      <TableCell color="text.secondary">{sup.email}</TableCell>
                      <TableCell color="text.secondary">{sup.phone}</TableCell>
                      <TableCell align="right">
                        <Tooltip title="Edit">
                          <IconButton size="small" color="primary" onClick={() => handleOpenEditModal(sup)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => setDeleteId(sup.id)}>
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

      {/* Add / Edit Supplier Modal */}
      <Dialog open={openModal} onClose={handleCloseModal} maxWidth="sm" fullWidth>
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <DialogTitle sx={{ fontWeight: 700 }}>
            {editingSupplier ? 'Edit Supplier' : 'Add New Supplier'}
          </DialogTitle>
          <DialogContent divider>
            <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <TextField
                label="Supplier Name"
                fullWidth
                size="small"
                {...register('name', {
                  required: 'Supplier name is required',
                  minLength: { value: 2, message: 'Name must be at least 2 characters' },
                })}
                error={!!errors.name}
                helperText={errors.name?.message}
              />
              <TextField
                label="Email Address"
                fullWidth
                size="small"
                type="email"
                {...register('email', {
                  required: 'Email address is required',
                  pattern: {
                    value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                    message: 'Please enter a valid email address',
                  },
                })}
                error={!!errors.email}
                helperText={errors.email?.message}
              />
              <TextField
                label="Phone Number"
                fullWidth
                size="small"
                {...register('phone', {
                  required: 'Phone number is required',
                  pattern: {
                    value: /^[0-9+ -]{8,20}$/,
                    message: 'Please enter a valid phone number (8-20 digits)',
                  },
                })}
                error={!!errors.phone}
                helperText={errors.phone?.message}
              />
            </Box>
          </DialogContent>
          <DialogActions sx={{ px: 3, py: 2 }}>
            <Button onClick={handleCloseModal} color="inherit" disabled={submitting}>
              Cancel
            </Button>
            <Button type="submit" variant="contained" color="secondary" disabled={submitting}>
              {submitting ? 'Saving...' : editingSupplier ? 'Update Supplier' : 'Create Supplier'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Confirmation Dialog */}
      <ConfirmationDialog
        open={Boolean(deleteId)}
        title="Delete Supplier?"
        message="Are you sure you want to delete this supplier record? This action cannot be undone."
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleting}
      />
    </Box>
  );
};

export default SupplierPage;
