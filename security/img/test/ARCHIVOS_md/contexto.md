CASO DE ESTUDIO — BOB’S BARBER 

Sistema de gestión de turnos para barbería con integración de múltiples pasarelas de pago 

Patrón 1 — Comportamiento 

Chain of Responsibility 

Patrón 2 — Estructural 

Adapter 

 

1.  Contexto del Sistema 

 

La Barbería Bob’s Barber desea modernizar su sistema de atención al cliente mediante una aplicación web que permita gestionar turnos en línea, asignar barberos según disponibilidad y especialidad, y procesar pagos a través de diferentes plataformas. El sistema debe validar cada turno a través de una cadena de pasos ordenados antes de confirmarlo. 

 

Los servicios disponibles y sus precios base son: 

 

​​Código 

​Servicio 

​Precio 

​Especialidad 

​SER-001 

​Corte clásico 

​$25.000 

​CORTE 

​SER-002 

​Corte + barba 

​$38.000 

​CORTE_BARBA 

​SER-003 

​Diseño de cejas 

​$15.000 

​CEJAS 

​SER-004 

​Afeitado con navaja 

​$20.000 

​BARBA 

​SER-005 

​Tratamiento capilar 

​$45.000 

​CAPILAR​ 

 

Barberos disponibles: 

 

 

​​Barbero 

​Especialidades 

​Disponibilidad 

​Miguel 

​CORTE, CORTE_BARBA 

​Lunes a Jueves 10:00 AM - 5:00 PM 

​Laura 

​CAPILAR, CEJAS 

​Lunes a Viernes 10:00 AM - 5:00 PM 

​Andres 

​CORTE, BARBA 

​Martes, Jueves y Fines de Semana 10:00 AM - 2:00 PM​ 

 

Los barberos tienen distintas especialidades y disponibilidad por franja horaria. Un turno debe pasar obligatoriamente por las siguientes validaciones en orden antes de ser confirmado: 

 

# 

Validación 

Criterio de rechazo 

1 

Disponibilidad de franja 

Si la franja horaria está llena (máx. 3 clientes por hora), el turno se rechaza 

2 

Barbero disponible 

Si ningún barbero con la especialidad requerida está libre en esa franja, se rechaza 

3 

Datos del cliente 

Nombre (que no puede ser nulo), teléfono (10 dígitos) y correo válido (manejo de @gmail.com) son obligatorios 

4 

Servicio permitido 

Sólo los servicios del catálogo activo pueden reservarse 

5 

Pasarela de pago 

La pasarela seleccionada debe estar activa y disponible en ese momento 

 

Si alguna validación falla, el proceso se detiene inmediatamente y se retorna el motivo específico del rechazo. El sistema debe permitir agregar nuevas validaciones sin modificar el flujo principal. 

 

Pasarelas de Pago: 

La barberia maneja cuatro pasarelas de pago, pero cada una tiene interfaces incompatibles, en un intento de normalizar Bob’s Barber se implementó lo siguiente: 

 

Respuesta Normalizada: {payment_Id, estado, mensaje} 

Estados Posibles: APROBADO, RECHAZADO, PENDING (Solo PSE con Bancolombia) 

 

​​Proveedor 

​Interfaz externa Simulada 

​Mapeo Esperado 

​Nequi 

​sendpayment(phone, payerName, amount) 

​successful=true -> APROBADO 

​transactionCode  -> paymentId 

​PSE 

​executeBankTransaction(payerName, bank, accountNumber, amount) 

​0 -> APROBADO 

​1 -> RECHAZADO 

​10 -> PENDING (Solo Bancolombia) 

​Stripe 

​charge(cardNumber, cvv, expirationDate, payerName, amount) 

​paid=true -> APROBADO 

​false -> RECHAZADO 

​Efectivo 

​Sin API Externa 

​Debe adaptarse al mismo contrato interno​ 

 

IMPORTANTE: Todas las respuestas deben ser simuladas en su implementación, el cliente no debe interpretar respuestas específicas de cada proveedor mediante condiciones por medio de pago 

 

Nequi (API REST propia) - Si el número termina en 65 successful = true, de resto todo pago queda en false. 

PSE (SDK bancario legado) - Si el número de cuenta inicia en 1, 2 o 3 el resultado es código 0, si el número de cuenta inicia con 4 el resultado es código 1, y si si el banco es BANCOLOMBIA el resultado es codigo 10 

Tarjeta crédito/débito via Stripe (API externa) - Si el cvv es 555 el resultado es paid = true, para cualquier otro es false. 

Efectivo en caja (sin integración digital) - Si el monto es >=10.000 el resultado es APROBADO, si no es RECHAZADO 

 

