# Campus Admin Console

A web dashboard for managing a college's student records. It handles **10,500 sample students**, each with a unique 8-digit registration number, and lets an admin search, filter, sort and page through them instantly.

Built with Java, Spring Boot and a small JavaScript frontend by Digdarsak Panda, BCA student at Siksha ’O’ Anusandhan University.

![Dashboard](screenshots/dashboard.png)

## What it does

- Admin login with a hashed password (BCrypt) and a session
- Dashboard cards: total students, average CGPA, average attendance, and students below 75% attendance
- Charts for students per programme and CGPA distribution; clicking a bar filters the table
- Top performers list
- Search by name, registration number or email (for example, `2301` lists the BCA students who joined in 2023)
- Filter by programme, sort by registration number, name, CGPA or attendance
- Add, edit and delete students, with validation on the server
- Paging that stays fast with 10,000+ records

| Search and filter | Duplicate registration number blocked |
|---|---|
| ![Search](screenshots/search.png) | ![Duplicate error](screenshots/duplicate-error.png) |
![Login](screenshots/login.png)
![Add student form](screenshots/add-student.png)

## Tech stack

| Part | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3, Spring Web (REST) |
| Database | H2 (file-based), Spring Data JPA / Hibernate |
| Security | BCrypt password hashing, session login, request interceptor |
| Validation | Jakarta Bean Validation |
| Frontend | HTML, CSS, JavaScript (`fetch`) |
| Build tool | Maven |

## Run it on your computer

You need **Java 17+** and **Maven**.

```
mvn spring-boot:run
```

Then open http://localhost:8080 and sign in with `admin` / `admin123`.

The first start creates 10,500 sample students, which takes a few seconds. The data is saved in a `data/` folder, so it is still there after a restart. All students are generated sample data; no real people are included.

## Registration numbers

The format is `YY` + `BB` + `SSSS`:

| Part | Meaning | Example |
|---|---|---|
| YY | Year of admission | 23 = joined in 2023 |
| BB | Programme code | 01 = BCA |
| SSSS | Serial number within that year and programme | 0042 |

So `23010042` is the 42nd BCA student admitted in 2023. The database column is `UNIQUE`, so the same number can never be saved twice, even if two requests arrive at the same time. The API answers a duplicate with a clear 409 error.

## Design decisions

- **Server-side paging.** The page asks the server for 15 rows at a time instead of loading all 10,500. One database query handles search, filter, sort and paging together.
- **Unique constraint in the database.** Checking for duplicates in Java alone is not safe when requests overlap, so the database is the final check.
- **Statistics calculated by the database.** Counts and averages come from queries, so the dashboard does not slow down as data grows.
- **Debounced search.** The page waits 300 ms after typing stops before calling the server.
- **Hashed passwords.** The admin password is stored as a BCrypt hash, not as plain text.

## REST API

All routes except login and `me` need a signed-in session.

| Method | URL | Purpose |
|---|---|---|
| POST | `/api/login`, `/api/logout` | Start and end a session |
| GET | `/api/me` | Check whether I am signed in |
| GET | `/api/students?page=0&size=15&q=&course=&lowAtt=false&sort=regNo&dir=asc` | One page of students |
| POST | `/api/students` | Add a student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |
| GET | `/api/stats` | Numbers for the dashboard |

## Project structure

```
src/main/java/com/sms/
  Application.java         starts the app, creates the admin and sample data
  Student.java             the student table and its validation rules
  StudentRepository.java   database queries (search, paging, statistics)
  StudentController.java   student endpoints
  StatsController.java     dashboard statistics endpoint
  AuthController.java      login, logout, session check
  AdminUser.java, AdminRepository.java   admin account storage
  SampleData.java          generates the 10,500 sample students
  WebConfig.java           blocks API calls when not signed in
  ApiErrors.java           turns errors into clean JSON messages
src/main/resources/static/index.html   the dashboard page
```

## How I built it

I built this project to practise Java backend development for campus placements. I used an AI assistant (Claude) and documentation while building it, and I went through the code to understand how each part works: how a request goes from the browser to the controller, the repository and the database, why paging and unique constraints matter, and how login and password hashing work. I am happy to walk through any part of it.

## Known limitations and next steps

- No CSRF protection or roles yet; Spring Security would add both
- No automated tests; I plan to add JUnit and MockMvc tests
- Uses H2 for local use; PostgreSQL or MySQL would be the next step for deployment
- An audit log of who changed which record
