import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';

const LoadingSpinner = ({ message = 'Loading data...' }) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: 200,
        py: 4,
      }}
    >
      <CircularProgress size={40} thickness={4} color="secondary" />
      <Typography variant="body2" color="text.secondary" sx={{ mt: 2, fontWeight: 500 }}>
        {message}
      </Typography>
    </Box>
  );
};

export default LoadingSpinner;
