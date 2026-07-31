import React from 'react';
import { Box, Typography } from '@mui/material';

const Footer = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 2.5,
        px: 3,
        mt: 'auto',
        backgroundColor: '#FFFFFF',
        borderTop: '1px solid #E2E8F0',
        textAlign: 'center',
      }}
    >
      <Typography variant="body2" color="text.secondary">
        © {new Date().getFullYear()} Smart Inventory & Order Management System. Built with Spring Boot 3 & React 19.
      </Typography>
    </Box>
  );
};

export default Footer;
