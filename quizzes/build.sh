#!/usr/bin/env bash
# Regenerates the PDFs of one quiz folder into output/ (gitignored).
# For each <folder>/<name>.tex, produces output/<name>.pdf (no answers, for students)
# and output/<name>_key.pdf (answers shown, for the instructor).
# The quiz number in the page header is the number in the folder name.
set -euo pipefail
cd "$(dirname "$0")"

dir=${1:?usage: build.sh <folder>   e.g. build.sh quiz02}
dir=${dir%/}   # tab completion adds a trailing slash
num=$(echo "$dir" | tr -dc 0-9 | sed 's/^0*//')
mkdir -p output

for file in "$dir"/*.tex; do
    name=$(basename "$file" .tex)
    for variant in key student; do
        wrapper="_wrapper_${name}_${variant}.tex"
        toggle="\\answerstrue"
        [ "$variant" = "student" ] && toggle="\\answersfalse"
        printf '\\def\\quiznum{%s}\n\\input{quizpreamble}\n%s\n\\begin{document}\n\\input{%s}\n\\end{document}\n' \
            "$num" "$toggle" "$dir/$name" > "$wrapper"

        jobname="$name"
        [ "$variant" = "key" ] && jobname="${name}_key"

        for _ in 1 2; do
            pdflatex -interaction=nonstopmode -output-directory=output \
                -jobname="$jobname" "$wrapper" >/dev/null
        done
        rm -f "$wrapper"
    done
    echo "$name: output/${name}.pdf (student), output/${name}_key.pdf (answer key)"
done

rm -f output/*.aux output/*.log output/*.out
