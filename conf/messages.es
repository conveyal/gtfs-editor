# You can specialize this file for each language.
# For example, for French create a messages.fr file
#
secure.title=
secure.username=Username: Usuario
secure.password=Password: Contraseña
secure.remember= Recordar detalles
secure.signin=Sign in Entrar
secure.error=Unknown username or password. Nombre de usuario o contraseña desconocido.
secure.logout=You have been logged out. Ha cerrado sesión.


# You can specialize this file for each language.
# For example, for French create a messages.fr file

application.name=Transit Database

# nav-bar

nav-bar.agency= Información de organismo de transporte
nav-bar.home=  Inicio
nav-bar.contact= Contacto
nav-bar.user-greeting=¡Bienvenido!

nav-bar.export=Exportar
nav-bar.admin=Administrador

nav-bar.logout=Salir

# shared

shared.optional= Opcional
shared.required= Obligatorio/Requerido


shared.monday=Lunes
shared.tuesday=Martes
shared.wednesday=Miércoles
shared.thursday=Jueves
shared.friday=Viernes
shared.saturday=Sábado
shared.sunday=Domingo

shared.route-type.tram=Tranvía
shared.route-type.subway=Metro
shared.route-type.rail=Tren
shared.route-type.bus=Autobús
shared.route-type.ferry=Ferry
shared.route-type.colectivo=Microbús
shared.route-type.cable-car=Teleférico
shared.route-type.gondola=Góndola
shared.route-type.funicular=Funicular

shared.route-type.hvt.rail=Servicio de tren
shared.route-type.hvt.rail_hs=Servicio de tren de alta velocidad
shared.route-type.hvt.rail_ld=Trenes de larga distancia
shared.route-type.hvt.rail_shuttle=Tren de conexión (dentro de algún complejo)
shared.route-type.hvt.rail_suburban=Tren suburbano
shared.route-type.hvt.coach=Servicio de autobús foráneo
shared.route-type.hvt.coach_international=Servicio de autobús foráneo internacional
shared.route-type.hvt.coach_national=Servicio de autobús foráneo nacional
shared.route-type.hvt.coach_regional=Servicio de autobús foráneo regional
shared.route-type.hvt.coach_commuter=Servicio de autobús foráneo metropolitano
shared.route-type.hvt.urbanrail=Servicio de tren urbano
shared.route-type.hvt.urbanrail_metro=Servicio de metro
shared.route-type.hvt.urbanrail_underground=Servicio de subterráneo
shared.route-type.hvt.urbanrail_monorail=Monoriel
shared.route-type.hvt.bus=Servicio de autobús
shared.route-type.hvt.bus_regional=Servicio de autobús regional
shared.route-type.hvt.bus_express=Servicio de autobús exprés
shared.route-type.hvt.bus_local=Servicio de autobús local
shared.route-type.hvt.bus_unscheduled=Servicio de autobús sin programación
shared.route-type.hvt.trolleybus=Servicio de trolebús
shared.route-type.hvt.tram=Servicio de tranvía
shared.route-type.hvt.water=Servicio de transporte acuático
shared.route-type.hvt.air=Servicio aéreo
shared.route-type.hvt.telecabin=Servicio de teleférico-cabinas
shared.route-type.hvt.funicular=Servicio de funicular
shared.route-type.hvt.miscellaneous= Servicios Misceláneos
shared.route-type.hvt.miscellaneous_cable_car=Teleférico
shared.route-type.hvt.miscellaneous_horse_carriage=Carruaje a caballo



shared.details=Detalles

shared.button.save=Guardar
shared.button.delete=Borrar
shared.button.save-continue=Guardar y continuar
shared.button.edit=Editar
shared.button.close=Cerrar
shared.button.cancel=Cancelar
shared.button.add=Agregar
shared.button.update=Actualizar
shared.button.remove=Quitar

shared.export-data-btn=Exportar

shared.explore-search-routes=Explorar/Buscar rutas
shared.new-route=Nueva ruta


# /index


# /route

route.edit-route.title=Editar Ruta

route.steps-header= Pasos para agregar una nueva ruta

route.step1=Información básica
route.step2=Paradas/Estaciones
route.step3=Patrones de viajes
route.step4=Viajes
route.step5=Revisar

route.filter-stops=Filtrar Paradas/Estaciones
route.filter-stops-by-agency=Mostrar solo las paradas/estaciones de
route.filter-all-stops=Mostrar todas las paradas/estaciones
route.filter-hidden-stops= Algunas paradas/estaciones pueden estar ocultas con el nivel de zoom actual



