# Library Borrowing System exemplary project for SPQ subject — CI, Sphinx & Cloud Deployment

## Purpose

This application allows users to borrow and return books through a shared platform. Users can manage book availability, track borrowed books, and return them, facilitating an efficient book-sharing system.

The project is implemented with:

- **Spring Boot**
- **Spring MVC**
- **Spring Data JPA**
- **MySQL**
- **Docker**
- **Sphinx documentation**
- **GitHub Actions**
- **Aiven cloud MySQL**
- **Render cloud deployment**
---

## Documentation

| Section | URL |
|---|---|
| **Documentation portal** | https://dipina.github.io/SpringBootLibrarySphinx/ |
| **Sphinx hub** | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/index.html |
| **Sphinx PDF** | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/SpringBootLibrarySphinx.pdf |
| **Doxygen cross-reference** | https://dipina.github.io/SpringBootLibrarySphinx/doxygen/html/ |
| **Javadoc API reference** | https://dipina.github.io/SpringBootLibrarySphinx/site/apidocs/index.html |
| **JaCoCo coverage** | https://dipina.github.io/SpringBootLibrarySphinx/site/jacoco/index.html |
| **Surefire unit test report** | https://dipina.github.io/SpringBootLibrarySphinx/site/surefire-report.html |
| **Performance test report** | https://dipina.github.io/SpringBootLibrarySphinx/site/reports/perf-report.html |
| **Checkstyle** | https://dipina.github.io/SpringBootLibrarySphinx/site/checkstyle.html |
| **PMD** | https://dipina.github.io/SpringBootLibrarySphinx/site/pmd.html |
| **CPD** | https://dipina.github.io/SpringBootLibrarySphinx/site/cpd.html |
| **JDepend** | https://dipina.github.io/SpringBootLibrarySphinx/site/jdepend-report.html |
| **Full Maven site** | https://dipina.github.io/SpringBootLibrarySphinx/site/ |

---

## Architecture Overview

The application follows the **MVC (Model-View-Controller)** pattern:

- **Model** — JPA entities and database persistence
- **View** — static frontend in HTML, JavaScript, and CSS
- **Controller** — REST endpoints handling HTTP requests
- **Service** — business logic
- **Repository** — Spring Data JPA access to MySQL.

### Main source folders

| Path | Purpose |
|---|---|
| `src/main/java/com/example/restapi/` | Spring Boot application entry point |
| `src/main/java/com/example/restapi/controller/` | REST controllers |
| `src/main/java/com/example/restapi/service/` | Business logic |
| `src/main/java/com/example/restapi/repository/` | Spring Data JPA repositories |
| `src/main/java/com/example/restapi/model/` | JPA entity classes |
| `src/main/resources/static/` | HTML, CSS, and JavaScript frontend |
| `src/main/resources/application.properties` | Default local configuration |
| `Dockerfile` | Container image definition |
| `docker-compose.yml` | Local development environment with app + MySQL |

API documentation is generated automatically using **Springdoc OpenAPI**.

For a detailed technical explanation, see:

```text
HOWTO_SPRINGBOOT.md
```

---

# Running the Application Locally

## Option 1: Local MySQL installed on the machine

Create the local database:

```bash
mysql -u root -p < src/main/resources/dbsetup.sql
```

Check or edit the default local database configuration in:

```text
src/main/resources/application.properties
```

Run the application:

```bash
mvn spring-boot:run
```

or skip tests for a faster start:

```bash
mvn -DskipTests spring-boot:run
```

Access points:

| Service | URL |
|---|---|
| Frontend UI | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## Option 2: Local Docker Compose

This project includes a `docker-compose.yml` file that starts:

1. a MySQL container
2. the Spring Boot application container

Start the stack:

```bash
docker-compose up
```

Rebuild after code changes:

```bash
docker-compose up --build
```

Stop the stack:

```bash
docker-compose down
```

Stop the stack and delete the MySQL volume:

```bash
docker-compose down -v
```

See:

```text
docker_essentials_with_compose.md
```

for more Docker details.

---

# Environment Configuration

The application supports the following configuration sources:

1. real operating-system environment variables
2. `.env` file, if present
3. fallback values from `application.properties`

