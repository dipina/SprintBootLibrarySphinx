# Library Borrowing System exemplary project for SPQ subject — CI & Sphinx

## Purpose

This application allows users to borrow and return books seamlessly through a shared platform.
Users can manage book availability, track borrowed books, and return them, facilitating an
efficient book-sharing system.

## Documentation

| Section | URL |
|---|---|
| **Documentation portal** (landing page) | https://dipina.github.io/SpringBootLibrarySphinx/ |
| **Sphinx hub** (architecture, guides, CI/CD, Sphinx 101) | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/index.html |
| **Sphinx PDF** (offline reading) | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/SpringBootLibrarySphinx.pdf |
| Doxygen cross-reference | https://dipina.github.io/SpringBootLibrarySphinx/doxygen/html/ |
| Javadoc API reference | https://dipina.github.io/SpringBootLibrarySphinx/site/apidocs/index.html |
| JaCoCo coverage | https://dipina.github.io/SpringBootLibrarySphinx/site/jacoco/index.html |
| Surefire unit test report | https://dipina.github.io/SpringBootLibrarySphinx/site/surefire-report.html |
| Performance test report | https://dipina.github.io/SpringBootLibrarySphinx/site/reports/perf-report.html |
| Checkstyle | https://dipina.github.io/SpringBootLibrarySphinx/site/checkstyle.html |
| PMD | https://dipina.github.io/SpringBootLibrarySphinx/site/pmd.html |
| CPD (copy-paste) | https://dipina.github.io/SpringBootLibrarySphinx/site/cpd.html |
| JDepend | https://dipina.github.io/SpringBootLibrarySphinx/site/jdepend-report.html |
| Full Maven site | https://dipina.github.io/SpringBootLibrarySphinx/site/ |

## Architecture Overview

The application is built using **Spring Boot**, following the **MVC (Model-View-Controller)** pattern:

- **Model** — entities and database interaction via JPA
- **View** — frontend in HTML + JavaScript (served as static resources)
- **Controller** — REST endpoints handling HTTP requests and business logic

### Key Components

- `src/main/java/com/example/library/controller/` — RESTful controllers
- `src/main/java/com/example/library/service/` — business logic
- `src/main/java/com/example/library/repository/` — Spring Data JPA interfaces
- `src/main/java/com/example/library/model/` — JPA entity definitions
- `src/main/resources/static/` — HTML, JavaScript, and CSS for the UI
- `src/main/resources/application.properties` — database and server configuration

API documentation is generated automatically using **Springdoc OpenAPI** (Swagger UI).

For a detailed breakdown, refer to [HOWTO_SPRINGBOOT.md](HOWTO_SPRINGBOOT.md).

## Running the Application

```bash
# 1. Create the database
mysql -u root -p < src/main/resources/dbsetup.sql

# 2. Configure database credentials in src/main/resources/application.properties

# 3. Run
mvn spring-boot:run
# or skip tests for a fast start:
mvn -DskipTests spring-boot:run
```

Access points:

| Endpoint | URL |
|---|---|
| Frontend UI | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

## Testing

```bash
# Unit tests (excludes integration and performance)
mvn test

# Specific tests
mvn -Dtest=UserServiceTest,BookServiceTest test

# Integration tests (requires MySQL running)
mvn -Pintegration integration-test

# Performance tests
mvn -Pperformance integration-test

# JaCoCo coverage report
mvn clean test jacoco:report
# Open: target/site/jacoco/index.html

# Full Maven site (all reports)
mvn site
# Open: target/site/index.html
```

## Generating Documentation Locally

### Sphinx

```bash
cd docs-sphinx

# Windows
.\make.bat html

# Linux / macOS
make html

# Open: docs-sphinx/_build/html/index.html
```

### Sphinx PDF

The PDF is generated automatically in CI using WeasyPrint (converts Sphinx HTML to PDF).
To generate locally:

```bash
pip install weasyprint
cd docs-sphinx
make html
weasyprint _build/html/index.html _build/SpringBootLibrarySphinx.pdf --base-url _build/html/
```

### Doxygen and Maven Site

```bash
# Unit tests + JaCoCo
mvn test jacoco:report

# Performance tests + copy report
mvn -Pperformance integration-test
mvn -Pperformance resources:copy-resources@copy-perf-report

# Full Maven site (Javadoc, Doxygen, PMD, Checkstyle, Surefire, JaCoCo, JDepend)
mvn site

# Copy all output to docs/ for local preview (docs/ is in .gitignore)
mvn post-site
# Open: docs/site/index.html
```

Richly documented classes (Doxygen style):

- `BorrowingController.java`
- `BookService.java`
- `Borrowing.java`

## CI/CD with GitHub Actions

Two workflows run on every push to `main`:

| Workflow | File | What it does |
|---|---|---|
| **Sphinx docs** | `.github/workflows/sphinx-docs.yml` | Builds Sphinx HTML + PDF, deploys to `gh-pages/sphinx/` and writes the landing page |
| **Maven site** | `.github/workflows/maven-site-integration.yml` | Runs tests, generates Maven site + Doxygen, deploys to `gh-pages/` root |

**Required settings** (one-time, in the GitHub repo):

- Settings → Actions → General → Workflow permissions → **Read and write permissions**
- Settings → Pages → Source: **`gh-pages` branch**, folder `/`

Trigger a workflow manually via API:

```bash
curl -X POST \
  https://api.github.com/repos/dipina/SpringBootLibrarySphinx/actions/workflows/sphinx-docs.yml/dispatches \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer YOUR_PAT_HERE" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  -d '{"ref":"main"}'
```

## Docker

```bash
# Start app + MySQL
docker-compose up

# Rebuild after code changes
docker-compose up --build
```

See [docker_essentials_with_compose.md](docker_essentials_with_compose.md) for full details.

## Profiling with VisualVM

Add this to `spring-boot-maven-plugin` configuration in `pom.xml`:

```xml
<jvmArguments>-Xverify:none</jvmArguments>
```

Then run `mvn spring-boot:run` and connect VisualVM via the **Local** tab.

## Files to Delete from the Repo Root

The following files at the repo root are leftovers and should be removed:

```bash
git rm sphinx-docs.yml          # was misplaced; correct location: .github/workflows/
git rm pom-javadoc-snippet.xml  # content has been merged into pom.xml
```
