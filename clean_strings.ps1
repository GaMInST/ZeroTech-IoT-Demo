$content = Get-Content 'app/src/main/res/values/strings.xml'
$seen = @{}
$cleanContent = @()
$firstXml = $true

foreach($line in $content) {
    if($line -match '<\?xml') {
        if($firstXml) {
            $cleanContent += $line
            $firstXml = $false
        }
        # Skip subsequent XML declarations
    } elseif($line -match '<string name="([^"]+)"') {
        $name = $matches[1]
        if($seen.ContainsKey($name)) {
            # Skip duplicate
            continue
        } else {
            $seen[$name] = $true
            $cleanContent += $line
        }
    } else {
        $cleanContent += $line
    }
}

$cleanContent | Set-Content 'app/src/main/res/values/strings.xml'
Write-Host "Cleaned strings.xml - removed duplicates and extra XML declarations" 