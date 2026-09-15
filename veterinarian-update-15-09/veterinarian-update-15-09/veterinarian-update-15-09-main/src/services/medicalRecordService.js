import api from "./api";

export const getMedicalRecords = async () => (await api.get("/medical-records")).data;
export const getMedicalRecordById = async (id) => (await api.get(`/medical-records/${id}`)).data;
export const getMedicalRecordsByPatient = async (patientId) => (await api.get(`/medical-records/patient/${patientId}`)).data;
export const createMedicalRecord = async (payload) => (await api.post("/medical-records", payload)).data;
export const updateMedicalRecord = async (id, payload) => (await api.put(`/medical-records/${id}`, payload)).data;
export const deleteMedicalRecord = async (id) => (await api.delete(`/medical-records/${id}`)).data;
