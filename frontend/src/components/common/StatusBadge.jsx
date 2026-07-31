import React from 'react';
import { Chip } from '@mui/material';
import { getStockStatus } from '../../utils/formatters';

const StatusBadge = ({ quantity }) => {
  const status = getStockStatus(quantity);

  return (
    <Chip
      label={status.label}
      color={status.color}
      size="small"
      sx={{
        fontWeight: 600,
        fontSize: '0.75rem',
        borderRadius: '6px',
      }}
    />
  );
};

export default StatusBadge;
