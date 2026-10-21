# Coaching Manager

Backend for a small coaching centre: batches, enrollments, daily attendance, monthly
fees with part payments, and reports. Built with Spring Boot 4, Spring Data JPA,
Spring Security (JWT) and MySQL.

## Problem

Small coaching centres track fees and attendance in registers and WhatsApp groups.
Owners lose track of pending fees and parents have no visibility. This API gives the
owner a pending-fees report in one call and a per-student attendance percentage.

## Features

- Student, teacher and batch management (soft delete keeps fee history)
- Enrollment with duplicate prevention
- Bulk attendance for a whole batch in a single transaction (all or nothing)
- Monthly fee generation, safe to run twice (no duplicates)
- Part payments with row-level locking, due is marked PAID when fully paid
- Reports with raw SQL: pending fees per batch, attendance percentage
- JWT login with OWNER / TEACHER / STUDENT roles
- Request logging filter and an @Audited AOP aspect

## Tech

Java 17+, Spring Boot 4, Spring Data JPA / Hibernate, Spring JDBC, Spring Security,
JWT (jjwt), MySQL, JUnit 5, Mockito, Maven

## Run locally

1. Create database: `CREATE DATABASE coaching_db;`
2. Copy `application-local.properties.example` to `application-local.properties`
   (in `src/main/resources`) and fill your values
3. `mvnw spring-boot:run`
4. Login: `POST /api/auth/login` with the owner credentials from your local file

## Roles

| Role | Access |
|---|---|
| OWNER | Everything |
| TEACHER | View own batches and their students, mark attendance for own batches |
| STUDENT | Own fees and enrollments under `/api/me` |

## Main endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | /api/auth/login | Get JWT token |
| POST | /api/students | Add student |
| POST | /api/enrollments | Enroll student in a batch |
| POST | /api/attendance | Mark attendance for a batch |
| POST | /api/fees/generate | Generate monthly dues for a batch |
| POST | /api/payments | Record a payment |
| GET | /api/reports/pending-fees | Pending fees per batch |
| GET | /api/reports/attendance/batch/{id} | Attendance percentage |

## Design notes

- Fees are copied into each due, so changing a batch fee does not rewrite history
- Payments lock the due row (`PESSIMISTIC_WRITE`) so two simultaneous payments cannot
  overpay
- N+1 queries are removed with `@EntityGraph` on list endpoints
- Secrets come from environment variables in the `prod` profile

## Tests

`mvnw test` runs unit tests for payment and fee generation logic.