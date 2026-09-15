# Zenve Veterinary Frontend - Backend Integration

This frontend is configured to use the supplied Spring Boot backend.

## Backend URL

Default:

`http://localhost:8080/api`

You can override it with:

`VITE_API_URL=http://localhost:8080/api`

## Run

1. Start the Spring Boot backend on port 8080.
2. In this frontend folder run `npm install`.
3. Run `npm run dev`.
4. Open `http://localhost:5173`.

## Data rule

The primary dashboard pages no longer use frontend mock data. They call the backend REST APIs for patients, owners, appointments, prescriptions, follow-ups, medicines/inventory, invoices/payments, vaccinations, medical records, notifications, SMS, WhatsApp, email and PDF generation.

## Backend gaps

The supplied backend currently has empty/no mapped controllers for authentication, dashboard and marketplace, and it has no doctor-profile controller. Those frontend features cannot honestly be made persistent from the frontend alone until corresponding backend endpoints exist.
