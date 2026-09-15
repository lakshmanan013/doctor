import api from "./api";

export const getInvoices = async () => (await api.get("/invoices")).data;
export const getInvoiceById = async (id) => (await api.get(`/invoices/${id}`)).data;
export const createInvoice = async (payload) => (await api.post("/invoices", payload)).data;
export const updateInvoicePayment = async (id, paidAmount) => (await api.put(`/invoices/${id}/payment`, null, { params: { paidAmount } })).data;
export const cancelInvoice = async (id) => (await api.put(`/invoices/${id}/cancel`)).data;
export const deleteInvoice = async (id) => (await api.delete(`/invoices/${id}`)).data;
export const getPayments = async () => (await api.get("/payments")).data;
export const createPayment = async (payload) => (await api.post("/payments", payload)).data;

// --- Razorpay ---
export const createRazorpayOrder = async (payload) => (await api.post("/payments/razorpay/order", payload)).data;
export const verifyRazorpayPayment = async (payload) => (await api.post("/payments/razorpay/verify", payload)).data;
