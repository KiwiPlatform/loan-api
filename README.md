# KiwiPay Loan API - Middleware

## 📋 Descripción

KiwiPay Loan API es un middleware que actúa como intermediario entre los landings de KiwiPay y el backend principal. Su función principal es recibir leads de los formularios de los landings, validarlos y enviarlos al backend principal para su almacenamiento.

## 🏗️ Arquitectura

```
Landing Pages → KiwiPay Loan API (Middleware) → Backend Principal → Base de Datos
```

### Características principales:

- ✅ **Validación robusta** de datos de entrada
- ✅ **Circuit Breaker** para manejar fallos del backend
- ✅ **Retry automático** con backoff exponencial
- ✅ **Health Check** del backend principal
- ✅ **CORS configurado** para múltiples landings
- ✅ **Documentación OpenAPI/Swagger** integrada
- ✅ **Logging estructurado** para monitoreo

## 🚀 Instalación y Configuración

### Prerrequisitos

- Java 17 o superior
- Gradle 7.5 o superior
- Backend principal ejecutándose en `http://localhost:8080/api/v1`

### Pasos de instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/kiwipay/kiwipay-loan-api.git
cd kiwipay-loan-api
```

2. **Compilar el proyecto**
```bash
./gradlew build
```

3. **Ejecutar la aplicación**
```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8081`

## 📡 Endpoints Disponibles

### 1. Crear Lead
```http
POST /api/v1/leads
Content-Type: application/json

{
    "receptionistName": "Juan Pérez",
    "clientName": "María García",
    "clinicId": 1,
    "medicalSpecialtyId": 1,
    "dni": "12345678",
    "monthlyIncome": 3500.00,
    "approximateCost": 1200.00,
    "phone": "987654321",
    "email": "maria@email.com"
}
```

### 2. Obtener Clínicas
```http
GET /api/v1/clinics
```

### 3. Obtener Especialidades Médicas
```http
GET /api/v1/medical-specialties
```

### 4. Health Check
```http
GET /api/v1/health
```

## 📚 Documentación API

La documentación completa de la API está disponible en:
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api-docs`

## ⚙️ Configuración

### Variables de entorno principales

```yaml
# Backend Principal
BACKEND_BASE_URL=http://localhost:8080/api/v1

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200

# Timeouts
BACKEND_CONNECT_TIMEOUT=30s
BACKEND_READ_TIMEOUT=60s
```

### Configuración completa

Ver archivo `src/main/resources/application.yml` para todas las opciones de configuración.

## 🛡️ Manejo de Errores

La API sigue el estándar RFC 7807 (Problem Details) para el manejo de errores:

```json
{
    "ok": false,
    "errorType": "VALIDATION_ERROR",
    "message": "Error de validación en los datos enviados",
    "status": 400,
    "details": {
        "dni": "El DNI debe tener exactamente 8 dígitos"
    },
    "timestamp": "2024-01-15T10:30:00",
    "path": "/api/v1/leads"
}
```

## 🔧 Características Técnicas

### Circuit Breaker
- Se activa después de 5 fallos consecutivos
- Tiempo de espera en estado abierto: 5 segundos
- Umbral de tasa de fallos: 50%

### Retry Policy
- Máximo 3 intentos
- Backoff inicial: 1 segundo
- Backoff máximo: 10 segundos

### Validaciones
- DNI: Exactamente 8 dígitos
- Teléfono: Exactamente 9 dígitos
- Email: Formato válido
- Montos: Mayor a 0 con máximo 2 decimales

## 📊 Monitoreo

### Actuator Endpoints
- `/actuator/health` - Estado de la aplicación
- `/actuator/metrics` - Métricas de la aplicación
- `/actuator/circuitbreakers` - Estado de los circuit breakers

## 🧪 Testing

### Ejecutar tests
```bash
./gradlew test
```

### Ejemplo con cURL
```bash
curl -X POST http://localhost:8081/api/v1/leads \
  -H "Content-Type: application/json" \
  -d '{
    "receptionistName": "Test Recepcionista",
    "clientName": "Test Cliente",
    "clinicId": 1,
    "medicalSpecialtyId": 1,
    "dni": "12345678",
    "monthlyIncome": 5000.00,
    "approximateCost": 2000.00,
    "phone": "987654321",
    "email": "test@email.com"
  }'
```

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push al Branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es propiedad de KiwiPay. Todos los derechos reservados.

## 📞 Contacto

Equipo de Desarrollo KiwiPay - dev@kiwipay.pe

Proyecto Link: [https://github.com/kiwipay/kiwipay-loan-api](https://github.com/kiwipay/kiwipay-loan-api) 