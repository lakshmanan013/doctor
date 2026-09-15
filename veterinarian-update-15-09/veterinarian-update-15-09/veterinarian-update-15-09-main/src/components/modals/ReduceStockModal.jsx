import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import Modal from "../ui/Modal";
import Button from "../ui/Button";
import Input, { Field, Select, Textarea } from "../ui/Input";
import { createStockMovement } from "../../services/inventoryService";

const REASONS = [
  { value: "SALE", label: "Sold over the counter" },
  { value: "PRESCRIPTION", label: "Dispensed for a prescription" },
  { value: "EXPIRED", label: "Expired stock" },
  { value: "DAMAGED", label: "Damaged / wastage" },
];

export default function ReduceStockModal({ open, onClose, medicine, onSaved }) {
  const [movementType, setMovementType] = useState("SALE");
  const [quantity, setQuantity] = useState("1");
  const [notes, setNotes] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (open) {
      setMovementType("SALE");
      setQuantity("1");
      setNotes("");
    }
  }, [open, medicine?.id]);

  if (!medicine) return null;

  const currentStock = medicine.stockQuantity ?? 0;
  const qty = Number(quantity || 0);
  const remaining = currentStock - qty;

  const submit = async () => {
    if (!qty || qty <= 0) return toast.error("Enter a quantity greater than zero");
    if (qty > currentStock) return toast.error(`Only ${currentStock} in stock`);
    try {
      setSaving(true);
      await createStockMovement({
        medicineId: medicine.id,
        movementType,
        quantity: qty,
        reason: notes || REASONS.find((r) => r.value === movementType)?.label,
      });
      toast.success(`Stock reduced by ${qty}`);
      onSaved?.();
      onClose();
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not reduce stock");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={saving ? undefined : onClose}
      title={`Reduce stock — ${medicine.name || ""}`}
      subtitle={`Currently ${currentStock} ${medicine.unit || "units"} in stock`}
    >
      <div className="form-grid-2">
        <Field label="Reason">
          <Select value={movementType} onChange={(e) => setMovementType(e.target.value)}>
            {REASONS.map((r) => (
              <option key={r.value} value={r.value}>
                {r.label}
              </option>
            ))}
          </Select>
        </Field>
        <Field label="Quantity to remove">
          <Input
            type="number"
            min="1"
            max={currentStock}
            value={quantity}
            onChange={(e) => setQuantity(e.target.value)}
          />
        </Field>
        <Field label="Note (optional)" className="col-span-2">
          <Textarea rows={2} value={notes} onChange={(e) => setNotes(e.target.value)} />
        </Field>
      </div>

      <div className="invoice-line" style={{ marginTop: 4 }}>
        <span className="text-muted">Stock after this change</span>
        <span style={{ fontWeight: 600, color: remaining < 0 ? "var(--danger)" : undefined }}>
          {Number.isFinite(remaining) ? remaining : "—"} {medicine.unit || "units"}
        </span>
      </div>

      <div className="modal-actions">
        <Button variant="secondary" onClick={onClose} disabled={saving}>
          Cancel
        </Button>
        <Button variant="danger" onClick={submit} disabled={saving}>
          {saving ? "Saving..." : "Reduce stock"}
        </Button>
      </div>
    </Modal>
  );
}
