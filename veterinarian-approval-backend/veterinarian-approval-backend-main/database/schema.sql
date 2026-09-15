-- Zenve `vetcare` schema — reference DDL.
--
-- Hibernate's `ddl-auto: update` will create/evolve these tables automatically
-- when the backend starts against MySQL, so running this file by hand is
-- optional. It's provided so you (or the separate doctor-facing backend) can
-- provision the database manually, and so the schema is documented in one place.

CREATE DATABASE IF NOT EXISTS vetcare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vetcare;

CREATE TABLE IF NOT EXISTS admins (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS doctors (
    id                VARCHAR(36)  NOT NULL PRIMARY KEY,
    full_name         VARCHAR(255) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    phone             VARCHAR(50),
    clinic_name       VARCHAR(255),
    qualification     VARCHAR(255),
    status            VARCHAR(20)  NOT NULL DEFAULT 'pending', -- pending | approved | rejected
    rejection_reason  VARCHAR(500),
    created_at        TIMESTAMP(6) NOT NULL,
    updated_at        TIMESTAMP(6) NOT NULL,
    INDEX idx_doctors_status (status),
    INDEX idx_doctors_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    type        VARCHAR(30)  NOT NULL, -- doctor_registered | doctor_approved | doctor_rejected
    title       VARCHAR(255) NOT NULL,
    message     VARCHAR(500) NOT NULL,
    `read`      BOOLEAN      NOT NULL DEFAULT FALSE,
    doctor_id   VARCHAR(36),
    created_at  TIMESTAMP(6) NOT NULL,
    INDEX idx_notifications_read (`read`),
    INDEX idx_notifications_doctor (doctor_id)
) ENGINE=InnoDB;

-- Seeded admin (admin@zenve.in / Admin@123) is created automatically on first
-- boot by DataSeeder — no manual INSERT needed here.
