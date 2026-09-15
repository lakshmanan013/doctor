import { useEffect, useState } from "react";
import toast from "react-hot-toast";

import Modal from "../ui/Modal";
import Button from "../ui/Button";
import Input, { Field, Select, Textarea } from "../ui/Input";

import {
  getOwners,
  createOwner,
} from "../../services/ownerService";

const EMPTY = {
  petName: "",
  species: "Dog",
  breed: "",
  sex: "Male",
  dateOfBirth: "",
  weight: "",

  ownerId: "",
  ownerName: "",
  ownerPhone: "",
  ownerEmail: "",
  ownerAddress: "",
  ownerCity: "",
  ownerState: "",
  ownerPincode: "",

  medicalAlerts: "",
};

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

export default function AddPatientModal({
  open,
  onClose,
  onCreate,
  saving = false,
}) {
  const [form, setForm] = useState(EMPTY);
  const [owners, setOwners] = useState([]);
  const [loadingOwners, setLoadingOwners] = useState(false);
  const [newOwner, setNewOwner] = useState(false);

  // =====================================================
  // UPDATE FORM
  // =====================================================

  const update = (key, value) => {
    setForm((previous) => ({
      ...previous,
      [key]: value,
    }));
  };

  // =====================================================
  // LOAD OWNERS
  // =====================================================

  useEffect(() => {
    if (!open) {
      return;
    }

    const loadOwners = async () => {
      setLoadingOwners(true);

      try {
        const data = await getOwners();

        setOwners(
          Array.isArray(data)
            ? data
            : []
        );
      } catch (error) {
        console.error(
          "Failed to load owners:",
          error
        );

        setOwners([]);

        toast.error(
          "Could not load owners"
        );
      } finally {
        setLoadingOwners(false);
      }
    };

    loadOwners();
  }, [open]);

  // =====================================================
  // RESET FORM WHEN MODAL OPENS
  // =====================================================

  useEffect(() => {
    if (open) {
      setForm(EMPTY);
      setNewOwner(false);
    }
  }, [open]);

  // =====================================================
  // CREATE PATIENT
  // =====================================================

  const submit = async () => {
    // ---------------------------------------------------
    // VALIDATE PET NAME
    // ---------------------------------------------------

    if (!form.petName.trim()) {
      toast.error(
        "Pet name is required"
      );

      return;
    }

    // ---------------------------------------------------
    // VALIDATE OWNER
    // ---------------------------------------------------

    let ownerId = form.ownerId
      ? Number(form.ownerId)
      : null;

    try {
      // =================================================
      // CREATE NEW OWNER
      // =================================================

      if (!ownerId) {
        if (!form.ownerName.trim()) {
          toast.error(
            "Select an owner or create a new owner"
          );

          return;
        }

        const owner = await createOwner({
          fullName:
            form.ownerName.trim(),

          phone:
            form.ownerPhone.trim() ||
            null,

          email:
            form.ownerEmail.trim() ||
            null,

          address:
            form.ownerAddress.trim() ||
            null,

          city:
            form.ownerCity.trim() ||
            null,

          state:
            form.ownerState.trim() ||
            null,

          pincode:
            form.ownerPincode.trim() ||
            null,
        });

        if (!owner?.id) {
          throw new Error(
            "Owner was created but no owner ID was returned"
          );
        }

        ownerId = Number(owner.id);
      }

      // =================================================
      // CREATE PATIENT
      // =================================================

      const patientPayload = {
        name: form.petName.trim(),

        species: form.species,

        breed:
          form.breed.trim() ||
          null,

        gender:
          form.sex ||
          null,

        dateOfBirth:
          form.dateOfBirth ||
          null,

        weight:
          form.weight !== ""
            ? Number(form.weight)
            : null,

        medicalAlerts:
          form.medicalAlerts.trim() ||
          null,

        status: "ACTIVE",

        icon:
          ICONS[form.species] ||
          "🐾",

        ownerId,
      };

      console.log(
        "Creating patient:",
        patientPayload
      );

      // Send patient to backend
      await onCreate(
        patientPayload
      );

      // =================================================
      // SUCCESS
      // =================================================

      toast.success(
        `${form.petName} registered successfully`
      );

      setForm(EMPTY);
      setNewOwner(false);

      onClose();

    } catch (error) {
      console.error(
        "Failed to create patient:",
        error
      );

      const message =
        error?.response?.data?.message ||
        error?.response?.data?.error ||
        error?.message ||
        "Could not save patient";

      toast.error(message);
    }
  };

  // =====================================================
  // RENDER
  // =====================================================

  return (
    <Modal
      open={open}
      onClose={
        saving
          ? undefined
          : onClose
      }
      title="Register new patient"
      subtitle="Patient and owner information is saved to the backend."
    >
      <div className="form-grid-2">

        {/* =================================================
            PET NAME
        ================================================= */}

        <Field label="Pet name">
          <Input
            value={form.petName}
            onChange={(e) =>
              update(
                "petName",
                e.target.value
              )
            }
            autoFocus
            placeholder="Enter pet name"
          />
        </Field>

        {/* =================================================
            SPECIES
        ================================================= */}

        <Field label="Species">
          <Select
            value={form.species}
            onChange={(e) =>
              update(
                "species",
                e.target.value
              )
            }
          >
            {[
              "Dog",
              "Cat",
              "Buffalo",
              "Goat",
              "Cattle",
              "Rabbit",
              "Parrot",
              "Other",
             ].map((species) => (
              <option
                key={species}
                value={species}
              >
                {species}
              </option>
            ))}
          </Select>
        </Field>

        {/* =================================================
            BREED
        ================================================= */}

        <Field label="Breed">
          <Input
            value={form.breed}
            onChange={(e) =>
              update(
                "breed",
                e.target.value
              )
            }
            placeholder="Enter breed"
          />
        </Field>

        {/* =================================================
            SEX
        ================================================= */}

        <Field label="Sex">
          <Select
            value={form.sex}
            onChange={(e) =>
              update(
                "sex",
                e.target.value
              )
            }
          >
            <option value="Male">
              Male
            </option>

            <option value="Female">
              Female
            </option>
          </Select>
        </Field>

        {/* =================================================
            DATE OF BIRTH
        ================================================= */}

        <Field label="Date of birth">
          <Input
            type="date"
            value={form.dateOfBirth}
            onChange={(e) =>
              update(
                "dateOfBirth",
                e.target.value
              )
            }
          />
        </Field>

        {/* =================================================
            WEIGHT
        ================================================= */}

        <Field label="Weight (kg)">
          <Input
            type="number"
            min="0"
            step="0.1"
            value={form.weight}
            onChange={(e) =>
              update(
                "weight",
                e.target.value
              )
            }
            placeholder="Enter weight"
          />
        </Field>

        {/* =================================================
            OWNER
        ================================================= */}

        <Field
          label="Owner"
          className="col-span-2"
        >
          {!newOwner && (
            <Select
              value={form.ownerId}
              disabled={loadingOwners}
              onChange={(e) =>
                update(
                  "ownerId",
                  e.target.value
                )
              }
            >
              <option value="">
                {loadingOwners
                  ? "Loading owners..."
                  : "Select owner"}
              </option>

              {owners.map((owner) => (
                <option
                  key={owner.id}
                  value={owner.id}
                >
                  {owner.fullName}
                  {" · "}
                  {owner.phone ||
                    "No phone"}
                </option>
              ))}
            </Select>
          )}

          <button
            type="button"
            className="link-btn"
            style={{
              marginTop: 8,
            }}
            onClick={() => {
              setNewOwner(
                (previous) =>
                  !previous
              );

              update(
                "ownerId",
                ""
              );
            }}
          >
            {newOwner
              ? "Choose existing owner"
              : "+ Create new owner"}
          </button>
        </Field>

        {/* =================================================
            NEW OWNER NAME
        ================================================= */}

        {newOwner && (
          <>
            <Field
              label="Owner name"
              className="col-span-2"
            >
              <Input
                value={form.ownerName}
                onChange={(e) =>
                  update(
                    "ownerName",
                    e.target.value
                  )
                }
                placeholder="Enter owner name"
              />
            </Field>

            <Field label="Owner phone">
              <Input
                value={form.ownerPhone}
                onChange={(e) =>
                  update(
                    "ownerPhone",
                    e.target.value
                  )
                }
                placeholder="Enter phone number"
              />
            </Field>

            <Field label="Owner email">
              <Input
                type="email"
                value={form.ownerEmail}
                onChange={(e) =>
                  update(
                    "ownerEmail",
                    e.target.value
                  )
                }
                placeholder="Enter email"
              />
            </Field>

            <Field
              label="Owner address"
              className="col-span-2"
            >
              <Textarea
                rows={2}
                value={form.ownerAddress}
                onChange={(e) =>
                  update(
                    "ownerAddress",
                    e.target.value
                  )
                }
                placeholder="House / street / area"
              />
            </Field>

            <Field label="City">
              <Input
                value={form.ownerCity}
                onChange={(e) =>
                  update(
                    "ownerCity",
                    e.target.value
                  )
                }
                placeholder="Enter city"
              />
            </Field>

            <Field label="State">
              <Input
                value={form.ownerState}
                onChange={(e) =>
                  update(
                    "ownerState",
                    e.target.value
                  )
                }
                placeholder="Enter state"
              />
            </Field>

            <Field label="Pincode">
              <Input
                value={form.ownerPincode}
                onChange={(e) =>
                  update(
                    "ownerPincode",
                    e.target.value
                  )
                }
                placeholder="Enter pincode"
              />
            </Field>
          </>
        )}

        {/* =================================================
            MEDICAL ALERTS
        ================================================= */}

        <Field
          label="Medical alerts"
          className="col-span-2"
        >
          <Input
            value={form.medicalAlerts}
            onChange={(e) =>
              update(
                "medicalAlerts",
                e.target.value
              )
            }
            placeholder="e.g. Allergy, Diabetes"
          />
        </Field>

      </div>

      {/* =================================================
          ACTIONS
      ================================================= */}

      <div className="modal-actions">

        <Button
          variant="secondary"
          onClick={onClose}
          disabled={saving}
        >
          Cancel
        </Button>

        <Button
          onClick={submit}
          disabled={saving}
        >
          {saving
            ? "Saving..."
            : "Register patient"}
        </Button>

      </div>
    </Modal>
  );
}