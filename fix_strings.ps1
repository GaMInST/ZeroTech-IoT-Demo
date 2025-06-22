$content = Get-Content 'app/src/main/res/values/strings.xml'
$cleanContent = @()
$firstXml = $true
$inString = $false
$currentString = ""

foreach($line in $content) {
    if($line -match '<\?xml') {
        if($firstXml) {
            $cleanContent += $line
            $firstXml = $false
        }
        # Skip subsequent XML declarations
    } elseif($line -match '<string name="([^"]+)"') {
        # Start of a new string
        if($inString) {
            # Previous string wasn't properly closed, add it
            $cleanContent += $currentString
        }
        $inString = $true
        $currentString = $line
    } elseif($line -match '</string>') {
        # End of string
        if($inString) {
            $currentString += $line
            $cleanContent += $currentString
            $inString = $false
            $currentString = ""
        }
    } elseif($inString) {
        # Content inside string
        $currentString += $line
    } elseif($line -match '^[^<]*$' -and $line.Trim() -ne "") {
        # Orphaned text content, skip it
        continue
    } else {
        # Other XML content (like comments, closing tags, etc.)
        $cleanContent += $line
    }
}

# Add any remaining string
if($inString -and $currentString -ne "") {
    $cleanContent += $currentString
}

$cleanContent | Set-Content 'app/src/main/res/values/strings.xml'
Write-Host "Fixed strings.xml - removed orphaned content and ensured proper XML structure" 