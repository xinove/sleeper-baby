"""
Genera MP3 de los cuentos con Edge TTS, igual que en audioguiatravel.

Uso:
  python tools/generate_story_audio.py
  python tools/generate_story_audio.py --dry-run
  python tools/generate_story_audio.py caperucita

Requisitos: pip install edge-tts
"""
from __future__ import annotations

import argparse
import asyncio
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
CATALOG = ROOT / "app" / "src" / "main" / "java" / "com" / "sleeperbaby" / "app" / "library" / "StoryCatalog.kt"
OUT_DIR = ROOT / "app" / "src" / "main" / "assets" / "stories"

VOICE = "es-ES-XimenaNeural"
RATE = "-4%"
PITCH = "+0Hz"
MAX_CHARS = 4500
CLOSING = "¡Buenas noches, chicos!"

FILE_BY_ID = {
    "Caperucita": "caperucita",
    "TresCerditos": "tres_cerditos",
    "Ricitos": "ricitos",
    "PatitoFeo": "patito_feo",
    "Cenicienta": "cenicienta",
    "BellaDurmiente": "bella_durmiente",
    "GatoConBotas": "gato_con_botas",
    "PrincesaGuisante": "princesa_guisante",
    "LiebreTortuga": "liebre_tortuga",
    "Pulgarcito": "pulgarcito",
    "Soldadito": "soldadito",
    "Rapunzel": "rapunzel",
    "NubeCohete": "nube_cohete",
    "RobotDormilon": "robot_dormilon",
    "EstrellaNavidad": "estrella_navidad",
    "RenoCalcetin": "reno_calcetin",
    "CapitanLuciernaga": "capitan_luciernaga",
    "Superabuela": "superabuela",
    "BosqueSusurros": "bosque_susurros",
    "PrincesaNube": "princesa_nube",
    "GuillePirata": "guille_pirata",
    "TesoroGalleta": "tesoro_galleta",
    "IslaSiesta": "isla_siesta",
    "DueloCumplidos": "duelo_cumplidos",
    "GuilleElige": "guille_elige",
    "EstrellaElige": "estrella_elige",
    "NinoElige": "nino_elige",
}

NODE_RE = re.compile(
    r"StoryNode\(\s*// audio:(?P<file>\w+)\s*id = \"(?P<id>[^\"]+)\","
    r"\s*paragraphs = listOf\((?P<body>.*?)\n\s*\),"
    r"(?:\s*question = \"(?P<question>[^\"]+)\",)?",
    re.S,
)

STORY_RE = re.compile(
    r"Story\(\s*id = StoryId\.(?P<id>\w+),\s*title = \"(?P<title>[^\"]+)\",.*?"
    r"paragraphs = listOf\((?P<body>.*?)\n\s*\),",
    re.S,
)
STRING_RE = re.compile(r'"((?:\\.|[^"\\])*)"')


def unescape(text: str) -> str:
    return (
        text.replace("\\n", " ")
        .replace('\\"', '"')
        .replace("\\'", "'")
    )


def normalize(text: str) -> str:
    text = text.replace("\n", " ")
    return re.sub(r"\s+", " ", text).strip()


def parse_catalog(path: Path) -> list[dict]:
    source = path.read_text(encoding="utf-8")
    stories: list[dict] = []
    titles: dict[str, str] = {}
    for match in STORY_RE.finditer(source):
        story_id = match.group("id")
        title = match.group("title")
        paragraphs = [unescape(item) for item in STRING_RE.findall(match.group("body"))]
        narration = normalize(f"{title}. {' '.join(paragraphs)} {CLOSING}")
        file_stem = FILE_BY_ID.get(story_id)
        if not file_stem:
            raise SystemExit(f"Falta nombre de audio para {story_id}")
        titles[file_stem] = title
        stories.append(
            {
                "id": story_id,
                "title": title,
                "file": f"{file_stem}.mp3",
                "text": narration,
            }
        )
    if len(stories) != len(FILE_BY_ID):
        raise SystemExit(f"Se esperaban {len(FILE_BY_ID)} cuentos, hay {len(stories)}")

    nodes: list[dict] = []
    for match in NODE_RE.finditer(source):
        file_stem = match.group("file")
        paragraphs = [unescape(item) for item in STRING_RE.findall(match.group("body"))]
        question = match.group("question")
        is_start = file_stem in FILE_BY_ID.values()
        parts: list[str] = []
        if is_start:
            parts.append(f"{titles.get(file_stem, '')}.")
        parts.extend(paragraphs)
        if question:
            parts.append(question)
        else:
            parts.append(CLOSING)
        nodes.append(
            {
                "id": match.group("id"),
                "title": next(
                    (titles[stem] for stem in sorted(titles, key=len, reverse=True) if file_stem == stem or file_stem.startswith(f"{stem}_")),
                    file_stem,
                ),
                "file": f"{file_stem}.mp3",
                "text": normalize(" ".join(parts)),
            }
        )

    covered = {item["file"] for item in nodes}
    linear = [item for item in stories if item["file"] not in covered]
    return linear + nodes


