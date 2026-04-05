$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$mainClass = if ($args.Count -gt 0 -and $args[0]) { $args[0] } else { 'com.tuyensinh.Main' }

Write-Host "Running main class: $mainClass"

$runMainJava = Join-Path $projectRoot 'run-main-java.ps1'
if (Test-Path $runMainJava) {
	& $runMainJava $mainClass
	exit $LASTEXITCODE
}

$argFile = Join-Path $projectRoot '.java-main.args'
if (Test-Path $argFile) {
	$lines = Get-Content $argFile
	if ($lines.Count -ge 2 -and $lines[0].Trim().ToLower() -eq '-cp') {
		$cp = $lines[1]
		$runArgsFile = Join-Path $projectRoot '.java-run.args'
		"-cp`n$cp`n$mainClass" | Set-Content -Path $runArgsFile -Encoding ASCII
		java "@$runArgsFile"
		exit $LASTEXITCODE
	}
}

$repo = Join-Path $env:USERPROFILE '.m2\repository'
$cpItems = New-Object 'System.Collections.Generic.List[string]'
$cpItems.Add((Resolve-Path 'target/classes').Path)

if (Test-Path 'src/main/resources') {
	$cpItems.Add((Resolve-Path 'src/main/resources').Path)
}

if (Test-Path $repo) {
	$jars = Get-ChildItem $repo -Recurse -Filter '*.jar' |
		Where-Object { $_.Name -notmatch '(-sources|-javadoc|tests?)\.jar$' } |
		Select-Object -ExpandProperty FullName
	foreach ($jar in $jars) {
		$cpItems.Add($jar)
	}
}

$cp = [string]::Join(';', $cpItems)
$runArgsFile = Join-Path $projectRoot '.java-run.args'
"-cp`n$cp`n$mainClass" | Set-Content -Path $runArgsFile -Encoding ASCII
java "@$runArgsFile"
exit $LASTEXITCODE
