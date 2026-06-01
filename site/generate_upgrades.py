import json
from PIL import Image
import os

HITBOX_JSON = "image/Vanilla/Crafting-Inventory-Hitboxes.json"
CRAFTING_TABLE = "image/Vanilla/Crafting_Table_GUI.webp"
OUT_DIR = "image/generated"

tiers = [
    ["wood"], # Tier 0
    ["stone", "copper"], # Tier 1
    ["iron", "bronze", "silver"], # Tier 2
    ["gold", "steel", "electrum", "invar"], # Tier 3
    ["diamond", "platinum"], # Tier 4
    ["emerald"], # Tier 5
    ["netherite"] # Tier 6
]

# Create a flat map of jetpack to material and tier
jetpacks_data = [
    ("wood", 0, "image/Vanilla/Oak_Planks.png"),
    ("stone", 1, "image/Vanilla/Stone.png"),
    ("copper", 1, "image/Vanilla/Copper_Ingot.png"),
    ("iron", 2, "image/Vanilla/Iron_Ingot.png"),
    ("bronze", 2, "image/TechReborn/bronze_ingot.png"),
    ("silver", 2, "image/TechReborn/silver_ingot.png"),
    ("gold", 3, "image/Vanilla/Gold_Ingot.png"),
    ("steel", 3, "image/TechReborn/steel_ingot.png"),
    ("electrum", 3, "image/TechReborn/electrum_ingot.png"),
    ("invar", 3, "image/TechReborn/invar_ingot.png"),
    ("diamond", 4, "image/Vanilla/Diamond.png"),
    ("platinum", 4, "image/TechReborn/platinum_ingot.png"),
    ("emerald", 5, "image/Vanilla/Emerald.png"),
    ("netherite", 6, "image/Vanilla/Netherite_Ingot.png"),
]
jp_map = {n: (t, m) for n, t, m in jetpacks_data}

with open(HITBOX_JSON, 'r') as f:
    hitboxes = json.load(f)

slots = {}
for hb in hitboxes:
    if hb['label'].startswith("Slot "):
        idx = int(hb['label'].split(' ')[1]) - 1
        slots[idx] = (hb['x'], hb['y'], hb['width'], hb['height'])
    elif hb['label'] == "Output":
        slots['output'] = (hb['x'], hb['y'], hb['width'], hb['height'])

def compose_frame(grid, output_image_path):
    bg = Image.open(CRAFTING_TABLE).convert("RGBA")
    
    for i, img_path in enumerate(grid):
        if img_path and os.path.exists(img_path):
            img = Image.open(img_path).convert("RGBA")
            x, y, w, h = slots[i]
            img = img.resize((w, h), Image.NEAREST)
            bg.paste(img, (x, y), img)
            
    if output_image_path and os.path.exists(output_image_path):
        img = Image.open(output_image_path).convert("RGBA")
        x, y, w, h = slots['output']
        img = img.resize((w, h), Image.NEAREST)
        bg.paste(img, (x, y), img)
        
    return bg

# Generate upgrade GIFs
for name, tier, mat in jetpacks_data:
    if tier == 0: continue
    
    capacitor_img = f"image/IronJetpacks/capacitor_{name}.png" if os.path.exists(f"image/IronJetpacks/capacitor_{name}.png") else "image/IronJetpacks/capacitor.png"
    thruster_img = f"image/IronJetpacks/thruster_{name}.png" if os.path.exists(f"image/IronJetpacks/thruster_{name}.png") else "image/IronJetpacks/thruster.png"
    jetpack_img = f"image/IronJetpacks/jetpack_{name}.png"
    
    prev_tier_names = tiers[tier - 1]
    
    frames = []
    for pt in prev_tier_names:
        pt_img = f"image/IronJetpacks/jetpack_{pt}.png"
        grid = [
            mat, capacitor_img, mat,
            mat, pt_img, mat,
            thruster_img, None, thruster_img
        ]
        frame = compose_frame(grid, jetpack_img)
        frames.append(frame)
        
    out_path = os.path.join(OUT_DIR, f"{name}_upgrade_recipe.gif")
    if frames:
        frames[0].save(out_path, save_all=True, append_images=frames[1:], duration=1500, loop=0, disposal=2)
        print(f"Generated {out_path}")
