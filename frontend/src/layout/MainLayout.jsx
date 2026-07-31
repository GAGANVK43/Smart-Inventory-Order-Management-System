import React from 'react';
import { Box, Snackbar, Alert } from '@mui/material';
import { Outlet } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import Footer from './Footer';
import BreadcrumbsNav from '../components/common/BreadcrumbsNav';
import { useApp } from '../context/AppContext';

const drawerWidth = 240;

const MainLayout = () => {
  const { toast, hideNotification } = useApp();

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh', backgroundColor: '#F8FAFC' }}>
      <Navbar />

      <Box sx={{ display: 'flex', flexGrow: 1 }}>
        <Sidebar />

        <Box
          component="main"
          sx={{
            flexGrow: 1,
            p: { xs: 2, sm: 3, md: 4 },
            width: { md: `calc(100% - ${drawerWidth}px)` },
            display: 'flex',
            flexDirection: 'column',
            minHeight: 'calc(100vh - 64px)',
          }}
        >
          <BreadcrumbsNav />
          <Box sx={{ flexGrow: 1 }}>
            <Outlet />
          </Box>
          <Footer />
        </Box>
      </Box>

      {/* Global Toast Notifications */}
      <Snackbar
        open={toast.open}
        autoHideDuration={4000}
        onClose={hideNotification}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={hideNotification} severity={toast.severity} variant="filled" sx={{ width: '100%', fontWeight: 600 }}>
          {toast.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default MainLayout;
