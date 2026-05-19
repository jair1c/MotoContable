# 🏍️ MotoContable

App Android para moto taxistas — registro de ingresos diarios por alumno y viajes extra.

## 📋 Funcionalidades
- ✅ Registro diario: **ida y vuelta** por separado (alumno: 2 soles c/u, profesor: 6 soles c/u)
- ✅ Registro de **viajes extra** con descripción y monto libre
- ✅ Resumen semanal (lunes–viernes)
- ✅ Historial de semanas anteriores
- ✅ Configuración de nombres y precios

## 🏗️ Stack
| Capa | Tecnología |
|------|-----------|
| UI | Jetpack Compose + Material 3 |
| Estado | ViewModel + StateFlow |
| BD | Room (SQLite local) |
| DI | Hilt |
| CI | GitHub Actions |

## 🚀 Compilar APK
Cada `push` a `main` genera automáticamente un APK en la pestaña **Actions → Artifacts**.

## 📁 Estructura
```
app/src/main/kotlin/com/motocontable/app/
├── MainActivity.kt
├── MotoContableApp.kt
├── data/          ← (Punto 2: Room DB)
├── ui/
│   ├── screens/   ← (Punto 3: Pantallas)
│   └── theme/     ← Colores, tipografía
└── viewmodel/     ← (Punto 2: ViewModel)
```

## 📌 Roadmap
- [x] **Punto 1** — Base del proyecto (Gradle, CI, tema)  
- [ ] **Punto 2** — Base de datos Room + ViewModel  
- [ ] **Punto 3** — Pantalla diaria (ida/vuelta + extras)  
- [ ] **Punto 4** — Resumen semanal  
- [ ] **Punto 5** — Historial y configuración  
"# MotoContable" 
