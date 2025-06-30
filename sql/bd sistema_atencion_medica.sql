CREATE DATABASE sistema_atencion_medica;

USE sistema_atencion_medica;

-- -------- Modulo: Pacientes --------

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE NULL,
    dni_id VARCHAR(50) UNIQUE NULL, -- DNI/ID, puede ser único
    telefono VARCHAR(30) NULL,
    email VARCHAR(100) UNIQUE NULL,
    direccion TEXT NULL,
    activo BOOLEAN DEFAULT TRUE, -- Para soft delete (TRUE = activo, FALSE = inactivo)
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------- Modulo: Médicos --------

CREATE TABLE medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NULL,
    email VARCHAR(100) UNIQUE NULL,
    tarifa_cita DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Tarifa fija por cita
    activo BOOLEAN DEFAULT TRUE, -- Para soft delete
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- -------- Modulo: Citas --------

CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_Medico INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    estado ENUM('Agendada', 'Confirmada', 'Cancelada', 'Completada') DEFAULT 'Agendada',
    notas TEXT NULL, -- Notas adicionales sobre la cita
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente) REFERENCES pacientes(id) ON DELETE RESTRICT, -- No borrar paciente si tiene citas
    FOREIGN KEY (id_medico) REFERENCES medicos(id) ON DELETE RESTRICT -- No borrar médico si tiene citas
);
-- Índices para búsquedas comunes en Citas
CREATE INDEX idx_citas_fecha ON citas(fecha_hora);
CREATE INDEX idx_citas_paciente ON citas(id_paciente);
CREATE INDEX idx_citas_medico ON citas(id_medico);


-- -------- Modulo: Usuarios del Sistema --------

CREATE TABLE usuarios_sistema (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL, -- ¡NUNCA guardar en texto plano! Usar hash seguro.
    rol ENUM('Administrador', 'Recepcionista', 'Medico') NOT NULL, -- Roles definidos
    nombre_completo VARCHAR(150) NULL, -- Nombre real del usuario
    activo BOOLEAN DEFAULT TRUE, -- Para soft delete
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- -------- Modulo: Medicamentos --------

CREATE TABLE medicamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    descripcion_presentacion TEXT NULL,
    precio DECIMAL(10, 2) NULL, -- Precio si se factura
    activo BOOLEAN DEFAULT TRUE, -- Para soft delete
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- -------- Modulo: Diagnóstico --------
-- Un Diagnóstico está asociado a una Cita específica

CREATE TABLE diagnosticos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL UNIQUE, -- Un diagnóstico por cita (generalmente)
    id_paciente INT NOT NULL, -- Redundante (ya está en cita) pero útil para buscar historial
    id_medico INT NOT NULL, -- Redundante (ya está en cita) pero útil
    fecha_diagnostico DATETIME NOT NULL, -- Puede ser la misma que la cita o ligeramente posterior
    notas_diagnostico TEXT NOT NULL, -- Descripción del diagnóstico
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_cita) REFERENCES citas(id) ON DELETE RESTRICT, -- No borrar cita si tiene diagnóstico
    FOREIGN KEY (id_paciente) REFERENCES pacientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_medico) REFERENCES medicos(id) ON DELETE RESTRICT
);
-- Índices para búsquedas comunes en Diagnósticos
CREATE INDEX idx_diagnosticos_paciente ON diagnosticos(id_paciente);


-- Tabla intermedia para la relación Muchos-a-Muchos entre Diagnóstico y Medicamentos
CREATE TABLE diagnostico_medicamentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_diagnostico INT NOT NULL,
    id_medicamento INT NOT NULL,
    indicaciones TEXT NULL, -- Dosis, frecuencia, etc.
    cantidad INT DEFAULT 1, -- Cantidad recetada (útil para facturación/inventario)

    FOREIGN KEY (id_diagnostico) REFERENCES diagnosticos(id) ON DELETE CASCADE, -- Si se borra el diagnóstico, borrar las recetas asociadas
    FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id) ON DELETE RESTRICT, -- No borrar medicamento si está en un diagnóstico
    UNIQUE KEY uk_diag_med (id_diagnostico, id_medicamento) -- Evitar duplicados del mismo medicamento en el mismo diagnóstico
);


