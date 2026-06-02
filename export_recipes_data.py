import json
import os

JETPACKS = [
    ("wood", 0, "Oak Planks", "Building Blocks"),
    ("stone", 1, "Stone", "Building Blocks|Natural Blocks"),
    ("copper", 1, "Copper Ingot", "Ingredients"),
    ("iron", 2, "Iron Ingot", "Ingredients"),
    ("bronze", 2, "Bronze Ingot", "Ingredients"),
    ("silver", 2, "Silver Ingot", "Ingredients"),
    ("gold", 3, "Gold Ingot", "Ingredients"),
    ("steel", 3, "Steel Ingot", "Ingredients"),
    ("electrum", 3, "Electrum Ingot", "Ingredients"),
    ("invar", 3, "Invar Ingot", "Ingredients"),
    ("diamond", 4, "Diamond", "Ingredients"),
    ("platinum", 4, "Platinum Ingot", "Ingredients"),
    ("emerald", 5, "Emerald", "Ingredients"),
    ("netherite", 6, "Netherite Ingot", "Ingredients"),
]

# Provide some basic stats. In a real scenario these would be parsed from JSON files.
def get_stats(tier, name=None):
    # Default fallback
    stats = {
        "capacity": "30,000,000",
        "fuel": "0 E/t",
        "speed": "0.0",
        "vAccel": "0.1",
        "hSpeed": "0.1",
        "hover": "0.1",
        "descend": "0.2",
        "sprintMod": "1.2",
        "sprintFuel": "1.5",
        "armor": "0"
    }
    
    if name:
        try:
            with open(f"jetpacks/{name}.md", "r") as f:
                for line in f:
                    if "|" not in line: continue
                    parts = [p.strip() for p in line.split("|")]
                    if len(parts) < 3: continue
                    key = parts[1].replace("**", "").strip()
                    val = parts[2].strip()
                    
                    if key == "Capacity": stats["capacity"] = val.replace(" FE", "").replace(" E", "")
                    elif key == "Usage": stats["fuel"] = val.replace(" FE/tick", " E/t").replace(" E/tick", " E/t").replace(" FE", " E/t")
                    elif key == "Vertical Speed": stats["speed"] = val
                    elif key == "Vertical Accel": stats["vAccel"] = val
                    elif key == "Side Speed": stats["hSpeed"] = val
                    elif key == "Hover Speed": stats["hover"] = val
                    elif key == "Hover Slow": stats["descend"] = val
                    elif key == "Sprint Speed": stats["sprintMod"] = val
                    elif key == "Sprint Fuel": stats["sprintFuel"] = val
                    elif key == "Armor": stats["armor"] = f"+{val}" if val != "0" else "0"
        except:
            pass
            
    return stats

recipes_data = {}

def add_recipe(recipe_id, item_data, output_slot_idx=4):
    recipes_data[recipe_id] = item_data

# Common items
redstone = {"name": "Redstone Dust", "tags": ["Redstone Blocks", "Ingredients"]}
glass = {"name": "Glass", "tags": ["Building Blocks"]}
iron = {"name": "Iron Ingot", "tags": ["Ingredients"]}
nugget = {"name": "Iron Nugget", "tags": ["Ingredients"]}
leather = {"name": "Leather", "tags": ["Ingredients"]}

# Straps
add_recipe("strap_recipe.png", {
    "1": nugget, "3": leather, "4": leather, "5": leather, "7": nugget,
    "output": {"name": "Strap", "tags": ["Iron Jetpacks"]}
})

def get_coil_by_tier(tier):
    if tier <= 2: return "Basic Coil"
    elif tier == 3: return "Advanced Coil"
    elif tier == 4: return "Elite Coil"
    elif tier == 5: return "Ultimate Coil"
    elif tier == 6: return "Expert Coil"
    return "Basic Coil"

