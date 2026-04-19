#!/bin/bash
echo "====================================================="
echo " Smart Classroom Management System - Build Script"
echo "====================================================="
mkdir -p out

find src -name "*.java" > sources.txt
javac -cp ".:lib/mysql-connector-j-9.6.0.jar" -d out @sources.txt

if [ $? -eq 0 ]; then
    echo ""
    echo "[OK] Compilation successful!"
    echo ""
    echo "To run:"
    echo "  java -cp '.:out:lib/mysql-connector-j-9.6.0.jar' com.smartclassroom.Main"
else
    echo ""
    echo "[ERR] Compilation failed. Check errors above."
fi
rm -f sources.txt
