$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
[xml]$pom = Get-Content (Join-Path $scriptDir '..\pom.xml')
$nsmgr = New-Object System.Xml.XmlNamespaceManager($pom.NameTable)
$nsmgr.AddNamespace('m','http://maven.apache.org/POM/4.0.0')
$deps = $pom.SelectNodes('//m:dependency', $nsmgr)
$i = 0
foreach ($d in $deps) {
    $i++
    Write-Host "--- dependency #$i ---"
    Write-Host $d.OuterXml
}
Write-Host "Total: $i dependencies"
