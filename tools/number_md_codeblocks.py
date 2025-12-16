"""Add line numbers to every fenced code block in DOCS/*.md files.

Usage: run from workspace root with the project's Python environment.
This script will modify files in-place and create a .bak backup for each file.
"""
from pathlib import Path
import re

DOCS = Path(__file__).resolve().parents[1] / 'DOCS'

fence_re = re.compile(r'^(?P<fence>```(?P<lang>\S*)?)\s*$')

changed_files = []

for md in sorted(DOCS.glob('*.md')):
    text = md.read_text(encoding='utf-8')
    lines = text.splitlines()
    out_lines = []
    in_code = False
    code_buf = []
    fence_line = None
    for line in lines:
        m = fence_re.match(line)
        if m:
            if not in_code:
                # start fence
                in_code = True
                fence_line = line
                out_lines.append(line)
                code_buf = []
            else:
                # end fence - emit numbered code lines
                in_code = False
                # number lines starting at 1
                numbered = []
                width = len(str(max(1, len(code_buf))))
                for i, cl in enumerate(code_buf, start=1):
                    # keep original line endings/indent
                    numbered.append(f"{str(i).rjust(width)}: {cl}")
                out_lines.extend(numbered)
                out_lines.append(line)
                code_buf = []
                fence_line = None
        else:
            if in_code:
                code_buf.append(line)
            else:
                out_lines.append(line)
    new_text = "\n".join(out_lines) + ("\n" if text.endswith("\n") else "")
    if new_text != text:
        # backup
        bak = md.with_suffix(md.suffix + '.bak')
        md.rename(bak)
        md.write_text(new_text, encoding='utf-8')
        changed_files.append(md.name)

if changed_files:
    print('Updated files:')
    for f in changed_files:
        print(' -', f)
else:
    print('No changes needed.')
