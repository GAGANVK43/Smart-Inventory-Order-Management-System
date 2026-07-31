import React, { createContext, useContext, useState } from 'react';

const AppContext = createContext();

export const AppProvider = ({ children }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [toast, setToast] = useState({ open: false, message: '', severity: 'success' });
  const [refreshKey, setRefreshKey] = useState(0);

  const toggleSidebar = () => {
    setSidebarOpen((prev) => !prev);
  };

  const showNotification = (message, severity = 'success') => {
    setToast({ open: true, message, severity });
  };

  const hideNotification = () => {
    setToast((prev) => ({ ...prev, open: false }));
  };

  const triggerRefresh = () => {
    setRefreshKey((prev) => prev + 1);
  };

  return (
    <AppContext.Provider
      value={{
        sidebarOpen,
        setSidebarOpen,
        toggleSidebar,
        toast,
        showNotification,
        hideNotification,
        refreshKey,
        triggerRefresh,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => useContext(AppContext);
