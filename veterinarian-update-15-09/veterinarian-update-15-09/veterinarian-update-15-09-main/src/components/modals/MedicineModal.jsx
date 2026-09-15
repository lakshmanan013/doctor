import { useState } from "react";
import toast from "react-hot-toast";
import Modal from "../ui/Modal";
import Button from "../ui/Button";
import Input, { Field, Select } from "../ui/Input";
import { createMedicine } from "../../services/medicineService";
const EMPTY = { name: "", category: "", manufacturer: "", description: "", dosageForm: "", strength: "", unit: "", price: "", stockQuantity: "", reorderLevel: "", status: "ACTIVE" };
export default function MedicineModal({ open, onClose, onSaved }) {
  const [form, setForm] = useState(EMPTY);
  const [saving, setSaving] = useState(false);
  const u = (k, v) => setForm((f) => ({ ...f, [k]: v }));
  const submit = async () => {
    if (!form.name) return toast.error("Medicine name is required");
    try {
      setSaving(true);
      await createMedicine({ ...form, price: Number(form.price || 0), stockQuantity: Number(form.stockQuantity || 0), reorderLevel: Number(form.reorderLevel || 0) });
      toast.success("Medicine added");
      setForm(EMPTY);
      onSaved?.();
      onClose();
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not add medicine");
    } finally {
      setSaving(false);
    }
  };
  return <Modal open={open} onClose={saving ? undefined : onClose} title="Add medicine" subtitle="Create a new medicine in the catalog, then receive stock as a batch.">
    <div className="form-grid-2">
      <Field label="Name" className="col-span-2"><Input value={form.name} onChange={(e) => u("name", e.target.value)} /></Field>
      <Field label="Category"><Input value={form.category} onChange={(e) => u("category", e.target.value)} /></Field>
      <Field label="Manufacturer"><Input value={form.manufacturer} onChange={(e) => u("manufacturer", e.target.value)} /></Field>
      <Field label="Dosage form"><Input value={form.dosageForm} onChange={(e) => u("dosageForm", e.target.value)} placeholder="Tablet, Syrup, Injection..." /></Field>
      <Field label="Strength"><Input value={form.strength} onChange={(e) => u("strength", e.target.value)} placeholder="500mg" /></Field>
      <Field label="Unit"><Input value={form.unit} onChange={(e) => u("unit", e.target.value)} placeholder="tablets, ml..." /></Field>
      <Field label="Price"><Input type="number" value={form.price} onChange={(e) => u("price", e.target.value)} /></Field>
      <Field label="Opening stock"><Input type="number" value={form.stockQuantity} onChange={(e) => u("stockQuantity", e.target.value)} /></Field>
      <Field label="Reorder level"><Input type="number" value={form.reorderLevel} onChange={(e) => u("reorderLevel", e.target.value)} /></Field>
      <Field label="Status"><Select value={form.status} onChange={(e) => u("status", e.target.value)}><option value="ACTIVE">Active</option><option value="INACTIVE">Inactive</option></Select></Field>
      <Field label="Description" className="col-span-2"><Input value={form.description} onChange={(e) => u("description", e.target.value)} /></Field>
    </div>
    <div className="modal-actions"><Button variant="secondary" onClick={onClose} disabled={saving}>Cancel</Button><Button onClick={submit} disabled={saving}>{saving ? "Saving..." : "Add medicine"}</Button></div>
  </Modal>;
}
