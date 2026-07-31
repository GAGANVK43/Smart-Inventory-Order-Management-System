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

import { customerApi } from '../../services/customerApi';
import { useApp } from '../../context/AppContext';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorAlert from '../../components/common/ErrorAlert';
import ConfirmationDialog from '../../components/common/ConfirmationDialog';

const CustomerPage = () => {
  const { showNotification } = useApp();
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modal State
  const [openModal, setOpenModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
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

  const fetchCustomers = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await customerApi.getAll();
      setCustomers(data);
    } catch (err) {
      setError(err.message || 'Failed to load customers');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleOpenAddModal = () => {
    setEditingCustomer(null);
    reset({ name: '', email: '', phone: '' });
    setOpenModal(true);
  };

  const handleOpenEditModal = (cust) => {
    setEditingCustomer(cust);
    setValue('name', cust.name);
    setValue('email', cust.email);
    setValue('phone', cust.phone);
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    setEditingCustomer(null);
  };

  const onSubmitForm = async (formData) => {
    setSubmitting(true);
    try {
      if (editingCustomer) {
        await customerApi.update(editingCustomer.id, formData);
        showNotification('Customer updated successfully!', 'success');
      } else {
        await customerApi.create(formData);
        showNotification('Customer created successfully!', 'success');
      }
      handleCloseModal();
      fetchCustomers();
    } catch (err) {
      showNotification(err.message || 'Failed to save customer', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteConfirm = async () => {
    if (!deleteId) return;
    setDeleting(true);
    try {
      await customerApi.delete(deleteId);
      showNotification('Customer deleted successfully!', 'success');
      setDeleteId(null);
      fetchCustomers();
    } catch (err) {
      showNotification(err.message || 'Failed to delete customer', 'error');
    } finally {
      setDeleting(false);
    }
  };

  // Filter Customers
  const filteredCustomers = customers.filter(
    (c) =>
      c.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.phone.includes(searchTerm)
  );

  return (
    <Box sx={{ pb: 4 }}>
      {/* Header Bar */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
            Customer Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage customer accounts, emails, and contact records.
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1.5 }}>
          <Tooltip title="Refresh">
            <IconButton onClick={fetchCustomers} color="primary" sx={{ border: '1px solid #E2E8F0', borderRadius: 2 }}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button variant="contained" color="secondary" startIcon={<AddIcon />} onClick={handleOpenAddModal}>
            Add Customer
          </Button>
        </Box>
      </Box>

      {/* Main Content Paper */}
      <Paper sx={{ p: 3, borderRadius: 3, border: '1px solid #E2E8F0' }}>
        <Box sx={{ mb: 3, maxWidth: 360 }}>
          <TextField
            fullWidth
            size="small"
            placeholder="Search customers..."
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

        {loading && <LoadingSpinner message="Loading customers..." />}
        {error && <ErrorAlert message={error} onRetry={fetchCustomers} />}

        {!loading && !error && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Customer Name</TableCell>
                  <TableCell>Email Address</TableCell>
                  <TableCell>Phone Number</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredCustomers.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={5} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                      No customers found. Click "Add Customer" to create one.
                    </TableCell>
                  </TableRow>
                ) : (
                  filteredCustomers.map((cust) => (
                    <TableRow key={cust.id} hover>
                      <TableCell sx={{ fontWeight: 700 }}>#{cust.id}</TableCell>
                      <TableCell sx={{ fontWeight: 600 }}>{cust.name}</TableCell>
                      <TableCell color="text.secondary">{cust.email}</TableCell>
                      <TableCell color="text.secondary">{cust.phone}</TableCell>
                      <TableCell align="right">
                        <Tooltip title="Edit">
                          <IconButton size="small" color="primary" onClick={() => handleOpenEditModal(cust)}>
                            <EditIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => setDeleteId(cust.id)}>
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

      {/* Add / Edit Customer Modal */}
      <Dialog open={openModal} onClose={handleCloseModal} maxWidth="sm" fullWidth>
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <DialogTitle sx={{ fontWeight: 700 }}>
            {editingCustomer ? 'Edit Customer' : 'Add New Customer'}
          </DialogTitle>
          <DialogContent divider>
            <Box sx={{ pt: 1, display: 'flex', flexDirection: 'column', gap: 2.5 }}>
              <TextField
                label="Customer Name"
                fullWidth
                size="small"
                {...register('name', {
                  required: 'Customer name is required',
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
              {submitting ? 'Saving...' : editingCustomer ? 'Update Customer' : 'Create Customer'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Confirmation Dialog */}
      <ConfirmationDialog
        open={Boolean(deleteId)}
        title="Delete Customer?"
        message="Are you sure you want to delete this customer account? This action cannot be undone."
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteId(null)}
        loading={deleting}
      />
    </Box>
  );
};

export default CustomerPage;
