# 📋 Configuración de Perfiles - KiwiPay Loan API

## 🏗️ Estructura de Archivos

```
src/main/resources/
├── application.properties          # Configuración base común
├── application-dev.properties      # Desarrollo/Render  
└── application-prod.properties     # Producción
```

## 🔄 Cambio de Perfiles

### Via Variable de Entorno (Recomendado)
```bash
SPRING_PROFILES_ACTIVE=dev   # Para desarrollo
SPRING_PROFILES_ACTIVE=prod  # Para producción
```

### Via Command Line
```bash
java -jar app.jar --spring.profiles.active=dev
```

## 📊 Comparación de Perfiles

| Configuración | DEV | PROD |
|---------------|-----|------|
| **Logging Level** | DEBUG | WARN/INFO |
| **Circuit Breaker** | Permisivo (60%) | Estricto (50%) |
| **Timeout Connect** | 30s | 20s |
| **Timeout Read** | 60s | 30s |
| **Retry Attempts** | 3 | 2 |
| **Swagger Try-it-out** | ✅ Habilitado | ❌ Deshabilitado |
| **Health Details** | always | when-authorized |
| **Actuator Endpoints** | Todos | Restringidos |
| **Caché Resources** | Deshabilitado | Habilitado (24h) |

## 🎯 Configuraciones Específicas

### **DEV Profile** (development)
- **Propósito**: Desarrollo local y despliegue en Render
- **Logging**: Más verbose para debugging
- **Circuit Breaker**: Más tolerante a fallos
- **CORS**: Incluye localhost ports
- **Swagger**: Completamente funcional

### **PROD Profile** (production)  
- **Propósito**: Producción real
- **Logging**: Optimizado para performance
- **Circuit Breaker**: Más estricto
- **CORS**: Solo dominios específicos (requerido)
- **Swagger**: Limitado (sin try-it-out)

## 🔧 Variables de Entorno por Perfil

### Comunes (Todos los Perfiles)
```bash
KIWIPAY_BACKEND_BASE_URL=https://loan-backend-develop.onrender.com/api/v1
SPRING_APPLICATION_NAME=kiwipay-loan-api
```

### Específicas de DEV
```bash
SPRING_PROFILES_ACTIVE=dev
LOGGING_LEVEL_COM_KIWIPAY=DEBUG
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
```

### Específicas de PROD
```bash
SPRING_PROFILES_ACTIVE=prod
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_KIWIPAY=INFO
CORS_ALLOWED_ORIGINS=https://tu-frontend.com,https://tu-app.com
```

## 🚀 Uso en Render

### Para Desarrollo (Recomendado)
1. Usar perfil `dev`
2. Variables más permisivas
3. Debugging habilitado

### Para Producción
1. Cambiar a perfil `prod`  
2. Definir CORS específicos
3. Ajustar logging levels

## 📝 Notas Importantes

⚠️ **CORS en Producción**: Obligatorio especificar dominios específicos  
⚠️ **Backend URL**: Debe estar configurada en variables de entorno  
⚠️ **Health Check**: En prod, detalles solo con autorización  
⚠️ **Logging**: En prod, menos verbose para mejor performance  
⚠️ **Resilience4j**: Usar `max-attempts` (no `max-retry-attempts`) y duration con unidades (`2000ms` no `2000`) 