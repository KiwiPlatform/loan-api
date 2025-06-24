// ===================================================================
// 🚀 KIWIPAY SQUARESPACE INTEGRATION - VERSIÓN FINAL
// ===================================================================
// Endpoint correcto: https://loan-api-j1n6.onrender.com/api/v1/squarespace/lead
// ===================================================================

(function() {
    'use strict';
    
    const CONFIG = {
        API_URL: 'https://loan-api-j1n6.onrender.com/api/v1/squarespace/lead',
        DEBUG: true
    };
    
    // ===================================================================
    // 🎨 MENSAJES DE NOTIFICACIÓN
    // ===================================================================
    function showMessage(message, type = 'success') {
        const existingMessages = document.querySelectorAll('.kiwipay-message');
        existingMessages.forEach(msg => msg.remove());
        
        const messageDiv = document.createElement('div');
        messageDiv.className = 'kiwipay-message';
        messageDiv.style.cssText = `
            position: fixed; top: 20px; right: 20px; z-index: 10000;
            background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
            color: white; padding: 15px 20px; border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            font-size: 14px; font-weight: 500; max-width: 400px;
        `;
        messageDiv.innerHTML = `
            <div style="display: flex; align-items: center; gap: 10px;">
                <div style="font-size: 18px;">
                    ${type === 'success' ? '✅' : type === 'error' ? '❌' : '⏳'}
                </div>
                <div>${message}</div>
            </div>
        `;
        
        document.body.appendChild(messageDiv);
        
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    }
    
    // ===================================================================
    // 🔍 DETECCIÓN INTELIGENTE DE CAMPOS
    // ===================================================================
    function detectFormFields() {
        const form = document.querySelector('form');
        if (!form) {
            console.warn('❌ KIWIPAY: No se encontró formulario');
            return null;
        }
        
        // Obtener todos los inputs
        const inputs = form.querySelectorAll('input[type="text"], input[type="email"], input[type="tel"], input[type="number"], select, textarea');
        
        console.log('✅ KIWIPAY: 🔍 Analizando formulario...');
        console.log('✅ KIWIPAY: 📊 Total de campos encontrados:', inputs.length);
        
        // Mostrar información detallada de cada campo
        inputs.forEach((input, index) => {
            const info = {
                index: index + 1,
                tag: input.tagName.toLowerCase(),
                type: input.type || 'N/A',
                name: input.name || 'sin-name',
                id: input.id || 'sin-id',
                placeholder: input.placeholder || 'sin-placeholder',
                value: input.value || 'vacío'
            };
            
            // Buscar etiqueta asociada
            let labelText = '';
            if (input.id) {
                const label = document.querySelector(`label[for="${input.id}"]`);
                if (label) labelText = label.textContent.trim();
            }
            
            if (!labelText) {
                const parent = input.closest('.form-item, .field-element, .form-wrapper, div, label');
                if (parent) {
                    const textEl = parent.querySelector('.form-item-title, .field-element h3, .caption, label, span');
                    if (textEl) labelText = textEl.textContent.trim();
                }
            }
            
            info.label = labelText || 'sin-label';
            
            console.log(`✅ KIWIPAY: 📋 Campo ${info.index}:`, info);
        });
        
        return { form, inputs: Array.from(inputs) };
    }
    
    // ===================================================================
    // 📝 EXTRACCIÓN DE DATOS POR POSICIÓN
    // ===================================================================
    function extractFormData(formData) {
        const { inputs } = formData;
        
        // Mapeo por posición esperada (basado en la imagen del formulario)
        const fieldMapping = [
            'receptionistName',  // Campo 1: Nombres y Apellidos Recepcionista
            'sede',              // Campo 2: Sede (dropdown)
            'clientName',        // Campo 3: Nombres y Apellidos Cliente
            'dni',               // Campo 4: N° DNI
            'monthlyIncome',     // Campo 5: Ingreso Mensual Promedio
            'treatmentCost',     // Campo 6: Costo Aprox. Tratamiento
            'phone'              // Campo 7: Teléfono
        ];
        
        const data = {};
        
        console.log('✅ KIWIPAY: 📝 Extrayendo datos por posición...');
        
        fieldMapping.forEach((fieldName, index) => {
            if (index < inputs.length) {
                const input = inputs[index];
                let value = '';
                
                if (input.tagName.toLowerCase() === 'select') {
                    const selectedOption = input.options[input.selectedIndex];
                    value = selectedOption ? selectedOption.text.trim() : '';
                } else {
                    value = input.value.trim();
                }
                
                data[fieldName] = value;
                console.log(`✅ KIWIPAY: ✓ ${fieldName} (pos ${index + 1}): "${value}"`);
            } else {
                data[fieldName] = '';
                console.warn(`⚠️ KIWIPAY: ⚠️ ${fieldName} (pos ${index + 1}): No existe campo en esta posición`);
            }
        });
        
        // Validaciones
        const errors = [];
        if (!data.dni || data.dni.length < 8) errors.push('DNI es obligatorio (mínimo 8 dígitos)');
        if (!data.monthlyIncome || isNaN(parseFloat(data.monthlyIncome))) errors.push('Ingreso Mensual es obligatorio');
        if (!data.treatmentCost || isNaN(parseFloat(data.treatmentCost))) errors.push('Costo de Tratamiento es obligatorio');
        if (!data.phone || data.phone.length < 9) errors.push('Teléfono es obligatorio (mínimo 9 dígitos)');
        
        console.log('✅ KIWIPAY: 📊 Datos extraídos:', data);
        console.log('✅ KIWIPAY: 🔍 Validación:', errors.length === 0 ? '✅ Sin errores' : `❌ ${errors.length} errores:`, errors);
        
        return { data, errors };
    }
    
    // ===================================================================
    // 🚀 ENVÍO A LA API
    // ===================================================================
    async function sendToAPI(data) {
        try {
            console.log('✅ KIWIPAY: 📡 Enviando a API...');
            console.log('✅ KIWIPAY: 🔗 Endpoint:', CONFIG.API_URL);
            console.log('✅ KIWIPAY: 📦 Payload:', JSON.stringify(data, null, 2));
            
            showMessage('⏳ Enviando información a KiwiPay...', 'loading');
            
            const response = await fetch(CONFIG.API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(data)
            });
            
            console.log('✅ KIWIPAY: 📡 Respuesta recibida:', response.status, response.statusText);
            
            if (response.ok) {
                const result = await response.json();
                console.log('✅ KIWIPAY: ✅ Éxito:', result);
                showMessage('✅ ¡Información enviada correctamente a KiwiPay!', 'success');
                return { success: true, data: result };
            } else {
                const errorData = await response.text();
                console.error('❌ KIWIPAY: Error del servidor:', response.status, errorData);
                showMessage(`❌ Error del servidor: ${response.status}`, 'error');
                return { success: false, error: `Error ${response.status}` };
            }
            
        } catch (error) {
            console.error('❌ KIWIPAY: Error de conexión:', error);
            showMessage('❌ Error de conexión. Verifique su internet.', 'error');
            return { success: false, error: error.message };
        }
    }
    
    // ===================================================================
    // 🎯 MANEJADOR DEL FORMULARIO
    // ===================================================================
    async function handleFormSubmit(event) {
        console.log('✅ KIWIPAY: 🚀 === INTERCEPTANDO ENVÍO DE FORMULARIO ===');
        
        // Detectar campos del formulario
        const formData = detectFormFields();
        if (!formData) {
            console.error('❌ KIWIPAY: No se pudo detectar el formulario');
            return;
        }
        
        // Extraer datos
        const { data, errors } = extractFormData(formData);
        
        // Si hay errores de validación, mostrar y detener
        if (errors.length > 0) {
            console.log('❌ KIWIPAY: Errores de validación encontrados:', errors);
            showMessage(`❌ Errores: ${errors.join(', ')}`, 'error');
            return; // No prevenir el envío, dejar que Squarespace maneje sus validaciones
        }
        
        // Prevenir el envío normal solo si todo está correcto
        event.preventDefault();
        event.stopPropagation();
        
        // Enviar a nuestra API
        const result = await sendToAPI(data);
        
        console.log('✅ KIWIPAY: 🏁 === PROCESO COMPLETADO ===');
        console.log('✅ KIWIPAY: 📊 Resultado final:', result);
        
        // Si fue exitoso, opcionalmente permitir que el formulario se envíe también
        if (result.success) {
            // Opcional: enviar el formulario original después de éxito
            // setTimeout(() => formData.form.submit(), 1000);
        }
    }
    
    // ===================================================================
    // 🔧 HERRAMIENTAS DE DEBUG
    // ===================================================================
    window.KiwiPayDebug = {
        showFormStructure: () => {
            console.log('🔍 === ESTRUCTURA DEL FORMULARIO ===');
            const formData = detectFormFields();
            if (formData) {
                console.log('📊 Formulario detectado con', formData.inputs.length, 'campos');
                return formData;
            }
            return null;
        },
        
        testExtraction: () => {
            console.log('🧪 === TEST DE EXTRACCIÓN ===');
            const formData = detectFormFields();
            if (formData) {
                return extractFormData(formData);
            }
            return null;
        },
        
        testAPI: async () => {
            console.log('🧪 === TEST DE API ===');
            const testData = {
                receptionistName: 'Juan Test',
                sede: 'Principal',
                clientName: 'María Test',
                dni: '12345678',
                monthlyIncome: '3000',
                treatmentCost: '5000',
                phone: '987654321'
            };
            
            return await sendToAPI(testData);
        }
    };
    
    // ===================================================================
    // 🚀 INICIALIZACIÓN
    // ===================================================================
    function init() {
        console.log('✅ KIWIPAY: 🎯 === INICIANDO INTEGRACIÓN KIWIPAY-SQUARESPACE ===');
        console.log('✅ KIWIPAY: 📍 Página:', window.location.pathname);
        console.log('✅ KIWIPAY: 🔗 API Endpoint:', CONFIG.API_URL);
        
        // Esperar a que el DOM esté listo
        if (document.readyState === 'loading') {
            console.log('✅ KIWIPAY: ⏳ Esperando DOM...');
            document.addEventListener('DOMContentLoaded', init);
            return;
        }
        
        // Configurar interceptor usando delegación de eventos
        document.addEventListener('submit', function(event) {
            if (event.target.tagName.toLowerCase() === 'form') {
                console.log('✅ KIWIPAY: 📝 Formulario detectado, procesando...');
                handleFormSubmit(event);
            }
        }, true);
        
        console.log('✅ KIWIPAY: ✅ Interceptor configurado');
        console.log('✅ KIWIPAY: 🔧 Debug disponible en: window.KiwiPayDebug');
        console.log('✅ KIWIPAY: 💡 Prueba: KiwiPayDebug.showFormStructure()');
        console.log('✅ KIWIPAY: 🎉 === INTEGRACIÓN ACTIVADA ===');
    }
    
    // Inicializar
    init();
    
})(); 