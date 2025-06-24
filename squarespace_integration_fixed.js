// ===================================================================
// 🚀 KIWIPAY SQUARESPACE INTEGRATION - VERSIÓN MEJORADA
// ===================================================================
// Versión: 2.0 - Detección automática mejorada
// Endpoint: https://loan-api-j1n6.onrender.com/api/v1/squarespace/lead
// ===================================================================

(function() {
    'use strict';
    
    // ===================================================================
    // 🔧 CONFIGURACIÓN
    // ===================================================================
    const CONFIG = {
        API_URL: 'https://loan-api-j1n6.onrender.com/api/v1/squarespace/lead',
        DEBUG: true,
        MAX_RETRIES: 3,
        RETRY_DELAY: 1000,
        FORM_DETECTION_TIMEOUT: 10000
    };
    
    // ===================================================================
    // 🎨 ESTILOS PARA MENSAJES
    // ===================================================================
    const STYLES = {
        success: `
            position: fixed; top: 20px; right: 20px; z-index: 10000;
            background: linear-gradient(135deg, #10b981, #059669);
            color: white; padding: 15px 20px; border-radius: 8px;
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            font-size: 14px; font-weight: 500; max-width: 400px;
            animation: slideInRight 0.3s ease-out;
        `,
        error: `
            position: fixed; top: 20px; right: 20px; z-index: 10000;
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: white; padding: 15px 20px; border-radius: 8px;
            box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            font-size: 14px; font-weight: 500; max-width: 400px;
            animation: slideInRight 0.3s ease-out;
        `,
        loading: `
            position: fixed; top: 20px; right: 20px; z-index: 10000;
            background: linear-gradient(135deg, #3b82f6, #2563eb);
            color: white; padding: 15px 20px; border-radius: 8px;
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            font-size: 14px; font-weight: 500; max-width: 400px;
            animation: slideInRight 0.3s ease-out;
        `
    };
    
    // ===================================================================
    // 📱 UTILIDADES DE UI
    // ===================================================================
    function showMessage(message, type = 'success', duration = 5000) {
        // Remover mensajes existentes
        const existingMessages = document.querySelectorAll('.kiwipay-message');
        existingMessages.forEach(msg => msg.remove());
        
        // Crear nuevo mensaje
        const messageDiv = document.createElement('div');
        messageDiv.className = 'kiwipay-message';
        messageDiv.style.cssText = STYLES[type];
        messageDiv.innerHTML = `
            <div style="display: flex; align-items: center; gap: 10px;">
                <div style="font-size: 18px;">
                    ${type === 'success' ? '✅' : type === 'error' ? '❌' : '⏳'}
                </div>
                <div>${message}</div>
            </div>
        `;
        
        document.body.appendChild(messageDiv);
        
        // Auto-remover después del tiempo especificado
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.style.animation = 'slideOutRight 0.3s ease-out';
                setTimeout(() => messageDiv.remove(), 300);
            }
        }, duration);
    }
    
    // ===================================================================
    // 🔍 DETECCIÓN MEJORADA DE CAMPOS
    // ===================================================================
    function detectFormFields() {
        const form = document.querySelector('form');
        if (!form) {
            console.warn('❌ KIWIPAY: No se encontró formulario');
            return null;
        }
        
        // Obtener todos los inputs del formulario
        const inputs = form.querySelectorAll('input[type="text"], input[type="email"], input[type="tel"], input[type="number"], select, textarea');
        const labels = form.querySelectorAll('label, .form-item-title, .field-element h3, .caption');
        
        console.log('✅ KIWIPAY: 🔍 Analizando formulario...');
        console.log('✅ KIWIPAY: 📊 Inputs encontrados:', inputs.length);
        console.log('✅ KIWIPAY: 🏷️ Labels encontrados:', labels.length);
        
        // Mapear campos por contenido de texto y posición
        const fieldMap = {};
        const fieldPatterns = {
            receptionistName: /recepcion|empleado|vendedor|asesor/i,
            sede: /sede|sucursal|clinica|oficina|ubicacion/i,
            clientName: /cliente|paciente|nombres.*apellidos|nombre.*completo|persona/i,
            dni: /dni|documento|cedula|identificacion|ruc/i,
            monthlyIncome: /ingreso|salario|sueldo|mensual|s\/|soles/i,
            treatmentCost: /costo|precio|tratamiento|aproximado|aprox/i,
            phone: /telefono|celular|movil|contacto|phone/i
        };
        
        // Analizar cada input
        inputs.forEach((input, index) => {
            const inputInfo = {
                element: input,
                index: index,
                type: input.type,
                name: input.name || '',
                id: input.id || '',
                placeholder: input.placeholder || '',
                value: input.value || ''
            };
            
            // Buscar label asociado
            let labelText = '';
            
            // Método 1: Label con for= que apunta al input
            if (input.id) {
                const label = document.querySelector(`label[for="${input.id}"]`);
                if (label) labelText = label.textContent.trim();
            }
            
            // Método 2: Label que contiene el input
            if (!labelText) {
                const parentLabel = input.closest('label');
                if (parentLabel) labelText = parentLabel.textContent.trim();
            }
            
            // Método 3: Buscar elementos de texto cercanos
            if (!labelText) {
                const parent = input.closest('.form-item, .field-element, .form-wrapper, div');
                if (parent) {
                    const textElements = parent.querySelectorAll('.form-item-title, .field-element h3, .caption, label, span, div');
                    textElements.forEach(el => {
                        const text = el.textContent.trim();
                        if (text && text.length > 0 && text.length < 100) {
                            labelText = text;
                        }
                    });
                }
            }
            
            inputInfo.labelText = labelText;
            
            console.log(`✅ KIWIPAY: 📋 Campo ${index + 1}:`, {
                type: input.type,
                name: input.name,
                id: input.id,
                placeholder: input.placeholder,
                labelText: labelText,
                value: input.value
            });
            
            // Intentar mapear el campo
            for (const [fieldName, pattern] of Object.entries(fieldPatterns)) {
                const searchText = `${labelText} ${input.placeholder} ${input.name} ${input.id}`.toLowerCase();
                if (pattern.test(searchText)) {
                    if (!fieldMap[fieldName]) {
                        fieldMap[fieldName] = inputInfo;
                        console.log(`✅ KIWIPAY: ✅ ${fieldName} mapeado a campo ${index + 1}: "${labelText}"`);
                    }
                }
            }
        });
        
        // Si algunos campos no se mapearon, intentar por posición
        const unmappedFields = Object.keys(fieldPatterns).filter(field => !fieldMap[field]);
        if (unmappedFields.length > 0) {
            console.log('✅ KIWIPAY: 🔄 Intentando mapeo por posición para campos faltantes:', unmappedFields);
            
            // Orden esperado basado en la imagen del formulario
            const expectedOrder = ['receptionistName', 'sede', 'clientName', 'dni', 'monthlyIncome', 'treatmentCost', 'phone'];
            
            let inputIndex = 0;
            expectedOrder.forEach(fieldName => {
                if (!fieldMap[fieldName] && inputIndex < inputs.length) {
                    fieldMap[fieldName] = {
                        element: inputs[inputIndex],
                        index: inputIndex,
                        type: inputs[inputIndex].type,
                        labelText: `Campo ${inputIndex + 1}`,
                        fallback: true
                    };
                    console.log(`✅ KIWIPAY: 🎯 ${fieldName} mapeado por posición a campo ${inputIndex + 1}`);
                    inputIndex++;
                }
            });
        }
        
        return { form, fieldMap, allInputs: inputs };
    }
    
    // ===================================================================
    // 📝 EXTRACCIÓN DE DATOS
    // ===================================================================
    function extractFormData(fieldMap) {
        const data = {};
        const errors = [];
        
        // Extraer datos de cada campo
        Object.entries(fieldMap).forEach(([fieldName, fieldInfo]) => {
            if (fieldInfo && fieldInfo.element) {
                let value = '';
                
                if (fieldInfo.element.tagName.toLowerCase() === 'select') {
                    value = fieldInfo.element.options[fieldInfo.element.selectedIndex]?.text || '';
                } else {
                    value = fieldInfo.element.value.trim();
                }
                
                data[fieldName] = value;
                console.log(`✅ KIWIPAY: ✓ ${fieldName}: "${value}"`);
            } else {
                data[fieldName] = '';
                console.warn(`⚠️ KIWIPAY: Campo ${fieldName} no mapeado`);
            }
        });
        
        // Validaciones
        const validations = [
            { field: 'dni', message: 'DNI es obligatorio', validate: v => v && v.length >= 8 },
            { field: 'monthlyIncome', message: 'Ingreso Mensual es obligatorio', validate: v => v && !isNaN(parseFloat(v)) },
            { field: 'treatmentCost', message: 'Costo de Tratamiento es obligatorio', validate: v => v && !isNaN(parseFloat(v)) },
            { field: 'phone', message: 'Teléfono es obligatorio', validate: v => v && v.length >= 9 }
        ];
        
        validations.forEach(({ field, message, validate }) => {
            if (!validate(data[field])) {
                errors.push(message);
            }
        });
        
        console.log('✅ KIWIPAY: Datos extraídos completos:', data);
        console.log('✅ KIWIPAY: Validación completada:', errors.length === 0 ? 'Sin errores' : `${errors.length} errores encontrados`, errors);
        
        return { data, errors };
    }
    
    // ===================================================================
    // 🚀 ENVÍO A LA API
    // ===================================================================
    async function sendToAPI(data) {
        try {
            console.log('✅ KIWIPAY: 📡 Enviando a API:', CONFIG.API_URL);
            console.log('✅ KIWIPAY: 📦 Datos a enviar:', data);
            
            showMessage('⏳ Enviando información...', 'loading', 2000);
            
            const response = await fetch(CONFIG.API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            console.log('✅ KIWIPAY: 📡 Respuesta del servidor:', response.status, response.statusText);
            
            if (response.ok) {
                const result = await response.json();
                console.log('✅ KIWIPAY: ✅ Respuesta exitosa:', result);
                showMessage('✅ ¡Información enviada correctamente!', 'success');
                return { success: true, data: result };
            } else {
                const errorText = await response.text();
                console.error('❌ KIWIPAY: Error del servidor:', response.status, errorText);
                showMessage(`❌ Error del servidor: ${response.status}`, 'error');
                return { success: false, error: `Error ${response.status}: ${errorText}` };
            }
            
        } catch (error) {
            console.error('❌ KIWIPAY: Error de red:', error);
            showMessage('❌ Error de conexión. Verifique su internet.', 'error');
            return { success: false, error: error.message };
        }
    }
    
    // ===================================================================
    // 🎯 MANEJADOR PRINCIPAL DEL FORMULARIO
    // ===================================================================
    async function handleSubmit(event) {
        console.log('✅ KIWIPAY: 🚀 === INICIANDO PROCESO DE ENVÍO ===');
        
        const formData = detectFormFields();
        if (!formData) {
            showMessage('❌ No se pudo detectar el formulario', 'error');
            return;
        }
        
        console.log('✅ KIWIPAY: Extrayendo datos del formulario...');
        const { data, errors } = extractFormData(formData.fieldMap);
        
        if (errors.length > 0) {
            console.log('❌ KIWIPAY: Errores de validación:', errors);
            showMessage(`❌ Error: ${errors.join(', ')}`, 'error', 8000);
            return;
        }
        
        // Prevenir el envío normal del formulario
        event.preventDefault();
        event.stopPropagation();
        
        // Enviar a nuestra API
        const result = await sendToAPI(data);
        
        if (result.success) {
            // Opcional: enviar también el formulario original después de éxito
            // formData.form.submit();
        }
        
        console.log('✅ KIWIPAY: 🏁 === FIN DEL PROCESO DE ENVÍO ===');
    }
    
    // ===================================================================
    // 🔧 HERRAMIENTAS DE DEBUG
    // ===================================================================
    window.KiwiPaySquarespace = {
        testFieldExtraction: () => {
            console.log('🧪 KIWIPAY: === TEST DE EXTRACCIÓN DE CAMPOS ===');
            const formData = detectFormFields();
            if (formData) {
                const { data, errors } = extractFormData(formData.fieldMap);
                console.log('📊 Datos extraídos:', data);
                console.log('⚠️ Errores:', errors);
                return { data, errors, fieldMap: formData.fieldMap };
            }
            return null;
        },
        
        testConnection: async () => {
            console.log('🧪 KIWIPAY: === TEST DE CONEXIÓN ===');
            const testData = {
                receptionistName: 'Test Receptionist',
                sede: 'Test Sede',
                clientName: 'Test Client',
                dni: '12345678',
                monthlyIncome: '3000',
                treatmentCost: '5000',
                phone: '987654321'
            };
            
            return await sendToAPI(testData);
        },
        
        showFormStructure: () => {
            const form = document.querySelector('form');
            if (form) {
                console.log('📋 ESTRUCTURA DEL FORMULARIO:');
                const inputs = form.querySelectorAll('input, select, textarea');
                inputs.forEach((input, i) => {
                    console.log(`${i + 1}. ${input.tagName} [${input.type}] - Name: "${input.name}" - ID: "${input.id}" - Placeholder: "${input.placeholder}"`);
                });
            }
        }
    };
    
    // ===================================================================
    // 🚀 INICIALIZACIÓN
    // ===================================================================
    function init() {
        console.log('✅ KIWIPAY: 🎯 === INICIANDO INTEGRACIÓN KIWIPAY SQUARESPACE ===');
        console.log('✅ KIWIPAY: 📍 Página actual:', window.location.pathname);
        console.log('✅ KIWIPAY: 🔗 API URL:', CONFIG.API_URL);
        
        // Esperar a que el DOM esté completamente cargado
        if (document.readyState === 'loading') {
            console.log('✅ KIWIPAY: ⏳ DOM cargando, esperando...');
            document.addEventListener('DOMContentLoaded', init);
            return;
        }
        
        // Agregar estilos CSS para animaciones
        const style = document.createElement('style');
        style.textContent = `
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOutRight {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
        `;
        document.head.appendChild(style);
        
        // Configurar interceptor de formularios
        console.log('✅ KIWIPAY: Configurando interceptor de formularios...');
        
        // Usar delegación de eventos para capturar formularios dinámicos
        document.addEventListener('submit', function(event) {
            const form = event.target;
            if (form.tagName.toLowerCase() === 'form') {
                console.log('✅ KIWIPAY: 📝 Formulario detectado, interceptando envío...');
                handleSubmit(event);
            }
        }, true);
        
        console.log('✅ KIWIPAY: ✅ Interceptor de formularios configurado');
        console.log('✅ KIWIPAY: 🔧 Herramientas de debug disponibles en: window.KiwiPaySquarespace');
        console.log('✅ KIWIPAY: 💡 Prueba: KiwiPaySquarespace.testFieldExtraction() o KiwiPaySquarespace.testConnection()');
        console.log('✅ KIWIPAY: 🎉 === INTEGRACIÓN KIWIPAY ACTIVADA CORRECTAMENTE ===');
        
        // Mostrar campos esperados para debug
        const expectedFields = ['receptionistName', 'sede', 'clientName', 'dni', 'monthlyIncome', 'treatmentCost', 'phone'];
        console.log('✅ KIWIPAY: 📋 Campos esperados:', expectedFields);
    }
    
    // Ejecutar inmediatamente o esperar a que el DOM esté listo
    init();
    
})(); 