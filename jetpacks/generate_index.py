import os

materials = [
    ("wood", "Wood", 0),
    ("stone", "Stone", 1),
    ("copper", "Copper", 1),
    ("iron", "Iron", 2),
    ("bronze", "Bronze", 2),
    ("silver", "Silver", 2),
    ("gold", "Gold", 3),
    ("steel", "Steel", 3),
    ("electrum", "Electrum", 3),
    ("invar", "Invar", 3),
    ("diamond", "Diamond", 4),
    ("platinum", "Platinum", 4),
    ("emerald", "Emerald", 5),
    ("netherite", "Netherite", 6),
    ("creative", "Creative", 0),
]

content = """# Jetpacks Overview

Welcome to the Jetpacks section of the Iron Jetpacks Wiki. Here you can browse all jetpacks or learn how to Craft you're first jetpack!

## Crafting You're First Jetpack

To craft you're first Iron Jetpack, you will need the following base materials:

- **1** Crafting Table
- **28** Wooden Planks
- **11** Iron
- **54** Redstone Dust
- **16** Cobblestone
- **3** Leather
- **2** Iron Nuggets

**Step-by-Step Assembly:**

1. **Initial Components:** Use those ingredients in the Crafting Table to produce **2 Furnaces**, **11 Basic Coils**, and **1 Leather Strap**.
2. **Crafting Cells:** Combine **5 Basic Coils** with your remaining resources to make **5 Wood Cells**.
3. **Crafting Thrusters:** Use the leftover **6 Basic Coils**, the **2 Furnaces**, **2 Wood Cells**, and **8 Wooden Planks** to assemble **2 Wood Thrusters**.
4. **Crafting the Capacitor:** Combine **3 Wood Cells** and **6 Oak Planks** to make the **Wood Capacitor**.
5. **Final Assembly:** You now have everything needed to build your very first jetpack!

[Wooden Jetpacks](wood.md)

## Sections

Browse all available Jetpacks by Tier.

<div class="grid cards" markdown>

"""

for mat_id, mat_name, tier in materials:
    tier_label = "Creative" if mat_id == "creative" else f"Tier {tier}"
    content += f"-   **[{mat_name} Jetpack]({mat_id}.md)**\n    \n    ---\n    \n    ![{mat_name} Jetpack](../image/IronJetpacks/jetpack_{mat_id}.png){{ width=\"64\" align=\"middle\" }}\n    \n    {tier_label} Jetpack.\n\n"

content += "</div>\n"

with open("index.md", "w") as f:
    f.write(content)
