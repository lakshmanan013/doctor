import { useEffect, useState } from "react";
import toast from "react-hot-toast";

import {
  FiShield,
  FiCreditCard,
  FiSave,
} from "react-icons/fi";

import Input, {
  Field,
} from "../../components/ui/Input";

import Button from "../../components/ui/Button";
import Badge from "../../components/ui/Badge";

import {
  getDoctorProfile,
  updateDoctorProfile,
} from "../../services/doctorProfileService";
import { useAuth } from "../../hooks/useAuth";

import "./Settings.css";

const EMPTY = {
  fullName: "",
  qualification: "",
  speciality: "",
  council: "",
  clinic: "",
  phone: "",
  email: "",
  signature: "",
  consultationFee: "",
  followupFee: "",
  slotLength: "",
};

const VERIFICATION_ITEMS = [
  {
    label: "Veterinary registration",
    key: "veterinaryRegistrationVerified",
  },
  {
    label: "KYC verification",
    key: "kycVerified",
  },
  {
    label: "Digital signature",
    key: "digitalSignatureVerified",
  },
  {
    label: "State council sync",
    key: "stateCouncilSyncVerified",
  },
];

export default function DoctorProfile() {
  const { doctor: authDoctor } = useAuth();
  const [form, setForm] = useState(EMPTY);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [verifications, setVerifications] = useState({
    veterinaryRegistrationVerified: false,
    kycVerified: false,
    digitalSignatureVerified: false,
    stateCouncilSyncVerified: false,
  });

  // ==========================================
  // UPDATE FORM
  // ==========================================

  const update = (key, value) => {
    setForm((current) => ({
      ...current,
      [key]: value,
    }));
  };

  // ==========================================
  // LOAD PROFILE
  // ==========================================

  useEffect(() => {
    const loadProfile = async () => {
      try {
        setLoading(true);

        const data =
          await getDoctorProfile();

        console.log(
          "Doctor profile from backend:",
          data
        );

        setForm({
          fullName:
            data?.fullName || authDoctor?.fullName || "",

          qualification:
            data?.qualification ?? "",

          speciality:
            data?.speciality ?? "",

          council:
            data?.councilRegistration ?? "",

          clinic:
            data?.clinicHospital ?? "",

          phone:
            data?.phone || authDoctor?.phone || "",

          email:
            data?.email || authDoctor?.email || "",

          signature:
            data?.digitalSignatureName ?? "",

          consultationFee:
            data?.consultationFee ??
            "",

          followupFee:
            data?.followUpFee ??
            "",

          slotLength:
            data?.slotLength ??
            "",
        });

        setVerifications({
          veterinaryRegistrationVerified:
            data?.veterinaryRegistrationVerified ?? false,
          kycVerified:
            data?.kycVerified ?? false,
          digitalSignatureVerified:
            data?.digitalSignatureVerified ?? false,
          stateCouncilSyncVerified:
            data?.stateCouncilSyncVerified ?? false,
        });
      } catch (error) {
        console.error(
          "Failed to load doctor profile:",
          error
        );

        toast.error(
          "Failed to load doctor profile"
        );

        setForm({
          ...EMPTY,
          fullName: authDoctor?.fullName || "",
          phone: authDoctor?.phone || "",
          email: authDoctor?.email || "",
        });
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [authDoctor?.email]);

  // ==========================================
  // SAVE PROFILE
  // ==========================================

  const save = async () => {
    try {
      setSaving(true);

      const profile = {
        fullName:
          form.fullName.trim(),

        qualification:
          form.qualification.trim(),

        speciality:
          form.speciality.trim(),

        councilRegistration:
          form.council.trim(),

        clinicHospital:
          form.clinic.trim(),

        phone:
          form.phone.trim(),

        email:
          form.email.trim(),

        digitalSignatureName:
          form.signature.trim(),

        consultationFee:
          form.consultationFee === ""
            ? null
            : Number(form.consultationFee),

        followUpFee:
          form.followupFee === ""
            ? null
            : Number(form.followupFee),

        slotLength:
          form.slotLength === ""
            ? null
            : Number(form.slotLength),
      };

      console.log(
        "Saving doctor profile:",
        profile
      );

      const saved =
        await updateDoctorProfile(
          profile
        );

      console.log(
        "Doctor profile saved:",
        saved
      );

      // Update form with backend response
      setForm({
        fullName:
          saved?.fullName ?? "",

        qualification:
          saved?.qualification ?? "",

        speciality:
          saved?.speciality ?? "",

        council:
          saved?.councilRegistration ?? "",

        clinic:
          saved?.clinicHospital ?? "",

        phone:
          saved?.phone ?? "",

        email:
          saved?.email ?? "",

        signature:
          saved?.digitalSignatureName ??
          "",

        consultationFee:
          saved?.consultationFee ??
          "",

        followupFee:
          saved?.followUpFee ??
          "",

        slotLength:
          saved?.slotLength ??
          "",
      });

      toast.success(
        "Doctor profile saved successfully"
      );
      window.dispatchEvent(new Event("doctorProfileUpdated"));
    } catch (error) {
      console.error(
        "Failed to save doctor profile:",
        error
      );

      console.error(
        "Backend response:",
        error?.response?.data
      );

      toast.error(
        error?.response?.data?.message ||
          "Failed to save doctor profile"
      );
    } finally {
      setSaving(false);
    }
  };

  // ==========================================
  // LOADING
  // ==========================================

  if (loading) {
    return (
      <div className="panel">
        <p className="text-muted">
          Loading doctor profile...
        </p>
      </div>
    );
  }

  // ==========================================
  // PAGE
  // ==========================================

  return (
    <div className="settings-layout">

      {/* ===================================== */}
      {/* LEFT SIDE                             */}
      {/* ===================================== */}

      <div className="stack-6">

        {/* PROFESSIONAL DETAILS */}

        <div className="panel">

          <h3 className="settings-heading">
            <FiShield className="settings-icon" />

            Professional details
          </h3>

          <div className="form-grid-2">

            <Field label="Full name">
              <Input
                type="text"
                value={form.fullName}
                onChange={(e) =>
                  update(
                    "fullName",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Qualification">
              <Input
                type="text"
                value={form.qualification}
                onChange={(e) =>
                  update(
                    "qualification",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Speciality">
              <Input
                type="text"
                placeholder="e.g. Small animal surgery, Dermatology"
                value={form.speciality}
                onChange={(e) =>
                  update(
                    "speciality",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Council registration">
              <Input
                type="text"
                value={form.council}
                onChange={(e) =>
                  update(
                    "council",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Clinic / hospital">
              <Input
                type="text"
                value={form.clinic}
                onChange={(e) =>
                  update(
                    "clinic",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Phone">
              <Input
                type="text"
                value={form.phone}
                onChange={(e) =>
                  update(
                    "phone",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Email">
              <Input
                type="email"
                value={form.email}
                onChange={(e) =>
                  update(
                    "email",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Digital signature name">
              <Input
                type="text"
                value={form.signature}
                onChange={(e) =>
                  update(
                    "signature",
                    e.target.value
                  )
                }
              />
            </Field>

          </div>

        </div>

        {/* FEES */}

        <div className="panel">

          <h3 className="settings-heading">
            <FiCreditCard className="settings-icon" />

            Fees & scheduling
          </h3>

          <div className="grid-3">

            <Field label="Consultation fee (₹)">
              <Input
                type="number"
                min="0"
                value={
                  form.consultationFee
                }
                onChange={(e) =>
                  update(
                    "consultationFee",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Follow-up fee (₹)">
              <Input
                type="number"
                min="0"
                value={
                  form.followupFee
                }
                onChange={(e) =>
                  update(
                    "followupFee",
                    e.target.value
                  )
                }
              />
            </Field>

            <Field label="Slot length (min)">
              <Input
                type="number"
                min="1"
                value={
                  form.slotLength
                }
                onChange={(e) =>
                  update(
                    "slotLength",
                    e.target.value
                  )
                }
              />
            </Field>

          </div>

          <div
            style={{
              marginTop: 20,
              display: "flex",
              justifyContent:
                "flex-end",
            }}
          >
            <Button
              icon={FiSave}
              onClick={save}
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : "Save profile"}
            </Button>
          </div>

        </div>

      </div>

      {/* ===================================== */}
      {/* VERIFICATION                          */}
      {/* ===================================== */}

      <div className="panel settings-side">

        <h3
          className="settings-heading"
          style={{
            marginBottom: 4,
          }}
        >
          <FiShield className="settings-icon" />

          Verification
        </h3>

        <p
          className="panel-subtitle"
          style={{
            marginBottom: 16,
          }}
        >
          Reviewed and verified by an
          admin.
        </p>

        <div className="stack-3">

          {VERIFICATION_ITEMS.map(
            (verification) => {
              const isVerified =
                verifications[
                  verification.key
                ];

              return (
                <div
                  key={
                    verification.label
                  }
                  className="settings-verify-row"
                >
                  <span
                    className="text-muted"
                    style={{
                      fontSize: 14,
                    }}
                  >
                    {
                      verification.label
                    }
                  </span>

                  <Badge
                    variant={
                      isVerified
                        ? "success"
                        : "warning"
                    }
                  >
                    {isVerified
                      ? "Verified"
                      : "Pending admin review"}
                  </Badge>
                </div>
              );
            }
          )}

        </div>

      </div>

    </div>
  );
}