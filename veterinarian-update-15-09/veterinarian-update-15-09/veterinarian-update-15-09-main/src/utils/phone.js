// Twilio's SMS API requires E.164 format: a leading "+", country code, then
// the subscriber number, with no spaces/dashes/leading zero (e.g.
// "+919876543210"). Numbers stored on the patient/owner record are usually
// just a plain 10-digit Indian mobile number, so sending that straight to
// Twilio fails validation and the send silently ends up as status "FAILED".
// This mirrors the assumption already used for WhatsApp (utils/whatsapp.js):
// a bare 10-digit number is treated as domestic and gets +91 prefixed;
// anything that already looks like it has a country code (11+ digits) or a
// leading "+" is left alone.
const DEFAULT_COUNTRY_CODE = "91";

export function toE164(phone) {
  if (!phone) return "";
  const digits = String(phone).replace(/\D/g, "");
  if (!digits) return "";
  const withCountryCode = digits.length === 10 ? DEFAULT_COUNTRY_CODE + digits : digits;
  return `+${withCountryCode}`;
}
