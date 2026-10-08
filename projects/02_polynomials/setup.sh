#!/usr/bin/env bash
# Builds the project handout: instructions.pdf into lab/, then zips lab/ as <project>.zip
set -euo pipefail
cd "$(dirname "$0")"
name=$(basename "$PWD")

pandoc instructions.md -o lab/instructions.pdf -V colorlinks=true -V geometry:margin=0.75in
rm -f "$name.zip"   # zip appends to an existing archive
zip -qr "$name.zip" lab -x 'lab/out/*' '*.class'
echo "$name: lab/instructions.pdf, $name.zip"
