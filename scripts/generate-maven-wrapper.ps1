# This script generates the Maven Wrapper in environments where Maven is installed.
# Run from the project root:
#   powershell -ExecutionPolicy Bypass -File scripts\generate-maven-wrapper.ps1

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "Maven (mvn) is not installed or not in PATH. Install Maven and rerun this script to generate the Maven Wrapper."
    exit 1
}

mvn -N io.takari:maven:wrapper
Write-Host "Maven Wrapper generated (mvnw, mvnw.cmd, .mvn/wrapper/*). Commit them into the repo if you want the wrapper available to others."