import api from "./api";

// ==========================================
// GET ALL OWNERS
// ==========================================

export const getOwners = async () => {
  try {
    const response = await api.get("/owners");

    console.log(
      "GET /owners response:",
      response.data
    );

    return Array.isArray(response.data)
      ? response.data
      : [];
  } catch (error) {
    console.error(
      "Failed to get owners:",
      error
    );

    console.error(
      "Backend response:",
      error?.response?.data
    );

    throw error;
  }
};

// ==========================================
// GET OWNER BY ID
// ==========================================

export const getOwnerById = async (id) => {
  if (!id) {
    throw new Error(
      "Owner ID is required"
    );
  }

  try {
    const response = await api.get(
      `/owners/${id}`
    );

    console.log(
      `GET /owners/${id} response:`,
      response.data
    );

    return response.data;
  } catch (error) {
    console.error(
      `Failed to get owner ${id}:`,
      error
    );

    console.error(
      "Backend response:",
      error?.response?.data
    );

    throw error;
  }
};

// ==========================================
// CREATE OWNER
// ==========================================

export const createOwner = async (payload) => {
  try {
    // Make sure we only send the fields
    // expected from the AddPatientModal
    const ownerPayload = {
      fullName:
        payload?.fullName?.trim() || "",

      phone:
        payload?.phone?.trim() || null,

      email:
        payload?.email?.trim() || null,

      address:
        payload?.address?.trim() || null,

      city:
        payload?.city?.trim() || null,

      state:
        payload?.state?.trim() || null,

      pincode:
        payload?.pincode?.trim() || null,
    };

    console.log(
      "POST /owners payload:",
      ownerPayload
    );

    const response = await api.post(
      "/owners",
      ownerPayload
    );

    console.log(
      "POST /owners response:",
      response.data
    );

    if (!response.data) {
      throw new Error(
        "Owner was not returned by backend"
      );
    }

    if (!response.data.id) {
      console.error(
        "Owner response does not contain ID:",
        response.data
      );

      throw new Error(
        "Owner was created but backend did not return owner ID"
      );
    }

    return response.data;
  } catch (error) {
    console.error(
      "Failed to create owner:",
      error
    );

    console.error(
      "Request payload:",
      payload
    );

    console.error(
      "Backend response:",
      error?.response?.data
    );

    throw error;
  }
};

// ==========================================
// UPDATE OWNER
// ==========================================

export const updateOwner = async (
  id,
  payload
) => {
  if (!id) {
    throw new Error(
      "Owner ID is required"
    );
  }

  try {
    const ownerPayload = {
      fullName:
        payload?.fullName?.trim() || "",

      phone:
        payload?.phone?.trim() || null,

      email:
        payload?.email?.trim() || null,

      address:
        payload?.address?.trim() || null,

      city:
        payload?.city?.trim() || null,

      state:
        payload?.state?.trim() || null,

      pincode:
        payload?.pincode?.trim() || null,
    };

    console.log(
      `PUT /owners/${id} payload:`,
      ownerPayload
    );

    const response = await api.put(
      `/owners/${id}`,
      ownerPayload
    );

    console.log(
      `PUT /owners/${id} response:`,
      response.data
    );

    return response.data;
  } catch (error) {
    console.error(
      `Failed to update owner ${id}:`,
      error
    );

    console.error(
      "Backend response:",
      error?.response?.data
    );

    throw error;
  }
};

// ==========================================
// DELETE OWNER
// ==========================================

export const deleteOwner = async (id) => {
  if (!id) {
    throw new Error(
      "Owner ID is required"
    );
  }

  try {
    console.log(
      `DELETE /owners/${id}`
    );

    const response = await api.delete(
      `/owners/${id}`
    );

    console.log(
      `DELETE /owners/${id} response:`,
      response.data
    );

    return response.data;
  } catch (error) {
    console.error(
      `Failed to delete owner ${id}:`,
      error
    );

    console.error(
      "Backend response:",
      error?.response?.data
    );

    throw error;
  }
};