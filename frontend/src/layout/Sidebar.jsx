import React from 'react';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Box,
  Typography,
  Divider,
} from '@mui/material';
import { NavLink, useLocation } from 'react-router-dom';
import DashboardIcon from '@mui/icons-material/Dashboard';
import CategoryIcon from '@mui/icons-material/Category';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ShoppingBagIcon from '@mui/icons-material/ShoppingBag';
import PeopleIcon from '@mui/icons-material/People';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import BarChartIcon from '@mui/icons-material/BarChart';
import SettingsIcon from '@mui/icons-material/Settings';
import { useApp } from '../context/AppContext';

const drawerWidth = 240;

const menuItems = [
  { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
  { text: 'Category', icon: <CategoryIcon />, path: '/categories' },
  { text: 'Supplier', icon: <LocalShippingIcon />, path: '/suppliers' },
  { text: 'Products', icon: <ShoppingBagIcon />, path: '/products' },
  { text: 'Customers', icon: <PeopleIcon />, path: '/customers' },
  { text: 'Orders', icon: <ReceiptLongIcon />, path: '/orders' },
  { text: 'Analytics', icon: <BarChartIcon />, path: '/analytics' },
  { text: 'Settings', icon: <SettingsIcon />, path: '/settings' },
];

const Sidebar = () => {
  const location = useLocation();
  const { sidebarOpen, setSidebarOpen } = useApp();

  const drawerContent = (
    <Box sx={{ overflow: 'auto', py: 2 }}>
      <Box sx={{ px: 3, pb: 2 }}>
        <Typography variant="caption" sx={{ textTransform: 'uppercase', letterSpacing: '0.1em', fontWeight: 700, color: '#94A3B8' }}>
          Menu Options
        </Typography>
      </Box>
      <List sx={{ px: 1.5 }}>
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));

          return (
            <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
              <ListItemButton
                component={NavLink}
                to={item.path}
                onClick={() => setSidebarOpen(false)}
                sx={{
                  borderRadius: 2,
                  py: 1.2,
                  px: 2,
                  backgroundColor: isActive ? '#0F172A' : 'transparent',
                  color: isActive ? '#FFFFFF' : '#475569',
                  '&:hover': {
                    backgroundColor: isActive ? '#0F172A' : '#F1F5F9',
                  },
                }}
              >
                <ListItemIcon sx={{ minWidth: 36, color: isActive ? '#10B981' : '#64748B' }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontSize: '0.875rem',
                    fontWeight: isActive ? 600 : 500,
                  }}
                />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Box>
  );

  return (
    <>
      {/* Temporary Drawer for Mobile */}
      <Drawer
        variant="temporary"
        open={sidebarOpen}
        onClose={() => setSidebarOpen(false)}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth, backgroundColor: '#FFFFFF', borderRight: '1px solid #E2E8F0' },
        }}
      >
        {drawerContent}
      </Drawer>

      {/* Permanent Sidebar for Desktop */}
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: drawerWidth,
            boxSizing: 'border-box',
            backgroundColor: '#FFFFFF',
            borderRight: '1px solid #E2E8F0',
            top: 64,
            height: 'calc(100% - 64px)',
          },
        }}
        open
      >
        {drawerContent}
      </Drawer>
    </>
  );
};

export default Sidebar;
