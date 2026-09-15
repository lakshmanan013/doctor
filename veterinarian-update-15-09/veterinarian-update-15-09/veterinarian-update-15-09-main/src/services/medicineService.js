import api from "./api";

export const getMedicines = async () => (await api.get("/medicines")).data;
export const getMedicineById = async (id) => (await api.get(`/medicines/${id}`)).data;
export const searchMedicines = async (query) => (await api.get("/medicines/search", { params: { query } })).data;
export const getMedicinesByCategory = async (category) => (await api.get(`/medicines/category/${encodeURIComponent(category)}`)).data;
export const getMedicinesByStatus = async (status) => (await api.get(`/medicines/status/${encodeURIComponent(status)}`)).data;
export const createMedicine = async (payload) => (await api.post("/medicines", payload)).data;
export const updateMedicine = async (id, payload) => (await api.put(`/medicines/${id}`, payload)).data;
export const updateMedicineStock = async (id, quantity) => (await api.patch(`/medicines/${id}/stock`, null, { params: { quantity } })).data;
export const deleteMedicine = async (id) => (await api.delete(`/medicines/${id}`)).data;
