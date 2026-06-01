import json
from PIL import Image
import os

# Configuration
HITBOX_JSON = "image/Vanilla/Crafting-Inventory-Hitboxes.json"
CRAFTING_TABLE = "image/Vanilla/Crafting_Table_GUI.webp"
OUT_DIR = "image/generated"
os.makedirs(OUT_DIR, exist_ok=True)

# Jetpacks data
# Tier mappings for coils
def get_coil_for_tier(tier):
    if tier <= 2: return "image/IronJetpacks/basic_coil.png"
    elif tier == 3: return "image/IronJetpacks/advanced_coil.png"
    elif tier == 4: return "image/IronJetpacks/elite_coil.png"
    elif tier == 5: return "image/IronJetpacks/ultimate_coil.png"
    elif tier == 6: return "image/IronJetpacks/expert_coil.png"
    return "image/IronJetpacks/basic_coil.png"

# Jetpacks configuration: (name, tier, material_image)
JETPACKS = [
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

# Other images
FURNACE = "image/Vanilla/Furnace.png"
REDSTONE = "image/Vanilla/Redstone_Dust.png"
STRAP = "image/IronJetpacks/strap.png"
LEATHER = "image/Vanilla/Leather.png"
IRON_NUGGET = "image/Vanilla/Iron_Nugget.png"

with open(HITBOX_JSON, 'r') as f:
    hitboxes = json.load(f)

# Build a map of slots
slots = {}
for hb in hitboxes:
    if hb['label'].startswith("Slot "):
        idx = int(hb['label'].split(' ')[1]) - 1
        slots[idx] = (hb['x'], hb['y'], hb['width'], hb['height'])
    elif hb['label'] == "Output":
        slots['output'] = (hb['x'], hb['y'], hb['width'], hb['height'])

def generate_recipe_image(output_name, grid, output_image_path):
    bg = Image.open(CRAFTING_TABLE).convert("RGBA")
    
    # Grid is a 1D array of 9 image paths or None
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
        
    bg.save(os.path.join(OUT_DIR, output_name))

# 1. Straps Recipe
# Strap: Iron Nugget, Leather, Iron Nugget / Leather, Leather, Leather / Iron Nugget, Leather, Iron Nugget
strap_grid = [
    None, IRON_NUGGET, None,
    LEATHER, LEATHER, LEATHER,
    None, IRON_NUGGET, None
]
generate_recipe_image("strap_recipe.png", strap_grid, STRAP)

# 2. Coils Recipes
coils_data = [
    ("basic_coil", "image/Vanilla/Iron_Ingot.png"),
    ("advanced_coil", "image/Vanilla/Gold_Ingot.png"),
    ("elite_coil", "image/Vanilla/Diamond.png"),
    ("ultimate_coil", "image/Vanilla/Emerald.png"),
    ("expert_coil", "image/Vanilla/Netherite_Ingot.png")
]
for coil_name, mat in coils_data:
    coil_img = f"image/IronJetpacks/{coil_name}.png"
    grid = [
        None, REDSTONE, None,
        REDSTONE, mat, REDSTONE,
        None, REDSTONE, None
    ]
    generate_recipe_image(f"{coil_name}_recipe.png", grid, coil_img)

# 3. Jetpack components
for name, tier, mat in JETPACKS:
    coil = get_coil_for_tier(tier)
    
    cell_img = f"image/IronJetpacks/cell_{name}.png" if os.path.exists(f"image/IronJetpacks/cell_{name}.png") else "image/IronJetpacks/cell.png"
    thruster_img = f"image/IronJetpacks/thruster_{name}.png" if os.path.exists(f"image/IronJetpacks/thruster_{name}.png") else "image/IronJetpacks/thruster.png"
    capacitor_img = f"image/IronJetpacks/capacitor_{name}.png" if os.path.exists(f"image/IronJetpacks/capacitor_{name}.png") else "image/IronJetpacks/capacitor.png"
    jetpack_img = f"image/IronJetpacks/jetpack_{name}.png" if os.path.exists(f"image/IronJetpacks/jetpack_{name}.png") else "image/IronJetpacks/jetpack.png"
    
    # Cell
    cell_grid = [
        None, REDSTONE, None,
        mat, coil, mat,
        None, REDSTONE, None
    ]
    generate_recipe_image(f"{name}_cell_recipe.png", cell_grid, cell_img)
    
    # Thruster
    thruster_grid = [
        mat, coil, mat,
        coil, cell_img, coil,
        mat, FURNACE, mat
    ]
    generate_recipe_image(f"{name}_thruster_recipe.png", thruster_grid, thruster_img)
    
    # Capacitor
    capacitor_grid = [
        mat, cell_img, mat,
        mat, cell_img, mat,
        mat, cell_img, mat
    ]
    generate_recipe_image(f"{name}_capacitor_recipe.png", capacitor_grid, capacitor_img)
    
    # Jetpack
    jetpack_grid = [
        mat, capacitor_img, mat,
        mat, STRAP, mat,
        thruster_img, None, thruster_img
    ]
    generate_recipe_image(f"{name}_jetpack_recipe.png", jetpack_grid, jetpack_img)

print("Recipes generated successfully.")
