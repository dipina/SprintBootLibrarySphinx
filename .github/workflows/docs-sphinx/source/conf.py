# docs-sphinx/source/conf.py
# Sphinx configuration for SpringBootLibrarySphinx

# -- Project information -------------------------------------------------------
project   = 'SpringBootLibrarySphinx'
copyright = '2026, dipina'
author    = 'dipina'
release   = '0.0.1-SNAPSHOT'

# -- General configuration -----------------------------------------------------
extensions = [
    'sphinx.ext.autodoc',
    'sphinx.ext.viewcode',
    'sphinx.ext.napoleon',
    'myst_parser',       # Markdown (.md) page support
    'sphinx_rtd_theme',  # Read the Docs HTML theme
]

templates_path   = ['_templates']
exclude_patterns = []

# Accept both .rst and .md source files
source_suffix = {
    '.rst': 'restructuredtext',
    '.md':  'markdown',
}

# -- HTML output ---------------------------------------------------------------
html_theme       = 'sphinx_rtd_theme'
html_static_path = ['_static']
html_title       = 'SpringBootLibrarySphinx Documentation'

html_theme_options = {
    'navigation_depth':        4,
    'collapse_navigation':     False,
    'sticky_navigation':       True,
    'includehidden':           True,
    'titles_only':             False,
    'display_version':         True,
    'prev_next_buttons_location': 'bottom',
}

# -- MyST (Markdown) options ---------------------------------------------------
myst_enable_extensions = [
    'colon_fence',  # ::: fence syntax for directives
    'deflist',      # definition lists
    'tasklist',     # - [ ] task items
]