for name, tier, mat_name, tags in JETPACKS:
    mat = {"name": mat_name, "tags": tags.split("|")}
    coil = {"name": get_coil_by_tier(tier), "tags": ["Iron Jetpacks"]}
    
    cap_name = f"{name.capitalize()} Capacitor"
    cell_name = f"{name.capitalize()} Cell"
    thruster_name = f"{name.capitalize()} Thruster"
    jp_name = f"{name.capitalize()} Jetpack"
    
    cap = {"name": cap_name, "tags": ["Iron Jetpacks"]}
    cell = {"name": cell_name, "tags": ["Iron Jetpacks"]}
    thruster = {"name": thruster_name, "tags": ["Iron Jetpacks"]}
    strap = {"name": "Strap", "tags": ["Iron Jetpacks"]}
    
    jp_stats = get_stats(tier, name)
    jp_item = {
        "name": jp_name,
        "isJetpack": True,
        "tier": tier,
        "stats": jp_stats
    }
    
    # Cell Recipe
    add_recipe(f"{name}_cell_recipe.png", {
        "1": redstone, "3": mat, "4": coil, "5": mat, "7": redstone,
        "output": cell
    })
    
    # Capacitor Recipe
    add_recipe(f"{name}_capacitor_recipe.png", {
        "0": mat, "1": cell, "2": mat,
        "3": mat, "4": cell, "5": mat,
        "6": mat, "7": cell, "8": mat,
        "output": cap
    })
    
    # Thruster Recipe
    add_recipe(f"{name}_thruster_recipe.png", {
        "0": mat, "1": coil, "2": mat,
        "3": coil, "4": cell, "5": coil,
        "6": mat, "7": {"name": "Furnace", "tags": ["Vanilla"]}, "8": mat,
        "output": thruster
    })
    
    # Jetpack Recipe
    add_recipe(f"{name}_jetpack_recipe.png", {
        "0": mat, "1": cap, "2": mat,
        "3": mat, "4": strap, "5": mat,
        "6": thruster, "8": thruster,
        "output": jp_item
    })
    
    # Upgrades (if applicable)
    if tier > 0:
        prev_tiers = []
        if tier == 1: prev_tiers = ["wood"]
        elif tier == 2: prev_tiers = ["stone", "copper"]
        elif tier == 3: prev_tiers = ["iron", "bronze", "silver"]
        elif tier == 4: prev_tiers = ["gold", "steel", "electrum", "invar"]
        elif tier == 5: prev_tiers = ["diamond", "platinum"]
        elif tier == 6: prev_tiers = ["emerald"]
        
        # We need to map the upgrade GIF to its slots
        # The center slot is dynamic, but for tooltips, we can just display the first valid one or a generic "Previous Tier Jetpack"
        # Or better, we can animate the tooltip too? No, a generic tooltip is fine:
        prev_jp = {"name": f"Tier {tier-1} Jetpack", "tags": ["Iron Jetpacks"]}
        
        add_recipe(f"{name}_upgrade_recipe.gif", {
            "0": mat, "1": cap, "2": mat,
            "3": mat, "4": prev_jp, "5": mat,
            "6": thruster, "8": thruster,
            "output": jp_item
        })

# Coils
add_recipe("basic_coil_recipe.png", {
    "1": redstone, "3": redstone, "4": {"name": "Iron Ingot", "tags": ["Ingredients"]}, "5": redstone, "7": redstone,
    "output": {"name": "Basic Coil", "tags": ["Iron Jetpacks"]}
})
add_recipe("advanced_coil_recipe.png", {
    "1": redstone, "3": redstone, "4": {"name": "Gold Ingot", "tags": ["Ingredients"]}, "5": redstone, "7": redstone,
    "output": {"name": "Advanced Coil", "tags": ["Iron Jetpacks"]}
})
add_recipe("elite_coil_recipe.png", {
    "1": redstone, "3": redstone, "4": {"name": "Diamond", "tags": ["Ingredients"]}, "5": redstone, "7": redstone,
    "output": {"name": "Elite Coil", "tags": ["Iron Jetpacks"]}
})
add_recipe("ultimate_coil_recipe.png", {
    "1": redstone, "3": redstone, "4": {"name": "Emerald", "tags": ["Ingredients"]}, "5": redstone, "7": redstone,
    "output": {"name": "Ultimate Coil", "tags": ["Iron Jetpacks"]}
})
add_recipe("expert_coil_recipe.png", {
    "1": redstone, "3": redstone, "4": {"name": "Netherite Ingot", "tags": ["Ingredients"]}, "5": redstone, "7": redstone,
    "output": {"name": "Expert Coil", "tags": ["Iron Jetpacks"]}
})

add_recipe("sap_smelting_recipe.gif", {
    "Input": {"name": "Sap", "tags": ["Tech Reborn"]},
    "Fuel": {"name": "Any Fuel Source"},
    "Output": {"name": "Rubber", "tags": ["Tech Reborn"]}
})

