# PowerShell script to fix XML structure in strings.xml
$file = 'app\src\main\res\values\strings.xml'
$lines = Get-Content $file
$fixed = @()
$inResource = $false
$resourceStarted = $false
foreach ($line in $lines) {
    if ($line -match '<resources') {
        if (-not $resourceStarted) {
            $fixed += $line
            $inResource = $true
            $resourceStarted = $true
        }
        # skip extra <resources> tags
    } elseif ($line -match '</resources>') {
        $inResource = $false
        # skip all </resources> tags for now
    } elseif ($inResource -or $resourceStarted) {
        $fixed += $line
    }
}
# Ensure only one closing </resources> at the end
$fixed += '</resources>'
$fixed | Set-Content $file -Encoding UTF8
Write-Host 'XML structure in strings.xml has been fixed.' 