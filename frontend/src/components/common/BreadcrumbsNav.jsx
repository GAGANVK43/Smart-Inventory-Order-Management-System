import React from 'react';
import { Breadcrumbs, Link, Typography, Box } from '@mui/material';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';

const BreadcrumbsNav = () => {
  const location = useLocation();
  const pathnames = location.pathname.split('/').filter((x) => x);

  return (
    <Box sx={{ mb: 2 }}>
      <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} aria-label="breadcrumb">
        <Link component={RouterLink} to="/" underline="hover" color="inherit" sx={{ fontSize: '0.875rem' }}>
          Dashboard
        </Link>
        {pathnames.map((name, index) => {
          const routeTo = `/${pathnames.slice(0, index + 1).join('/')}`;
          const isLast = index === pathnames.length - 1;
          const formattedName = name.charAt(0).toUpperCase() + name.slice(1);

          return isLast ? (
            <Typography key={name} color="text.primary" sx={{ fontSize: '0.875rem', fontWeight: 600 }}>
              {formattedName}
            </Typography>
          ) : (
            <Link key={name} component={RouterLink} to={routeTo} underline="hover" color="inherit" sx={{ fontSize: '0.875rem' }}>
              {formattedName}
            </Link>
          );
        })}
      </Breadcrumbs>
    </Box>
  );
};

export default BreadcrumbsNav;