La información de turnos y pagos persiste en AWS Mongo Atlas. Los comprobantes se generan y almacenan en AWS S3. 

 

Adicional la Barbería nos menciona: 

● Colores de la marca: Azul (#1B2A4A) y Rojo oscuro (#7B2D2D) 

● Tipografía: Calibri 

● Debe ser compatible con móvil, tablet y escritorio 

● El sistema debe procesar un turno en ≤ 2 s para el 95% de las solicitudes 

● Debe estar disponible con un porcentaje de 99.5% en horario de operación (lunes a domingo) 

 

2.  Ejemplo de Entrada y Salida 

 

A continuación se muestra cómo el sistema procesa las solicitudes de turno a través de la cadena de validaciones: 

 

Escenario #1: Turno Aprobado con Nequi 

 

Datos de Entrada 

Datos de Salida 

TURNO #001 

Cliente: Carlos Ruiz 

Correo: carlos@gmail.com 

Teléfono: 3208872165 

Servicio: SER-002 

Franja: Martes 10:00 AM 

Barbero: Sin preferencia 

Pago: NEQUI 

Procesando TURNO #001 

[OK] Franja disponible 

[OK] Barbero asignado: Miguel 

[OK] Datos del cliente válidos 

[OK] Servicio en catálogo 

[OK] Pasarela NEQUI 

 

PAGO: NQ-1001 - APROBADO 

TURNO CONFIRMADO 

Total: $38.000 

 

Escenario #2: Rechazo en datos del cliente 

 

Datos de Entrada 

Datos de Salida 

TURNO #002 

Cliente: Ana Lopez 

Correo: ana@gmail.com 

Teléfono: 312345 

Servicio: SER-005 

Franja: Lunes 10:00 AM 

Barbero: Sin preferencia 

Pago: PSE 

Procesando TURNO #002 

[OK] Franja disponible 

[OK] Barbero asignado: Laura 

[RECHAZADO] Telefono invalido 

Error: teléfono debe tener 10 dígitos 

 

PAGO: PSE-1002 - RECHAZADO 

TURNO NO CONFIRMADO 

 

Escenario #3: Pago con PSE Bancolombia 

 

​​Datos de Entrada 

​Datos de Salida 

​TURNO #003 

​Cliente: Julián Mesa 

​Correo: julian@gmail.com 

​Teléfono: 3112145678 

​Servicio: SER-001, SER-004 

​Franja: Sábado 1:00 PM 

​Barbero: Sin preferencia 

​Pago: PSE 

​Banco: BANCOLOMBIA 

​Cuenta: 123456789 

​Procesando TURNO #003 

​[OK] Franja disponible 

​[OK] Barbero asignado: Andres 

​[OK] Datos del cliente válidos 

​[OK] Servicio en catálogo 

​[OK] Pasarela PSE BANCOLOMBIA 

​ 

​PAGO: PSE-1003 - PENDING 

​TURNO CONFIRMADO 

​Total: $45.000​ 

 

3.  Tu Misión 

 

Realice el diagrama de contexto con las generalidades del sistema. (Añadirlo al README.md) 

 

Identifique 5 requerimientos del sistema: 3 funcionales y 2 no funcionales. Garantice que al menos 1 requerimiento funcional utilice Chain of Responsibility y al menos 1 utilice Adapter. (Añadirlo al README.md) 

 

Seleccione los 2 requerimientos funcionales más importantes y desarrolle un diagrama de casos de uso con su respectiva historia de usuario. Al menos uno debe utilizar un patrón identificado. (Añadirlo al README.md) 

 

Especifique los 2 requerimientos funcionales siguiendo la plantilla de Análisis de Requerimientos. ( Añadir a docs/requirements/) - Genera las pantallas con Figma y adjuntalas 

Pueden usar figma I.A solo para la generación de sus pantallas 

 

Seleccione un requerimiento asociado a los patrones y realice la descomposición de tareas:Épica → Feature → Historia de Usuario → mínimo 3 tareas. (Añadirlo al README.md) 

 

Identifique los 2 patrones asignados (Chain of responsability y Adapter), especificando para cada uno: 

Nombre del patrón y tipo (creacional, estructural o de comportamiento) 

Justificación de la decisión en el contexto de BOBS Barber 

Diagrama de clases UML de la solución con los dos patrones aplicados 

Cuáles principios SOLID está aplicando y porque 

 

Desarrolle en código la solución propuesta. El método main deberá ejecutar todos los escenarios mínimos definidos. Los resultados obtenidos deberán ser consistentes con los resultados esperados. No se evaluarán únicamente los resultados finales: se evaluará que sean consecuencia de una aplicación correcta de los patrones. 