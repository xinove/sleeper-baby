"""Recorta ilustraciones a un cuadrado lleno, sin bandas ni marcos."""

from __future__ import annotations

from pathlib import Path

from PIL import Image


ASSETS = Path(
    r"C:\Users\CheChu\.cursor\projects\c-Users-CheChu-sleeper-baby\assets"
)
OUT = Path(r"c:\Users\CheChu\sleeper-baby\app\src\main\res\drawable-xxhdpi")
RES = OUT


def _px(im: Image.Image, x: int, y: int) -> tuple[int, int, int]:
    p = im.getpixel((x, y))
    return (p[0], p[1], p[2])


def _is_margin(c: tuple[int, int, int], corners: list[tuple[int, int, int]]) -> bool:
    r, g, b = c
    if r >= 226 and g >= 216 and b >= 196 and abs(r - g) < 40:
        return True
    if r > 246 and g > 246 and b > 246:
        return True
    mx, mn = max(r, g, b), min(r, g, b)
    if mx < 72 and (mx - mn) < 38:
        return True
    for cr, cg, cb in corners:
        if abs(r - cr) + abs(g - cg) + abs(b - cb) < 38:
            return True
    return False


def _corner_samples(im: Image.Image) -> list[tuple[int, int, int]]:
    w, h = im.size
    samples: list[tuple[int, int, int]] = []
    for sx, sy in ((0, 0), (w - 1, 0), (0, h - 1), (w - 1, h - 1)):
        for i in range(10):
            for j in range(10):
                x = min(w - 1, max(0, sx + i if sx == 0 else sx - i))
                y = min(h - 1, max(0, sy + j if sy == 0 else sy - j))
                samples.append(_px(im, x, y))
    step = max(1, len(samples) // 24)
    return samples[::step]


def _row_spread(im: Image.Image, y: int, x0: int, x1: int) -> int:
    acc = 0
    n = 0
    for x in range(x0, x1, 4):
        r, g, b = _px(im, x, y)
        acc += abs(r - g) + abs(g - b) + abs(r - b)
        n += 1
    return acc // max(1, n)


def _col_spread(im: Image.Image, x: int, y0: int, y1: int) -> int:
    acc = 0
    n = 0
    for y in range(y0, y1, 4):
        r, g, b = _px(im, x, y)
        acc += abs(r - g) + abs(g - b) + abs(r - b)
        n += 1
    return acc // max(1, n)


def content_box(im: Image.Image, extra_inset: float = 0.02) -> tuple[int, int, int, int]:
    rgb = im.convert("RGB")
    w, h = rgb.size
    corners = _corner_samples(rgb)
    x0, y0, x1, y1 = w, h, 0, 0
    step = 2 if max(w, h) > 400 else 1
    for y in range(0, h, step):
        for x in range(0, w, step):
            if not _is_margin(_px(rgb, x, y), corners):
                if x < x0:
                    x0 = x
                if y < y0:
                    y0 = y
                if x > x1:
                    x1 = x
                if y > y1:
                    y1 = y
    if x1 <= x0 or y1 <= y0:
        return (0, 0, w, h)

    # Recorta bordes casi planos (marco azul, glow, página de libro).
    for _ in range(80):
        if y1 - y0 < 40:
            break
        if _row_spread(rgb, y0, x0, x1) < 14:
            y0 += 2
            continue
        if _row_spread(rgb, y1 - 1, x0, x1) < 14:
            y1 -= 2
            continue
        if _col_spread(rgb, x0, y0, y1) < 14:
            x0 += 2
            continue
        if _col_spread(rgb, x1 - 1, y0, y1) < 14:
            x1 -= 2
            continue
        break

    pad = int(min(x1 - x0, y1 - y0) * extra_inset)
    x0 = max(0, x0 + pad)
    y0 = max(0, y0 + pad)
    x1 = min(w, x1 - pad)
    y1 = min(h, y1 - pad)
    return (x0, y0, x1, y1)


def to_square(im: Image.Image, extra_inset: float = 0.02, align: str = "center") -> Image.Image:
    box = content_box(im, extra_inset=extra_inset)
    crop = im.convert("RGBA").crop(box)
    cw, ch = crop.size
    side = min(cw, ch)
    if align == "center":
        left = (cw - side) // 2
        top = (ch - side) // 2
    elif align == "top":
        left = (cw - side) // 2
        top = 0
    else:
        left = (cw - side) // 2
        top = ch - side
    square = crop.crop((left, top, left + side, top + side))
    return square.resize((768, 768), Image.Resampling.LANCZOS)


def save(im: Image.Image, name: str) -> None:
    path = OUT / name
    im.convert("RGBA").save(path, "PNG")
    print("wrote", path.name, im.size)


def load_original(fragment: str) -> Image.Image | None:
    matches = list(ASSETS.glob(f"*{fragment}*"))
    if not matches:
        return None
    return Image.open(matches[0])


def main() -> None:
    jobs: list[tuple[str, Image.Image, float, str]] = []

    originals = {
        "ill_guille_elige.png": ("30097ff4", 0.015, "center"),
        "ill_estrella_elige.png": ("d0a577b2", 0.02, "center"),
        "ill_capitan_luciernaga.png": ("daf4f347", 0.04, "center"),
        "ill_superabuela.png": ("bbb1b1de", 0.03, "center"),
    }
    for name, (frag, inset, align) in originals.items():
        src = load_original(frag)
        if src is None:
            src = Image.open(RES / name)
        jobs.append((name, src, inset, align))

    story_covers = [
        "ill_caperucita.png",
        "ill_tres_cerditos.png",
        "ill_ricitos.png",
        "ill_patito_feo.png",
        "ill_cenicienta.png",
        "ill_bella_durmiente.png",
        "ill_gato_con_botas.png",
        "ill_princesa_guisante.png",
        "ill_liebre_tortuga.png",
        "ill_pulgarcito.png",
        "ill_soldadito.png",
        "ill_rapunzel.png",
        "ill_nube_cohete.png",
        "ill_robot_dormilon.png",
        "ill_estrella_navidad.png",
        "ill_nino_elige.png",
    ]
    for name in story_covers:
        path = RES / name
        if path.exists():
            jobs.append((name, Image.open(path), 0.01, "center"))

    shelves = [
        ("ill_shelf_classic.png", "top"),
        ("ill_shelf_modern.png", "center"),
        ("ill_shelf_christmas.png", "center"),
        ("ill_shelf_heroes.png", "center"),
        ("ill_shelf_magic.png", "center"),
        ("ill_shelf_princesses.png", "center"),
        ("ill_shelf_pirates.png", "center"),
    ]
    for name, align in shelves:
        path = RES / name
        if path.exists():
            jobs.append((name, Image.open(path), 0.04, align))

    for name, src, inset, align in jobs:
        # Luciérnaga / Superabuela: quita la barra de reproductor de la captura.
        rgb = src.convert("RGB")
        w, h = rgb.size
        if name == "ill_capitan_luciernaga.png" and h < 700:
            src = src.crop((0, 0, w, int(h * 0.85)))
        if name == "ill_superabuela.png" and h >= 900:
            src = src.crop((0, 0, w, int(h * 0.90)))
        fitted = to_square(src, extra_inset=inset, align=align)
        save(fitted, name)


if __name__ == "__main__":
    main()
