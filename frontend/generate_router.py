import json
import os

EXTRACT_SRC = '../DOCS/extracted_ui.json'
with open(EXTRACT_SRC, 'r', encoding='utf-8') as f:
    data = json.load(f)

import re

imports = []
routes = []

def get_comp_name(task_name):
    # This matches the logic from generate_react_app.py
    comp_name = re.sub(r'[^a-zA-Z0-9]', '', task_name.title())
    return comp_name

for idx, item in enumerate(data):
    if not item['ui_code']: continue
    task_name = item['task_name']
    comp = get_comp_name(task_name)
    path = f"/{comp.lower()}"
    
    imports.append(f"import {comp} from './pages/{comp}';")
    routes.append(f'        <Route path="{path}" element={{<{comp} />}} />')

app_jsx = f"""import React from 'react';
import {{ BrowserRouter as Router, Routes, Route, Link }} from 'react-router-dom';

{chr(10).join(imports)}

function App() {{
  return (
    <Router>
      <div className="flex bg-gray-100 min-h-screen">
        <aside className="w-64 bg-white shadow-md flex-shrink-0 flex flex-col hidden md:flex">
          <div className="p-4 border-b font-bold text-xl text-blue-600">
            EduPort Portal
          </div>
          <div className="overflow-y-auto flex-1 p-4 space-y-2 text-sm">
            <h3 className="text-gray-500 font-semibold mb-2">PAGES</h3>
            {chr(10).join([f'<Link to="/{get_comp_name(item["task_name"]).lower()}" className="block p-2 hover:bg-blue-50 text-gray-700 rounded transition">{item["task_name"]}</Link>' for item in data if item['ui_code']])}
          </div>
        </aside>
        
        <main className="flex-1 overflow-auto bg-gray-50">
          <Routes>
{chr(10).join(routes)}
            <Route path="/" element={{<div className="p-10 font-bold text-2xl">Welcome to EduPort Dashboard<br/><span className="text-sm font-normal text-gray-500">Please select a page from the sidebar to preview the UI extracted from Excel.</span></div>}} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}}

export default App;
"""

with open('src/App.jsx', 'w', encoding='utf-8') as f:
    f.write(app_jsx)

print("App.jsx created successfully.")
