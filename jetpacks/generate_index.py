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

content = "# Jetpacks Index\n\nBrowse all available Jetpacks by Tier.\n\n<div class=\"grid cards\" markdown>\n\n"

for mat_id, mat_name, tier in materials:
    tier_label = "Creative" if mat_id == "creative" else f"Tier {tier}"
    content += f"-   **[{mat_name} Jetpack]({mat_id}.md)**\n    \n    ---\n    \n    ![{mat_name} Jetpack](../image/IronJetpacks/jetpack_{mat_id}.png){{ width=\"64\" align=\"middle\" }}\n    \n    {tier_label} Jetpack.\n\n"

content += "</div>\n"

with open("index.md", "w") as f:
    f.write(content)
