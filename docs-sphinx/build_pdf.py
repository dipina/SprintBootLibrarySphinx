#!/usr/bin/env python3
"""
build_pdf.py  —  docs-sphinx/build_pdf.py

Combines every Sphinx HTML page (in toctree order) into a single HTML file
and converts it to PDF using WeasyPrint.

Run from the repo root:
    python3 docs-sphinx/build_pdf.py

Output:
    docs-sphinx/_build/SpringBootLibrarySphinx.pdf
"""

import os
import re
from pathlib import Path
from bs4 import BeautifulSoup

# ── Configuration ─────────────────────────────────────────────────────────────

BUILD_DIR  = Path("docs-sphinx/_build/html")
OUTPUT_PDF = Path("docs-sphinx/_build/SpringBootLibrarySphinx.pdf")
TITLE      = "SpringBootLibrarySphinx Documentation"

# Pages in toctree order (matches docs-sphinx/source/index.rst)
PAGES = [
    "index.html",
    "overview.html",
    "architecture.html",
    "getting_started.html",
    "testing.html",
    "reports.html",
    "api_rest.html",
    "javadoc_link.html",
    "cicd.html",
    "docker.html",
    "sphinx_101.html",
]

# ── CSS ───────────────────────────────────────────────────────────────────────

def make_css() -> str:
    """Return self-contained print CSS. No external @import — avoids file URI issues."""
    return """
@page {
    size: A4;
    margin: 2cm 2.5cm 2.5cm 2.5cm;
    @bottom-center {
        content: "SpringBootLibrarySphinx  ·  page " counter(page) " / " counter(pages);
        font-size: 9pt;
        color: #888;
    }
}

body {
    font-family: "Segoe UI", Arial, sans-serif;
    font-size: 11pt;
    line-height: 1.6;
    color: #2c3e50;
    background: white;
}

/* Each chapter starts on a new PDF page */
.sphinx-page {
    page-break-before: always;
}
.sphinx-page:first-child {
    page-break-before: avoid;
}

/* Cover page */
#cover {
    text-align: center;
    padding-top: 8cm;
    page-break-after: always;
}
#cover h1 {
    font-size: 28pt;
    color: #1a252f;
    margin-bottom: 0.4cm;
}
#cover .subtitle {
    font-size: 13pt;
    color: #6c7a89;
    margin-top: 0.3cm;
}
#cover .meta {
    margin-top: 2cm;
    font-size: 9pt;
    color: #aaa;
}

/* Headings */
h1 {
    font-size: 20pt;
    color: #1a252f;
    border-bottom: 2px solid #2980b9;
    padding-bottom: 4pt;
    margin-top: 18pt;
    margin-bottom: 8pt;
}
h2 {
    font-size: 15pt;
    color: #2c3e50;
    margin-top: 14pt;
    margin-bottom: 6pt;
}
h3 {
    font-size: 12pt;
    color: #34495e;
    margin-top: 10pt;
    margin-bottom: 4pt;
}
h4 {
    font-size: 11pt;
    color: #555;
    margin-top: 8pt;
}

/* Code blocks */
pre {
    font-family: "Consolas", "Courier New", monospace;
    font-size: 9pt;
    background: #f6f8fa;
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 8pt 10pt;
    white-space: pre-wrap;
    word-break: break-word;
    page-break-inside: avoid;
}
code {
    font-family: "Consolas", "Courier New", monospace;
    font-size: 9.5pt;
    background: #f0f0f0;
    border-radius: 3px;
    padding: 1pt 4pt;
}
pre code {
    background: none;
    padding: 0;
}

/* Tables */
table {
    border-collapse: collapse;
    width: 100%;
    font-size: 10pt;
    page-break-inside: avoid;
    margin: 8pt 0;
}
th {
    background: #2980b9;
    color: white;
    padding: 5pt 8pt;
    text-align: left;
    font-weight: bold;
}
td {
    padding: 4pt 8pt;
    border-bottom: 1px solid #ddd;
    vertical-align: top;
}
tr:nth-child(even) td {
    background: #f9f9f9;
}

/* Admonitions (note, warning, important, tip) */
.note, .warning, .important, .tip, .admonition {
    border-left: 4px solid #2980b9;
    padding: 6pt 10pt;
    margin: 8pt 0;
    background: #eaf4fb;
    page-break-inside: avoid;
}
.warning { border-color: #e67e22; background: #fef5e7; }
.important, .danger { border-color: #c0392b; background: #fdf2f2; }
.tip { border-color: #27ae60; background: #eafaf1; }

/* Inline links: show URL after the text for print readability */
a { color: #2980b9; text-decoration: none; }
a[href^="http"]::after {
    content: " (" attr(href) ")";
    font-size: 8pt;
    color: #888;
    word-break: break-all;
}
/* But don't show URLs for internal anchor links */
a[href^="#"]::after { content: ""; }

/* Suppress all Sphinx navigation chrome */
.wy-nav-side, .wy-nav-top, .rst-footer-buttons,
.wy-breadcrumbs, .wy-breadcrumbs-aside,
.related, nav, footer, .toctree-wrapper { display: none !important; }
"""

# ── Content extraction ────────────────────────────────────────────────────────

def extract_body(html_path: Path) -> str:
    """Extract the main documentation content from a Sphinx HTML page."""
    soup = BeautifulSoup(html_path.read_text(encoding="utf-8"), "html.parser")

    # Sphinx RTD theme wraps page content in <div role="main">
    main = (
        soup.find("div", role="main")
        or soup.find("div", class_="body")
        or soup.find("article")
    )
    if not main:
        return ""

    # Remove ¶ permalink anchors (clutter in PDF)
    for a in main.find_all("a", class_="headerlink"):
        a.decompose()

    # Remove toctree navigation blocks (just lists of links, not content)
    for div in main.find_all("div", class_="toctree-wrapper"):
        div.decompose()

    return str(main)

# ── Main ──────────────────────────────────────────────────────────────────────

def build() -> None:
    try:
        from weasyprint import HTML, CSS
    except ImportError:
        raise SystemExit(
            "WeasyPrint not installed. Run:  pip install weasyprint beautifulsoup4"
        )

    print(f"Building PDF from {len(PAGES)} pages...")

    # Cover page
    sections = ["""
<div id="cover">
  <h1>SpringBootLibrarySphinx</h1>
  <p class="subtitle">Technical Documentation</p>
  <p class="meta">Generated automatically by GitHub Actions · dipina</p>
</div>
"""]

    # Content pages
    for page_name in PAGES:
        page_path = BUILD_DIR / page_name
        if not page_path.exists():
            print(f"  WARNING: {page_name} not found — skipping")
            continue
        print(f"  + {page_name}")
        body = extract_body(page_path)
        if body:
            sections.append(f'<div class="sphinx-page">{body}</div>')

    # Assemble single HTML document
    combined = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>{TITLE}</title>
</head>
<body>
{"".join(sections)}
</body>
</html>"""

    # Resolve absolute base URL so WeasyPrint can find local assets
    base_url = BUILD_DIR.resolve().as_uri() + "/"

    print(f"Rendering PDF → {OUTPUT_PDF}")
    HTML(string=combined, base_url=base_url).write_pdf(
        str(OUTPUT_PDF),
        stylesheets=[CSS(string=make_css())],
        presentational_hints=True,
    )

    size_kb = OUTPUT_PDF.stat().st_size // 1024
    print(f"Done — {size_kb} KB")


if __name__ == "__main__":
    build()
