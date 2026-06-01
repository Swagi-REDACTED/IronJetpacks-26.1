import re
import glob
import json

HITBOX_JSON = "image/Vanilla/Crafting-Inventory-Hitboxes.json"
with open(HITBOX_JSON, 'r') as f:
    hitboxes = json.load(f)

slots = {}
for hb in hitboxes:
    if hb['label'].startswith("Slot "):
        idx = int(hb['label'].split(' ')[1]) - 1
        slots[idx] = (hb['x'], hb['y'], hb['width'], hb['height'])
    elif hb['label'] == "Output":
        slots['output'] = (hb['x'], hb['y'], hb['width'], hb['height'])

def get_tooltip_html(item_name):
    item_name = item_name.strip()
    if not item_name:
        return ""
        
    title = item_name.replace("_", " ").title()
    
    if "Jetpack" in title:
        return f'''<div class="mc-tooltip" markdown="0">
  <div class="mc-title">{title}</div>
  <div class="mc-subtitle">0 E / 30M E</div>
  <div class="mc-badges">
    <span class="text-gray">TIER 4</span> <span class="text-gray">|</span> 
    <span class="text-green">ENGINE</span> <span class="text-gray">|</span> 
    <span class="text-green">HOVER</span>
  </div>
  <div class="mc-separator"></div>
  <div class="mc-stats">
    <div class="text-gray">Fuel Usage: 650.0 E/t</div>
    <div class="text-gray">Vertical Speed: 0.9</div>
  </div>
  <div class="mc-separator"></div>
  <div class="mc-footer">
    <span class="text-gray">Hold </span><span class="text-yellow-italic">SHIFT</span><span class="text-gray"> for info</span>
  </div>
  <div class="mc-separator"></div>
  <div class="text-gray">When on Chest:</div>
  <div class="text-blue">+4 Armor</div>
</div>'''
    elif "Stone" in title:
        return f'''<div class="mc-tooltip" markdown="0">
  <div class="mc-title">{title}</div>
  <div class="text-blue">Building Blocks</div>
  <div class="text-blue">Natural Blocks</div>
</div>'''
    elif "Redstone" in title:
        return f'''<div class="mc-tooltip" markdown="0">
  <div class="mc-title">{title}</div>
  <div class="text-blue">Redstone Blocks</div>
  <div class="text-blue">Ingredients</div>
</div>'''
    elif "Ingot" in title or "Planks" in title or "Diamond" in title or "Emerald" in title:
        return f'''<div class="mc-tooltip" markdown="0">
  <div class="mc-title">{title}</div>
  <div class="text-blue">Ingredients</div>
</div>'''
    else:
        return f'''<div class="mc-tooltip" markdown="0">
  <div class="mc-title">{title}</div>
  <div class="text-blue">Iron Jetpacks</div>
</div>'''

def generate_interactive_recipe(img_src, alt_text):
    # Extract filename from img_src
    filename = img_src.split('/')[-1]
    # Use ../../image/generated/ path because all pages are in subdirectories and use_directory_urls makes them depth 2
    html_src = f"../../image/generated/{filename}"
    
    html = f'<div class="crafting-container" style="position: relative; display: inline-block;">\n'
    html += f'    <img src="{html_src}" alt="{alt_text}" style="display:block;">\n'
    
    # Output Slot
    out_x, out_y, out_w, out_h = slots['output']
    html += f'    <div class="mc-tooltip-wrapper" style="position: absolute; left: {out_x}px; top: {out_y}px; width: {out_w}px; height: {out_h}px;">\n'
    html += f'        {get_tooltip_html(alt_text)}\n'
    html += f'    </div>\n'
    
    html += f'</div>'
    return html

# First, revert the previous HTML wrappers back to markdown syntax to clean it up before reapplying
for md_file in glob.glob("jetpacks/*.md") + glob.glob("recipes/*.md") + glob.glob("upgrades/*.md"):
    with open(md_file, "r") as f:
        content = f.read()
        
    # Regex to find our injected container and extract the image
    # We look for <div class="crafting-container"... <img src="..." alt="..."> ... </div>
    # Actually, it's easier to just rebuild the files from scratch if it's too messy, but let's try to extract.
    def revert_html(match):
        img_src = match.group(1)
        alt_text = match.group(2)
        return f"![{alt_text}]({img_src})"
    
    # Since we only wrote HTML in the previous run, we can revert it by finding the <img> tags
    # Wait, the previous run wrote <img src="X" alt="Y" ...
    content = re.sub(r'<div class="crafting-container(?:.*?)>\s*<img src="([^"]+)" alt="([^"]+)"(?:.*?)\s*(?:<div.*?</div>\s*)+</div>', revert_html, content, flags=re.DOTALL)
    
    # Then apply the NEW markdown-based HTML wrapper
    def replace_func(match):
        alt_text = match.group(1)
        img_src = match.group(2)
        return generate_interactive_recipe(img_src, alt_text)
        
    content = re.sub(r'!\[([^\]]+)\]\((../image/generated/[^\)]+)\)', replace_func, content)
    
    with open(md_file, "w") as f:
        f.write(content)
