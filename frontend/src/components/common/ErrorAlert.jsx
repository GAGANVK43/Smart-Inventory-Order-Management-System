import React from 'react';
import { Alert, AlertTitle, Button, Box } from '@mui/material';

const ErrorAlert = ({ message, onRetry }) => {
  return (
    <Box sx={{ my: 2 }}>
      <Alert
        severity="error"
        action={
          onRetry && (
            <Button color="inherit" size="small" onClick={onRetry}>
              Retry
            </Button>
          )
        }
      >
        <AlertTitle>Error</AlertTitle>
        {message || 'Failed to load data from backend server.'}
      </Alert>
    </Box>
  );
};

export default ErrorAlert;
