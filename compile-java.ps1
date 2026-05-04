$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$repo = Join-Path $env:USERPROFILE '.m2\repository'
$sourceDir = 'src/main/java'
$resourceDir = 'src/main/resources'
$outputDir = 'target/classes'
$argsFile = 'target/javac.args'

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
        throw "Thieu dependency bat buoc: $JarPrefix"
    }
}

if (-not (Test-Path $sourceDir)) {
    throw "Khong tim thay thu muc nguon: $sourceDir"
}

if (-not (Test-Path $repo)) {
    throw "Khong tim thay kho .m2: $repo"
}

$javaFiles = Get-ChildItem $sourceDir -Recurse -Filter '*.java' | Select-Object -ExpandProperty FullName
if (-not $javaFiles -or $javaFiles.Count -eq 0) {
    throw 'Khong tim thay file .java de bien dich'
}

New-Item -ItemType Directory -Force $outputDir | Out-Null

$compileJars = Get-ChildItem $repo -Recurse -Filter '*.jar' |
    Where-Object { $_.Name -notmatch '(-sources|-javadoc|tests?)\.jar$' } |
    Select-Object -ExpandProperty FullName

# Lombok compiler plugin only
$lombokJars = New-Object 'System.Collections.Generic.List[string]'
Add-LatestJar @('org\projectlombok\lombok') 'lombok' $lombokJars $true
$lombokJar = $lombokJars[0]

$cpItems = New-Object 'System.Collections.Generic.List[string]'
if (Test-Path $resourceDir) {
    $cpItems.Add((Resolve-Path $resourceDir).Path)
}
foreach ($jar in $compileJars) {
    $cpItems.Add($jar)
}
$cpItems.Add($lombokJar)

$cpItemsUnique = New-Object 'System.Collections.Generic.List[string]'
$seen = New-Object 'System.Collections.Generic.HashSet[string]'
foreach ($item in $cpItems) {
    if ($seen.Add($item)) {
        $cpItemsUnique.Add($item)
    }
}

$cp = [string]::Join(';', $cpItemsUnique)

$processorPath = $lombokJar

$argsContent = @(
    '-encoding', 'UTF-8',
    '-cp', $cp,
    '-processorpath', $processorPath,
    '-d', (Resolve-Path $outputDir).Path
) + $javaFiles

Set-Content -Path $argsFile -Value $argsContent -Encoding ASCII

Write-Host "Compiling $($javaFiles.Count) source files..."
javac "@$argsFile"
if ($LASTEXITCODE -ne 0) {
    throw "Compile that bai (exit code: $LASTEXITCODE)"
}

Write-Host "Compile thanh cong. Output: $outputDir" -ForegroundColor Green
