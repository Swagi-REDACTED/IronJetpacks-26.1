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
    REFINED_IRON = "image/TechReborn/refined_iron_ingot.png"
    TIN = "image/TechReborn/tin_ingot.png"
    REDSTONE = "image/Vanilla/Redstone_Dust.png"
    INSULATED_COPPER = "image/TechReborn/Power/Cable/insulated_copper_cable.png"
    FURNACE = "image/Vanilla/Furnace.png"
    
    BASIC_FRAME = "image/TechReborn/Power/Machine/basic_machine_frame.png"
    RED_CELL = "image/TechReborn/Power/Battery/red_cell_battery.png"
    GENERATOR = "image/TechReborn/Power/Machine/Generator.png"

    # Basic Machine Frame: 8 Refined Iron Ingots
    generate_recipe("basic_machine_frame_recipe.png", [
        REFINED_IRON, REFINED_IRON, REFINED_IRON,
        REFINED_IRON, None,         REFINED_IRON,
        REFINED_IRON, REFINED_IRON, REFINED_IRON
    ], BASIC_FRAME)

    # Red Cell Battery: Insulated Copper Cable, 2 Redstone, 4 Tin Ingots
    generate_recipe("red_cell_battery_recipe.png", [
        None, INSULATED_COPPER, None,
        REDSTONE, None,         REDSTONE,
        TIN,  TIN,              TIN
    ], RED_CELL)

    # Generator: Red Cell Battery, Basic Machine Frame, Furnace
    generate_recipe("generator_recipe.png", [
        None, RED_CELL,    None,
        None, BASIC_FRAME, None,
        None, FURNACE,     None
    ], GENERATOR)

    print("Generated generator recipes")
