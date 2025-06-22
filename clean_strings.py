#!/usr/bin/env python3
import re
import sys

def clean_strings_xml(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove all XML declarations except the first one
    lines = content.split('\n')
    first_xml_found = False
    cleaned_lines = []
    
    for line in lines:
        if line.strip().startswith('<?xml'):
            if not first_xml_found:
                cleaned_lines.append(line)
                first_xml_found = True
            # Skip subsequent XML declarations
        else:
            cleaned_lines.append(line)
    
    # Now remove duplicate string entries
    seen_strings = set()
    final_lines = []
    
    for line in cleaned_lines:
        # Check if this is a string definition
        match = re.search(r'<string name="([^"]+)"', line)
        if match:
            string_name = match.group(1)
            if string_name in seen_strings:
                # Skip this duplicate
                continue
            else:
                seen_strings.add(string_name)
                final_lines.append(line)
        else:
            # Keep non-string lines
            final_lines.append(line)
    
    # Write back to file
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(final_lines))
    
    print(f"Cleaned {file_path}")
    print(f"Removed {len(lines) - len(final_lines)} duplicate lines")

if __name__ == "__main__":
    file_path = "app/src/main/res/values/strings.xml"
    clean_strings_xml(file_path) 