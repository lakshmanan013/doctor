import { useEffect, useMemo, useState } from "react";
import {
  FiPlus,
  FiTrash2,
  FiExternalLink,
} from "react-icons/fi";
import { useNavigate } from "react-router-dom";

import Badge from "../../components/ui/Badge";
import Button from "../../components/ui/Button";
import SearchBar from "../../components/ui/SearchBar";
import AddPatientModal from "../../components/modals/AddPatientModal";

import {
  getPatients,
  createPatient,
  deletePatient,
} from "../../services/patientService";

// =====================================================
// SPECIES
// =====================================================

const SPECIES = [
  "All",
  "Dog",
  "Cat",
  "Buffalo",
  "Goat",
  "Cattle",
  "Rabbit",
  "Parrot",
  "Other",
];

// =====================================================
// ICONS
// =====================================================

const ICONS = {
  Dog: "🐶",
  Cat: "🐱",
  Buffalo: "🐃",
  Goat: "🐐",
  Cattle: "🐄",
  Rabbit: "🐰",
  Parrot: "🦜",
  Other: "🐾",
};

// =====================================================
// AGE CALCULATOR
// =====================================================

function ageFromDob(dateOfBirth) {
  if (!dateOfBirth) {
    return "—";
  }

  const dob = new Date(dateOfBirth);

  if (Number.isNaN(dob.getTime())) {
    return "—";
  }

  const now = new Date();

  let age =
    now.getFullYear() -
    dob.getFullYear();

  const monthDifference =
    now.getMonth() -
    dob.getMonth();

  if (
    monthDifference < 0 ||
    (
      monthDifference === 0 &&
      now.getDate() < dob.getDate()
    )
  ) {
    age -= 1;
  }

  return `${Math.max(age, 0)}y`;
}

// =====================================================
// PATIENTS PAGE
// =====================================================

