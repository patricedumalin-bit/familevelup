from PIL import Image, ImageDraw
import os
import math

sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

def draw_icon(size):
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    radius = int(size * 0.22)
    draw.rounded_rectangle([(0, 0), (size-1, size-1)], radius=radius, fill="#8B5CF6")

    center = size // 2
    for r in range(int(size * 0.7), 0, -2):
        alpha = int(30 * (1 - r / (size * 0.7)))
        draw.ellipse([(center - r, center - r), (center + r, center + r)], fill=(255, 255, 255, alpha))

    s = size / 400.0

    def sc(val):
        return int(val * s)

    # House Roof
    roof_pts = [(sc(200), sc(150)), (sc(260), sc(200)), (sc(140), sc(200))]
    draw.polygon(roof_pts, fill="#7C3AED", outline="#FFFFFF", width=max(1, sc(4)))

    # House Body / Checklist Card
    card_box = [sc(158), sc(198), sc(242), sc(270)]
    draw.rounded_rectangle(card_box, radius=sc(6), fill="#F1F5F9", outline="#FFFFFF", width=max(1, sc(2.5)))

    # Checklist items
    for y_offset in [208, 224, 240]:
        draw.rectangle([sc(164), sc(y_offset), sc(174), sc(y_offset + 10)], fill="#7C3AED")
        line_len = 48 if y_offset == 208 else (40 if y_offset == 224 else 32)
        draw.rectangle([sc(180), sc(y_offset + 3), sc(180 + line_len), sc(y_offset + 7)], fill="#64748B")

    # Star on top of roof
    star_center = (sc(200), sc(126))
    star_r1 = sc(16)
    star_r2 = sc(8)
    star_pts = []
    for i in range(10):
        angle = math.pi * i / 5 - math.pi / 2
        r = star_r1 if i % 2 == 0 else star_r2
        star_pts.append((star_center[0] + r * math.cos(angle), star_center[1] + r * math.sin(angle)))
    draw.polygon(star_pts, fill="#FBBF24", outline="#FFFFFF", width=max(1, sc(2)))

    # Level-up Badge
    badge_center = (sc(236), sc(250))
    badge_r = sc(20)
    draw.ellipse([badge_center[0] - badge_r, badge_center[1] - badge_r, badge_center[0] + badge_r, badge_center[1] + badge_r], fill="#6D28D9", outline="#FFFFFF", width=max(1, sc(4)))

    arrow_pts1 = [(sc(228), sc(255)), (sc(236), sc(245)), (sc(244), sc(255))]
    arrow_pts2 = [(sc(228), sc(247)), (sc(236), sc(237)), (sc(244), sc(247))]
    draw.line(arrow_pts1, fill="#FFFFFF", width=max(1, sc(3.5)))
    draw.line(arrow_pts2, fill="#FFFFFF", width=max(1, sc(3.5)))

    return img

base_dir = "G:/SlyDroidStudio/Developpement/familevelup/app/src/main/res"
for folder, sz in sizes.items():
    res_path = os.path.join(base_dir, folder)
    os.makedirs(res_path, exist_ok=True)

    icon_img = draw_icon(sz)
    icon_img.save(os.path.join(res_path, "ic_launcher.png"))

    round_img = Image.new('RGBA', (sz, sz), (0, 0, 0, 0))
    mask = Image.new('L', (sz, sz), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([(0, 0), (sz-1, sz-1)], fill=255)
    round_img.paste(icon_img, (0, 0), mask=mask)
    round_img.save(os.path.join(res_path, "ic_launcher_round.png"))

print("Icons generated successfully!")
