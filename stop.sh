#!/bin/bash
# ========================================
# SUMAQ SPA - Detener Aplicacion
# ========================================

if [ -f "app.pid" ]; then
    PID=$(cat app.pid)
    echo "Deteniendo aplicacion (PID: $PID)..."
    kill $PID
    rm -f app.pid
    echo "✅ Aplicacion detenida"
else
    echo "⚠️ No se encuentra archivo app.pid"
    echo "La aplicacion podria no estar corriendo"
fi

