from pathlib import Path
from PIL import Image, ImageDraw

root = Path(__file__).resolve().parents[1] / 'dist'
for size in (180, 192, 512):
    scale = size * 4 / 192
    im = Image.new('RGB', (size * 4, size * 4), '#164e40')
    draw = ImageDraw.Draw(im)
    def box(points):
        return [round(p * scale) for p in points]
    draw.rectangle(box((57, 63, 77, 132)), fill='#d8ed9d')
    draw.rounded_rectangle(box((64, 63, 128, 113)), radius=round(23*scale), fill='#d8ed9d')
    draw.rounded_rectangle(box((76, 80, 107, 95)), radius=round(7*scale), fill='#164e40')
    draw.polygon([tuple(box(p)) for p in [(84,101),(108,101),(134,132),(105,132),(77,112)]], fill='#d8ed9d')
    draw.ellipse(box((129, 41, 149, 61)), fill='#d8ed9d')
    im.resize((size, size), Image.Resampling.LANCZOS).save(root / f'icon-{size}.png')
