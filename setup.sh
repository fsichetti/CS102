#!/usr/bin/env bash
# Builds one lab activity: instructions.pdf into lab/, then zips lab/ as <activity>.zip
set -euo pipefail
cd "$(dirname "$0")"

dir=${1:?usage: setup.sh <activity>   e.g. setup.sh warmup}
dir=${dir%/}   # tab completion adds a trailing slash

pandoc "$dir/instructions.md" -o "$dir/lab/instructions.pdf" -V colorlinks=true -V geometry:margin=0.75in
rm -f "$dir/$dir.zip"   # zip appends to an existing archive
(cd "$dir" && zip -qr "$dir.zip" lab)
echo "$dir: lab/instructions.pdf, $dir.zip"
