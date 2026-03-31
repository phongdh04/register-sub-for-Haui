import json
import os
import re

EXTRACT_SRC = '../DOCS/extracted_ui.json'
with open(EXTRACT_SRC, 'r', encoding='utf-8') as f:
    data = json.load(f)

def get_comp_name(task_name):
    comp_name = re.sub(r'[^a-zA-Z0-9]', '', task_name.title())
    return comp_name

roles_dict = {}

for idx, item in enumerate(data):
    if not item['ui_code']: continue
    task_name = item['task_name']
    role = str(item['role']).strip().upper()
    if role == 'NONE' or not role:
        role = 'ALL'
    
    comp = get_comp_name(task_name)
    path = f"/{comp.lower()}"
    
    if role not in roles_dict:
        roles_dict[role] = []
    
    roles_dict[role].append({
        'comp': comp,
        'path': path,
        'title': task_name
    })

os.makedirs('src/layouts', exist_ok=True)

layout_wrapper = """import React from 'react';
import { Link, Outlet } from 'react-router-dom';

const ROLELayout = () => {
  return (
    <div className="flex bg-gray-100 min-h-screen">
      <aside className="w-64 bg-white shadow-md flex-shrink-0 flex flex-col hidden md:flex border-r border-[#dce2f7]">
        <div className="p-4 border-b border-[#dce2f7] font-bold text-xl text-[#00288e] flex items-center gap-2">
          <span className="material-symbols-outlined">school</span>
          EduPort ROLE
        </div>
        <div className="overflow-y-auto flex-1 p-4 space-y-1 text-sm">
LINKS
        </div>
        <div className="p-4 border-t border-[#dce2f7]">
          <Link to="/" className="text-[#ba1a1a] flex items-center gap-2 px-2 py-2 hover:bg-[#ffdad6] rounded transition">
            <span className="material-symbols-outlined">logout</span> Thoát
          </Link>
        </div>
      </aside>
      
      <main className="flex-1 overflow-auto bg-[#f9f9ff]">
        <header className="bg-white shadow-sm p-4 flex items-center justify-between">
           <h1 className="text-xl font-bold text-[#141b2b]">ROLE Portal</h1>
        </header>
        <div className="p-4">
           <Outlet />
        </div>
      </main>
    </div>
  );
};

export default ROLELayout;
"""

routes = []
imports = []
app_links = []

for role, pages in roles_dict.items():
    # Write Layout
    layout_name = f"{role.capitalize()}Layout"
    links_html = []
    
    role_path_prefix = f"/{role.lower()}"
    
    for page in pages:
        full_path = f"{role_path_prefix}{page['path']}"
        links_html.append(f"""          <Link to="{full_path}" className="flex items-center gap-2 p-2 hover:bg-[#dde1ff] text-[#141b2b] rounded transition">
            <span className="material-symbols-outlined shrink-0 text-base">arrow_right</span>
            <span className="truncate">{page['title']}</span>
          </Link>""")
        
        imports.append(f"import {page['comp']} from './pages/{page['comp']}';")
        routes.append(f'          <Route path="{full_path.lstrip("/")}" element={{<{page["comp"]} />}} />')
        
    layout_code = layout_wrapper.replace('ROLE', role.capitalize()).replace('LINKS', chr(10).join(links_html))
    with open(f'src/layouts/{layout_name}.jsx', 'w', encoding='utf-8') as f:
        f.write(layout_code)
        
    imports.append(f"import {layout_name} from './layouts/{layout_name}';")
    
    app_links.append(f'<Link to="{role_path_prefix}" className="block p-4 border rounded bg-white shadow hover:-translate-y-1 transition text-center font-bold text-lg text-[#00288e]">{role.capitalize()} Portal</Link>')


app_jsx = f"""import React from 'react';
import {{ BrowserRouter as Router, Routes, Route, Link }} from 'react-router-dom';

{chr(10).join(imports)}

function App() {{
  return (
    <Router>
      <Routes>
        <Route path="/" element={{
          <div className="min-h-screen bg-[#f1f3ff] flex flex-col items-center justify-center p-4">
            <h1 className="text-4xl font-black text-[#001453] mb-8">Hệ Thống Phân Quyền EduPort</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full max-w-4xl">
              {chr(10).join(app_links)}
            </div>
          </div>
        }} />
        
"""

for role, pages in roles_dict.items():
    layout_name = f"{role.capitalize()}Layout"
    role_path_prefix = f"/{role.lower()}"
    app_jsx += f'        <Route path="{role_path_prefix}" element={{<{layout_name} />}}>\n'
    app_jsx += f'          <Route index element={{<div className="p-8 text-center text-gray-500">Welcome to {role.capitalize()} Portal. Select a module from the sidebar.</div>}} />\n'
    for page in pages:
        full_path = f"{page['path'].lstrip('/')}"
        app_jsx += f'          <Route path="{full_path}" element={{<{page["comp"]} />}} />\n'
    app_jsx += f'        </Route>\n'

app_jsx += """      </Routes>
    </Router>
  );
}

export default App;
"""

with open('src/App.jsx', 'w', encoding='utf-8') as f:
    f.write(app_jsx)

print("Generated roles and layouts routing.")
