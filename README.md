# Actualizar el contenido del manual de usuario en Markdown con todas las imágenes recibidas
final_markdown_content = """
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

![WhatsApp Image 2024-11-28 at 1 09 26 AM](https://github.com/user-attachments/assets/b29bbb15-e025-4db1-890d-5849c352297a)


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

![e99dcb27-0f3b-4fcc-b7c1-080f6e1c63e2](https://github.com/user-attachments/assets/c4b8dbd7-901d-4e0e-803b-5f2c47c7151e)


## 4. Cerrar Sesión
- **Cerrar Sesión**: Para cerrar sesión, ve al menú de configuración (ícono de tres puntos en la esquina superior derecha) y selecciona **“Cerrar Sesión”**.

![Cerrar Sesión](29bbe819-cc35-4ade-8308-6e8e453ad6bf.jpg)

## 5. Editar Perfil
- **Editar Información de Perfil**: Puedes modificar tu información accediendo a la sección de configuración.
  - **Nombre**: Actualiza tu nombre.
  - **Apellido**: Cambia tu apellido.
  - **Teléfono**: Proporciona un nuevo número de contacto si es necesario.
  - **Correo Electrónico**: Introduce un nuevo correo electrónico si lo deseas.
  - **Imagen de Perfil**: Cambia tu imagen de perfil seleccionando una nueva desde tu galería.

![image](https://github.com/user-attachments/assets/d1498e23-b3f1-4864-bdef-2597126c4c64)

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

![image](https://github.com/user-attachments/assets/d07db95c-317f-4828-bb1e-bc972937454d)

**podras eliminar o editar**:

- tendras la opcion de editar o eliminar al trabajo creado

![1aa3475f-9fa2-49b0-987b-a94382153038](https://github.com/user-attachments/assets/73752b54-dc52-4c79-853d-c25b07ce1945)


## 7. Vista del Trabajador / empresa

**trabajador**:

- **Postulaciones**:
  - Antes de postular a un empleo, se te pedirá completar información adicional como experiencia previa, habilidades y una breve descripción personal.

 ![image](https://github.com/user-attachments/assets/d07db95c-317f-4828-bb1e-bc972937454d)
 
- **Detalle de Postulaciones**:
  - Al postular, podrás visualizar información sobre el empleo, incluyendo requisitos, beneficios y estado de tu postulación.

![image](https://github.com/user-attachments/assets/e4e23922-0623-4161-bd17-9d7e60300b52)

**empresa**:

- tendras la opcion de poder aceptar o rechazar la postulacion del trabajador

  ![image](https://github.com/user-attachments/assets/186f135b-ae52-4286-8683-198ca4b0bdae)


## 8. Soporte y Contacto
Si experimentas problemas durante el registro, inicio de sesión o cualquier otro inconveniente, contacta a nuestro equipo de soporte para obtener asistencia:

- **Correo Electrónico**: Vanner@empresa.com
"""

    f.write(final_markdown_content)

output_final_md_file

