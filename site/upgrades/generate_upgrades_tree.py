import os

tiers = {
    1: [("stone", "Stone Jetpack"), ("copper", "Copper Jetpack")],
    2: [("iron", "Iron Jetpack"), ("bronze", "Bronze Jetpack"), ("silver", "Silver Jetpack")],
    3: [("gold", "Gold Jetpack"), ("steel", "Steel Jetpack"), ("electrum", "Electrum Jetpack"), ("invar", "Invar Jetpack")],
    4: [("diamond", "Diamond Jetpack"), ("platinum", "Platinum Jetpack")],
    5: [("emerald", "Emerald Jetpack")],
    6: [("netherite", "Netherite Jetpack")]
}

# Generate Index
index_content = "# Upgrades Index\n\nBrowse Jetpack upgrades by Tier.\n\n<div class=\"grid cards\" markdown>\n\n"
for t in range(1, 7):
    index_content += f"-   **[Tier {t} Upgrades](tier{t}.md)**\n    \n    ---\n    \n    View upgrade recipes for Tier {t} Jetpacks.\n\n"
index_content += "</div>\n"

with open("index.md", "w") as f:
    f.write(index_content)

# Generate Tier pages
for t, jetpacks in tiers.items():
    content = f"# Tier {t} Upgrades\n\n"
    for j_id, j_name in jetpacks:
        content += f"### {j_name} Upgrade\n"
        content += f"![{j_name} Upgrade](../image/generated/{j_id}_upgrade_recipe.gif)\n\n"
    
    with open(f"tier{t}.md", "w") as f:
        f.write(content)
