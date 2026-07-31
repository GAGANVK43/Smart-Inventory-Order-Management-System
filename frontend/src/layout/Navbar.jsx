import React from 'react';
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  Avatar,
  Chip,
  Tooltip,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import InventoryIcon from '@mui/icons-material/Inventory';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import { useApp } from '../context/AppContext';

const Navbar = () => {
  const { toggleSidebar } = useApp();

  return (
    <AppBar
      position="sticky"
      elevation={0}
      sx={{
        backgroundColor: '#FFFFFF',
        color: '#0F172A',
        borderBottom: '1px solid #E2E8F0',
        zIndex: (theme) => theme.zIndex.drawer + 1,
      }}
    >
      <Toolbar sx={{ justifyContent: 'space-between', minHeight: 64 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={toggleSidebar}
            sx={{ mr: 1, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>

          <Box
            sx={{
              width: 36,
              height: 36,
              borderRadius: 2,
              backgroundColor: '#0F172A',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#10B981',
            }}
          >
            <InventoryIcon fontSize="small" />
          </Box>

          <Typography
            variant="h6"
            noWrap
            sx={{
              fontWeight: 700,
              letterSpacing: '-0.02em',
              display: { xs: 'none', sm: 'block' },
            }}
          >
            Smart Inventory
          </Typography>

          <Chip
            label="System Online"
            color="success"
            size="small"
            variant="outlined"
            sx={{ height: 22, fontSize: '0.7rem', fontWeight: 600, ml: 1 }}
          />
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Tooltip title="Notifications">
            <IconButton size="small" color="inherit">
              <NotificationsNoneIcon />
            </IconButton>
          </Tooltip>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, pl: 1 }}>
            <Avatar sx={{ width: 36, height: 36, bgcolor: '#0F172A', fontSize: '0.875rem', fontWeight: 700 }}>
              AD
            </Avatar>
            <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
              <Typography variant="body2" sx={{ fontWeight: 600, lineHeight: 1.2 }}>
                Admin User
              </Typography>
              <Typography variant="caption" color="text.secondary">
                System Administrator
              </Typography>
            </Box>
          </Box>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
