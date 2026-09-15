import api from "./api";

export const getBatches = async () => (await api.get("/inventory/batches")).data;
export const getBatchById = async (id) => (await api.get(`/inventory/batches/${id}`)).data;
export const getBatchesByMedicine = async (medicineId) => (await api.get(`/inventory/batches/medicine/${medicineId}`)).data;
export const getExpiringBatches = async () => (await api.get("/inventory/batches/expiring")).data;
export const createBatch = async (payload) => (await api.post("/inventory/batches", payload)).data;
export const updateBatch = async (id, payload) => (await api.put(`/inventory/batches/${id}`, payload)).data;
export const deleteBatch = async (id) => (await api.delete(`/inventory/batches/${id}`)).data;
export const getStockMovements = async () => (await api.get("/inventory/movements")).data;
export const getRecentStockMovements = async () => (await api.get("/inventory/movements/recent")).data;
export const createStockMovement = async (payload) => (await api.post("/inventory/movements", payload)).data;
export const deleteStockMovement = async (id) => (await api.delete(`/inventory/movements/${id}`)).data;
