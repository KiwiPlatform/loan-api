# 🚀 Guía de Despliegue en Render - KiwiPay Loan API

## 📋 Prerrequisitos

✅ **Cuenta en Render**: [render.com](https://render.com/)  
✅ **Repositorio en GitHub** con el código  
✅ **Backend principal desplegado**: `https://loan-backend-develop.onrender.com`  

## 🔧 Archivos ya configurados

- ✅ `Dockerfile` - Imagen Docker optimizada
- ✅ `.dockerignore` - Optimización de build  
- ✅ `render.env` - Variables de entorno
- ✅ `src/main/resources/application.properties` - Configuración base común
- ✅ `src/main/resources/application-dev.properties` - Configuración para desarrollo/render
- ✅ `src/main/resources/application-prod.properties` - Configuración para producción

## 🚀 Pasos para Desplegar

### 1. **Conectar Repositorio**
1. Ve a [render.com](https://render.com) y haz login
2. Clic en **"New" → "Web Service"**
3. Selecciona **"Build and deploy from a Git repository"**
4. Conecta tu cuenta de GitHub
5. Selecciona tu repositorio `kiwipay-loan-api`

### 2. **Configurar el Servicio**
- **Name**: `kiwipay-loan-api` (o el nombre que prefieras)
- **Region**: `Oregon (US West)` (más cercano)
- **Branch**: `master` (o tu rama principal)
- **Runtime**: **Docker** (debería detectarse automáticamente)
- **Instance Type**: **Free** 🆓

### 3. **Configurar Variables de Entorno**
Copia y pega estas variables en **Environment → Add from .env**:

```bash
# ===================================================================
# KIWIPAY LOAN API - VARIABLES DE ENTORNO PARA RENDER
# ===================================================================

# ===================================================================
# CONFIGURACIÓN DE PERFILES
# ===================================================================
# Perfil activo - 'dev' para desarrollo/render, 'prod' para producción
SPRING_PROFILES_ACTIVE=dev

# ===================================================================
# CONFIGURACIÓN DEL SERVIDOR
# ===================================================================
# Render asigna automáticamente el puerto mediante $PORT
# No es necesario establecer SERVER_PORT ya que se usa ${PORT}
SPRING_APPLICATION_NAME=kiwipay-loan-api

# ===================================================================
# BACKEND PRINCIPAL (¡ESTA ES LA MÁS IMPORTANTE!)
# ===================================================================
# URL del backend principal desplegado en Render
KIWIPAY_BACKEND_BASE_URL=https://loan-backend-develop.onrender.com/api/v1

# ===================================================================
# CONFIGURACIÓN DE CORS PARA PRODUCCIÓN
# ===================================================================
# Consistente con el backend principal - incluye el loan-api en los orígenes permitidos
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,http://localhost:8080,http://localhost:8081,https://tu-frontend.onrender.com,https://tu-loan-api.onrender.com

# OPCIÓN DESARROLLO: Permitir cualquier origen (menos seguro)
# CORS_ALLOWED_ORIGINS=*

# ===================================================================
# TIMEOUTS Y CONFIGURACIÓN DE CONEXIÓN
# ===================================================================
KIWIPAY_BACKEND_TIMEOUT_CONNECT=30s
KIWIPAY_BACKEND_TIMEOUT_READ=60s
KIWIPAY_BACKEND_RETRY_MAX_ATTEMPTS=3
KIWIPAY_BACKEND_RETRY_BACKOFF_DELAY=1000ms

# ===================================================================
# CONFIGURACIÓN DE LOGGING PARA PRODUCCIÓN
# ===================================================================
# Consistente con backend principal
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_KIWIPAY=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB_REACTIVE=DEBUG
LOGGING_LEVEL_IO_GITHUB_RESILIENCE4J=INFO
LOGGING_PATTERN_CONSOLE=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# ===================================================================
# CONFIGURACIÓN DE CIRCUIT BREAKER CONFIGURACIÓN
# ===================================================================
RESILIENCE4J_CIRCUITBREAKER_INSTANCES_BACKEND_SLIDING_WINDOW_SIZE=10
RESILIENCE4J_CIRCUITBREAKER_INSTANCES_BACKEND_MINIMUM_NUMBER_OF_CALLS=5
RESILIENCE4J_CIRCUITBREAKER_INSTANCES_BACKEND_FAILURE_RATE_THRESHOLD=50

# ===================================================================
# SWAGGER/OPENAPI CONFIGURACIÓN
# ===================================================================
SPRINGDOC_API_DOCS_PATH=/v3/api-docs
SPRINGDOC_SWAGGER_UI_PATH=/swagger-ui.html

# ===================================================================
# CONFIGURACIÓN DE ACTUATOR PARA MONITOREO
# ===================================================================
# Consistente con backend principal + específicos para middleware
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,circuitbreakers,env,configprops
MANAGEMENT_ENDPOINTS_WEB_BASE_PATH=/actuator
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
MANAGEMENT_PROMETHEUS_METRICS_EXPORT_ENABLED=true

# ===================================================================
# CONFIGURACIONES ADICIONALES PARA PRODUCCIÓN
# ===================================================================
# Compresión del servidor
SERVER_COMPRESSION_ENABLED=true
SERVER_COMPRESSION_MIME_TYPES=application/json,application/xml,text/html

# Manejo de errores
SERVER_ERROR_INCLUDE_MESSAGE=always
SERVER_ERROR_INCLUDE_BINDING_ERRORS=always
```

### 4. **Desplegar**
1. Clic en **"Create Web Service"**
2. El build iniciará automáticamente
3. Espera ⏳ (puede tomar 5-10 minutos)

## 🌐 URLs de tu Aplicación

Una vez desplegado, tendrás:

- **URL base**: `https://tu-servicio.onrender.com`
- **Swagger**: `https://tu-servicio.onrender.com/swagger-ui.html`
- **Health**: `https://tu-servicio.onrender.com/api/v1/health`

### Endpoints disponibles:
- **GET** `/api/v1/clinics` - Listar clínicas
- **GET** `/api/v1/medical-specialties` - Especialidades médicas  
- **POST** `/api/v1/leads` - Crear nuevo lead
- **GET** `/actuator/health` - Health check

## 🔧 Configuraciones Adicionales

### Perfiles Disponibles:

#### **DEV Profile** (Render/Desarrollo)
```bash
SPRING_PROFILES_ACTIVE=dev
```
- Logging más verbose (DEBUG)
- Swagger habilitado con try-it-out
- Circuit breaker más permisivo
- CORS más flexible
- Health check con detalles completos

#### **PROD Profile** (Producción)
```bash
SPRING_PROFILES_ACTIVE=prod
```
- Logging optimizado (WARN/INFO)
- Swagger con try-it-out deshabilitado
- Circuit breaker más estricto
- CORS específico (dominios requeridos)
- Health check restringido
- Caché habilitado

### Para Cambiar a Producción:
1. Cambia en Render: `SPRING_PROFILES_ACTIVE=prod`
2. Define CORS específicos:
```bash
CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app,https://tu-dominio.com
```
3. Ajusta logging si necesario:
```bash
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_KIWIPAY=INFO
```

### Monitoreo:
- Los logs están disponibles en Render Dashboard
- Health check en `/actuator/health`
- Métricas en `/actuator/metrics`
- Circuit breakers en `/actuator/circuitbreakers`

## ⚡ Características del Despliegue

✅ **Multi-stage Docker build** - Optimizado  
✅ **Health checks** - Monitoreo automático  
✅ **Variables de entorno** - Configuración flexible  
✅ **Circuit Breaker** - Resiliencia  
✅ **CORS configurado** - Listo para frontend  
✅ **Swagger UI** - Documentación automática  
✅ **Perfiles separados** - Dev y Prod optimizados  
✅ **Configuración consistente** - Patrón igual al backend principal  

## 🐛 Troubleshooting

### Si el despliegue falla:
1. Revisa los **logs** en Render Dashboard
2. Verifica que `KIWIPAY_BACKEND_BASE_URL` sea correcta
3. Asegúrate que el backend principal esté funcionando

### Error de Resilience4j Retry Properties:
Si ves errores como `'2000 ' is not a valid duration`:
- ✅ **CORRECTO**: `wait-duration=2000ms`
- ❌ **INCORRECTO**: `wait-duration=2000`
- ✅ **CORRECTO**: `max-attempts=3`  
- ❌ **INCORRECTO**: `max-retry-attempts=3`

### Si hay problemas de CORS:
- Verifica `CORS_ALLOWED_ORIGINS` en variables de entorno
- Para desarrollo, usa `*`
- Para producción, usa URLs específicas

### Performance:
- ⚠️ **Free tier** se "duerme" después de 15min sin uso
- ⏱️ Puede tomar hasta 50 segundos "despertar"

## 🎉 ¡Listo!

Tu aplicación estará disponible en:
`https://tu-servicio.onrender.com/swagger-ui.html`

**¡Felicitaciones! 🎊 Tu KiwiPay Loan API está desplegada y funcionando!** 