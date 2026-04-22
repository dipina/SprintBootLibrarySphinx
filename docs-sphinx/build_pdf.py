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

BUILD_DIR   = Path("docs-sphinx/_build/html")
OUTPUT_PDF  = Path("docs-sphinx/_build/SpringBootLibrarySphinx.pdf")
TITLE       = "SpringBootLibrarySphinx Documentation"
AUTHOR      = "dipina"

# Pages in the order they appear in the Sphinx toctree (matches index.rst)
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

# ── Helpers ───────────────────────────────────────────────────────────────────

def extract_body(html_path: Path) -> str:
    """Return the main content div from a Sphinx HTML page, cleaned up."""
    soup = BeautifulSoup(html_path.read_text(encoding="utf-8"), "html.parser")

    # Sphinx RTD theme wraps content in <div role="main">
    main = soup.find("div", role="main")
    if not main:
        # Fallback: <div class="body"> or <article>
        main = soup.find("div", class_="body") or soup.find("article")
    if not main:
        return ""

    # Remove navigation arrows (prev/next), search boxes, and footer chrome
    for tag in main.find_all(["div"], class_=re.compile(
            r"(footer|prev-next|navigation|headerlink|toctree-wrapper|admonition-todo)")):
        tag.decompose()

    # Remove the "headerlink" ¶ anchors but keep the heading text
    for a in main.find_all("a", class_="headerlink"):
        a.decompose()

    return str(main)


def make_css(build_dir: Path) -> str:
    """Return CSS that makes the combined HTML print well."""
    # Embed the Sphinx RTD stylesheet by path so WeasyPrint can find it
    css_path = build_dir / "_static" / "css" / "theme.css"
    embed = ""
    if css_path.exists():
        embed = f"@import url('{css_path.as_uri()}');\n"

    return embed + """
/* ── PDF layout ── */
@page {
    size: A4;
    margin: 2cm 2.5cm 2.5cm 2.5cm;
    @bottom-center {
        content: "SpringBootLibrarySphinx Documentation  ·  " counter(page) " / " counter(pages);
        font-size: 9pt;
        color: #888;
    }
}
body {
    font-family: "Segoe UI", Arial, sans-serif;
    font-size: 11pt;
    line-height: 1.6;
    color: #2c3e50;
}
/* Each page section starts on a new PDF page */
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
    margin-bottom: 0.5cm;
}
#cover p {
    font-size: 13pt;
    color: #6c7a89;
}
/* Headings */
h1 { font-size: 20pt; color: #1a252f; border-bottom: 2px solid #2980b9;
     padding-bottom: 4pt; margin-top: 18pt; }
h2 { font-size: 15pt; color: #2c3e50; margin-top: 14pt; }
h3 { font-size: 12pt; color: #34495e; margin-top: 10pt; }
h4 { font-size: 11pt; color: #555; margin-top: 8pt; }
/* Code */
pre, code {
    font-family: "Consolas", "Courier New", monospace;
    font-size: 9.5pt;
    background: #f4f4f4;
    border: 1px solid #ddd;
    border-radius: 3px;
}
pre {
    padding: 8pt;
    white-space: pre-wrap;
    word-break: break-word;
    page-break-inside: avoid;
}
code { padding: 1pt 4pt; }
/* Tables */
table {
    border-collapse: collapse;
    width: 100%;
    font-size: 10pt;
    page-break-inside: avoid;
}
th {
    background: #2980b9;
    color: white;
    padding: 5pt 8pt;
    text-align: left;
}
td {
    padding: 4pt 8pt;
    border-bottom: 1px solid #ddd;
}
tr:nth-child(even) td { background: #f9f9f9; }
/* Admonitions */
.note, .warning, .important, .tip {
    border-left: 4px solid #2980b9;
    padding: 8pt 12pt;
    margin: 10pt 0;
    background: #eaf4fb;
    page-break-inside: avoid;
}
.warning { border-color: #e67e22; background: #fef5e7; }
.important { border-color: #c0392b; background: #fdf2f2; }
/* Links — show URL in parentheses for external links */
a { color: #2980b9; text-decoration: none; }
a[href^="http"]:after {
    content: " (" attr(href) ")";
    font-size: 8pt;
    color: #888;
}
/* Hide sidebar navigation, search, and other UI chrome */
.wy-nav-side, .wy-nav-top, .rst-footer-buttons,
.wy-breadcrumbs, .wy-breadcrumbs-aside,
nav, footer, .related { display: none !important; }
"""


def build() -> None:
    try:
        from weasyprint import HTML, CSS
    except ImportError:
        raise SystemExit("WeasyPrint not installed. Run: pip install weasyprint beautifulsoup4")

    print(f"Building PDF from {len(PAGES)} pages...")

    # ── Cover page ──
    sections = ["""
<div id="cover">
  <h1>SpringBootLibrarySphinx</h1>
  <p>Technical Documentation</p>
  <p style="margin-top:1cm;font-size:10pt;color:#aaa;">Generated automatically by GitHub Actions</p>
</div>
"""]

    # ── Content pages ──
    for page_name in PAGES:
        page_path = BUILD_DIR / page_name
        if not page_path.exists():
            print(f"  WARNING: {page_name} not found, skipping")
            continue
        print(f"  + {page_name}")
        body = extract_body(page_path)
        if body:
            sections.append(f'<div class="sphinx-page">{body}</div>')

    # ── Assemble combined HTML ──
    combined_html = f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <title>{TITLE}</title>
</head>
<body>
{"".join(sections)}
</body>
</html>"""

    # ── Render PDF ──
    css = CSS(string=make_css(BUILD_DIR))
    base_url = BUILD_DIR.resolve().as_uri() + "/"

    print(f"Rendering PDF → {OUTPUT_PDF}")
    HTML(string=combined_html, base_url=base_url).write_pdf(
        str(OUTPUT_PDF),
        stylesheets=[css],
        presentational_hints=True,
    )
    size_kb = OUTPUT_PDF.stat().st_size // 1024
    print(f"Done — {size_kb} KB")


if __name__ == "__main__":
    build()
