# Sistema de Seguimiento y Control de Garantías para Tiendas de Tecnología

**Proyecto de Aula**

**DarkDevs**  
Universidad Popular del Cesar  
Octubre 2026

---

## Resumen

El presente trabajo tiene como objetivo el diseño, desarrollo e implementación de un sistema de seguimiento y control de garantías orientado a tiendas de tecnología. La solución se construye bajo una arquitectura en capas (Model, Repository, Service y UI) con interfaz gráfica, permitiendo una clara separación de responsabilidades, facilitando el mantenimiento del código y posibilitando futuras mejoras sin afectar significativamente su estructura.

En el sector del comercio tecnológico, muchas tiendas aún gestionan las garantías de forma manual o con herramientas poco adaptadas, lo que genera pérdida de información, dificultad para verificar la vigencia de los productos, retrasos en la atención de reclamos y escasa trazabilidad del proceso de postventa. Estos problemas afectan tanto la eficiencia operativa como la satisfacción y fidelización de los clientes.

Ante esta problemática se desarrolla un sistema que permite registrar productos, clientes y garantías, consultar el estado de vigencia, gestionar reclamaciones y generar reportes básicos. Como resultado se obtiene una solución práctica, mantenible y de bajo costo que centraliza la información, reduce errores y eleva la calidad del servicio de postventa en un mercado altamente competitivo.

*Palabras clave:* sistema de garantías, arquitectura en capas, tiendas de tecnología, control de postventa, interfaz gráfica

---

## Introducción

En el sector del comercio de productos tecnológicos, un gran número de tiendas, especialmente las de tamaño pequeño y mediano, todavía gestionan el control de las garantías de forma manual o mediante herramientas genéricas poco adaptadas a sus necesidades reales. Esta situación genera problemas frecuentes como la pérdida o desorganización de la información, la dificultad para verificar de manera rápida y confiable la vigencia de un producto, retrasos en la atención de los reclamos y una escasa trazabilidad del proceso de postventa. Estos inconvenientes afectan tanto la eficiencia operativa del negocio como la satisfacción y fidelización de los clientes en un mercado cada vez más competitivo.

Ante esta realidad, resulta necesario contar con una solución que permita centralizar y organizar la información relacionada con las garantías, reducir errores humanos, mejorar los tiempos de respuesta y elevar la calidad del servicio de postventa. Un sistema especializado no solo optimiza los procesos internos de la tienda, sino que también fortalece la imagen del negocio al ofrecer a los clientes un seguimiento más claro, transparente y confiable de sus productos durante el período de garantía.

El presente trabajo se enfoca en el diseño e implementación de un sistema de seguimiento y control de garantías para tiendas de tecnología, desarrollado bajo una arquitectura en capas (Model, Repository, Service y UI) y con interfaz gráfica. La aplicación permitirá registrar productos, clientes y garantías, consultar el estado de vigencia, gestionar reclamaciones y generar reportes básicos, aplicando principios de diseño orientado a objetos para lograr un sistema mantenible, funcional y de bajo costo.

---

## Planteamiento del Problema

En muchas tiendas de tecnología, especialmente aquellas de tamaño pequeño y mediano, el control de las garantías se realiza todavía de manera manual o a través de herramientas genéricas que no están diseñadas para este propósito. Esta forma de trabajo genera dificultades constantes, como la pérdida o desorganización de la información, la imposibilidad de verificar de forma rápida y confiable si un producto se encuentra dentro del período de garantía, retrasos en la atención de los reclamos y una escasa trazabilidad de todo el proceso de postventa. Como consecuencia, se afectan tanto la eficiencia interna del negocio como la experiencia del cliente.

Esta situación justifica la necesidad de contar con un sistema especializado que permita organizar, consultar y dar seguimiento a las garantías de manera ordenada y eficiente. Una solución de este tipo no solo reduce los errores humanos y los tiempos de respuesta, sino que también contribuye a mejorar la calidad del servicio de postventa, fortalecer la imagen de la tienda y cumplir de forma más adecuada con las expectativas de los clientes en un mercado cada vez más exigente.

Actualmente existen diversos sistemas de gestión comercial y de inventarios que incluyen módulos de garantías; sin embargo, la mayoría están orientados a grandes empresas, requieren inversiones elevadas o presentan una complejidad que resulta excesiva para tiendas de menor tamaño. Por esta razón, se identifica la oportunidad de desarrollar una alternativa más sencilla, de bajo costo y con interfaz gráfica, basada en una arquitectura en capas, que responda de manera específica a las necesidades de control y seguimiento de garantías en el sector de tiendas de tecnología.

---

## Objetivos

### Objetivo General

Diseñar e implementar un sistema de seguimiento y control de garantías para tiendas de tecnología, desarrollado bajo una arquitectura en capas y con interfaz gráfica, que permita gestionar de forma ordenada y eficiente la información relacionada con productos, clientes, garantías y reclamaciones.

### Objetivos Específicos

1. Analizar los requerimientos funcionales y no funcionales del sistema de control de garantías orientado a tiendas de tecnología.
2. Diseñar la arquitectura del sistema aplicando el patrón de capas (Model, Repository, Service y UI) para garantizar la separación de responsabilidades.
3. Implementar las funcionalidades principales del sistema: registro de productos y clientes, gestión de garantías, consulta de vigencia y seguimiento de reclamaciones.
4. Desarrollar una interfaz gráfica intuitiva que permita al usuario interactuar de manera sencilla con todas las operaciones del sistema.
5. Validar el correcto funcionamiento del sistema mediante pruebas que verifiquen el cumplimiento de los requerimientos establecidos.

---

## Descripción General de la Solución

