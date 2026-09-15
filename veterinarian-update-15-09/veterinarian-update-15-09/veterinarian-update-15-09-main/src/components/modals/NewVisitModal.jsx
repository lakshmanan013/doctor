import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Modal from "../ui/Modal";
import Button from "../ui/Button";
import Input, { Field, Select, Textarea } from "../ui/Input";
import { getPatients } from "../../services/patientService";
import { createAppointment, updateAppointment } from "../../services/appointmentService";

const EMPTY = { patientId: "", appointmentDate: new Date().toISOString().slice(0, 10), appointmentTime: "10:00", appointmentType: "In Clinic", reason: "", status: "Confirmed", notes: "", doctorName: "" };

// `appointment` (optional): pass an existing appointment to edit it instead
// of creating a new one. The modal prefills the form from it and calls
// updateAppointment on save.
export default function NewVisitModal({ open, onClose, onCreated, onUpdated, appointment }) {
  const isEdit = Boolean(appointment);
  const [patients, setPatients] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);
  const update = (k, v) => setForm((f) => ({ ...f, [k]: v }));

  useEffect(() => {
    if (open) getPatients().then((d) => setPatients(Array.isArray(d) ? d : [])).catch(() => setPatients([]));
  }, [open]);

  useEffect(() => {
    if (!open) return;
    if (appointment) {
      setForm({
        patientId: appointment.patientId ?? "",
        appointmentDate: appointment.appointmentDate || new Date().toISOString().slice(0, 10),
        appointmentTime: appointment.appointmentTime || "10:00",
        appointmentType: appointment.appointmentType || "In Clinic",
        reason: appointment.reason || "",
        status: appointment.status || "Confirmed",
        notes: appointment.notes || "",
        doctorName: appointment.doctorName || "",
      });
    } else {
      setForm(EMPTY);
    }
  }, [open, appointment]);

  const submit = async () => {
    if (!form.patientId) return toast.error("Select a patient");
    try {
      setSaving(true);
      const payload = { ...form, patientId: Number(form.patientId) };
      if (isEdit) {
        const updated = await updateAppointment(appointment.id, payload);
        toast.success("Visit updated");
        onUpdated?.(updated);
      } else {
        const created = await createAppointment(payload);
        toast.success("Visit created");
        onCreated?.(created);
      }
      setForm(EMPTY);
      onClose();
    } catch (e) {
      toast.error(e?.response?.data?.message || `Could not ${isEdit ? "update" : "create"} visit`);
    } finally {
      setSaving(false);
    }
  };

  return <Modal open={open} onClose={saving ? undefined : onClose} title={isEdit ? "Edit visit" : "New visit"} subtitle={isEdit ? "Update this appointment in the backend." : "Create a real appointment in the backend."}>
    <div className="form-grid-2">
      <Field label="Patient" className="col-span-2"><Select value={form.patientId} onChange={(e) => update("patientId", e.target.value)}><option value="">Select a patient</option>{patients.map((p) => <option key={p.id} value={p.id}>{p.name} · {p.breed || p.species} · {p.ownerName || ""}</option>)}</Select></Field>
      <Field label="Date"><Input type="date" value={form.appointmentDate} onChange={(e) => update("appointmentDate", e.target.value)} /></Field>
      <Field label="Time"><Input type="time" value={form.appointmentTime} onChange={(e) => update("appointmentTime", e.target.value)} /></Field>
      <Field label="Visit type"><Select value={form.appointmentType} onChange={(e) => update("appointmentType", e.target.value)}><option>In Clinic</option><option>Video</option><option>Home Visit</option><option>Emergency</option></Select></Field>
      <Field label="Status"><Select value={form.status} onChange={(e) => update("status", e.target.value)}><option>Confirmed</option><option>Waiting</option><option>In Consultation</option><option>Completed</option><option>Cancelled</option></Select></Field>
      <Field label="Reason" className="col-span-2"><Input value={form.reason} onChange={(e) => update("reason", e.target.value)} /></Field>
      <Field label="Doctor"><Input value={form.doctorName} onChange={(e) => update("doctorName", e.target.value)} placeholder="Doctor name" /></Field>
      <Field label="Notes"><Textarea rows={2} value={form.notes} onChange={(e) => update("notes", e.target.value)} /></Field>
    </div>
    <div className="modal-actions"><Button variant="secondary" onClick={onClose} disabled={saving}>Cancel</Button><Button onClick={submit} disabled={saving}>{saving ? "Saving..." : isEdit ? "Save changes" : "Create visit"}</Button></div>
  </Modal>;
}
