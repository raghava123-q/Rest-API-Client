# PowerShell script to run the Weather Client without pre-installed Maven
# It downloads the required Jackson JARs, compiles the project, and runs it.

$ErrorActionPreference = "Stop"

# 1. Create a lib folder for dependencies
$libDir = Join-Path $PSScriptRoot "lib"
if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
    Write-Host "Created lib directory." -ForegroundColor Green
}

# 2. Define Jackson JARs to download
$dependencies = @(
    @{
        Name = "jackson-databind-2.17.1.jar"
        Url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.17.1/jackson-databind-2.17.1.jar"
    },
    @{
        Name = "jackson-core-2.17.1.jar"
        Url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.17.1/jackson-core-2.17.1.jar"
    },
    @{
        Name = "jackson-annotations-2.17.1.jar"
        Url  = "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.17.1/jackson-annotations-2.17.1.jar"
    }
)

# 3. Download JARs if they don't exist
foreach ($dep in $dependencies) {
    $targetPath = Join-Path $libDir $dep.Name
    if (-not (Test-Path $targetPath)) {
        Write-Host "Downloading $($dep.Name)..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $dep.Url -OutFile $targetPath
    }
}

# 4. Create bin directory for compiled classes
$binDir = Join-Path $PSScriptRoot "bin"
if (-not (Test-Path $binDir)) {
    New-Item -ItemType Directory -Path $binDir | Out-Null
}

# 5. Copy resources (properties file) to bin directory so ClassLoader can find it
$resourcesSrc = Join-Path $PSScriptRoot "src/main/resources"
Copy-Item -Path "$resourcesSrc\*" -Destination $binDir -Recurse -Force

# 6. Find all Java files
$javaFiles = Get-ChildItem -Path (Join-Path $PSScriptRoot "src/main/java") -Filter *.java -Recurse | ForEach-Object { $_.FullName }

# 7. Compile files
$classpath = "$libDir\*"
Write-Host "Compiling Java source files..." -ForegroundColor Cyan
javac -cp $classpath -d $binDir $javaFiles

# 8. Run the main class
Write-Host "Running Weather Client..." -ForegroundColor Green
java -cp "$binDir;$libDir\*" com.example.weatherclient.Main
