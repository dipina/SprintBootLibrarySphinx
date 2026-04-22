# Project Overview

## Purpose

**SprintBootLibrarySphinx** is an exemplary Spring Boot application developed for the
**SPQ (Software Process and Quality)** subject. It demonstrates best practices in RESTful
API design, layered architecture, automated testing, CI/CD pipelines, code quality tooling,
and technical documentation.

The application models a **library borrowing system** where users can borrow and return
books through a shared platform.

## Key Features

| Feature | Technology |
|---|---|
| REST API backend | Spring Boot 3.4, Spring MVC |
| Database ORM | Spring Data JPA + Hibernate |
| Database | MySQL (prod), H2 (test) |
| API docs (interactive) | Springdoc OpenAPI / Swagger UI |
| Unit tests | JUnit 5 + Mockito |
| Integration tests | Spring Boot Test + MySQL |
| Performance tests | JUnitPerf |
| Code coverage | JaCoCo |
| Static analysis | PMD, Checkstyle, JDepend |
| Containerisation | Docker + Docker Compose |
| Technical docs | Doxygen, Maven Site, Sphinx |

## Live Documentation Links

| Document | URL |
|---|---|
| This portal (landing page) | https://dipina.github.io/SprintBootLibrarySphinx/ |
| Sphinx hub | https://dipina.github.io/SprintBootLibrarySphinx/sphinx/index.html |
| Javadoc | https://dipina.github.io/SprintBootLibrarySphinx/site/apidocs/index.html |
| JaCoCo coverage | https://dipina.github.io/SprintBootLibrarySphinx/site/jacoco/index.html |
| Surefire unit tests | https://dipina.github.io/SprintBootLibrarySphinx/site/surefire.html |
| Performance tests | https://dipina.github.io/SprintBootLibrarySphinx/site/reports/perf-report.html |
| Checkstyle | https://dipina.github.io/SprintBootLibrarySphinx/site/checkstyle.html |
| PMD | https://dipina.github.io/SprintBootLibrarySphinx/site/pmd.html |
| Doxygen | https://dipina.github.io/SprintBootLibrarySphinx/doxygen/html/index.html |
| Swagger UI | http://localhost:8080/swagger-ui.html (local only) |

## Repository Structure

```
SprintBootLibrarySphinx/
├── .github/workflows/
│   ├── sphinx-docs.yml              ← Sphinx → GitHub Pages
│   └── maven-site-integration.yml  ← Build, test, Maven site → GitHub Pages
├── docs-sphinx/                     ← Sphinx source
│   ├── source/
│   │   ├── conf.py
│   │   ├── index.rst
│   │   ├── overview.md
│   │   ├── architecture.md
│   │   ├── getting_started.md
│   │   ├── testing.md
│   │   ├── reports.md
│   │   ├── api_rest.md
│   │   ├── javadoc_link.md
│   │   ├── cicd.md
│   │   ├── docker.md
│   │   └── sphinx_101.md
│   ├── Makefile
│   ├── make.bat
│   └── requirements.txt
├── src/
├── pom.xml
└── README.md
```
