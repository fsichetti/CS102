#!/usr/bin/env bash
# Regenerates the PDFs of one quiz (or exam) folder, next to their sources.
# For each <folder>/<name>.tex, produces <folder>/<name>.pdf (no answers, for students)
# and <folder>/<name>_key.pdf (answers shown, for the instructor).
# The page header reads "Quiz N" if the folder name contains a number (e.g. quiz04),
# otherwise it reads "Midterm" (e.g. for a folder named "midterm").
set -euo pipefail
cd "$(dirname "$0")"

dir=${1:?usage: build.sh <folder>   e.g. build.sh quiz02}
dir=${dir%/}   # tab completion adds a trailing slash
num=$(echo "$dir" | tr -dc 0-9 | sed 's/^0*//')
label="Quiz $num"
[ -z "$num" ] && label="Midterm"

for file in "$dir"/*.tex; do
    name=$(basename "$file" .tex)
    for variant in key student; do
        wrapper="_wrapper_${name}_${variant}.tex"
        toggle="\\answerstrue"
        [ "$variant" = "student" ] && toggle="\\answersfalse"
        printf '\\def\\examlabel{%s}\n\\input{quizpreamble}\n%s\n\\begin{document}\n\\input{%s}\n\\end{document}\n' \
            "$label" "$toggle" "$dir/$name" > "$wrapper"

        jobname="$name"
        [ "$variant" = "key" ] && jobname="${name}_key"

        for _ in 1 2; do
            pdflatex -interaction=nonstopmode -output-directory="$dir" \
                -jobname="$jobname" "$wrapper" >/dev/null
        done
        rm -f "$wrapper"
    done
    echo "$name: $dir/${name}.pdf (student), $dir/${name}_key.pdf (answer key)"
done

rm -f "$dir"/*.aux "$dir"/*.log "$dir"/*.out