route.route-info-tpl.agency=Organismo de Transporte

route.route-info-tpl.gtfs-id=GTFS Id

route.route-info-tpl.short-name=Nombre corto
route.route-info-tpl.short-name-content= El campo route_short_name contiene el nombre corto de una ruta. Esto comúnmente será un identificador corto y abstracto como &quot;32&quot;, &quot;100X&quot;, o &quot;Verde&quot; que los usuarios utilizan para identificar la ruta, pero que no brinda información sobre los lugares que la ruta sirve.

route.route-info-tpl.long-name=Nombre largo.
route.route-info-tpl.long-name-content= El campo route_long_name contiene el nombre completo de una ruta. Este nombre es generalmente más descriptivo que route_short_name y comúnmente incluirá el destino o terminal. Si la ruta no tiene un nombre largo, por favor especifique route_short_name y deje vacío este campo.

route.route-info-tpl.route-type=Tipo
route.route-info-tpl.route-type-content=El campo route_type describe el tipo de transporte de una ruta.

route.route-info-tpl.description=Descripción
route.route-info-tpl.description-content=El campo route_desc contiene la descripción de una ruta. Por favor brinde información útil y de calidad. No duplique el nombre de la ruta.

route.route-info-tpl.url=URL
route.route-info-tpl.url-content=El campo route_url  contiene el URL de una página web sobre una ruta en particular. Esta debe ser distinta al campo agency_url. El valor debe ser una URL válida y completa que incluya http:// o https://, y cualquier carácter especial en el URL debe insertarse correctamente.

route.route-info-tpl.route-color=Color
route.route-info-tpl.route-color-content=En los sistemas que tienen colores asignados a las rutas, el campo route_color  define el color que corresponde a una ruta. El color se debe ingresar como un número hexadecimal de seis dígitos, por ejemplo, 00FFFF. Si no se especifica ningún color, el color predeterminado es blanco (FFFFFF).

route.route-info-tpl.route-text-color=Color del texto
route.route-info-tpl.route-text-color-content=El campo route_text_color  puede usarse para especificar un color legible para el texto a dibujar sobre un fondo  del color ingresado en route_color. El color se deber ingresar como un número hexadecimal de seis dígitos, por ejemplo, FFD700. Si no se especifica ningún color, el color de texto predeterminado es negro (000000).

route.route-info-tpl.air-con-label=Aire Acondicionado?
route.route-info-tpl.air-con-checkbox=Con Aire acondicionado

route.route-info-tpl.comments=Comentarios
route.route-info-tpl.comments-content=Agregue cualquier información interna útil sobre esta ruta. Este es un campo no oficial y no aparecerá en el archivo GTFS.


route.stop-instructions-tpl= Clic derecho en el mapa para agregar una nueva parada/estación, o clic al marcador para editar los detalles.

route.stops-import=Importar paradas/estaciones
route.stops-upload-data=Subir información de paradas/estaciones

route.stops-stop-details=Detalles de la parada/estación

route.stops-stop-name=Nombre de parada/estación
route.stops-stop-description=Descripción
route.stops-major-stop= Parada/ estación principal



route.trip-pattern-trip-patterns=Patrones de viaje
route.trip-pattern-select-pattern=Seleccionar patrón

route.trip-pattern-create-new-pattern=Crear un nuevo patrón
route.trip-pattern-delete-pattern=Borrar patrón

route.trip-pattern-stop-sequence=Detener la secuencia
route.trip-pattern-pattern-stops=Parada/estaciones del patrón

route.trip-pattern-zoom-pattern=Zoom al patrón
route.trip-pattern-clear-pattern=Limpiar patrón

route.trip-pattern-kmh=km/hr
route.trip-pattern-calculate-times=Calcular tiempos


route.trip-pattern-alignment=Alineamiento
route.trip-pattern-create-alignment-from-pattern= Crear alineamiento a partir del patrón

route.trip-pattern-new-pattern-name=Nombre del nuevo patrón de viaje
route.trip-pattern-transit-wand-id=Identificador de seis dígitos de TransitWand
route.trip-pattern-load-transitwand=Cargar desde TransitWand

route.trip-pattern-popup-add-stop-pattern=Añadir parada/estación a
route.trip-pattern-popup-pattern-stop=Parada/estación del patrón
route.trip-pattern-travel-time=Tiempo de recorrido (mm:ss)
route.trip-pattern-dwell-time=Tiempo de acenso-descenso Time (mm:ss)



