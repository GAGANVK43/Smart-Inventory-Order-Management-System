import React from 'react';
import { Box, Paper, Typography, TextField, Button, Grid, Divider } from '@mui/material';
import SaveIcon from '@mui/icons-material/Save';
import { useApp } from '../../context/AppContext';

const SettingsPage = () => {
  const { showNotification } = useApp();

  const handleSave = (e) => {
    e.preventDefault();
    showNotification('System configuration saved successfully!', 'success');
  };

  return (
    <Box sx={{ pb: 4 }}>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700, color: '#0F172A' }}>
          System Settings
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Configure API connection thresholds, notification defaults, and system parameters.
        </Typography>
      </Box>

      <Paper sx={{ p: 4, borderRadius: 3, border: '1px solid #E2E8F0', maxWidth: 800 }}>
        <form onSubmit={handleSave}>
          <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
            API Connection Parameters
          </Typography>
          <Grid container spacing={2.5} sx={{ mb: 3 }}>
            <Grid item xs={12}>
              <TextField
                label="Spring Boot REST API Base URL"
                fullWidth
                size="small"
                defaultValue={import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1'}
                disabled
                helperText="Configured via .env (VITE_API_BASE_URL)"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField label="Low Stock Threshold (Units)" type="number" fullWidth size="small" defaultValue={5} />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField label="Request Timeout (ms)" type="number" fullWidth size="small" defaultValue={10000} />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />

          <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
            Administrator Profile
          </Typography>
          <Grid container spacing={2.5} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={6}>
              <TextField label="Admin User Name" fullWidth size="small" defaultValue="Admin User" />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField label="Admin Contact Email" fullWidth size="small" defaultValue="admin@inventory.com" />
            </Grid>
          </Grid>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button type="submit" variant="contained" color="secondary" startIcon={<SaveIcon />}>
              Save Settings
            </Button>
          </Box>
        </form>
      </Paper>
    </Box>
  );
};

export default SettingsPage;
