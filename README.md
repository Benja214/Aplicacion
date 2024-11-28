# Crear el contenido del manual en formato Markdown
markdown_content = """
# Manual de Usuario

## Índice
- [1. Requisitos Previos](#1-requisitos-previos)
- [2. Registro de Usuario](#2-registro-de-usuario)
- [3. Iniciar Sesión](#3-iniciar-sesion)
- [4. Cerrar Sesión](#4-cerrar-sesion)
- [5. Editar Perfil](#5-editar-perfil)
- [6. Creación de Trabajo (Para Empresas/Admins)](#6-creacion-de-trabajo-para-empresasadmins)
- [7. Vista del Trabajador](#7-vista-del-trabajador)
- [8. Soporte y Contacto](#8-soporte-y-contacto)

## 1. Requisitos Previos
- **Dispositivo Compatible**: Asegúrate de tener un dispositivo móvil compatible con la aplicación (sistemas operativos iOS o Android actualizados).
- **Conexión a Internet**: Verifica que tu dispositivo esté conectado a internet. Se recomienda una conexión estable (Wi-Fi o datos móviles con buena señal).

## 2. Registro de Usuario
### 2.1 Proceso de Registro
1. **Abrir la Aplicación**: Inicia la aplicación en tu dispositivo móvil.
2. **Seleccionar "Registrarse"**: En la pantalla de inicio, toca el botón **“Registrarse”**.
3. **Completar el Formulario de Registro**:
   - **Nombre**: Escribe tu nombre completo.
   - **Apellido**: Introduce tu apellido.
   - **Teléfono**: Proporciona un número de contacto.
   - **Correo Electrónico**: Usa un correo profesional y accesible.
   - **Contraseña**: Ingresa una contraseña segura que incluya letras mayúsculas, minúsculas, números y símbolos.
   - **Confirmación de Contraseña**: Reingresa la misma contraseña para confirmar.
4. **Subir Imagen de Perfil** *(opcional)*: Puedes seleccionar una imagen desde tu galería para establecerla como tu foto de perfil.
5. **Enviar Información**: Toca el botón **“Registrar”** para completar el proceso de registro.

![9240f483-5a8e-4898-bb63-d3dcfa3fec01](https://github.com/user-attachments/assets/392f83a3-055f-45eb-8518-450e2249d646)

### 2.2 Confirmación de Registro
- **Verificación de Correo**: Recibirás un correo de confirmación con un enlace de verificación. Debes hacer clic en el enlace para activar tu cuenta y poder iniciar sesión. Si no lo encuentras en tu bandeja de entrada, revisa la carpeta de spam.

## 3. Iniciar Sesión
### 3.1 Proceso de Inicio de Sesión
1. **Abrir la Aplicación**: Inicia la aplicación en tu dispositivo móvil.
2. **Seleccionar "Iniciar Sesión"**: Toca el botón **“Iniciar Sesión”** en la pantalla de inicio.
3. **Introducir Credenciales**:
   - **Correo Electrónico**: Escribe el correo que utilizaste para registrarte.
   - **Contraseña**: Introduce tu contraseña. Puedes usar el ícono de "mostrar contraseña" para visualizarla.
4. **Tocar "Iniciar Sesión"**: Si tus credenciales son correctas, accederás a la pantalla principal de la aplicación.

![e99dcb27-0f3b-4fcc-b7c1-080f6e1c63e2](https://github.com/user-attachments/assets/f735d2bc-cb54-408f-b347-49403d3c3d60)


## 4. Cerrar Sesión
- **Cerrar Sesión**: Para cerrar sesión, ve al menú de configuración (ícono de tres puntos en la esquina superior derecha) y selecciona **“Cerrar Sesión”**.

![Cerrar Sesión](file:/mnt/data/processed_images/29bbe819-cc35-4ade-8308-6e8e453ad6bf.jpg)

## 5. Editar Perfil
- **Editar Información de Perfil**: Puedes modificar tu información accediendo a la sección de configuración.
  - **Nombre**: Actualiza tu nombre.
  - **Apellido**: Cambia tu apellido.
  - **Teléfono**: Proporciona un nuevo número de contacto si es necesario.
  - **Correo Electrónico**: Introduce un nuevo correo electrónico si lo deseas.
  - **Imagen de Perfil**: Cambia tu imagen de perfil seleccionando una nueva desde tu galería.

![b1e465bb-c098-4c78-bf48-851947da9f6e](https://github.com/user-attachments/assets/a0ae2095-47ea-49ce-9e57-f3874c2223a9)


## 6. Creación de Trabajo (Para Empresas/Admins)

### 6.2 Cómo Crear una Oferta de Trabajo
3. **Completar el Formulario**:
   - Llena los campos requeridos:
     - **Título del Puesto**: Nombre del trabajo.
     - **Descripción**: Detalla las tareas, requisitos y beneficios.
     - **Sueldo**: Indica el monto ofrecido (opcional).
     - **Vacantes**: Número de posiciones disponibles.
     - **Modalidad**: Selecciona una opción: Part-time, Full-time o Por horas.
     - **Fecha de Vencimiento**: Hasta cuándo estará activa la oferta.

![3a63dfa0-56bb-42bb-84a6-4b1fda6421a3](https://github.com/user-attachments/assets/9f027579-2570-4cb9-8faa-9e5c36d798d5)


## 7. Vista del Trabajador
- **Postulaciones**:
  - Antes de postular a un empleo, se te pedirá completar información adicional como experiencia previa, habilidades y una breve descripción personal.
- **Detalle de Postulaciones**:
  - Al postular, podrás visualizar información sobre el empleo, incluyendo requisitos, beneficios y estado de tu postulación.

![29bbe819-cc35-4ade-8308-6e8e453ad6bf](https://github.com/user-attachments/assets/7f71333a-dd0c-48b6-bd19-a29fdf44aab3)

## 8. Soporte y Contacto
Si experimentas problemas durante el registro, inicio de sesión o cualquier otro inconveniente, contacta a nuestro equipo de soporte para obtener asistencia:

- **Correo Electrónico**: Vanner@empresa.com
"""

# Guardar el contenido en un archivo .md
output_md_file = "/mnt/data/manual_usuario.md"
with open(output_md_file, "w") as f:
    f.write(markdown_content)

output_md_file
