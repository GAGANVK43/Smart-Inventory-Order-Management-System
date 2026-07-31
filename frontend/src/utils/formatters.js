/**
 * Format number as Currency ($ USD)
 */
export const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return '$0.00';
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

/**
 * Format ISO datetime string into human readable format
 */
export const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Get inventory stock status badge props
 */
export const getStockStatus = (quantity) => {
  if (quantity <= 0) {
    return { label: 'Out of Stock', color: 'error' };
  } else if (quantity <= 5) {
    return { label: 'Low Stock', color: 'warning' };
  } else {
    return { label: 'In Stock', color: 'success' };
  }
};