La solución propuesta consiste en un sistema de seguimiento y control de garantías orientado a tiendas de tecnología, desarrollado bajo una arquitectura en capas (Model, Repository, Service y UI) y con interfaz gráfica. El sistema permite gestionar de manera centralizada la información de productos, clientes, garantías y reclamaciones, facilitando la consulta del estado de vigencia, el registro de reclamos y la generación de reportes básicos. Su diseño busca ser funcional, mantenible y de fácil uso, adaptándose a las necesidades de pequeñas y medianas tiendas que requieren un control ordenado del proceso de postventa.

---

## Requerimientos del Sistema

### Requerimientos Funcionales

- Registrar, consultar, modificar y eliminar productos tecnológicos.
- Registrar, consultar, modificar y eliminar clientes.
- Registrar garantías asociadas a un producto y un cliente, especificando fecha de compra y tiempo de cobertura.
- Consultar el estado de vigencia de una garantía mediante número de serie, datos del cliente o identificador de la garantía.
- Registrar reclamaciones de garantía, indicando el problema reportado y el estado del reclamo.
- Actualizar el estado de las reclamaciones (pendiente, en reparación, resuelta o rechazada).
- Generar reportes básicos de garantías activas, garantías próximas a vencer y reclamaciones pendientes.
- Permitir la búsqueda de información mediante diferentes criterios (número de serie, documento del cliente, fechas, entre otros).

### Requerimientos No Funcionales

- El sistema debe contar con una interfaz gráfica intuitiva y de fácil navegación.
- La arquitectura debe seguir el patrón de capas (Model, Repository, Service y UI) para garantizar la separación de responsabilidades.
- El sistema debe ser mantenible y permitir futuras ampliaciones sin afectar significativamente su estructura.
- Los tiempos de respuesta de las operaciones principales deben ser adecuados para un uso fluido.
- La información debe manejarse de forma consistente, evitando duplicidad y pérdida de datos.
- El sistema debe ser compatible con el entorno de desarrollo y ejecución establecido para el proyecto.

---

## Repositorio del Proyecto

- **Nombre del repositorio:** SIGATEC
- **URL:** https://github.com/JhonfczDev/SIGATEC-Project
- **Descripción:** Sistema de seguimiento y control de garantías para tiendas de tecnología, desarrollado bajo una arquitectura en capas (Model, Repository, Service y UI) con interfaz gráfica. El repositorio contiene el código fuente, la documentación y el historial de cambios del proyecto.

---

## Definición de Roles del Equipo

El equipo de trabajo está conformado por tres integrantes, con los siguientes roles:

- **Líder de proyecto:** Responsable de la interfaz gráfica (UI), la clase principal (main) y las clases integradoras que conectan las diferentes capas del sistema. Además, se encarga de la coordinación general, la revisión de avances y la integración final del código.
- **Desarrollador 1:** Encargado del desarrollo de los modelos (entidades del dominio) y de los repositorios correspondientes.
- **Desarrollador 2:** Encargado del desarrollo de los servicios (lógica de negocio) y de apoyar en la implementación de los repositorios.

---

## Estrategia de Ramas

Se utiliza la siguiente estrategia de ramas:

- **main:** Rama principal del proyecto. Contiene únicamente el código estable y revisado. Solo se realizan fusiones (merge) hacia esta rama después de la revisión correspondiente.
- **Ramas por integrante / funcionalidad:**
  - `feature/ui-lider` → Desarrollo de la interfaz gráfica y clases integradoras (Líder).
  - `feature/modelos-dev1` → Desarrollo de los modelos y repositorios (Desarrollador 1).
  - `feature/servicios-dev2` → Desarrollo de los servicios y apoyo en repositorios (Desarrollador 2).

Cada integrante trabaja en su rama correspondiente y, una vez finalizada y probada la funcionalidad, se solicita la integración a la rama main.

### Tabla 1  
*Asignación de Roles, Tareas y Ramas del Equipo*

| Integrante       | Rol                  | Tareas principales                                                                 | Rama asignada            |
|------------------|----------------------|------------------------------------------------------------------------------------|--------------------------|
| Líder            | Líder de proyecto    | Desarrollo de la interfaz gráfica (UI), clase main, clases integradoras, coordinación del equipo e integración final | feature/ui-lider        |
| Desarrollador 1  | Desarrollador        | Diseño e implementación de los modelos (Producto, Cliente, Garantía, Reclamación) y repositorios | feature/modelos-dev1    |
| Desarrollador 2  | Desarrollador        | Diseño e implementación de los servicios (lógica de negocio) y apoyo en repositorios | feature/servicios-dev2  |

*Nota.* Elaboración propia.

---

## Conclusiones

El desarrollo del sistema de seguimiento y control de garantías para tiendas de tecnología permitió dar respuesta a la problemática identificada en la gestión manual o desorganizada de este proceso. A través de una arquitectura en capas (Model, Repository, Service y UI) y una interfaz gráfica, se logró construir una solución ordenada, mantenible y funcional que facilita el registro de productos, clientes y garantías, la consulta de vigencia y el seguimiento de reclamaciones.

La aplicación de principios de diseño orientado a objetos y la separación clara de responsabilidades entre las distintas capas del sistema contribuyeron a obtener un software más organizado y fácilmente escalable. De esta manera, se cumple con el objetivo general y los objetivos específicos planteados, al tiempo que se ofrece una alternativa de bajo costo y adecuada para pequeñas y medianas tiendas de tecnología que requieren mejorar su control de postventa.

En términos generales, el proyecto demuestra que es posible desarrollar una herramienta práctica y útil aplicando conceptos fundamentales de ingeniería de software. El sistema no solo optimiza los procesos internos de la tienda, sino que también aporta valor al mejorar la atención al cliente y fortalecer la gestión de las garantías en un sector altamente competitivo.
