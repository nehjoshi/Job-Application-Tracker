# Job Application Tracker
A secure, reliable and easy to configure job status tracker API.
## What This Application is About
This Spring Boot application allows clients to organize job applications in 
a sorted fashion, optimized for quick retrieval and modification. Furthermore, 
certain API routes add a more social feel to the application,
wherein users can check out trending companies that other applicants apply to. 
There is a full authentication workflow using JSON Web Tokens (JWT) that will be
used as a filter through Spring Security. Redis is also used for caching repeated
database queries (PostgreSQL) and AWS S3 is used for storing user media.

## Getting Started
Follow these steps for configuring and running the application.
1. Install all dependencies using the `pom.xml` file (I've used Maven).
2. Create a `.env` file inside the root directory. If you choose not to 
create an environment file, you can directly set the following environment 
variables through your IDE:
    1. `DB_URI`: The URI of your database (I'm using PostgreSQL)
   2. `DB_USERNAME`: The username of your DB
   3. `DB_PASSWORD`: The password of your DB
   4. `JWT_SECRET`: A hexadecimal 256-bit secret that will be used for JWTs
   5. `S3_ACCESS_KEY`: The access key of your S3 bucket
   6. `S3_SECRET_KEY`: The secret key of your S3 bucket
   7. `S3_BUCKET_NAME`: The name of your S3 bucket
3. If you change the name of the above environment variables,
make sure to update the `/src/main/resources/application.yml` file
with these changes.
4. Run the application.

## Endpoints
There are three unprotected (i.e, unsecured) endpoints, that will
not be passed through the security filter. These are:
1. `GET /api/v1/user`: A test route. Returns "Hello World!"
2. `POST /api/v1/user/auth/register`: Registration route. Returns
`EmailAlreadyExistsException` for a reused email. Otherwise, returns
a ResponseEntity object with a status code `201`.
3. `POST /api/v1/user/auth/login`: Returns `IncorrectCredentialsException`
for wrong usernames/passwords. Otherwise, returns status code `200`.

The following endpoints are secured through the security filter chain,
and each request **must** have a valid bearer token in its 'Authentication'
header:
1. `POST /api/v1/user/profile-picture`: Accepts a multipart file with
a maximum size of 1MB for profile picture. Returns `IOException` 
for any processing operations, otherwise a status code `201`.
2. `GET /api/v1/user/profile-picture`: Fetch user's profile picture.
Returns `IOException` for any processing operations, otherwise a status code `200`.
3. `POST /api/v1/application/new`: Accepts an ApplicationDto object. Creates a 
new application associated with the user fetched from the security context holder and
returns a status code `201`.
4. `GET /api/v1/application/all/{pageNumber}`: Returns a list of all applications
of the specified page number, and if none found, returns an empty list. Note
that the service function for this route is cached with Redis.
5. `PUT /api/v1/application/edit/{applicationId}`: Accepts the ID of the 
application to be edited as the path variable, and the application as the 
request body. If application isn't found, returns `ApplicationNotFoundException`,
otherwise returns `201`.
6. `DELETE /api/v1/application/${applicationId}`: Deletes an application
with the specified ID. If application isn't found, returns `ApplicationNotFoundException`,
   otherwise returns `204`.
7. `GET /api/v1/application?company-name={name}`: Finds all applications of 
the authenticated user whose company names start with the specified company name.
Returns an empty list for no applications found, with status code `200`.
8. `GET /api/v1/application/social?page={}`: Finds the most recent applications
with the status "Applied" according to the page number. Returns `200`.