route.trip-info-tpl.trip-pattern=Patrón de viaje

route.trip-info-tpl.trip-description=Descripción del viaje

route.trip-info-tpl.start-time=Hora de inicio
route.trip-info-tpl.start-time-content=La hora del día en que este patrón empieza a operar.

route.trip-info-tpl.end-time=Hora de cierre.
route.trip-info-tpl.end-time-content=La hora del día en la que este patrón termina de operar.

route.trip-info-tpl.service-frequency=Frecuencia en el servicio
route.trip-info-tpl.service-frequency-content=Número de minutos y segundos entre los tiempos de arribo promedio.

route.trip-info-tpl.days-in-service=Días en servicio

route.trip-info-tpl.route-types.title=Tipo de ruta

route.trip-info-tpl.select-calendar=Seleccionar calendario

route.trip-info-tpl.hhmmss=hh:mm[:ss]

route.trip-info-tpl.calendar-create-modal.title=Crear calendario
route.trip-info-tpl.calendar-create-modal.calendar-name=Nombre del calendario

# timetable
timetable.save-changes=Guardar cambios del horario
timetable.new-trip=Crear viaje

# /search

search.title=Rutas de


# /manage/routeTypes

manage.route-types.title=Administrar tipo de rutas
manage.route-types.dialog-title=Nuevo tipo de ruta
manage.route-types.create-new-type-button=Crear nuevo tipo


manage.route-types.localized-vehicle-type=Tipo de vehículo local
manage.route-types.localized-vehicle-type-content= Los tipos de vehículo locales son una extensión de la especificación GTFS que define los nombres de los servicios de transporte como los entiende el público en una ciudad o región en especifico (ej. El metro en Washington DC se conoce como &quot;Metro&quot; el BRT en la Ciudad de México es  &quot;Metrobús&quot;). Deben reflejar el idioma y caracteres de un lugar en específico. El tipo de vehículo local se refiere al servicio brindado, no al operador o al organismo de transporte, aunque en algunos lugares los nombres de tipo de vehículos y los operadores pueden coincidir.

manage.route-types.description=Descripción
manage.route-types.description-content=Descripción del tipo de ruta

manage.route-types.gtfs-route-type=Tipo de ruta GTFS
manage.route-types.gtfs-route-type-content=El campo route_type describe el tipo de transporte utilizado en una ruta.

manage.route-types.hsv-route-type= Tipo de ruta HVT
manage.route-types.hsv-route-type-content= El campo route_type describe el tipo de transporte utilizado en una ruta, usando el sistema de nomenclatura alterno HVT.

# manage/stopTypes

manage.stop-types.title=Administrar el tipo de parada/estación


# /manage/agencies

manage.agencies.title=Administrar organismos de transporte
manage.agencies.dialog-title=Nuevo organismo de transporte
manage.agencies.create-new-agency-button=Crear un nuevo organismo de transporte

manage.agencies.gtfs-agency-id= ID GTFS de Organismo de Transporte
manage.agencies.gtfs-agency-id-content= Identificador opcional GTFS de Organismo de Transporte. Debe ser un identificador alfanumérico corto, único para el organismo de transporte (ej. El nombre del organismo de transporte abreviado). Si no se especifica, se asignará un número exclusivo automáticamente.

manage.agencies.name=Nombre
manage.agencies.name-content= Nombre público del organismo de transporte

manage.agencies.url=URL
manage.agencies.url-content= URL del sitio web del organismo de transporte

manage.agencies.default-lat=Latitud predeterminada
manage.agencies.default-lat-content=Latitud predeterminada como punto central del mapa

manage.agencies.default-lon=Longitud predeterminada
manage.agencies.default-lon-content= Longitud predeterminada como punto central del mapa

manage.agencies.default-route-type=Tipo de ruta predeterminado
manage.agencies.default-route-type-content= Tipo de ruta predeterminado

manage.agencies.timezone=Huso horario
manage.agencies.timezone-content=Huso horario del feed.

manage.agencies.lang=Idioma
manage.agencies.lang-content=Idioma del feed.

# /export/gtfs

export.gtfs.title=Exportar como GTFS

export.gtfs.calendar-from=GTFS Válido desde

export.gtfs.calendar-to=GTFS Válido hasta

# /export/kml

export.kml.title=Exportar como KML

# /export/shapefile

