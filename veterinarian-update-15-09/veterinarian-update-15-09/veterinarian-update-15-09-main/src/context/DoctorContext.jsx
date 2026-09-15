import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";

import {
  getDoctorProfile,
  updateDoctorProfile,
} from "../services/doctorProfileService";

const DoctorContext = createContext(null);

export function DoctorProvider({ children }) {
  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);

  const loadDoctor = useCallback(async () => {
    try {
      setLoading(true);

      const data = await getDoctorProfile();

      setDoctor(data || null);
    } catch (error) {
      console.error(
        "Failed to load doctor profile:",
        error
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadDoctor();
  }, [loadDoctor]);

  const saveDoctor = async (profile) => {
    const updatedDoctor =
      await updateDoctorProfile(profile);

    setDoctor(updatedDoctor);

    return updatedDoctor;
  };

  return (
    <DoctorContext.Provider
      value={{
        doctor,
        loading,
        loadDoctor,
        saveDoctor,
      }}
    >
      {children}
    </DoctorContext.Provider>
  );
}

export function useDoctor() {
  const context = useContext(DoctorContext);

  if (!context) {
    throw new Error(
      "useDoctor must be used inside DoctorProvider"
    );
  }

  return context;
}