# Campus Admin Console (Java + Spring Boot)

An admin dashboard for a college with **10,500 students**, each with a unique 8-digit registration number.
Spring Boot REST API, H2 database (saved to a file), BCrypt-hashed admin login, HTML/CSS/JS frontend.

## Run it
Needs **Java 17+** and **Maven**.
```
mvn spring-boot:run
```
Open http://localhost:8080 and sign in with `admin` / `admin123`.
The first start creates 10,500 sample students (takes a few seconds). Data is kept in the `data/` folder.

## Registration numbers
`YY` + `BB` + `SSSS`, for example **23010042** = joined in 2023, programme 01 (BCA), serial 42.
The database has a UNIQUE constraint on this column, so a duplicate can never be saved, even if two admins add the same number at once.
Tip: searching `2301` lists every BCA student who joined in 2023.

## Built to handle 10,000+ rows
- **Server-side paging:** the browser asks for one page (15 rows) at a time, never all 10,500.
- **Server-side search, filter and sort:** done in one database query (`StudentRepository.search`).
- **Indexes** on programme and name; the registration number index comes from the UNIQUE constraint.
- **Dashboard statistics** (counts, averages, CGPA bands) are calculated by the database, not in JavaScript.
- **Debounced search:** the page waits 300 ms after typing stops, so it doesn't call the server on every key.

## REST API (all need login except login/me)
| Method | URL | Purpose |
|---|---|---|
| POST | /api/login, /api/logout | Session login and logout |
| GET | /api/me | Am I signed in? |
| GET | /api/students?page=0&size=15&q=&course=&lowAtt=false&sort=regNo&dir=asc | One page of students |
| POST / PUT / DELETE | /api/students, /api/students/{id} | Add, update, delete |
| GET | /api/stats | Dashboard numbers |

## Code map
`Student` (table) | `StudentRepository` (queries) | `StudentController`, `StatsController`, `AuthController` (endpoints) | `SampleData` (generates the 10,500 students) | `WebConfig` (login check) | `ApiErrors` (JSON error messages) | `static/index.html` (the page)

## Ideas to extend it
MySQL instead of H2, Spring Security + JWT, role-based access (admin vs teacher), attendance history, unit tests with MockMvc, deployment.
