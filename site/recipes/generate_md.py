import os

materials = [
    ("wood", "Wood", "Planks"),
    ("stone", "Stone", "Stone"),
    ("copper", "Copper", "Copper Ingot"),
    ("iron", "Iron", "Iron Ingot"),
    ("bronze", "Bronze", "Bronze Ingot"),
    ("silver", "Silver", "Silver Ingot"),
    ("gold", "Gold", "Gold Ingot"),
    ("steel", "Steel", "Steel Ingot"),
    ("electrum", "Electrum", "Electrum Ingot"),
    ("invar", "Invar", "Invar Ingot"),
    ("diamond", "Diamond", "Diamond"),
    ("platinum", "Platinum", "Platinum Ingot"),
    ("emerald", "Emerald", "Emerald"),
    ("netherite", "Netherite", "Netherite Ingot"),
]

# Generate capacities, cells, thrusters markdown
for item_type in ["capacitors", "cells", "thrusters"]:
    title = item_type.capitalize()
    md_content = f"# {title}\n\n{title} are essential components for crafting jetpacks.\n\n"
    
    for mat_id, mat_name, ing in materials:
        recipe_img = f"../image/generated/{mat_id}_{item_type[:-1]}_recipe.png"
        md_content += f"## {mat_name} {title[:-1]}\n"
        md_content += f"![{mat_name} {title[:-1]}]({recipe_img})\n\n"
    
    with open(f"{item_type}.md", "w") as f:
        f.write(md_content)
