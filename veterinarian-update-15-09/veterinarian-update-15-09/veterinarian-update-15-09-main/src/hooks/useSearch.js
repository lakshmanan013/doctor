import { useEffect, useState } from "react";

import { getPatients } from "../services/patientService";
import { getAppointments } from "../services/appointmentService";
import { getMedicines } from "../services/medicineService";
import { getInvoices } from "../services/billingService";

// =====================================================
// NORMALIZE
// =====================================================

const norm = (value) =>
  (value ?? "")
    .toString()
    .toLowerCase()
    .trim();

// =====================================================
// MATCH
// =====================================================

const match = (query, ...fields) => {
  if (!query) return false;

  return fields.some((field) =>
    norm(field).includes(query)
  );
};

// =====================================================
// USE SEARCH
// =====================================================

export default function useSearch(query, limit = 8) {
  const [results, setResults] = useState([]);

  useEffect(() => {
    let cancelled = false;

    const q = norm(query);

    // Empty search
    if (!q) {
      setResults([]);
      return undefined;
    }

    const search = async () => {
      try {
        const [
          patients,
          appointments,
          medicines,
          invoices,
        ] = await Promise.all([
          getPatients().catch(() => []),
          getAppointments().catch(() => []),
          getMedicines().catch(() => []),
          getInvoices().catch(() => []),
        ]);

        if (cancelled) return;

        const output = [];

        // =================================================
        // PATIENT SEARCH
        // =================================================

        (patients || []).forEach((patient) => {
          const isMatch = match(
            q,

            // Pet
            patient.name,
            patient.petId,
            patient.species,
            patient.breed,

            // Owner
            patient.ownerName,
            patient.ownerPhone,

            // Other
            patient.microchipNumber
          );

          if (!isMatch) return;

          const petId =
            patient.petId || "Pet ID not generated";

          const species =
            patient.species || "Pet";

          const breed =
            patient.breed || "—";

          const ownerName =
            patient.ownerName || "No owner";

          output.push({
            id: `patient-${patient.id}`,

            type: "Patient",

            icon:
              patient.icon ||
              "🐾",

            title:
              patient.name ||
              "Unnamed patient",

            subtitle:
              `${petId} · ${species} · ${breed} · Owner: ${ownerName}`,

            // IMPORTANT:
            // Open patient profile
            path:
              `/patients/${patient.id}`,
          });
        });

        // =================================================
        // APPOINTMENTS
        // =================================================

        (appointments || []).forEach((appointment) => {
          const isMatch = match(
            q,
            appointment.patientName,
            appointment.ownerName,
            appointment.reason,
            appointment.appointmentType,
            appointment.breed
          );

          if (!isMatch) return;

          output.push({
            id:
              `appointment-${appointment.id}`,

            type:
              "Appointment",

            icon:
              appointment.icon ||
              "🐾",

            title:
              `${appointment.patientName || "Patient"} — ${
                appointment.reason || "Visit"
              }`,

            subtitle:
              `${appointment.appointmentDate || ""} · ${
                appointment.ownerName || ""
              }`,

            path:
              "/appointments",
          });
        });

        // =================================================
        // MEDICINES
        // =================================================

        (medicines || []).forEach((medicine) => {
          const isMatch = match(
            q,
            medicine.name,
            medicine.manufacturer,
            medicine.category,
            medicine.strength
          );

          if (!isMatch) return;

          output.push({
            id:
              `medicine-${medicine.id}`,

            type:
              "Medicine",

            icon:
              "💊",

            title:
              medicine.name,

            subtitle:
              `${medicine.category || ""} · ${
                medicine.status || ""
              }`,

            path:
              "/inventory",
          });
        });

        // =================================================
        // INVOICES
        // =================================================

        (invoices || []).forEach((invoice) => {
          const isMatch = match(
            q,
            invoice.invoiceNumber,
            invoice.patientId,
            invoice.ownerId,
            invoice.status,
            invoice.paymentStatus
          );

          if (!isMatch) return;

          output.push({
            id:
              `invoice-${invoice.id}`,

            type:
              "Invoice",

            icon:
              "🧾",

            title:
              `${invoice.invoiceNumber || `Invoice ${invoice.id}`} · ₹${
                invoice.totalAmount ?? 0
              }`,

            subtitle:
              `${invoice.status || ""} · ${
                invoice.paymentStatus || ""
              }`,

            path:
              "/billing",
          });
        });

        // =================================================
        // LIMIT
        // =================================================

        if (!cancelled) {
          setResults(
            output.slice(0, limit)
          );
        }

      } catch (error) {
        console.error(
          "Global search failed:",
          error
        );

        if (!cancelled) {
          setResults([]);
        }
      }
    };

    search();

    return () => {
      cancelled = true;
    };
  }, [query, limit]);

  return results;
}