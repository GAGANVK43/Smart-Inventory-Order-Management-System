import api from './api';

export const orderApi = {
  getAll: async () => {
    const response = await api.get('/orders');
    return response.data;
  },
  getById: async (id) => {
    const response = await api.get(`/orders/${id}`);
    return response.data;
  },
  getByCustomer: async (customerId) => {
    const response = await api.get(`/orders/customer/${customerId}`);
    return response.data;
  },
  create: async (data) => {
    const response = await api.post('/orders', data);
    return response.data;
  },
};
