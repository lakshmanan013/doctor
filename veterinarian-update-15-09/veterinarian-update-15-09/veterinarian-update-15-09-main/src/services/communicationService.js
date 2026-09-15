import api from "./api";

export const sendSms = async (payload) => (await api.post("/sms", payload)).data;
export const sendWhatsApp = async (payload) => (await api.post("/whatsapp", payload)).data;
export const sendEmail = async (payload) => (await api.post("/emails", payload)).data;
export const generatePdf = async (title, content) => (await api.post("/pdfs/generate", null, { params: { title, content }, responseType: "blob" })).data;
