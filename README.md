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
- PostgreSQL
- Maven

***

## Setup and Run

**1. Clone the repository**

```
git clone https://github.com/tistar5000/healthbook.git
cd healthbook
```

**2. Create the PostgreSQL database**

```
psql postgres -c "CREATE DATABASE healthbook;"
```

**3. Configure the application**

```
cp src/main/resources/application.properties.template \
   src/main/resources/application.properties
```

Edit application.properties and set your PostgreSQL username and password:

```
spring.datasource.username=YOUR_PG_USERNAME
spring.datasource.password=YOUR_PG_PASSWORD
```

**4. Build the project**

```
./mvnw clean install
```

**5. Run the application**

```
./mvnw spring-boot:run
```

**6. Open in browser**

```
http://localhost:8080
```

The schema and seed data are applied automatically on startup from `schema.sql` and `data.sql`.

>[!NOTE]
>***A test account is available:***
>```
>Email:    thor.odinson@example.com
>Password: password123
>```

***

## Notes

- The notification service is mocked internally and does not send real emails or SMS.
- `application.properties` is excluded from the repository. Use the provided template to configure your local environment.
