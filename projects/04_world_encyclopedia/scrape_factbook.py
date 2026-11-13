#!/usr/bin/env python3
"""Scrape the "Background" blurb of every territory on theworldfactbook.org
into lab/data/corpus/<slug>.txt, one file per territory, for use as the
search project's real corpus.

Not part of the student handout: lives outside lab/, so setup.sh's zip never
picks it up.

Usage:
    python3 scrape_factbook.py [--limit N] [--delay SECONDS] [--out DIR]
"""
import argparse
import html
import re
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path

BASE = "https://theworldfactbook.org"
INDEX_URL = f"{BASE}/country/"
USER_AGENT = "CS102-Factbook-Scraper/1.0 (educational use; one-time run)"

LINK_RE = re.compile(r'href="(/country/[^"]*\.html)"')
CANONICAL_RE = re.compile(r'<link rel="canonical" href="([^"]*)"')
H1_RE = re.compile(r"<h1[^>]*>(.*?)</h1>", re.S)
SPAN_RE = re.compile(r"<span>(.*?)</span>")
BACKGROUND_RE = re.compile(
    r'id="section-background".*?<p class="deck country-section-intro">(.*?)</p>', re.S
)
TAG_RE = re.compile(r"<[^>]+>")

# The site's own URL slugs are mostly clear (they match the territory name), but a few are
# cryptic two-letter codes or absurdly long concatenations of every island in a group. Override
# just those, keyed by the original slug, so filenames stay readable without renaming everything.
SLUG_OVERRIDES = {
    "ae": "united-arab-emirates",
    "cg": "congo-dr",
    "ct": "central-african-republic",
    "dr": "dominican-republic",
    "ee": "european-union",
    "fm": "micronesia",
    "io": "british-indian-ocean-territory",
    "sh": "saint-helena",
    "svalbard-sometimes-referred-to-as-spitsbergen-the-largest-island-in-the-archipelago": "svalbard",
    "baker-island-howland-island-jarvis-island-johnston-atoll-kingman-reef-midway-islands-palmyra-atoll":
        "pacific-remote-islands",
}


def fetch(url: str) -> str:
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(request, timeout=15) as response:
        return response.read().decode("utf-8")


def fetch_with_retry(url: str, attempts: int = 2) -> str | None:
    for attempt in range(attempts):
        try:
            return fetch(url)
        except (urllib.error.URLError, TimeoutError) as e:
            if attempt + 1 == attempts:
                print(f"  FAILED: {url}: {e}", file=sys.stderr)
                return None
            time.sleep(1)
    return None


def clean_text(fragment: str) -> str:
    text = TAG_RE.sub("", fragment)
    text = html.unescape(text)
    # collapse runs of spaces/tabs, but keep paragraph breaks (blank lines) as one blank line
    paragraphs = [" ".join(p.split()) for p in re.split(r"\n\s*\n", text)]
    return "\n\n".join(p for p in paragraphs if p).strip()


def extract_name(html_text: str) -> str | None:
    m = H1_RE.search(html_text)
    if not m:
        return None
    spans = SPAN_RE.findall(m.group(1))
    if not spans:
        return None
    return html.unescape(spans[-1]).strip()


def extract_background(html_text: str) -> str | None:
    m = BACKGROUND_RE.search(html_text)
    if not m:
        return None
    text = clean_text(m.group(1))
    return text or None


def collect_links() -> list[str]:
    index_html = fetch(INDEX_URL)
    links = sorted(set(LINK_RE.findall(index_html)))
    return links


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--limit", type=int, default=None, help="scrape only the first N territories")
    parser.add_argument("--delay", type=float, default=0.3, help="seconds to sleep between requests")
    parser.add_argument(
        "--out", type=Path, default=Path(__file__).parent / "lab" / "data" / "corpus",
        help="output directory (default: lab/data/corpus)",
    )
    args = parser.parse_args()

    print(f"Fetching {INDEX_URL} ...")
    links = collect_links()
    if args.limit:
        links = links[: args.limit]
    print(f"{len(links)} territory pages found.")

    args.out.mkdir(parents=True, exist_ok=True)

    saved = skipped = failed = 0
    for i, link in enumerate(links, 1):
        url = BASE + link
        slug = link.removeprefix("/country/").removesuffix(".html")
        slug = SLUG_OVERRIDES.get(slug, slug)
        print(f"[{i}/{len(links)}] {slug} ...", end=" ")

        page = fetch_with_retry(url)
        if page is None:
            failed += 1
            continue

        canonical = CANONICAL_RE.search(page)
        if canonical and canonical.group(1) != url:
            print(f"skipped (alias of {canonical.group(1)})")
            skipped += 1
            continue

        name = extract_name(page)
        background = extract_background(page)
        if not background:
            print("skipped (no Background section)")
            skipped += 1
            continue

        (args.out / f"{slug}.txt").write_text(background, encoding="utf-8")
        print(f"saved ({name}, {len(background)} chars)")
        saved += 1

        time.sleep(args.delay)

    print(f"\n{saved} saved, {skipped} skipped, {failed} failed. Output in {args.out}")


if __name__ == "__main__":
    main()