This allows the same codebase to run locally, in Docker, and in Render.

## `.env` support

The application entry point loads `.env` values when a `.env` file exists in the project root.

The expected local `.env` file is:

```text
.env
```

Example:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-d49e810-dipina-2a6e.h.aivencloud.com:16552/defaultdb?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=YOUR_AIVEN_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

Do not commit `.env`.

Add this to `.gitignore`:

```gitignore
.env
```

A safe template may be committed as:

```text
.env.example
```

Example `.env.example`:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:PORT/DATABASE?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=USERNAME
SPRING_DATASOURCE_PASSWORD=PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

## Java change required to load `.env`

The application must load `.env` before Spring Boot starts.

The application entry point should look like this:

```java
package com.example.restapi;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestApiApplication {

    public static void main(String[] args) {
        loadDotEnvIfPresent();
        SpringApplication.run(RestApiApplication.class, args);
    }

    private static void loadDotEnvIfPresent() {
        Path dotenvPath = Path.of(".env");

        if (!Files.exists(dotenvPath)) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_DATASOURCE_URL", "spring.datasource.url");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_DATASOURCE_USERNAME", "spring.datasource.username");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_DATASOURCE_PASSWORD", "spring.datasource.password");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_DATASOURCE_DRIVER_CLASS_NAME", "spring.datasource.driver-class-name");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_JPA_DATABASE_PLATFORM", "spring.jpa.database-platform");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_JPA_HIBERNATE_DDL_AUTO", "spring.jpa.hibernate.ddl-auto");
        setSpringPropertyFromDotEnvIfNoRealEnv(dotenv, "SPRING_JPA_SHOW_SQL", "spring.jpa.show-sql");
    }

    private static void setSpringPropertyFromDotEnvIfNoRealEnv(
            Dotenv dotenv,
            String envName,
            String springPropertyName
    ) {
        String realEnvironmentValue = System.getenv(envName);

        if (realEnvironmentValue != null && !realEnvironmentValue.isBlank()) {
            System.setProperty(springPropertyName, realEnvironmentValue);
            return;
        }

        String dotenvValue = dotenv.get(envName);

        if (dotenvValue != null && !dotenvValue.isBlank()) {
            System.setProperty(springPropertyName, dotenvValue);
        }
    }
}
```

The required Maven dependency is:

```xml
<dependency>
    <groupId>io.github.cdimascio</groupId>
    <artifactId>dotenv-java</artifactId>
    <version>3.2.0</version>
</dependency>
```

---

# Frontend URL Configuration for Local and Cloud Deployment

The frontend JavaScript must not use a hardcoded localhost API URL when deployed to the cloud.

Incorrect for cloud deployment:

```javascript
const apiBaseUrl = "http://localhost:8080/api/library";
```

Correct:

```javascript
const apiBaseUrl = "/api/library";
```

With the relative URL, the browser calls the API on the same host that served the frontend.

Local deployment:

```text
http://localhost:8080/api/library
```

Render deployment:

```text
https://springbootlibrarysphinx.onrender.com/api/library
```

This avoids CORS and loopback-address errors such as:

```text
Access to fetch at 'http://localhost:8080/api/library/users/login'
from origin 'https://springbootlibrarysphinx.onrender.com'
has been blocked by CORS policy
```

---

# Aiven MySQL Cloud Database

## What is Aiven?

