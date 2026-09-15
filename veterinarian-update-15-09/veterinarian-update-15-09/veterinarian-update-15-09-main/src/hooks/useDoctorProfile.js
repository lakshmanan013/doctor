import { useCallback, useEffect, useState } from "react";

import {
  getDoctorProfile,
} from "../services/doctorProfileService";

export default function useDoctorProfile() {
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
    // Load when component starts
    loadDoctor();

    // Reload after doctor profile is saved
    const handleDoctorProfileUpdated = () => {
      loadDoctor();
    };

    window.addEventListener(
      "doctorProfileUpdated",
      handleDoctorProfileUpdated
    );

    return () => {
      window.removeEventListener(
        "doctorProfileUpdated",
        handleDoctorProfileUpdated
      );
    };
  }, [loadDoctor]);

  return {
    doctor,
    loading,
    reloadDoctor: loadDoctor,
  };
}