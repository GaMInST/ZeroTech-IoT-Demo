# Fix the malformed string entry in strings.xml
$stringsFile = "app\src\main\res\values\strings.xml"

# Read the file content
$content = Get-Content $stringsFile -Raw

# Fix the malformed line 1331
# Replace the problematic line with proper XML structure
$content = $content -replace '<string name="hs_repair_person">Repair person</string>\s*The alarm has not been sent to the police alarming platform</string>', '<string name="hs_repair_person">Repair person</string>
  <string name="hs_repair_person_alarm_not_sent">The alarm has not been sent to the police alarming platform</string>'

# Write the fixed content back to the file
$content | Set-Content $stringsFile -Encoding UTF8

Write-Host "Fixed malformed string entry in strings.xml" 