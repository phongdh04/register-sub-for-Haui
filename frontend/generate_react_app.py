import json
import os
import re

EXTRACT_SRC = '../DOCS/extracted_ui.json'
PAGES_DIR = 'src/pages'
COMP_DIR = 'src/components'
ROUTES_DIR = 'src/routes'

os.makedirs(PAGES_DIR, exist_ok=True)
os.makedirs(COMP_DIR, exist_ok=True)
os.makedirs(ROUTES_DIR, exist_ok=True)

def html_to_jsx(html_str):
    # Basic class to className
    jsx = re.sub(r'\bclass=', 'className=', html_str)
    jsx = re.sub(r'\bfor=', 'htmlFor=', jsx)
    jsx = re.sub(r'\btabindex=', 'tabIndex=', jsx)
    jsx = re.sub(r'\busemap=', 'useMap=', jsx)
    jsx = re.sub(r'\breadonly=', 'readOnly=', jsx)
    jsx = re.sub(r'\bautocomplete=', 'autoComplete=', jsx)
    jsx = re.sub(r'\bautofocus=', 'autoFocus=', jsx)
    jsx = re.sub(r'\bnovalidate=', 'noValidate=', jsx)
    jsx = re.sub(r'\bmaxlength=', 'maxLength=', jsx)
    jsx = re.sub(r'\bminlength=', 'minLength=', jsx)
    jsx = re.sub(r'\bstyle="([^"]*)"', r'style={{ /* FIXME: convert style string to object -> \1 */ }}', jsx)
    
    # Self-close unclosed tags: img, input, hr, br
    jsx = re.sub(r'<(img|input|hr|br|meta|link)([^>]*?)(?<!/)>', r'<\1\2 />', jsx)
    
    # Remove script tags as they break JSX
    jsx = re.sub(r'<script.*?>.*?</script>', '', jsx, flags=re.IGNORECASE | re.DOTALL)
    
    # Strip <aside> and <header> from the original HTML to prevent double navigation
    jsx = re.sub(r'<aside.*?>.*?</aside>', '', jsx, flags=re.IGNORECASE | re.DOTALL)
    jsx = re.sub(r'<header.*?>.*?</header>', '', jsx, flags=re.IGNORECASE | re.DOTALL)
    
    # Strip layout margins that offset the original absolute sidebars
    jsx = re.sub(r'\b(?:md:|lg:)?(?:ml|pl)-(?:64|72|80)\b', '', jsx)
    jsx = re.sub(r'\b(?:md:|lg:)?(?:pt|mt)-(?:16|20|24)\b', '', jsx) # Remove top margin/padding for header offsets
    
    # Extract body content if <html> exists
    match = re.search(r'<body[^>]*>(.*?)</body>', jsx, re.IGNORECASE | re.DOTALL)
    if match:
        jsx = match.group(1)
    elif '<!DOCTYPE html>' in jsx:
        # Just rip out what looks like the main container
        match2 = re.search(r'(<div[^>]*>.*</div>)', jsx, re.IGNORECASE | re.DOTALL)
        if match2:
            jsx = match2.group(1)
    
    # Simple fix for inline comments
    jsx = re.sub(r'<!--(.*?)-->', r'{/* \1 */}', jsx, flags=re.DOTALL)
    
    return jsx

with open(EXTRACT_SRC, 'r', encoding='utf-8') as f:
    data = json.load(f)

# Sort out unique roles
roles = set([x['role'] if x['role'] else 'PUBLIC' for x in data])
app_pages = []

for idx, item in enumerate(data):
    if not item['ui_code']: continue
    
    task_name = item['task_name']
    role = item['role'] if item['role'] else 'PUBLIC'
    
    # Make a valid component name
    comp_name = re.sub(r'[^a-zA-Z0-9]', '', task_name.title())
    if not comp_name:
        comp_name = f"Page{idx}"
    
    # Convert HTML
    jsx_content = html_to_jsx(item['ui_code'])
    
    # Wrap in React component
    comp_code = f"""import React from 'react';

const {comp_name} = () => {{
  return (
    <>
      {jsx_content}
    </>
  );
}};

export default {comp_name};
"""
    
    # Write to file
    file_path = os.path.join(PAGES_DIR, f"{comp_name}.jsx")
    
    # Sometimes parsing fails badly so let's try-catch parsing issues maybe? React will tell us.
    with open(file_path, 'w', encoding='utf-8') as out_f:
        out_f.write(comp_code)
        
    app_pages.append({'name': comp_name, 'role': role, 'path': f"/{comp_name.lower()}"})

print(json.dumps(app_pages, ensure_ascii=False, indent=2))

