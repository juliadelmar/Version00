# Syntra 🏋️‍♀️📆

> Syntra es una aplicación móvil nativa para Android diseñada para mujeres que entrenan regularmente, ya sea en el gimnasio o en casa. Integra de forma inteligente la planificación y el seguimiento del entrenamiento con la información del ciclo menstrual, para optimizar el rendimiento y el bienestar.

---

## 📑 Tabla de contenidos

1. [¿Qué es Syntra?](#qué-es-syntra)
2. [✨ Características Clave](#-características-clave)
3. [🛠️ Tecnologías Utilizadas](#️-tecnologías-utilizadas)
4. [🚀 Instalación y Uso](#-instalación-y-uso)

   * [🔒 Habilitar Orígenes Desconocidos](#-habilitar-orígenes-desconocidos)
   * [📥 Descargar el APK](#-descargar-el-apk)
   * [📲 Instalar la Aplicación](#-instalar-la-aplicación)
5. [📂 Estructura del Proyecto](#-estructura-del-proyecto)
6. [🤝 Contacto y Contribución](#-contacto-y-contribución)

---

## ¿Qué es Syntra?

Syntra es una app de entrenamiento personalizada para mujeres, que combina:

* **Planificación inteligente** del entrenamiento según las fases del ciclo menstrual.
* **Seguimiento detallado** de rutinas, estadísticas y medidas corporales.
* **Enfoque basado en datos** para mejorar el rendimiento y bienestar físico.

---

## ✨ Características Clave

* **Rutinas Personalizadas**: Planes de entrenamiento adaptados a fases hormonales (folicular, ovulatoria, lútea, menstrual) y objetivos individuales.
* **Seguimiento del Ciclo Menstrual**: Registro de periodos para recomendaciones de entrenamiento y nutrición.
* **Visualización SVG de Fatiga Muscular**: Modelo anatómico interactivo que muestra los músculos trabajados y su nivel de fatiga.
* **Registro de Medidas Corporales**: Peso, pliegues cutáneos y métricas avanzadas con gráficas interactivas.
* **Estadísticas de Rendimiento**: Resúmenes semanales de calorías, repeticiones, cargas y más.
* **Sugerencias Inteligentes (IA básica)**: Recomendaciones automáticas de peso, RIR y repeticiones según historial y fase del ciclo.
* **Gestión de Perfil**: Personalización de datos de usuario y avatar.
* **Modo Claro/Oscuro**: Temas visuales claro, oscuro o automático.
* **Red Social Básica**: Conexión con otras usuarias para motivación y apoyo.

---

## 🛠️ Tecnologías Utilizadas

* **Jetpack Compose**: Interfaces reactivas y modernas.
* **Firebase**: Autenticación, base de datos en tiempo real, almacenamiento y notificaciones push.
* **Vico**: Gráficas interactivas.
* **SVG Renderer Personalizado**: Visualización anatómica de fatiga.
* **Retrofit**: Integración con APIs externas (ejercicios, noticias, nutrición).
* **Clean Architecture (MVVM)**: Separación de responsabilidades y modularidad.
* **Hilt**: Inyección de dependencias.

---

## 🚀 Instalación y Uso

### 🔒 Habilitar Orígenes Desconocidos

1. Ve a **Ajustes ▶️ Seguridad** en tu dispositivo Android.
2. Activa la opción de **Instalar apps de orígenes desconocidos**.

### 📥 Descargar el APK

1. Abre el repositorio de GitHub: [juliadelmar/Version00](https://github.com/juliadelmar/Version00)
2. Navega a **app/release/** y descarga el archivo `app-release.apk`.

### 📲 Instalar la Aplicación

1. Abre el APK descargado en tu dispositivo.
2. Sigue las instrucciones para instalar Syntra.
3. ¡Listo! Inicia Syntra y crea tu cuenta o accede con tu usuario.

---

## 📂 Estructura del Proyecto

El proyecto sigue Clean Architecture:

* **Dominio**: Entidades y casos de uso.
* **Datos**: Repositorios, acceso a la red y persistencia.
* **Presentación**: Vistas y ViewModels con Jetpack Compose.
* **Inyección de Dependencias**: Configurada con Hilt.


---

## 🤝 Contacto y Contribución

¿Tienes preguntas, sugerencias o quieres contribuir? ¡Bienvenida!

* Repositorio: [https://github.com/juliadelmar/Version00](https://github.com/juliadelmar/Version00)
* Autora: Julia del Mar López

¡Gracias por formar parte de la comunidad Syntra y alcanzar tus metas de entrenamiento! 🎉
