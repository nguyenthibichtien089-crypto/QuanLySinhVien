# Prepares the OFFICIAL Gradle 8.7 wrapper on Windows; requires Internet once.
$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$wrapperJar = Join-Path $root 'gradle\wrapper\gradle-wrapper.jar'
if (Test-Path $wrapperJar) { Write-Host 'Official Gradle wrapper already present.'; exit 0 }

$cache = Join-Path $env:USERPROFILE '.gradle\ntbtien-bootstrap'
$zip = Join-Path $cache 'gradle-8.7-bin.zip'
$dist = Join-Path $cache 'gradle-8.7\bin\gradle.bat'
New-Item -ItemType Directory -Path $cache -Force | Out-Null
if (-not (Test-Path $dist)) {
    if (-not (Test-Path $zip)) {
        Write-Host 'Downloading official Gradle 8.7 distribution from services.gradle.org ...'
        Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.7-bin.zip' -OutFile $zip -UseBasicParsing
    }
    Write-Host 'Unzipping Gradle 8.7 ...'
    Expand-Archive -LiteralPath $zip -DestinationPath $cache -Force
}
if (-not (Test-Path $dist)) { throw 'Gradle 8.7 binary not found after extraction.' }
Push-Location $root
try {
    Write-Host 'Generating official Gradle wrapper scripts and JAR ...'
    & $dist wrapper --gradle-version 8.7 --distribution-type bin
    if ($LASTEXITCODE -ne 0) { throw 'Official Gradle wrapper task failed.' }
    if (-not (Test-Path $wrapperJar)) { throw 'Official wrapper JAR was not generated.' }
} finally {
    Pop-Location
}
Write-Host 'Wrapper created. Open the project root with Android Studio.'
