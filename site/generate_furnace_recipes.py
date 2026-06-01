import json
from PIL import Image
import os

HITBOX_JSON = "image/Vanilla/Furnace-Inventory-Hitboxes.json"
FURNACE_GUI = "image/Vanilla/Furnace_GUI.webp"
OUT_DIR = "image/generated"
os.makedirs(OUT_DIR, exist_ok=True)

with open(HITBOX_JSON, 'r') as f:
    hitboxes = json.load(f)

slots = {}
for hb in hitboxes:
    slots[hb['label']] = (hb['x'], hb['y'], hb['width'], hb['height'])

def generate_furnace_recipe_gif(output_name, input_img, fuel_img_1, fuel_img_2, output_img):
    frames = []
    
    for fuel_img in [fuel_img_1, fuel_img_2]:
        bg = Image.open(FURNACE_GUI).convert("RGBA")
        
        if input_img and os.path.exists(input_img):
            img = Image.open(input_img).convert("RGBA")
            x, y, w, h = slots['Input']
            img = img.resize((w, h), Image.NEAREST)
            bg.paste(img, (x, y), img)
            
        if fuel_img and os.path.exists(fuel_img):
            img = Image.open(fuel_img).convert("RGBA")
            x, y, w, h = slots['Fuel']
            img = img.resize((w, h), Image.NEAREST)
            bg.paste(img, (x, y), img)
            
        if output_img and os.path.exists(output_img):
            img = Image.open(output_img).convert("RGBA")
            x, y, w, h = slots['Output']
            img = img.resize((w, h), Image.NEAREST)
            bg.paste(img, (x, y), img)
            
        frames.append(bg)
        
    frames[0].save(
        os.path.join(OUT_DIR, output_name),
        save_all=True,
        append_images=frames[1:],
        duration=1000,
        loop=0
    )

if __name__ == "__main__":
    generate_furnace_recipe_gif(
        "sap_smelting_recipe.gif",
        "image/TechReborn/Power/Rubber/sap.png",
        "image/Vanilla/Coal.png",
        "image/Vanilla/Oak_Planks.png",
        "image/TechReborn/Power/Rubber/rubber.png"
    )
    print("Generated sap_smelting_recipe.gif")
