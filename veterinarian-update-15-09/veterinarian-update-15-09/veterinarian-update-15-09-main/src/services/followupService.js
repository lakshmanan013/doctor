import api from "./api";

export const getFollowups = async () => (await api.get("/follow-ups")).data;
export const getFollowupById = async (id) => (await api.get(`/follow-ups/${id}`)).data;
export const getFollowupsByPatient = async (patientId) => (await api.get(`/follow-ups/patient/${patientId}`)).data;
export const getFollowupsByStatus = async (status) => (await api.get(`/follow-ups/status/${encodeURIComponent(status)}`)).data;
export const getUpcomingFollowups = async (startDate, endDate) => (await api.get("/follow-ups/upcoming", { params: { startDate, endDate } })).data;
export const getOverdueFollowups = async (date) => (await api.get("/follow-ups/overdue", { params: { date } })).data;
export const getPendingReminders = async () => (await api.get("/follow-ups/reminders/pending")).data;
export const createFollowup = async (payload) => (await api.post("/follow-ups", payload)).data;
export const updateFollowup = async (id, payload) => (await api.put(`/follow-ups/${id}`, payload)).data;
export const markReminderSent = async (id) => (await api.patch(`/follow-ups/${id}/reminder`)).data;
export const deleteFollowup = async (id) => (await api.delete(`/follow-ups/${id}`)).data;
