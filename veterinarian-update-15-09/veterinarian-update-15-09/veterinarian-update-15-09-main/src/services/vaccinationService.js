import api from "./api";

export const getVaccinations = async () =>
  (await api.get("/vaccinations")).data;

export const getVaccinationById = async (id) =>
  (await api.get(`/vaccinations/${id}`)).data;

export const getVaccinationsByPatient = async (patientId) =>
  (await api.get(`/vaccinations/patient/${patientId}`)).data;

export const getDueVaccinations = async (date = new Date().toISOString().slice(0, 10)) =>
  (await api.get("/vaccinations/due", { params: { date } })).data;

export const createVaccination = async (payload) =>
  (await api.post("/vaccinations", payload)).data;

export const updateVaccination = async (id, payload) =>
  (await api.put(`/vaccinations/${id}`, payload)).data;

export const deleteVaccination = async (id) =>
  (await api.delete(`/vaccinations/${id}`)).data;