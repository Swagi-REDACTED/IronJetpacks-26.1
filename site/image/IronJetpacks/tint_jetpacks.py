import os
from PIL import Image

# Define colors
colors = {
    "wood": 0x6B511F,
    "stone": 0x7F7F7F,
    "copper": 0xCE7201,
    "iron": 0xC1C1C1,
    "bronze": 0xEC9E3F,
    "silver": 0x9FC4DD,
    "gold": 0xDEDE00,
    "steel": 0x565656,
    "electrum": 0xA79135,
    "invar": 0x929D97,
    "diamond": 0x4AEDD1,
    "platinum": 0x6FEAEF,
    "emerald": 0x41F384,
    "netherite": 0x382013,
    "creative": 0xCF1AE9,
}

base_img_path = "BaseJetpackUpscaled.png"

if not os.path.exists(base_img_path):
    print("Base image not found")
    exit(1)

base_img = Image.open(base_img_path).convert("RGBA")
pixels = base_img.load()
width, height = base_img.size

for material, color_hex in colors.items():
    # extract r, g, b from color_hex
    tr = (color_hex >> 16) & 0xFF
    tg = (color_hex >> 8) & 0xFF
    tb = color_hex & 0xFF

    tinted = Image.new("RGBA", (width, height))
    t_pixels = tinted.load()

    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            if a > 0:
                # Multiply color
                nr = int(r * (tr / 255.0))
                ng = int(g * (tg / 255.0))
                nb = int(b * (tb / 255.0))
                t_pixels[x, y] = (nr, ng, nb, a)
            else:
                t_pixels[x, y] = (0, 0, 0, 0)

    out_path = f"jetpack_{material}.png"
    tinted.save(out_path)
    print(f"Saved {out_path}")
