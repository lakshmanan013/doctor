import api from "./api";

export const getPrescriptions = async () => (await api.get("/prescriptions")).data;
export const getPrescriptionById = async (id) => (await api.get(`/prescriptions/${id}`)).data;
export const getPrescriptionsByPatient = async (patientId) => (await api.get(`/prescriptions/patient/${patientId}`)).data;
export const createPrescription = async (payload) => (await api.post("/prescriptions", payload)).data;
export const updatePrescription = async (id, payload) => (await api.put(`/prescriptions/${id}`, payload)).data;
export const deletePrescription = async (id) => (await api.delete(`/prescriptions/${id}`)).data;