export.shapefile.title=Exportar como Shapefile

export.gis.title=Exportar como Shapefile
export.gis.type=Tipo de Exportación

export.gis.stops=Exportar Paradas/Estaciones

export.gis.routes=Exportar Rutas

export.agencies=Exportar Organismo de Transporte




secure.title=
secure.username=Usuario:
secure.password=Contraseña:
secure.remember=Recordar detalles
secure.signin=Iniciar sesión
secure.error=Nombre de usuario o contraseña incorrectos
secure.logout=Has cerrado sesión

route.trip-pattern-timing=Tiempos


route.trip-info-instructions=Generar una programación de servicio agregando ‘viajes’ a los ‘patrones de viaje’.

route.trip-info-tpl.trip-pattern-content=Seleccionar un patrón de viaje existente para crear viajes.

route.route-info-tpl.pattern-count=Recuento de patrones de viaje
route.route-info-tpl.trip-count=Recuento de viajes


route.trip-info-tpl.schedule-type=Tipo de Programación

route.trip-pattern-select-schedule-type-frequency=Utilizar Frecuencia
route.trip-pattern-select-schedule-type-timetable=Utilizar Calendario

route.trip-info-tpl.trip=Viaje
route.trip-info-tpl.trip-content=Seleccionar o crear un viaje.

route.trip-info-tpl.trip-description-content=Descripción del viaje (ej. Hora pico en día hábil).

route.trip-pattern-select-trip=Seleccionar viaje

route.trip-info-tpl.create-trip-btn=Crear nuevo viaje
route.trip-info-tpl.delete-trip-btn=Borrar viaje
route.trip-info-tpl.edit-timetable=Cambiar horario

route.trip-info-new-trip-name=Descripción del nuevo viaje

route.trip-info-tpl.service-calendar=Calendario de servicio
route.trip-info-tpl.service-calendar-content= Especificar los días de la semana en los que este viaje funciona.

route.trip-info-tpl.calendar-create-modal.calendar-name-content=Nombre del calendario(ej. Servicio en día hábil).

route.trip-info-tpl.calendar-modify=Modificar Calendario

route.stops-bike-parking=Biciestacionamiento
route.stops-wheelchair-boarding=Acceso con silla de ruedas


route.route-review-instructions=Revisar ruta/línea y aprobar para publicación.

route.review-status=Estado
route.review-publicly-visible=Visible públicamente

route.review.status.in-progress=En curso
route.review.status.pending-approval=Aprobación pendiente
route.review.status.approved=Aprobada


shared.attribute.unknown=Sin información
shared.attribute.available=Disponible
shared.attribute.unavailable=No disponible


# this is already translated but the english text changed slightly.
route.trip-pattern-instructions=Clic en las paradas/estaciones en el mapa para crear la secuencia de un patrón de viaje. Establezca el tiempo de recorrido entre paradas/estaciones y el de ascenso-descenso de pasajeros y genere un alineamiento.


# error messages and prompts

route.stops-delete-stop-confirm=¿Está seguro de querer borrar esta parada/estación?

route.trip-pattern-clear-pattern-confirm==¿Está seguro de querer quitar las paradas/estaciones de este patrón?

route.trip-pattern-delete-pattern-confirm==¿Está seguro de querer borrar este patrón?


route.trip-info-delete-pattern-confirm==¿Está seguro de querer borrar este viaje?


route.trip-pattern-create-failed=Error al crear el patrón de viaje.
route.trip-pattern-create-failed-no-name=Error al crear patrón de viaje, por favor introduzca un nombre.
route.trip-pattern-add-stop-failed=Error al añadir la parada/estación
route.trip-pattern-add-stop-failed-no-pattern=Seleccionar o crear un patrón antes de añadir paradas/estaciones.


route.trip-pattern-no-tw-data=No hay datos disponibles para este número Id. de TransitWand.
route.trip-pattern-tw-unknown-id=Número Id. de TransitWand incorrecto.
route.trip-pattern-tw-unable-load=Error al cargar los datos desde TransitWand

shared.button.duplicate=Duplicar
route.trip-pattern-duplicate-pattern=Duplicar Patrón
route.trip-pattern-reverse-pattern=Patrón Inverso
route.trip-pattern-reverse-pattern-confirm=¿Quieres invertir el patrón de viaje?
route.trip-pattern-edit-pattern=Editar Patrón Nombre

route.trip-pattern-map-satellite=Utilizar Satélite
