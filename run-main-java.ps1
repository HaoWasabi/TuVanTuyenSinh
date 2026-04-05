$ErrorActionPreference = 'Stop'

$repo = Join-Path $env:USERPROFILE '.m2\repository'
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$mainClass = if ($args.Count -gt 0 -and $args[0]) { $args[0] } else { 'com.tuyensinh.Main' }

function Get-VersionKey {
    param([string]$VersionName)

    $match = [regex]::Match($VersionName, '^\d+(\.\d+){0,3}')
    if ($match.Success) {
        try {
            return [version]$match.Value
        } catch {
            return [version]'0.0.0.0'
        }
    }

    return [version]'0.0.0.0'
}

function Add-LatestJar {
    param(
        [string[]]$ArtifactPaths,
        [string]$JarPrefix,
        [System.Collections.Generic.List[string]]$List,
        [bool]$Required = $false
    )

    foreach ($artifactPath in $ArtifactPaths) {
        $artifactDir = Join-Path $repo $artifactPath
        if (-not (Test-Path $artifactDir)) {
            continue
        }

        $versionDirs = Get-ChildItem $artifactDir -Directory |
            Sort-Object @{ Expression = { Get-VersionKey $_.Name } } -Descending
        foreach ($ver in $versionDirs) {
            $candidate = Join-Path $ver.FullName ("$JarPrefix-" + $ver.Name + ".jar")
            if (Test-Path $candidate) {
                $List.Add($candidate)
                return
            }

            $anyJar = Get-ChildItem $ver.FullName -Filter '*.jar' |
                Where-Object { $_.Name -notmatch '(-sources|-javadoc|tests?)\.jar$' } |
                Sort-Object Name |
                Select-Object -First 1
            if ($anyJar) {
                $List.Add($anyJar.FullName)
                return
            }
        }
    }

    if ($Required) {
        Write-Host "[WARN] Missing required artifact: $JarPrefix" -ForegroundColor Yellow
    }
}

$priorityJars = New-Object 'System.Collections.Generic.List[string]'

# Core runtime
Add-LatestJar @('jakarta\persistence\jakarta.persistence-api') 'jakarta.persistence-api' $priorityJars $true
Add-LatestJar @('org\hibernate\orm\hibernate-core') 'hibernate-core' $priorityJars $true
Add-LatestJar @('com\mysql\mysql-connector-j') 'mysql-connector-j' $priorityJars $true
Add-LatestJar @('org\slf4j\slf4j-api') 'slf4j-api' $priorityJars $true
Add-LatestJar @('org\slf4j\slf4j-simple', 'org\slf4j\slf4j-jdk14') 'slf4j-simple' $priorityJars $false

# Hibernate transitives (best-effort)
Add-LatestJar @('jakarta\transaction\jakarta.transaction-api') 'jakarta.transaction-api' $priorityJars $false
Add-LatestJar @('org\jboss\logging\jboss-logging') 'jboss-logging' $priorityJars $false
Add-LatestJar @('org\hibernate\common\hibernate-commons-annotations') 'hibernate-commons-annotations' $priorityJars $false
Add-LatestJar @('org\jboss\jandex', 'io\smallrye\jandex') 'jandex' $priorityJars $false
Add-LatestJar @('com\fasterxml\classmate') 'classmate' $priorityJars $false
Add-LatestJar @('net\bytebuddy\byte-buddy') 'byte-buddy' $priorityJars $false
Add-LatestJar @('org\antlr\antlr4-runtime') 'antlr4-runtime' $priorityJars $false

# POI runtime (prevents org/apache/poi/ss/usermodel/Sheet errors)
Add-LatestJar @('org\apache\poi\poi') 'poi' $priorityJars $true
Add-LatestJar @('org\apache\poi\poi-ooxml') 'poi-ooxml' $priorityJars $true
Add-LatestJar @('org\apache\poi\poi-ooxml-lite') 'poi-ooxml-lite' $priorityJars $false
Add-LatestJar @('org\apache\xmlbeans\xmlbeans') 'xmlbeans' $priorityJars $true
Add-LatestJar @('commons-io\commons-io') 'commons-io' $priorityJars $true
Add-LatestJar @('org\apache\commons\commons-compress') 'commons-compress' $priorityJars $true
Add-LatestJar @('org\apache\commons\commons-collections4') 'commons-collections4' $priorityJars $true
Add-LatestJar @('com\github\virtuald\curvesapi') 'curvesapi' $priorityJars $false

# Other declared deps
Add-LatestJar @('org\apache\logging\log4j\log4j-api') 'log4j-api' $priorityJars $false
Add-LatestJar @('org\apache\logging\log4j\log4j-core') 'log4j-core' $priorityJars $false
Add-LatestJar @('org\mindrot\jbcrypt', 'de\svenkubiak\jBCrypt') 'jbcrypt' $priorityJars $false

$allJars = Get-ChildItem $repo -Recurse -Filter '*.jar' |
    Where-Object { $_.Name -notmatch '(-sources|-javadoc|tests?)\.jar$' } |
    Select-Object -ExpandProperty FullName

$fallbackJars = $allJars | Where-Object { $priorityJars -notcontains $_ }

$cpItems = New-Object 'System.Collections.Generic.List[string]'
$cpItems.Add((Resolve-Path 'target/classes').Path)
$cpItems.Add((Resolve-Path 'src/main/resources').Path)
foreach ($jar in $priorityJars) {
    $cpItems.Add($jar)
}
foreach ($jar in $fallbackJars) {
    $cpItems.Add($jar)
}

$cp = [string]::Join(';', $cpItems)
"-cp`n$cp`n$mainClass" | Set-Content -Path '.java-main.args' -Encoding ASCII

Write-Host "Running: $mainClass"
java "@.java-main.args"
