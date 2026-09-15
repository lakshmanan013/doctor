import api from "./api";

export const getNotifications = async () => (await api.get("/notifications")).data;
export const getUnreadNotifications = async () => (await api.get("/notifications/unread")).data;
export const markNotificationRead = async (id) => (await api.put(`/notifications/${id}/read`)).data;
export const markAllNotificationsRead = async (userId) => (await api.put(`/notifications/user/${userId}/read-all`)).data;
export const deleteNotification = async (id) => (await api.delete(`/notifications/${id}`)).data;
