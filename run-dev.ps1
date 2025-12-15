# Dev helper to build and run H2, server, and GUI
# Run from the project root in PowerShell

cd (Split-Path -Parent $MyInvocation.MyCommand.Path)

Write-Host "Compiling project..."
javac -d bin src/models/*.java src/utilities/*.java src/concurrency/*.java src/data/*.java src/services/*.java src/gui/*.java src/web/*.java src/main/Main.java

# Start H2 console
Write-Host "Starting H2 web console..."
Start-Process -NoNewWindow -FilePath 'java' -ArgumentList '-cp','lib\h2-2.1.214.jar','org.h2.tools.Server','-tcp','-web','-tcpAllowOthers'

# Dev env credentials (optional - use environment variables instead of CLI)
$env:DB_USER = 'sa'
$env:DB_PASS = ''

# create a random admin token (dev only) for the admin shutdown endpoint
$token = [guid]::NewGuid().ToString()
Write-Host "Admin token (for shutdown): $token"
$env:ADMIN_TOKEN = $token

# Start server with file-based DB and create DB schema if it doesn't exist
Write-Host "Starting Server on port 9000 (JDBC: ./vehicledb) with create-db..."
Start-Process -NoNewWindow -FilePath 'java' -ArgumentList '-cp','"bin;lib/h2-2.1.214.jar"','web.Server','--jdbc','--jdbcUrl=jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1','--port=9000','--create-db','--no-demo'

# Start GUI
Write-Host "Starting Swing GUI..."
Start-Process -NoNewWindow -FilePath 'java' -ArgumentList '-cp','bin','gui.VehicleGUI'

Write-Host "Done. H2 console at http://localhost:8082, server at http://localhost:9000"
