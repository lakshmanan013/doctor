import api from "./api";

export const getDoctorProfile = async () => {
  const response = await api.get(
    "/doctor-profile"
  );

  return response.data;
};

export const updateDoctorProfile = async (
  profile
) => {
  const response = await api.put(
    "/doctor-profile",
    profile
  );

  return response.data;
};