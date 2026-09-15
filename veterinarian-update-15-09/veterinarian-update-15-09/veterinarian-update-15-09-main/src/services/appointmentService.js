import api from "./api";

export const getAppointments = async () => (await api.get("/appointments")).data;
export const getAppointmentById = async (id) => (await api.get(`/appointments/${id}`)).data;
export const getAppointmentsByPatient = async (patientId) => (await api.get(`/appointments/patient/${patientId}`)).data;
export const getAppointmentsByDate = async (date) => (await api.get(`/appointments/date/${date}`)).data;
export const getAppointmentsByStatus = async (status) => (await api.get(`/appointments/status/${encodeURIComponent(status)}`)).data;
export const createAppointment = async (payload) => (await api.post("/appointments", payload)).data;
export const updateAppointment = async (id, payload) => (await api.put(`/appointments/${id}`, payload)).data;
export const deleteAppointment = async (id) => (await api.delete(`/appointments/${id}`)).data;
