import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { FiArrowLeft } from "react-icons/fi";

import Button from "../../components/ui/Button";
import Badge from "../../components/ui/Badge";

import { getPatientById } from "../../services/patientService";
import { getMedicalRecordsByPatient } from "../../services/medicalRecordService";
import { getVaccinationsByPatient } from "../../services/vaccinationService";

export default function PatientProfile() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [patient, setPatient] = useState(null);
  const [records, setRecords] = useState([]);
  const [vaccinations, setVaccinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =====================================================
  // LOAD PATIENT PROFILE
  // =====================================================

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        setError("");

        const [patientData, medicalData, vaccinationData] =
          await Promise.all([
            getPatientById(id),

            getMedicalRecordsByPatient(id).catch(() => []),

            getVaccinationsByPatient(id).catch(() => []),
          ]);

        console.log("Patient profile:", patientData);

        setPatient(patientData);

        setRecords(
          Array.isArray(medicalData)
            ? medicalData
            : []
        );

        setVaccinations(
          Array.isArray(vaccinationData)
            ? vaccinationData
            : []
        );
      } catch (e) {
        console.error(
          "Unable to load patient:",
          e
        );

        setError(
          e?.response?.data?.message ||
            e?.response?.data?.error ||
            "Unable to load patient."
        );
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      load();
    }
  }, [id]);

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="table-card">
        <div className="table-empty">
          Loading patient...
        </div>
      </div>
    );
  }

  // =====================================================
  // PATIENT NOT FOUND
  // =====================================================

  if (!patient) {
    return (
      <div className="table-card">
        <div className="table-empty">
          {error || "Patient not found."}
        </div>
      </div>
    );
  }

  // =====================================================
  // PATIENT PROFILE
  // =====================================================

  return (
    <div className="stack-6">

      {/* =================================================
          BACK BUTTON
      ================================================= */}

      <Button
        variant="secondary"
        icon={FiArrowLeft}
        onClick={() => navigate("/patients")}
      >
        Back to patients
      </Button>

      {/* =================================================
          ERROR
      ================================================= */}

      {error && (
        <div
          className="table-card"
          style={{
            padding: 14,
            color: "#be123c",
          }}
        >
          {error}
        </div>
      )}

      {/* =================================================
          PATIENT INFORMATION
      ================================================= */}

      <div className="panel">

        <div className="cell-primary">

          {/* PET ICON */}

          <div
            className="row-avatar"
            style={{
              width: 52,
              height: 52,
            }}
          >
            {patient.icon || "🐾"}
          </div>

          {/* PET NAME + PET ID */}

          <div>

            {/* Pet Name */}

            <h2 className="panel-title">
              {patient.name || "Unnamed patient"}
            </h2>

            {/* =================================================
                AUTOMATIC PET ID
                Example:
                Bruno
                DOG-0001
            ================================================= */}

            {patient.petId && (
              <p
                style={{
                  margin: "4px 0 6px",
                  fontSize: "13px",
                  fontWeight: 600,
                  letterSpacing: "0.4px",
                  opacity: 0.7,
                }}
              >
                {patient.petId}
              </p>
            )}

            {/* Species + Breed */}

            <p className="panel-subtitle">
              {patient.species || "—"}
              {" · "}
              {patient.breed || "—"}
            </p>

          </div>
        </div>

        {/* =================================================
            PATIENT DETAILS
        ================================================= */}

        <div
          className="rx-preview-grid"
          style={{
            marginTop: 20,
          }}
        >

          {/* OWNER */}

          <div>
            <p className="eyebrow">
              Owner
            </p>

            <p className="cell-title">
              {patient.ownerName || "—"}
            </p>
          </div>

          {/* PHONE */}

          <div>
            <p className="eyebrow">
              Phone
            </p>

            <p className="cell-title">
              {patient.ownerPhone || "—"}
            </p>
          </div>

          {/* EMAIL */}

          <div>
            <p className="eyebrow">
              Registered Email
            </p>

            <p className="cell-title">
              {patient.ownerEmail || "—"}
            </p>
          </div>

          {/* ADDRESS */}

          <div style={{ gridColumn: "1 / -1" }}>
            <p className="eyebrow">
              Address
            </p>

            <p className="cell-title">
              {[
                patient.ownerAddress,
                patient.ownerCity,
                patient.ownerState,
                patient.ownerPincode,
              ]
                .filter(Boolean)
                .join(", ") || "—"}
            </p>
          </div>

          {/* GENDER */}

          <div>
            <p className="eyebrow">
              Gender
            </p>

            <p className="cell-title">
              {patient.gender || "—"}
            </p>
          </div>

          {/* WEIGHT */}

          <div>
            <p className="eyebrow">
              Weight
            </p>

            <p className="cell-title">
              {patient.weight != null
                ? `${patient.weight} kg`
                : "—"}
            </p>
          </div>

          {/* STATUS */}

          <div>
            <p className="eyebrow">
              Status
            </p>

            <Badge variant="success">
              {patient.status || "ACTIVE"}
            </Badge>
          </div>

        </div>
      </div>

      {/* =================================================
          MEDICAL HISTORY + VACCINATIONS
      ================================================= */}

      <div className="dashboard-grid">

        {/* =================================================
            MEDICAL HISTORY
        ================================================= */}

        <div className="panel">

          <h3 className="panel-title">
            Medical history
          </h3>

          <div className="row-list">

            {records.map((record) => (
              <div
                className="row-item"
                key={record.id}
              >
                <div className="row-body">

                  <p className="row-title">
                    {record.visitDate || "—"}
                    {" · "}
                    {record.diagnosis || "Visit"}
                  </p>

                  <p className="row-desc">
                    {record.chiefComplaint ||
                      record.symptoms ||
                      record.treatment ||
                      "No notes"}
                  </p>

                </div>
              </div>
            ))}

            {!records.length && (
              <p className="table-empty">
                No medical records.
              </p>
            )}

          </div>
        </div>

        {/* =================================================
            VACCINATIONS
        ================================================= */}

        <div className="panel">

          <h3 className="panel-title">
            Vaccinations
          </h3>

          <div className="row-list">

            {vaccinations.map((vaccination) => (
              <div
                className="row-item"
                key={vaccination.id}
              >

                <div className="row-body">

                  <p className="row-title">
                    {vaccination.vaccineName}
                  </p>

                  <p className="row-desc">
                    Given{" "}
                    {vaccination.vaccinationDate ||
                      "—"}
                    {" · "}
                    Next{" "}
                    {vaccination.nextDueDate ||
                      "—"}
                  </p>

                </div>

                <Badge
                  variant={
                    vaccination.status ===
                    "COMPLETED"
                      ? "success"
                      : "warning"
                  }
                >
                  {vaccination.status ||
                    "Scheduled"}
                </Badge>

              </div>
            ))}

            {!vaccinations.length && (
              <p className="table-empty">
                No vaccinations.
              </p>
            )}

          </div>
        </div>

      </div>
    </div>
  );
}