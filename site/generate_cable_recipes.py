import json
from PIL import Image
import os

HITBOX_JSON = "image/Vanilla/Crafting-Inventory-Hitboxes.json"
CRAFTING_TABLE = "image/Vanilla/Crafting_Table_GUI.webp"
OUT_DIR = "image/generated"
os.makedirs(OUT_DIR, exist_ok=True)

with open(HITBOX_JSON, 'r') as f:
    hitboxes = json.load(f)

slots = {}
for hb in hitboxes:
    if hb['label'].startswith("Slot "):
        idx = int(hb['label'].split(' ')[1]) - 1
        slots[idx] = (hb['x'], hb['y'], hb['width'], hb['height'])
    elif hb['label'] == "Output":
        slots['output'] = (hb['x'], hb['y'], hb['width'], hb['height'])

def generate_recipe(output_name, grid, output_image_path):
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
        
    bg.save(os.path.join(OUT_DIR, output_name))

if __name__ == "__main__":
    RUBBER = "image/TechReborn/Power/Rubber/rubber.png"
    COPPER = "image/TechReborn/copper_ingot.png"
    GOLD = "image/Vanilla/Gold_Ingot.png"
    TIN = "image/TechReborn/tin_ingot.png"
    REFINED_IRON = "image/TechReborn/refined_iron_ingot.png"
    
    COPPER_CABLE = "image/TechReborn/Power/Cable/copper_cable.png"
    GOLD_CABLE = "image/TechReborn/Power/Cable/gold_cable.png"
    TIN_CABLE = "image/TechReborn/Power/Cable/tin_cable.png"
    HV_CABLE = "image/TechReborn/Power/Cable/hv_cable.png"
    
    INSULATED_COPPER = "image/TechReborn/Power/Cable/insulated_copper_cable.png"
    INSULATED_GOLD = "image/TechReborn/Power/Cable/insulated_gold_cable.png"
    INSULATED_HV = "image/TechReborn/Power/Cable/insulated_hv_cable.png"

    # Base cables (3 ingots in middle row)
    generate_recipe("copper_cable_recipe.png", [None, None, None, COPPER, COPPER, COPPER, None, None, None], COPPER_CABLE)
    generate_recipe("gold_cable_recipe.png", [None, None, None, GOLD, GOLD, GOLD, None, None, None], GOLD_CABLE)
    generate_recipe("tin_cable_recipe.png", [None, None, None, TIN, TIN, TIN, None, None, None], TIN_CABLE)
    generate_recipe("hv_cable_recipe.png", [None, None, None, REFINED_IRON, REFINED_IRON, REFINED_IRON, None, None, None], HV_CABLE)

    # Insulated cables (cable in middle, rubber around)
    generate_recipe("insulated_copper_cable_recipe.png", [None, None, None, RUBBER, COPPER_CABLE, None, None, None, None], INSULATED_COPPER)
    generate_recipe("insulated_gold_cable_recipe.png", [None, RUBBER, None, None, GOLD_CABLE, None, None, RUBBER, None], INSULATED_GOLD)
    generate_recipe("insulated_hv_cable_recipe.png", [None, RUBBER, None, None, HV_CABLE, None, None, RUBBER, None], INSULATED_HV) # HV needs 2 rubber

    # Direct crafting insulated cables
    # Copper (3 ingots, 6 rubber)
    generate_recipe("direct_insulated_copper_cable_recipe.png", [RUBBER, RUBBER, RUBBER, COPPER, COPPER, COPPER, RUBBER, RUBBER, RUBBER], INSULATED_COPPER)
    # Gold & HV (1 ingot, 8 rubber)
    generate_recipe("direct_insulated_gold_cable_recipe.png", [RUBBER, RUBBER, RUBBER, RUBBER, GOLD, RUBBER, RUBBER, RUBBER, RUBBER], INSULATED_GOLD)
    generate_recipe("direct_insulated_hv_cable_recipe.png", [RUBBER, RUBBER, RUBBER, RUBBER, REFINED_IRON, RUBBER, RUBBER, RUBBER, RUBBER], INSULATED_HV)

    print("Generated cable recipes")