def chunk_text(text: str, max_len: int = MAX_CHARS) -> list[str]:
    if len(text) <= max_len:
        return [text]
    chunks: list[str] = []
    sentences = re.split(r"(?<=[.!?])\s+", text)
    current = ""
    for sentence in sentences:
        if len(current) + len(sentence) + 1 <= max_len:
            current = f"{current} {sentence}".strip()
        else:
            if current:
                chunks.append(current)
            current = sentence
    if current:
        chunks.append(current)
    return chunks


async def synthesize_to_mp3(text: str, output: Path) -> None:
    import edge_tts

    output.parent.mkdir(parents=True, exist_ok=True)
    chunks = chunk_text(text)
    if len(chunks) == 1:
        communicate = edge_tts.Communicate(chunks[0], VOICE, rate=RATE, pitch=PITCH)
        await communicate.save(str(output))
        return

    temp_files: list[Path] = []
    for index, chunk in enumerate(chunks):
        temp = output.with_suffix(f".part{index}.mp3")
        communicate = edge_tts.Communicate(chunk, VOICE, rate=RATE, pitch=PITCH)
        await communicate.save(str(temp))
        temp_files.append(temp)

    try:
        from pydub import AudioSegment

        combined = AudioSegment.empty()
        for temp in temp_files:
            combined += AudioSegment.from_mp3(temp)
        combined.export(str(output), format="mp3", bitrate="64k")
    except ImportError:
        import shutil
        import subprocess

        if not shutil.which("ffmpeg"):
            temp_files[0].replace(output)
            print(f"  AVISO: texto largo y no hay pydub/ffmpeg; truncado -> {output.name}")
        else:
            list_file = output.with_suffix(".txt")
            list_file.write_text(
                "\n".join(f"file '{temp.resolve().as_posix()}'" for temp in temp_files),
                encoding="utf-8",
            )
            subprocess.run(
                [
                    "ffmpeg",
                    "-y",
                    "-f",
                    "concat",
                    "-safe",
                    "0",
                    "-i",
                    str(list_file),
                    "-c",
                    "copy",
                    str(output),
                ],
                check=True,
                capture_output=True,
            )
            list_file.unlink(missing_ok=True)

    for temp in temp_files:
        if temp.exists() and temp != output:
            temp.unlink(missing_ok=True)


async def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("story", nargs="?", help="stem del fichero, p.ej. caperucita")
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    stories = parse_catalog(CATALOG)
    if args.story:
        stories = [item for item in stories if item["file"].startswith(args.story)]
        if not stories:
            print(f"No se encontró el cuento: {args.story}")
            return 1

    if args.dry_run:
        for item in stories:
            print(f"{item['file']:24} {len(item['text']):5} chars  {item['title']}")
        return 0

    try:
        import edge_tts  # noqa: F401
    except ImportError:
        print("Instala: pip install edge-tts")
        return 1

    total = 0
    errors = 0
    print(f"Voz={VOICE} rate={RATE} pitch={PITCH}")
    for item in stories:
        out = OUT_DIR / item["file"]
        print(f"  {item['title']} -> {out.name} ({len(item['text'])} chars)...", end=" ", flush=True)
        try:
            await synthesize_to_mp3(item["text"], out)
            size_kb = out.stat().st_size // 1024
            print(f"OK ({size_kb} KB)")
            total += 1
        except Exception as exc:
            print(f"ERROR: {exc}")
            errors += 1

    print(f"\nListo: {total} archivos, {errors} errores.")
    print(f"Carpeta: {OUT_DIR}")
    return 0 if errors == 0 else 1


if __name__ == "__main__":
    sys.exit(asyncio.run(main()))
