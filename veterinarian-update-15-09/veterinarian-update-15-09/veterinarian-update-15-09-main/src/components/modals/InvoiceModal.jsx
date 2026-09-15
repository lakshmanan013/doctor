import { useState } from "react";
import toast from "react-hot-toast";
import { FiMessageCircle } from "react-icons/fi";
import { SiRazorpay } from "react-icons/si";
import Modal from "../ui/Modal";
import Button from "../ui/Button";
import { updateInvoicePayment, createRazorpayOrder, verifyRazorpayPayment } from "../../services/billingService";
import { openRazorpayCheckout } from "../../utils/razorpay";
import { toE164 } from "../../utils/phone";

const MODES = ["Cash", "Razorpay"];

export default function InvoiceModal({ open, onClose, invoice, onUpdated }) {
  const [mode, setMode] = useState("Cash");
  const [payment, setPayment] = useState(String(invoice?.dueAmount ?? 0));
  const [payingOnline, setPayingOnline] = useState(false);
  if (!invoice) return null;

  const markPaid = async () => {
    try {
      await updateInvoicePayment(invoice.id, Number(payment || 0));
      toast.success("Payment updated");
      onUpdated?.();
      onClose();
    } catch (e) {
      toast.error(e?.response?.data?.message || "Could not update payment");
    }
  };

  const payWithRazorpay = async () => {
    const amount = Number(payment || 0);
    if (amount <= 0) return toast.error("Enter an amount to pay");
    try {
      setPayingOnline(true);
      const order = await createRazorpayOrder({ invoiceId: invoice.id, amount });
      const result = await openRazorpayCheckout(order, {
        name: "Zenve Veterinary Clinic",
        description: invoice.invoiceNumber || `INV-${invoice.id}`,
        // Razorpay's UPI collect flow (and some wallet/UPI intent options)
        // only render when the checkout has a contact number and email to
        // work with — without these, Checkout can silently drop those
        // methods from the list instead of just leaving the fields blank.
        prefill: {
          contact: invoice.ownerPhone ? toE164(invoice.ownerPhone) : undefined,
          email: invoice.ownerEmail || undefined,
        },
      });
      await verifyRazorpayPayment({
        invoiceId: invoice.id,
        razorpayOrderId: result.razorpay_order_id,
        razorpayPaymentId: result.razorpay_payment_id,
        razorpaySignature: result.razorpay_signature,
        amount,
      });
      toast.success("Payment received via Razorpay");
      onUpdated?.();
      onClose();
    } catch (e) {
      const message = e?.response?.data?.message || e?.message || "Razorpay payment could not be completed";
      toast.error(message);
    } finally {
      setPayingOnline(false);
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={`${invoice.invoiceNumber || `INV-${invoice.id}`}`}
      subtitle={`${invoice.patientName || `Patient #${invoice.patientId || "—"}`} · Owner: ${invoice.ownerName || `#${invoice.ownerId || "—"}`}`}
    >
      <div className="stack-2">
        <div className="invoice-line">
          <span className="text-muted">Total</span>
          <span style={{ fontWeight: 600 }}>₹{Number(invoice.totalAmount || 0).toLocaleString("en-IN")}</span>
        </div>
        <div className="invoice-line">
          <span className="text-muted">Paid</span>
          <span style={{ fontWeight: 600 }}>₹{Number(invoice.paidAmount || 0).toLocaleString("en-IN")}</span>
        </div>
        <div className="invoice-line">
          <span className="text-muted">Due</span>
          <span style={{ fontWeight: 600 }}>₹{Number(invoice.dueAmount || 0).toLocaleString("en-IN")}</span>
        </div>
      </div>

      <div style={{ marginTop: 20 }}>
        <p className="eyebrow">Payment mode</p>
        <div className="payment-modes">
          {MODES.map((m) => (
            <button
              key={m}
              onClick={() => setMode(m)}
              className={`payment-mode-btn ${mode === m ? "active" : ""}`}
            >
              {m === "Razorpay" && <SiRazorpay size={13} style={{ marginRight: 5, verticalAlign: -2 }} />}
              {m}
            </button>
          ))}
        </div>
      </div>

      <div style={{ marginTop: 16 }}>
        <p className="eyebrow">Amount to apply</p>
        <input className="input" type="number" value={payment} onChange={(e) => setPayment(e.target.value)} />
      </div>

      {mode === "Razorpay" && (
        <p className="text-faint" style={{ marginTop: 8, fontSize: 12 }}>
          Opens the Razorpay checkout — the invoice is marked paid automatically once payment is verified.
        </p>
      )}

      <div className="flex-between" style={{ marginTop: 24 }}>
        <button
          onClick={() => toast("Invoice messaging needs an owner phone/email from the backend record")}
          className="link-btn"
        >
          <FiMessageCircle size={16} /> Send to owner
        </button>
        {mode === "Razorpay" ? (
          <Button onClick={payWithRazorpay} disabled={payingOnline}>
            {payingOnline ? "Opening Razorpay..." : "Pay with Razorpay"}
          </Button>
        ) : (
          <Button onClick={markPaid}>Record {mode} payment</Button>
        )}
      </div>
    </Modal>
  );
}