-- -------- Modulo: Reportes y Facturación --------

CREATE TABLE facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_cita INT NULL, -- La factura puede estar asociada a una cita específica
    id_diagnostico INT NULL, -- O directamente a un diagnóstico (que implica una cita)
    numero_factura VARCHAR(50) UNIQUE NOT NULL, -- Número de factura único y secuencial (puede generarse en la aplicación)
    fecha_emision DATETIME NOT NULL,
    monto_servicio_medico DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Costo de la consulta (tomado de medico.tarifa_cita en el momento de generar)
    monto_medicamentos DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Costo total de medicamentos (calculado de diagnostico_medicamentos)
    monto_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00, -- Suma de los anteriores + otros cargos si los hubiera
    estado ENUM('Pendiente', 'Pagada', 'Anulada') DEFAULT 'Pendiente',
    fecha_pago DATETIME NULL, -- Fecha en que se registró el pago
    metodo_pago VARCHAR(50) NULL, -- Ej: Efectivo, Tarjeta, Transferencia
    notas TEXT NULL, -- Notas adicionales sobre la factura o el pago
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_paciente) REFERENCES pacientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (id_cita) REFERENCES citas(id) ON DELETE SET NULL, -- Si se borra la cita, que la factura no quede huérfana del todo, pero se desvincule
    FOREIGN KEY (id_diagnostico) REFERENCES diagnosticos(id) ON DELETE SET NULL -- Similar a la cita
);
-- Índices para búsquedas comunes en Facturas
CREATE INDEX idx_facturas_paciente ON facturas(id_paciente);
CREATE INDEX idx_facturas_fecha ON facturas(fecha_emision);
CREATE INDEX idx_facturas_estado ON facturas(estado);

-- Opcional: Tabla para registrar pagos parciales si fuera necesario
-- CREATE TABLE pagos_factura (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     id_factura INT NOT NULL,
--     fecha_pago DATETIME NOT NULL,
--     monto_pagado DECIMAL(10, 2) NOT NULL,
--     metodo_pago VARCHAR(50) NULL,
--     referencia_pago VARCHAR(100) NULL, -- Ej: Nro de transacción
--     fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     FOREIGN KEY (id_factura) REFERENCES facturas(id) ON DELETE CASCADE
-- );


DELIMITER //

-- RF-MDC-001: Registrar Nuevo Medicamento
CREATE PROCEDURE sp_medicamento_registrar(
    IN p_nombre VARCHAR(150),
    IN p_descripcion TEXT,
    IN p_precio DECIMAL(10,2),
    OUT p_resultado VARCHAR(255)
)
BEGIN
    DECLARE precio_valido BOOLEAN;
    
    -- Validar que el precio sea positivo (RF-MDC-005)
    SET precio_valido = (p_precio > 0);
    
    IF NOT precio_valido THEN
        SET p_resultado = 'ERROR: El precio debe ser mayor a cero';
    ELSEIF EXISTS (SELECT 1 FROM medicamentos WHERE nombre = p_nombre AND activo = TRUE) THEN
        SET p_resultado = 'ERROR: Este medicamento ya está registrado';
    ELSE
        INSERT INTO medicamentos(nombre, descripcion_presentacion, precio)
        VALUES (p_nombre, p_descripcion, p_precio);
        
        SET p_resultado = CONCAT('OK: Medicamento registrado con ID ', LAST_INSERT_ID());
    END IF;
END //

-- RF-MDC-002: Búsqueda Avanzada de Medicamentos
CREATE PROCEDURE sp_medicamento_buscar(
    IN p_nombre VARCHAR(150),
    IN p_precio_min DECIMAL(10,2),
    IN p_precio_max DECIMAL(10,2),
    IN p_incluir_inactivos BOOLEAN
)
BEGIN
    SELECT 
        id,
        nombre,
        precio,
        descripcion_presentacion,
        IF(activo, 'Activo', 'Inactivo') AS estado,
        activo
    FROM medicamentos
    WHERE 
        (p_nombre IS NULL OR nombre LIKE CONCAT('%', p_nombre, '%')) AND
        (p_precio_min IS NULL OR precio >= p_precio_min) AND
        (p_precio_max IS NULL OR precio <= p_precio_max) AND
        (p_incluir_inactivos = TRUE OR activo = TRUE)
    ORDER BY nombre;
