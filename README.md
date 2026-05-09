# HealthBook — Mock Medical Office Online Appointment Scheduling Service
> SJSU: CMPE172

A web-based appointment scheduling system for a medical office.
Patients are able to register with a new account or log in with an existing account, browse available provider slots, book appointments, and view appointment history.

***

## Dependencies

- Java 17
- Spring Boot 3
- Thymeleaf
- Spring JDBC
- H2 in-memory DB
- Maven

***

## Setup and Run

**1. Clone the repository**

```
git clone https://github.com/tistar5000/healthbook.git
cd healthbook
```

**2. Build the project**

```
./mvnw clean install
```

**3. Run the application**

```
./mvnw spring-boot:run
```

**4. Open in browser**

```
http://localhost:8080
```

The database is seeded automatically on startup and resets on every restart. All data is seeded fresh from `schema.sql` and `data.sql`.

A test account is available:

```
Email:    thor.odinson@example.com
Password: password123
```

***

## Notes

- The notification service is mocked internally and does not send real emails or SMS.
