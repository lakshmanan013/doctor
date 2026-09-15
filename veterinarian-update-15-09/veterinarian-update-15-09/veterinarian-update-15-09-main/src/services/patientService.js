import api from "./api";

// =====================================================
// GET ALL PATIENTS
// =====================================================

export const getPatients = async () => {
  const response = await api.get("/patients");
  return response.data;
};

// =====================================================
// GET PATIENT BY DATABASE ID
// =====================================================

export const getPatientById = async (id) => {
  const response = await api.get(`/patients/${id}`);
  return response.data;
};

// =====================================================
// SEARCH PATIENTS BY NAME
// =====================================================

export const searchPatients = async (name) => {
  const response = await api.get("/patients/search", {
    params: {
      name,
    },
  });

  return response.data;
};

// =====================================================
// GET BY OWNER
// =====================================================

export const getPatientsByOwner = async (ownerId) => {
  const response = await api.get(
    `/patients/owner/${ownerId}`
  );

  return response.data;
};

// =====================================================
// GET BY SPECIES
// =====================================================

export const getPatientsBySpecies = async (species) => {
  const response = await api.get(
    `/patients/species/${species}`
  );

  return response.data;
};

// =====================================================
// CREATE PATIENT
// =====================================================

export const createPatient = async (patient) => {
  const response = await api.post(
    "/patients",
    patient
  );

  return response.data;
};

// =====================================================
// UPDATE PATIENT
// =====================================================

export const updatePatient = async (id, patient) => {
  const response = await api.put(
    `/patients/${id}`,
    patient
  );

  return response.data;
};

// =====================================================
// DELETE PATIENT
// =====================================================

export const deletePatient = async (id) => {
  const response = await api.delete(
    `/patients/${id}`
  );

  return response.data;
};