# Sphinx 101 — Complete Guide

## What Is Sphinx?

**Sphinx** is an open-source documentation generator originally created for the Python
language reference documentation. Today it is widely used across languages — including
Java, C++, and JavaScript projects — whenever developers need to produce structured,
professional, multi-page technical documentation from plain text source files.

Sphinx takes files written in **reStructuredText (`.rst`)** or **Markdown (`.md`, via
the MyST parser extension)** and generates static HTML websites, PDF documents, ePub
e-books, and man pages.

---

## What Is Sphinx Useful For?

| Use Case | Why Sphinx |
|---|---|
| **Project portals** | Aggregates API docs, guides, reports, and tutorials in one navigable site |
| **Tutorials & how-tos** | Numbered sections, admonition boxes, code blocks with syntax highlighting |
| **Search** | Built-in full-text client-side search (no server needed) |
| **Cross-references** | `:ref:`, `:doc:` directives create typed links that break at build time if the target disappears |
| **Theming** | Dozens of themes (`sphinx_rtd_theme`, `furo`, `pydata_sphinx_theme`) |

### Sphinx vs. Alternatives

| Tool | Primary audience | Markup | Best for |
|---|---|---|---|
| **Sphinx** | Python / polyglot | RST / Markdown | Comprehensive project portals |
| **Javadoc** | Java | Javadoc tags | API reference only |
| **Doxygen** | C/C++/Java | Doxygen tags | Cross-referenced code reference |
| **MkDocs** | Any | Markdown | Simple Markdown-first sites |

---

## How Is Sphinx Used?

### 1. Install Sphinx

```bash
pip install sphinx sphinx-rtd-theme myst-parser
```

Or pin versions in `requirements.txt`:

```
sphinx==7.4.7
sphinx-rtd-theme==2.0.0
myst-parser==3.0.1
```

### 2. Initialise a Project

```bash
sphinx-quickstart docs-sphinx
```

### 3. Source File Structure

```
docs-sphinx/
├── source/
│   ├── conf.py        ← Sphinx configuration
│   ├── index.rst      ← Master table of contents
│   ├── overview.md    ← Page in Markdown
│   └── _static/       ← Custom CSS / images
├── Makefile
├── make.bat           ← Windows equivalent of Makefile
└── requirements.txt
```

### 4. The `toctree` Directive

```rst
.. toctree::
   :maxdepth: 2
   :caption: Project Overview

   overview
   architecture
   getting_started
```

### 5. Build the Documentation

```bash
# Linux / macOS
cd docs-sphinx
make html

# Windows
cd docs-sphinx
.\make.bat html
```

### 6. Key reStructuredText Syntax

````rst
Section Heading
===============

Sub-heading
-----------

**bold**, *italic*, ``inline code``

.. code-block:: java

   public class Hello { }

.. note::
   This is a highlighted information box.
````

### 7. Markdown with MyST

````markdown
# My Page

**bold**, *italic*

```java
public class Hello { }
```

```{note}
MyST admonition box.
```
````

---

## How to Integrate Sphinx with GitHub Actions

```yaml
# .github/workflows/sphinx-docs.yml
name: Build and Deploy Sphinx Docs

on:
  push:
    branches: [main]
  workflow_dispatch:

permissions:
  contents: write

jobs:
  sphinx:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: '3.12'
      - run: pip install -r docs-sphinx/requirements.txt
      - run: cd docs-sphinx && make html
      - uses: peaceiris/actions-gh-pages@v4
        with:
          github_token: ${{ secrets.GITHUB_TOKEN }}
          publish_dir: ./docs-sphinx/_build/html
          destination_dir: sphinx
          keep_files: true
          enable_jekyll: false
```

**Required repository settings:**

1. **Settings → Actions → General → Workflow permissions** → *Read and write permissions*
2. **Settings → Pages** → Source: `gh-pages` branch, folder `/`

---

## How This Project Uses Sphinx

### Documentation Architecture

```
gh-pages branch (GitHub Pages)
├── index.html                      ← Landing page (card grid)
├── sphinx/                         ← Sphinx portal
│   ├── index.html
│   ├── overview.html
│   ├── reports.html                ← Links to all quality reports
│   └── sphinx_101.html             ← This page
├── site/                           ← Maven Site output
│   ├── apidocs/                    ← Javadoc
│   ├── jacoco/                     ← JaCoCo coverage
│   ├── surefire.html               ← Unit test results
│   ├── reports/perf-report.html    ← Performance report
│   ├── pmd.html
│   ├── cpd.html
│   ├── checkstyle.html
│   └── jdepend-report.html
└── doxygen/html/                   ← Doxygen cross-reference
```

### Integration Points

| External Artefact | Sphinx page | Published path |
|---|---|---|
| Javadoc | `javadoc_link.md` | `site/apidocs/index.html` |
| JaCoCo | `reports.md` | `site/jacoco/index.html` |
| Surefire | `reports.md` | `site/surefire.html` |
| PMD / Checkstyle | `reports.md` | `site/pmd.html` |
| Performance report | `reports.md` | `site/reports/perf-report.html` |
| Doxygen | `javadoc_link.md` | `doxygen/html/index.html` |

### Adding a New Page

1. Create `docs-sphinx/source/my_page.md`
2. Add `my_page` to the relevant `.. toctree::` in `index.rst`
3. Push to `main` — the workflow rebuilds and redeploys automatically

---

## Useful Resources

| Resource | URL |
|---|---|
| Official Sphinx docs | https://www.sphinx-doc.org |
| MyST Markdown parser | https://myst-parser.readthedocs.io |
| Read the Docs theme | https://sphinx-rtd-theme.readthedocs.io |
| Furo theme (modern) | https://pradyunsg.me/furo |
| peaceiris/actions-gh-pages | https://github.com/peaceiris/actions-gh-pages |
