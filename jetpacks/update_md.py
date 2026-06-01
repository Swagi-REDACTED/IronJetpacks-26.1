import os
import glob

jetpack_files = glob.glob("*.md")

def get_coil_recipe(name):
    tier012 = ["wood", "stone", "copper", "iron", "bronze", "silver"]
    tier3 = ["gold", "steel", "electrum", "invar"]
    tier4 = ["diamond", "platinum"]
    
    if name in tier012:
        return "basic_coil"
    elif name in tier3:
        return "advanced_coil"
    elif name in tier4:
        return "elite_coil"
    elif name == "emerald":
        return "ultimate_coil"
    elif name == "netherite":
        return "expert_coil"
    return None

for file_path in jetpack_files:
    if file_path == "index.md":
        continue
    
    name = file_path.replace(".md", "")
    
    # We already removed crafting from creative
    if name == "creative":
        continue
        
    with open(file_path, "r") as f:
        content = f.read()
    
    # We need to recreate ## Crafting because we might have already modified it
    if "## Crafting" in content:
        crafting_idx = content.find("## Crafting")
        
        new_crafting = f"## Crafting\n\n"
        new_crafting += f"### Jetpack\n![{name.capitalize()} Jetpack](../image/generated/{name}_jetpack_recipe.png)\n\n"
        new_crafting += f"### Capacitor\n![{name.capitalize()} Capacitor](../image/generated/{name}_capacitor_recipe.png)\n\n"
        new_crafting += f"### Thruster\n![{name.capitalize()} Thruster](../image/generated/{name}_thruster_recipe.png)\n\n"
        
        if os.path.exists(f"../image/generated/{name}_cell_recipe.png"):
            new_crafting += f"### Cell\n![{name.capitalize()} Cell](../image/generated/{name}_cell_recipe.png)\n\n"
            
        coil = get_coil_recipe(name)
        if coil:
            coil_name = " ".join(word.capitalize() for word in coil.split('_'))
            new_crafting += f"### {coil_name}\n![{coil_name}](../image/generated/{coil}_recipe.png)\n\n"
            
        content = content[:crafting_idx] + new_crafting
        
        with open(file_path, "w") as f:
            f.write(content)