[Aiven](https://aiven.io/) is a cloud platform for managed open-source data services. In this project it is used to host a **managed MySQL database** in the cloud.

Using Aiven avoids the need to run a MySQL container in Render. This is important because free container platforms are usually designed to run the web application, not to provide durable database storage inside the same container.

Recommended cloud architecture:

```text
Browser
   |
   v
Render Web Service
   |
   v
Spring Boot Docker Container
   |
   v
Aiven Managed MySQL Database
```

## Why use Aiven for this project?

Aiven provides:

- a managed MySQL service
- public connection credentials
- SSL-enabled database access
- a free tier suitable for demos, prototypes, and coursework
- database persistence independent of the Render application container

---

## Create a MySQL database on Aiven

1. Create an Aiven account.
2. Open the Aiven Console.
3. Create or select an Aiven project.
4. Click **Create service**.
5. Select **MySQL**.
6. Select the free tier or another suitable plan.
7. Create the service.
8. Wait until the service status is running.
9. Open the service overview.
10. Copy the connection parameters:

```text
HOST
PORT
DATABASE
USER
PASSWORD
```

For example:

```text
HOST=mysql-d49e810-dipina-2a6e.h.aivencloud.com
PORT=16552
DATABASE=defaultdb
USER=avnadmin
PASSWORD=YOUR_AIVEN_PASSWORD
```

---

## Spring Boot configuration for Aiven

Use this JDBC URL pattern:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://HOST:PORT/DATABASE?sslMode=REQUIRED
```

Example:

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-d49e810-dipina-2a6e.h.aivencloud.com:16552/defaultdb?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=YOUR_AIVEN_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

Use:

```text
sslMode=REQUIRED
```

inside the JDBC URL.

Do not use the MySQL CLI option spelling inside the JDBC URL:

```text
ssl-mode=REQUIRED
```

That form is for the `mysql` command-line client, not for JDBC.

---

# Connecting to Aiven MySQL from the MySQL Client

## Bash / Linux / macOS / Git Bash

Use:

```bash
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb
```

Then enter the password when prompted.

To test the connection with a query:

```bash
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb -e "SELECT 1;"
```

To show databases:

```bash
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb -e "SHOW DATABASES;"
```

## Windows CMD

```bat
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb
```

## PowerShell

```powershell
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb
```

## Security note

Do not put the database password directly in the command line unless strictly necessary. Passing the password inline may expose it in shell history or process listings.

Prefer:

```bash
-p
```

and type the password interactively.

---

# Deploying the Containerized Application to Render

## What is Render?

[Render](https://render.com/) is a cloud platform that can build and run web services directly from a GitHub repository. This project can be deployed to Render using its existing `Dockerfile`.

The local `docker-compose.yml` is useful for local development, but Render should deploy only the Spring Boot container. The database should be external, for example on Aiven.

---

## Render deployment steps

1. Push the repository to GitHub.
2. Create a Render account.
3. Click **New +**.
4. Select **Web Service**.
5. Connect the GitHub repository.
6. Select this repository.
7. Configure the service:
   - Runtime: **Docker**
   - Dockerfile path: `Dockerfile`
   - Branch: `main`
   - Instance type: **Free**, if available
8. Add the environment variables for Aiven:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-d49e810-dipina-2a6e.h.aivencloud.com:16552/defaultdb?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=YOUR_AIVEN_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQLDialect
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

9. Create the service.
10. Wait for Render to build and deploy the container.

After deployment, the app should be available at a URL similar to:

```text
https://springbootlibrarysphinx.onrender.com
```

Access points:

| Service | URL |
|---|---|
| Frontend UI | `https://springbootlibrarysphinx.onrender.com` |
| Swagger UI | `https://springbootlibrarysphinx.onrender.com/swagger-ui.html` |
| OpenAPI JSON | `https://springbootlibrarysphinx.onrender.com/v3/api-docs` |

---

## Source code changes needed for Render

### 1. Java configuration must support environment variables

Render injects configuration as environment variables.

The Java application must therefore resolve:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_JPA_HIBERNATE_DDL_AUTO
SPRING_JPA_SHOW_SQL
```

The `.env` support described earlier is useful locally, but in Render the real Render environment variables should take priority.

### 2. Frontend must use relative API paths

Change:

```javascript
const apiBaseUrl = "http://localhost:8080/api/library";
```

to:

```javascript
const apiBaseUrl = "/api/library";
```

This ensures that the frontend calls the backend on the same Render domain.

### 3. Do not deploy MySQL inside the Render container

The Render service should use the `Dockerfile`, not `docker-compose.yml`.

Use Aiven MySQL externally.

---

# GitHub Actions

This repository already includes workflows for documentation and reports.

| Workflow | File | Purpose |
|---|---|---|
| Sphinx docs | `.github/workflows/sphinx-docs.yml` | Builds and deploys Sphinx HTML + PDF to GitHub Pages |
| Maven site | `.github/workflows/maven-site-integration.yml` | Runs tests, Maven Site, Doxygen, and reports |

Required GitHub repository settings:

1. Go to **Settings → Actions → General**.
2. Enable **Read and write permissions** under Workflow permissions.
3. Go to **Settings → Pages**.
4. Set the source to:
   - Branch: `gh-pages`
   - Folder: `/`

---

# Automatic Deployment to Render with GitHub Actions

The goal is:

```text
Push to main
   |
   v
Build and deploy Sphinx documentation
   |
   v
Run Render deployment workflow
   |
   v
Render rebuilds and deploys the Spring Boot Docker container
```

The Render deployment workflow should run **only after** the Sphinx documentation workflow has completed successfully.

This is achieved with the `workflow_run` trigger.

---

## 1. Create a Render deploy hook

In Render:

1. Open the Render web service.
2. Go to **Settings**.
3. Locate **Deploy Hook**.
4. Copy the deploy hook URL.

It will look similar to:

```text
https://api.render.com/deploy/srv-xxxxxxxxxxxxxxxxxxxx?key=yyyyyyyyyyyyyyyy
```

This URL triggers a new Render deployment.

---

## 2. Add the Render deploy hook to GitHub Secrets

In GitHub:

1. Open the repository.
2. Go to **Settings → Secrets and variables → Actions**.
3. Click **New repository secret**.
4. Create this secret:

```text
RENDER_DEPLOY_HOOK
```

5. Paste the Render deploy hook URL as the value.

---

## 3. Create `.github/workflows/deploy-render.yml`

Create a new file:

```text
.github/workflows/deploy-render.yml
```

with this content:

```yaml
name: Deploy to Render

on:
  workflow_run:
    workflows:
      - Build and Deploy Sphinx Docs
    types:
      - completed
    branches:
      - main
  workflow_dispatch:

jobs:
  deploy-render:
    name: Deploy Spring Boot application to Render
    runs-on: ubuntu-latest

    if: >
      github.event_name == 'workflow_dispatch' ||
      github.event.workflow_run.conclusion == 'success'

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
          cache: maven

      - name: Run unit tests
        run: mvn test

      - name: Build application
        run: mvn -DskipTests package

      - name: Trigger Render deployment
        run: |
          curl --fail --request POST "${{ secrets.RENDER_DEPLOY_HOOK }}"
```

---

## Explanation of `deploy-render.yml`

### Workflow name

```yaml
name: Deploy to Render
```

This is the name shown in the GitHub Actions UI.

### Trigger after Sphinx documentation

```yaml
on:
  workflow_run:
    workflows:
      - Build and Deploy Sphinx Docs
    types:
      - completed
    branches:
      - main
```

This means the Render workflow is triggered only after the workflow named:

```text
Build and Deploy Sphinx Docs
```

has completed on the `main` branch.

The name must match the `name:` value inside:

```text
.github/workflows/sphinx-docs.yml
```

### Manual trigger

```yaml
workflow_dispatch:
```

This allows the Render deployment to be launched manually from the GitHub Actions tab.

### Run only if Sphinx succeeded

```yaml
if: >
  github.event_name == 'workflow_dispatch' ||
  github.event.workflow_run.conclusion == 'success'
```

This prevents a Render deployment if Sphinx failed.

Manual runs are still allowed.

### Test before deployment

```yaml
- name: Run unit tests
  run: mvn test
```

This ensures the application is tested before the deployment is triggered.

### Build before deployment

```yaml
- name: Build application
  run: mvn -DskipTests package
```

This confirms that the application can be packaged successfully.

### Trigger Render

```yaml
- name: Trigger Render deployment
  run: |
    curl --fail --request POST "${{ secrets.RENDER_DEPLOY_HOOK }}"
```

This calls the Render deploy hook. Render then pulls the repository, builds the Docker image using the `Dockerfile`, and redeploys the service.

---

## Recommended final workflow directory

```text
.github/workflows/
├── sphinx-docs.yml
├── maven-site-integration.yml
└── deploy-render.yml
```

---

## Manual trigger of the Sphinx workflow

```bash
curl -X POST \
  https://api.github.com/repos/dipina/SpringBootLibrarySphinx/actions/workflows/sphinx-docs.yml/dispatches \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer YOUR_PAT_HERE" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  -d '{"ref":"main"}'
```

When the Sphinx workflow completes successfully, the Render deployment workflow will run automatically.

---

# Testing

## Unit tests

```bash
mvn test
```

## Specific tests

```bash
mvn -Dtest=UserServiceTest,BookServiceTest test
```

## Integration tests

Integration tests require MySQL to be running:

```bash
mvn -Pintegration integration-test
```

## Performance tests

```bash
mvn -Pperformance integration-test
```

## JaCoCo coverage report

```bash
mvn clean test jacoco:report
```

Open:

```text
target/site/jacoco/index.html
```

## Full Maven site

```bash
mvn site
```

Open:

```text
target/site/index.html
```

---

# Generating Documentation Locally

## Sphinx HTML

```bash
cd docs-sphinx
make html
```

On Windows:

```bat
cd docs-sphinx
.\make.bat html
```

Open:

```text
docs-sphinx/_build/html/index.html
```

## Sphinx PDF

The PDF is generated automatically in CI using WeasyPrint.

To generate it locally:

```bash
pip install weasyprint
cd docs-sphinx
make html
weasyprint _build/html/index.html _build/SpringBootLibrarySphinx.pdf --base-url _build/html/
```

## Doxygen and Maven Site

```bash
mvn test jacoco:report
mvn -Pperformance integration-test
mvn -Pperformance resources:copy-resources@copy-perf-report
mvn site
mvn post-site
```

Open:

```text
docs/site/index.html
```

Richly documented classes include:

- `BorrowingController.java`
- `BookService.java`
- `Borrowing.java`

---

# Troubleshooting

## Spring Boot still connects to localhost

Symptom:

```text
Communications link failure
Connection refused
```

Check whether the app is still using:

```text
jdbc:mysql://localhost:3306/libraryapidb
```

Fixes:

1. Ensure `.env` exists in the project root.
2. Ensure the Java dotenv loader is present.
3. Ensure environment variable names are correct.
4. Ensure the Aiven JDBC URL uses `sslMode=REQUIRED`.
5. Run:

```bash
mvn spring-boot:run
```

from the project root.

---

## Browser tries to call localhost from Render

Symptom:

```text
Access to fetch at 'http://localhost:8080/api/library/users/login'
from origin 'https://springbootlibrarysphinx.onrender.com'
has been blocked by CORS policy
```

Fix:

```javascript
const apiBaseUrl = "/api/library";
```

Then commit and redeploy:

```bash
git add src/main/resources/static/scripts.js
git commit -m "Use relative API path for cloud deployment"
git push
```

After deployment, clear the browser cache or use an incognito window.

---

## Cannot connect to Aiven from mysql client

Test:

```bash
mysql -h mysql-d49e810-dipina-2a6e.h.aivencloud.com -P 16552 -u avnadmin -p --ssl-mode=REQUIRED defaultdb -e "SELECT 1;"
```

Check:

1. Host is correct.
2. Port is correct.
3. Username is correct.
4. Password is correct.
5. Aiven service is running.
6. SSL mode is enabled.

---

## Render deployment does not start from GitHub Actions

Check:

1. The secret `RENDER_DEPLOY_HOOK` exists.
2. The deploy hook URL is correct.
3. The Sphinx workflow completed successfully.
4. The workflow name in `deploy-render.yml` exactly matches:

```text
Build and Deploy Sphinx Docs
```

5. The workflow is running on branch `main`.

---

## Render starts but the app fails

Check Render logs for:

- missing environment variables
- wrong Aiven database password
- wrong JDBC URL
- database service not running
- insufficient free-tier resources

---

# Profiling with VisualVM

Add this JVM option to the `spring-boot-maven-plugin` configuration if required:

```xml
-Xverify:none
```

Then run:

```bash
mvn spring-boot:run
```

and connect VisualVM through the **Local** tab.

---

# Files to Delete from the Repo Root

The following files at the repository root are leftovers and should be removed if still present:

```bash
git rm sphinx-docs.yml
git rm pom-javadoc-snippet.xml
```

The valid workflow location is:

```text
.github/workflows/
```