add_recipe("copper_cable_recipe.png", {
    "3": {"name": "Copper Ingot", "tags": ["Ingredients"]},
    "4": {"name": "Copper Ingot", "tags": ["Ingredients"]},
    "5": {"name": "Copper Ingot", "tags": ["Ingredients"]},
    "output": {"name": "Copper Cable", "tags": ["Tech Reborn"]}
})
add_recipe("gold_cable_recipe.png", {
    "3": {"name": "Gold Ingot", "tags": ["Ingredients"]},
    "4": {"name": "Gold Ingot", "tags": ["Ingredients"]},
    "5": {"name": "Gold Ingot", "tags": ["Ingredients"]},
    "output": {"name": "Gold Cable", "tags": ["Tech Reborn"]}
})
add_recipe("tin_cable_recipe.png", {
    "3": {"name": "Tin Ingot", "tags": ["Ingredients"]},
    "4": {"name": "Tin Ingot", "tags": ["Ingredients"]},
    "5": {"name": "Tin Ingot", "tags": ["Ingredients"]},
    "output": {"name": "Tin Cable", "tags": ["Tech Reborn"]}
})
add_recipe("hv_cable_recipe.png", {
    "3": {"name": "Refined Iron Ingot", "tags": ["Ingredients"]},
    "4": {"name": "Refined Iron Ingot", "tags": ["Ingredients"]},
    "5": {"name": "Refined Iron Ingot", "tags": ["Ingredients"]},
    "output": {"name": "HV Cable", "tags": ["Tech Reborn"]}
})
add_recipe("insulated_copper_cable_recipe.png", {
    "3": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "4": {"name": "Copper Cable", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated Copper Cable", "tags": ["Tech Reborn"]}
})
add_recipe("insulated_gold_cable_recipe.png", {
    "1": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "4": {"name": "Gold Cable", "tags": ["Tech Reborn"]},
    "7": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated Gold Cable", "tags": ["Tech Reborn"]}
})
add_recipe("insulated_hv_cable_recipe.png", {
    "1": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "4": {"name": "HV Cable", "tags": ["Tech Reborn"]},
    "7": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated HV Cable", "tags": ["Tech Reborn"]}
})

add_recipe("direct_insulated_copper_cable_recipe.png", {
    "0": {"name": "Rubber", "tags": ["Tech Reborn"]}, "1": {"name": "Rubber", "tags": ["Tech Reborn"]}, "2": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "3": {"name": "Copper Ingot", "tags": ["Ingredients"]}, "4": {"name": "Copper Ingot", "tags": ["Ingredients"]}, "5": {"name": "Copper Ingot", "tags": ["Ingredients"]},
    "6": {"name": "Rubber", "tags": ["Tech Reborn"]}, "7": {"name": "Rubber", "tags": ["Tech Reborn"]}, "8": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated Copper Cable", "tags": ["Tech Reborn"]}
})

add_recipe("direct_insulated_gold_cable_recipe.png", {
    "0": {"name": "Rubber", "tags": ["Tech Reborn"]}, "1": {"name": "Rubber", "tags": ["Tech Reborn"]}, "2": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "3": {"name": "Rubber", "tags": ["Tech Reborn"]}, "4": {"name": "Gold Ingot", "tags": ["Ingredients"]}, "5": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "6": {"name": "Rubber", "tags": ["Tech Reborn"]}, "7": {"name": "Rubber", "tags": ["Tech Reborn"]}, "8": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated Gold Cable", "tags": ["Tech Reborn"]}
})

add_recipe("direct_insulated_hv_cable_recipe.png", {
    "0": {"name": "Rubber", "tags": ["Tech Reborn"]}, "1": {"name": "Rubber", "tags": ["Tech Reborn"]}, "2": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "3": {"name": "Rubber", "tags": ["Tech Reborn"]}, "4": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]}, "5": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "6": {"name": "Rubber", "tags": ["Tech Reborn"]}, "7": {"name": "Rubber", "tags": ["Tech Reborn"]}, "8": {"name": "Rubber", "tags": ["Tech Reborn"]},
    "output": {"name": "Insulated HV Cable", "tags": ["Tech Reborn"]}
})

add_recipe("basic_machine_frame_recipe.png", {
    "0": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "1": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "2": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "3": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "5": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "6": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "7": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "8": {"name": "Refined Iron Ingot", "tags": ["Tech Reborn"]},
    "output": {"name": "Basic Machine Frame", "tags": ["Tech Reborn"]}
})

add_recipe("red_cell_battery_recipe.png", {
    "1": {"name": "Insulated Copper Cable", "tags": ["Tech Reborn"]},
    "3": {"name": "Redstone Dust", "tags": ["Ingredients"]},
    "5": {"name": "Redstone Dust", "tags": ["Ingredients"]},
    "6": {"name": "Tin Ingot", "tags": ["Tech Reborn"]},
    "7": {"name": "Tin Ingot", "tags": ["Tech Reborn"]},
    "8": {"name": "Tin Ingot", "tags": ["Tech Reborn"]},
    "output": {"name": "Red Cell Battery", "tags": ["Tech Reborn"]}
})

add_recipe("generator_recipe.png", {
    "1": {"name": "Red Cell Battery", "tags": ["Tech Reborn"]},
    "4": {"name": "Basic Machine Frame", "tags": ["Tech Reborn"]},
    "7": {"name": "Furnace", "tags": ["Vanilla"]},
    "output": {"name": "Generator", "tags": ["Tech Reborn"]}
})

if not os.path.exists("javascripts"):
    os.makedirs("javascripts")

with open("javascripts/recipes_data.json", "w") as f:
    json.dump(recipes_data, f, indent=2)