export default function Patients() {

  const navigate = useNavigate();

  // ===================================================
  // STATE
  // ===================================================

  const [patients, setPatients] =
    useState([]);

  const [species, setSpecies] =
    useState("All");

  const [query, setQuery] =
    useState("");

  const [open, setOpen] =
    useState(false);

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  // ===================================================
  // LOAD PATIENTS
  // ===================================================

  const loadPatients = async () => {

    try {

      setLoading(true);
      setError("");

      const data =
        await getPatients();

      setPatients(
        Array.isArray(data)
          ? data
          : []
      );

    } catch (e) {

      console.error(
        "Failed to load patients:",
        e
      );

      setError(
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        "Unable to load patients from the server."
      );

    } finally {

      setLoading(false);
    }
  };

  // ===================================================
  // INITIAL LOAD
  // ===================================================

  useEffect(() => {
    loadPatients();
  }, []);

  // ===================================================
  // FILTER + LOCAL SEARCH
  //
  // Search now supports:
  // - Pet name
  // - Pet ID
  // - Owner name
  // - Breed
  // - Microchip number
  // - Species
  // ===================================================

  const filtered = useMemo(() => {

    const q =
      query
        .toLowerCase()
        .trim();

    return patients.filter((patient) => {

      // -----------------------------------------------
      // SPECIES FILTER
      // -----------------------------------------------

      const speciesOk =
        species === "All" ||
        patient.species === species;

      // -----------------------------------------------
      // SEARCH TEXT
      // -----------------------------------------------

      const text = [
        patient.name || "",
        patient.petId || "",
        patient.ownerName || "",
        patient.breed || "",
        patient.microchipNumber || "",
        patient.species || "",
      ]
        .join(" ")
        .toLowerCase();

      // -----------------------------------------------
      // FINAL RESULT
      // -----------------------------------------------

      return (
        speciesOk &&
        (!q || text.includes(q))
      );
    });

  }, [patients, species, query]);

  // ===================================================
  // CREATE PATIENT
  // ===================================================

  const handleCreate = async (form) => {

    try {

      setSaving(true);
      setError("");

      /*
       * IMPORTANT:
       *
       * form does NOT contain petId.
       *
       * Backend automatically creates:
       *
       * DOG-0001
       * CAT-0001
       * BUF-0001
       * GOA-0001
       */

      const created =
        await createPatient(form);

      console.log(
        "Created patient:",
        created
      );

      setPatients((previous) => [
        created,
        ...previous,
      ]);

      window.dispatchEvent(
        new Event("patientsUpdated")
      );

      setOpen(false);

    } catch (e) {

      console.error(
        "Failed to create patient:",
        e
      );

      setError(
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        "Failed to create patient."
      );

      throw e;

    } finally {

      setSaving(false);
    }
  };

  // ===================================================
  // DELETE PATIENT
  // ===================================================

  const handleDelete = async (patient) => {

    const confirmed =
      window.confirm(
        `Delete ${patient.name}?`
      );

    if (!confirmed) {
      return;
    }

    try {

      await deletePatient(
        patient.id
      );

      setPatients((previous) =>
        previous.filter(
          (item) =>
            item.id !== patient.id
        )
      );

      window.dispatchEvent(
        new Event("patientsUpdated")
      );

    } catch (e) {

      console.error(
        "Failed to delete patient:",
        e
      );

      setError(
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        "Failed to delete patient."
      );
    }
  };

  // ===================================================
  // LOADING
  // ===================================================

  if (loading) {

    return (
      <div className="table-card">

        <div className="table-empty">
          Loading patients...
        </div>

      </div>
    );
  }

  // ===================================================
  // RENDER
  // ===================================================

  return (
    <div className="stack-6">

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
          TOOLBAR
      ================================================= */}

      <div className="table-toolbar">

        {/* SEARCH */}

        <div className="table-search">

          <SearchBar
            value={query}
            onChange={(e) =>
              setQuery(e.target.value)
            }
            placeholder="Search by pet, Pet ID, owner or breed"
          />

        </div>

        {/* SPECIES FILTER */}

        <div className="filter-row">

          {SPECIES.map((item) => (

            <button
              key={item}
              onClick={() =>
                setSpecies(item)
              }
              className={`filter-chip ${
                species === item
                  ? "active"
                  : ""
              }`}
            >
              {item}
            </button>

          ))}

          {/* ADD PATIENT */}

          <Button
            icon={FiPlus}
            onClick={() =>
              setOpen(true)
            }
          >
            New patient
          </Button>

        </div>

      </div>

      {/* =================================================
          PATIENT TABLE
      ================================================= */}

      <div className="table-card">

        <table>

          <thead>

            <tr>

              <th>
                Pet
              </th>

              <th>
                Owner
              </th>

              <th>
                Age / Sex
              </th>

              <th>
                Weight
              </th>

              <th>
                Alerts
              </th>

              <th
                style={{
                  textAlign: "right",
                }}
              >
                Action
              </th>

            </tr>

          </thead>

          <tbody>

            {filtered.map((patient) => {

              // =========================================
              // MEDICAL ALERTS
              // =========================================

              const alerts =
                (
                  patient.medicalAlerts &&
                  patient.medicalAlerts
                    .toLowerCase() !== "none"
                )
                  ? patient.medicalAlerts
                      .split(",")
                      .map(
                        (item) =>
                          item.trim()
                      )
                      .filter(Boolean)
                  : [];

              // =========================================
              // PATIENT ROW
              // =========================================

              return (

                <tr
                  key={patient.id}
                >

                  {/* =====================================
                      PET
                  ===================================== */}

                  <td>

                    <div className="cell-primary">

                      {/* ICON */}

                      <div className="row-avatar">

                        {patient.icon ||
                          ICONS[
                            patient.species
                          ] ||
                          "🐾"}

                      </div>

                      {/* NAME + PET ID */}

                      <div>

                        {/* PET NAME */}

                        <p className="cell-title">
                          {patient.name ||
                            "Unnamed patient"}
                        </p>

                        {/* PET ID */}

                        {patient.petId && (

                          <p
                            className="cell-sub"
                            style={{
                              fontWeight: 600,
                              letterSpacing:
                                "0.3px",
                            }}
                          >
                            {patient.petId}
                          </p>

                        )}

                        {/* SPECIES + BREED */}

                        <p className="cell-sub">

                          {patient.species ||
                            "—"}

                          {" · "}

                          {patient.breed ||
                            "—"}

                        </p>

                      </div>

                    </div>

                  </td>

                  {/* =====================================
                      OWNER
                  ===================================== */}

                  <td>

                    <p className="cell-title">
                      {patient.ownerName ||
                        "—"}
                    </p>

                    <p className="cell-sub">
                      {patient.ownerPhone ||
                        "—"}
                    </p>

                  </td>

                  {/* =====================================
                      AGE / SEX
                  ===================================== */}

                  <td className="text-muted">

                    {ageFromDob(
                      patient.dateOfBirth
                    )}

                    {" · "}

                    {patient.gender ||
                      "—"}

                  </td>

                  {/* =====================================
                      WEIGHT
                  ===================================== */}

                  <td className="text-muted">

                    {patient.weight != null
                      ? `${patient.weight} kg`
                      : "—"}

                  </td>

                  {/* =====================================
                      ALERTS
                  ===================================== */}

                  <td>

                    {alerts.length ? (

                      <div
                        className="flex-row"
                        style={{
                          gap: 6,
                        }}
                      >

                        {alerts.map(
                          (alert) => (

                            <Badge
                              key={alert}
                              variant={
                                alert
                                  .toLowerCase()
                                  .includes(
                                    "allergy"
                                  )
                                  ? "danger"
                                  : "warning"
                              }
                            >
                              {alert}
                            </Badge>

                          )
                        )}

                      </div>

                    ) : (

                      <span className="text-faint">
                        —
                      </span>

                    )}

                  </td>

                  {/* =====================================
                      ACTIONS
                  ===================================== */}

                  <td
                    style={{
                      textAlign:
                        "right",
                    }}
                  >

                    <div
                      className="flex-row"
                      style={{
                        justifyContent:
                          "flex-end",
                        gap: 8,
                      }}
                    >

                      {/* OPEN PROFILE */}

                      <button
                        className="link-action"
                        onClick={() =>
                          navigate(
                            `/patients/${patient.id}`
                          )
                        }
                      >

                        Open

                        <FiExternalLink
                          size={13}
                        />

                      </button>

                      {/* DELETE */}

                      <button
                        className="link-action"
                        onClick={() =>
                          handleDelete(
                            patient
                          )
                        }
                        title="Delete"
                      >

                        <FiTrash2
                          size={14}
                        />

                      </button>

                    </div>

                  </td>

                </tr>

              );
            })}

            {/* =================================================
                NO RESULTS
            ================================================= */}

            {!filtered.length && (

              <tr>

                <td
                  colSpan={6}
                  className="table-empty"
                >
                  No patients found in
                  the database.
                </td>

              </tr>

            )}

          </tbody>

        </table>

      </div>

      {/* =================================================
          ADD PATIENT MODAL
      ================================================= */}

      <AddPatientModal
        open={open}
        saving={saving}
        onClose={() =>
          setOpen(false)
        }
        onCreate={handleCreate}
      />

    </div>
  );
}