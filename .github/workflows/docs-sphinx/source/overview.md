# Project Overview

## Purpose

**SpringBootLibrarySphinx** is an exemplary Spring Boot application developed for the
**SPQ (Software Process and Quality)** subject. It demonstrates best practices in:

- RESTful API design with Spring Boot
- Layered MVC architecture
- Automated testing (unit, integration, performance)
- CI/CD pipelines with GitHub Actions
- Code quality tooling (JaCoCo, PMD, Checkstyle, JDepend)
- Technical documentation (Doxygen, Maven Site, **Sphinx**)

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
| Landing page | https://dipina.github.io/SpringBootLibrarySphinx/ |
| This Sphinx hub | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/index.html |
| Sphinx PDF | https://dipina.github.io/SpringBootLibrarySphinx/sphinx/SpringBootLibrarySphinx.pdf |
| Javadoc | https://dipina.github.io/SpringBootLibrarySphinx/site/apidocs/index.html |
| JaCoCo coverage | https://dipina.github.io/SpringBootLibrarySphinx/site/jacoco/index.html |
| Surefire unit tests | https://dipina.github.io/SpringBootLibrarySphinx/site/surefire-report.html |
| Performance tests | https://dipina.github.io/SpringBootLibrarySphinx/site/reports/perf-report.html |
| Checkstyle | https://dipina.github.io/SpringBootLibrarySphinx/site/checkstyle.html |
| PMD | https://dipina.github.io/SpringBootLibrarySphinx/site/pmd.html |
| Doxygen | https://dipina.github.io/SpringBootLibrarySphinx/doxygen/html/index.html |
| Swagger UI | http://localhost:8080/swagger-ui.html (local only) |

## Repository Structure

```
SpringBootLibrarySphinx/
├── .github/workflows/
│   ├── sphinx-docs.yml              ← Sphinx + PDF → GitHub Pages
│   └── maven-site-integration.yml  ← Build, test, Maven site → GitHub Pages
├── docs-sphinx/                     ← All Sphinx source files
│   ├── index.html                   ← Landing page (committed, readable HTML)
│   ├── Makefile                     ← Linux/macOS build
│   ├── make.bat                     ← Windows build
│   ├── requirements.txt             ← Python dependencies
│   └── source/
│       ├── conf.py
│       ├── index.rst
│       ├── overview.md
│       ├── architecture.md
│       ├── getting_started.md
│       ├── testing.md
│       ├── reports.md
│       ├── api_rest.md
│       ├── javadoc_link.md
│       ├── cicd.md
│       ├── docker.md
│       └── sphinx_101.md
├── src/                             ← Java source code
├── pom.xml                          ← Maven build
└── README.md
```
