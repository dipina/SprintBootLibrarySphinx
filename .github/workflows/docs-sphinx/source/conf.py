# Configuration file for the Sphinx documentation builder.
# SpringBootLibrarySphinx — docs-sphinx/source/conf.py

# -- Project information -----------------------------------------------------
project = 'SpringBootLibrarySphinx'
copyright = '2026, dipina'
author = 'dipina'
release = '0.0.1-SNAPSHOT'

# -- General configuration ---------------------------------------------------
extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.viewcode',
    'sphinx.ext.napoleon',
    'myst_parser',
    'sphinx_rtd_theme',
    'rinoh.frontend.sphinx',   # PDF builder — handles MyST Markdown natively
]

templates_path = ['_templates']
exclude_patterns = []

# Support both .rst and .md source files
source_suffix = {
    '.rst': 'restructuredtext',
    '.md':  'markdown',
}

# -- HTML output -------------------------------------------------------------
html_theme = 'sphinx_rtd_theme'
html_static_path = ['_static']
html_title = 'SpringBootLibrarySphinx Documentation'

html_theme_options = {
    'navigation_depth': 4,
    'collapse_navigation': False,
    'sticky_navigation': True,
    'includehidden': True,
    'titles_only': False,
    'display_version': True,
    'prev_next_buttons_location': 'bottom',
}

# -- PDF output (rinohtype) --------------------------------------------------
# Output filename: _build/rinoh/SpringBootLibrarySphinx.pdf
rinoh_documents = [
    {
        'doc':    'index',
        'target': 'SpringBootLibrarySphinx',
        'title':  'SpringBootLibrarySphinx Documentation',
        'author': 'dipina',
    },
]

# -- MyST options ------------------------------------------------------------
myst_enable_extensions = [
    'colon_fence',
    'deflist',
    'tasklist',
]
