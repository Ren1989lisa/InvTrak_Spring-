-- Corregir ENUM estatus en tabla activo para incluir todos los valores del enum Java
ALTER TABLE activo
    MODIFY COLUMN estatus ENUM(
        'DISPONIBLE',
        'RESGUARDADO',
        'MANTENIMIENTO',
        'BAJA',
        'REPORTADO',
        'DEVOLUCION',
        'SOLICITUD_DE_BAJA'
    );
