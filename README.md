# Job Application Tracker
A secure, reliable and easy to configure job status tracker API.
## What This Application is About
This Spring Boot application helps users manage their job applications in an organized and efficient way, making it easy to retrieve and update entries quickly. It also includes social features, such as viewing trending companies that other users are applying to. The app uses a full authentication system based on JSON Web Tokens (JWT), integrated with Spring Security for secure access control. To improve performance, Redis is used to cache frequent database queries, and AWS S3 is used to store user-uploaded media files.

# Local Development Setup

Follow these steps to configure and run the application for local development.

## 1. Install Dependencies

Ensure you have [Maven](https://maven.apache.org/) installed. From the root of the project, run:

```bash
mvn install
```

This will install all required dependencies as specified in the `pom.xml` file.

---

## 2. Environment Variables

Create a `.env` file in the root directory. Alternatively, set the following environment variables through your IDE or terminal:

- `DB_URI`: URI of your PostgreSQL database
- `DB_USERNAME`: Your database username
- `DB_PASSWORD`: Your database password
- `JWT_SECRET`: A 256-bit hexadecimal secret for JWT generation
- `S3_ACCESS_KEY`: Your AWS S3 access key
- `S3_SECRET_KEY`: Your AWS S3 secret key
- `S3_BUCKET_NAME`: Name of your S3 bucket

> ⚠️ If you rename any of the above variables, update them accordingly in `/src/main/resources/application.yml`.

---

## 3. Run the Application

To run locally using Maven:

```bash
mvn spring-boot:run
```

---

## 4. Run with Docker

If you prefer a containerized setup, ensure [Docker](https://www.docker.com/) is installed. From the root of the project, build the Docker image:

```bash
docker build -t tracker-app .
```

Then run the Docker container:

```bash
docker run -p 5000:5000 tracker-app
```

---

## 5. Test the Application

Verify the application is running correctly by sending a `GET` request to:

```
http://localhost:5000/api/v1/user
```

Expected response: `"Hello World!"`
## 🔓 Public (Unsecured) Endpoints

These endpoints **do not require authentication** and bypass the security filter chain.

### `GET /api/v1/user`
- **Description**: A basic test route.
- **Response**:
  - `200 OK`: Returns `"Hello World!"`

---

### `POST /api/v1/user/auth/register`
- **Description**: Register a new user.
- **Request Body**:  
  JSON object with user registration details (e.g., email, password).
- **Responses**:
  - `201 Created`: Successfully registered.
  - `400 Bad Request`: Email already exists (`EmailAlreadyExistsException`)

---

### `POST /api/v1/user/auth/login`
- **Description**: Authenticate an existing user.
- **Request Body**:  
  JSON object with login credentials.
- **Responses**:
  - `200 OK`: Login successful.
  - `401 Unauthorized`: Invalid credentials (`IncorrectCredentialsException`)

---

## 🔐 Secured Endpoints

All secured endpoints **require a valid Bearer token** in the `Authorization` header.

---

### 🖼 Profile Picture Management

#### `POST /api/v1/user/profile-picture`
- **Description**: Upload a profile picture.
- **Request**: Multipart file (max size: 1MB)
- **Responses**:
  - `201 Created`: Upload successful.
  - `500 Internal Server Error`: Upload failure (`IOException`)

#### `GET /api/v1/user/profile-picture`
- **Description**: Retrieve user's profile picture.
- **Responses**:
  - `200 OK`: Image retrieved.
  - `500 Internal Server Error`: Retrieval failure (`IOException`)

---

### 📄 Application Management

#### `POST /api/v1/application/new`
- **Description**: Create a new job application.
- **Request Body**:  
  JSON object representing an `ApplicationDto`.
- **Response**:
  - `201 Created`: Application created successfully.

---

#### `GET /api/v1/application/all/{pageNumber}`
- **Description**: Get paginated list of applications.
- **Note**: Uses Redis caching.
- **Response**:
  - `200 OK`: List of applications (can be empty)

---

#### `PUT /api/v1/application/edit/{applicationId}`
- **Description**: Edit an existing application by ID.
- **Request Body**:  
  JSON object with updated application data.
- **Responses**:
  - `201 Created`: Update successful.
  - `404 Not Found`: Application not found (`ApplicationNotFoundException`)

---

#### `DELETE /api/v1/application/{applicationId}`
- **Description**: Delete a specific application.
- **Responses**:
  - `204 No Content`: Deletion successful.
  - `404 Not Found`: Application not found (`ApplicationNotFoundException`)

---

#### `GET /api/v1/application?company-name={name}`
- **Description**: Retrieve applications where company name starts with provided string.
- **Response**:
  - `200 OK`: List of matching applications (can be empty)

---

#### `GET /api/v1/application/social?page={pageNumber}`
- **Description**: Get recent applications with status `"Applied"`, by page.
- **Response**:
  - `200 OK`: List of applications
