// Opening wa.me directly hands off to the doctor's own WhatsApp
// (app or web), so a message can be sent without any Twilio/WhatsApp
// Business API account configured on the backend. The backend
// /api/whatsapp log is still written (best-effort, fire-and-forget)
// purely so there's a record of what was sent — it is never what
// actually delivers the message.

// India-only default: a bare 10-digit mobile number is assumed to be
// a domestic number and gets the country code prefixed. Numbers that
// already include a country code (11+ digits, or a leading "+") are
// left as-is.
const DEFAULT_COUNTRY_CODE = "91";

export function normalizePhoneForWhatsApp(phone) {
  if (!phone) return "";
  const digits = String(phone).replace(/\D/g, "");
  if (!digits) return "";
  if (digits.length === 10) return DEFAULT_COUNTRY_CODE + digits;
  return digits;
}

// Opens wa.me in a new tab with the message pre-filled. Returns false
// (and opens nothing) if there's no usable phone number, so callers
// can show their own "no phone on file" error instead.
export function openWhatsApp(phone, message) {
  const digits = normalizePhoneForWhatsApp(phone);
  if (!digits) return false;
  const url = `https://wa.me/${digits}?text=${encodeURIComponent(message || "")}`;
  window.open(url, "_blank", "noopener,noreferrer");
  return true;
}