END //

-- RF-MDC-003: Editar Información de Medicamento
CREATE PROCEDURE sp_medicamento_modificar(
    IN p_id INT,
    IN p_nombre VARCHAR(150),
    IN p_descripcion TEXT,
    IN p_precio DECIMAL(10,2),
    OUT p_resultado VARCHAR(255)
)
BEGIN
    DECLARE es_activo BOOLEAN;
    DECLARE precio_valido BOOLEAN;
    DECLARE nombre_existente BOOLEAN;
    
    -- Verificar si el medicamento está activo (RF-MDC-003)
    SELECT activo INTO es_activo FROM medicamentos WHERE id = p_id;
    
    -- Validar precio (RF-MDC-005)
    SET precio_valido = (p_precio > 0);
    
    -- Verificar si el nuevo nombre ya existe (RF-MDC-003)
    SELECT COUNT(*) > 0 INTO nombre_existente 
    FROM medicamentos 
    WHERE nombre = p_nombre AND id != p_id AND activo = TRUE;
    
    IF NOT es_activo THEN
        SET p_resultado = 'ERROR: No se puede editar un medicamento inactivo';
    ELSEIF NOT precio_valido THEN
        SET p_resultado = 'ERROR: El precio debe ser mayor a cero';
    ELSEIF nombre_existente THEN
        SET p_resultado = 'ERROR: Ya existe un medicamento activo con ese nombre';
    ELSE
        -- Actualización con fecha de modificación automática
        UPDATE medicamentos
        SET 
            nombre = p_nombre,
            descripcion_presentacion = p_descripcion,
            precio = p_precio,
            fecha_modificacion = CURRENT_TIMESTAMP
        WHERE id = p_id;
        
        SET p_resultado = 'OK: Medicamento actualizado correctamente';
    END IF;
END //

-- RF-MDC-004: Desactivar Medicamento
CREATE PROCEDURE sp_medicamento_desactivar(
    IN p_id INT,
    IN p_usuario_rol VARCHAR(20),
    OUT p_resultado VARCHAR(255)
)
BEGIN
    DECLARE precio_actual DECIMAL(10,2);
    DECLARE tiene_facturas_pendientes BOOLEAN;
    
    -- Obtener precio actual
    SELECT precio INTO precio_actual FROM medicamentos WHERE id = p_id;
    
    -- Verificar si tiene facturas pendientes (RF-MDC-004)
    SELECT EXISTS (
        SELECT 1 FROM facturas f
        JOIN diagnostico_medicamentos dm ON f.id_diagnostico = dm.id_diagnostico
        WHERE dm.id_medicamento = p_id AND f.estado = 'Pendiente'
    ) INTO tiene_facturas_pendientes;
    
    -- Validaciones
    IF p_usuario_rol != 'Administrador' THEN
        SET p_resultado = 'ERROR: Solo administradores pueden desactivar medicamentos';
    ELSEIF precio_actual <= 0 THEN
        SET p_resultado = 'ERROR: No se puede desactivar un medicamento con precio cero o negativo';
    ELSEIF tiene_facturas_pendientes THEN
        SET p_resultado = 'ERROR: No se puede desactivar, tiene facturas pendientes asociadas';
    ELSE
        UPDATE medicamentos 
        SET activo = FALSE 
        WHERE id = p_id;
        
        SET p_resultado = 'OK: Medicamento desactivado correctamente';
    END IF;
END //

-- RF-MDC-004: Reactivar Medicamento
CREATE PROCEDURE sp_medicamento_reactivar(
    IN p_id INT,
    IN p_usuario_rol VARCHAR(20),
    OUT p_resultado VARCHAR(255)
)
BEGIN
    -- Verificar rol de usuario (RF-MDC-004)
    IF p_usuario_rol != 'Administrador' THEN
        SET p_resultado = 'ERROR: Solo administradores pueden reactivar medicamentos';
    ELSE
        UPDATE medicamentos 
        SET activo = TRUE 
        WHERE id = p_id;
        
        SET p_resultado = 'OK: Medicamento reactivado correctamente';
    END IF;
END //

DELIMITER ;