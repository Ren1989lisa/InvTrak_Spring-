package com.example.integradora5d.service.resguardo;

import com.example.integradora5d.dto.resguardo.ConfirmarResguardoDTO;
import com.example.integradora5d.dto.resguardo.CreateResguardoDTO;
import com.example.integradora5d.dto.resguardo.DevolucionDTO;
import com.example.integradora5d.dto.resguardo.ResguardoResponseDTO;
import com.example.integradora5d.dto.resguardo.ResguardoUpdateDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.checklist_resguardo.BeanChecklist;
import com.example.integradora5d.models.checklist_resguardo.ChecklistRepository;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_EVIDENCIA;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.mantenimiento.ENUM_MANTENIMIENTO;
import com.example.integradora5d.models.marca.BeanMarca;
import com.example.integradora5d.models.modelo.BeanModelo;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.models.resguardo.ResguardoRepository;
import com.example.integradora5d.models.rol.BeanRol;
import com.example.integradora5d.models.solicitud_baja.BeanSolicitud;
import com.example.integradora5d.models.solicitud_baja.ENUM_SOLICITUD_BAJA;
import com.example.integradora5d.models.solicitud_baja.SolicitudBajaRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.service.historial_activo.HistorialService;
import com.example.integradora5d.service.notificacion.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ResguardoService {

    private final ResguardoRepository resguardoRepository;
    private final ActivoRepository activoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final ChecklistRepository checklistRepository;
    private final SolicitudBajaRepository solicitudBajaRepository;
    private final NotificacionService notificacionService;
    private final HistorialService historialService;

    private static final String UPLOAD_DIR = "uploads/evidencias/";

    public ResguardoService(ResguardoRepository resguardoRepository,
                            ActivoRepository activoRepository,
                            UsuarioRepository usuarioRepository,
                            EvidenciaRepository evidenciaRepository,
                            ChecklistRepository checklistRepository,
                            SolicitudBajaRepository solicitudBajaRepository,
                            NotificacionService notificacionService,
                            HistorialService historialService) {
        this.resguardoRepository = resguardoRepository;
        this.activoRepository = activoRepository;
        this.usuarioRepository = usuarioRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.checklistRepository = checklistRepository;
        this.solicitudBajaRepository = solicitudBajaRepository;
        this.notificacionService = notificacionService;
        this.historialService = historialService;
    }

    @Transactional
    public ResguardoResponseDTO asignar(CreateResguardoDTO dto) {
        BeanActivo activo = activoRepository.findById(dto.getActivoId())
                .orElseThrow(() -> new RuntimeException("Activo no encontrado"));

        if (activo.getEstatus() != ENUM_ESTATUS_ACTIVO.DISPONIBLE) {
            throw new RuntimeException("El activo no está disponible");
        }

        if (activo.getEstatus() == ENUM_ESTATUS_ACTIVO.BAJA) {
            throw new RuntimeException("El activo está dado de baja y no puede asignarse");
        }

        BeanUsuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BeanResguardo resguardo = new BeanResguardo();
        resguardo.setActivo(activo);
        resguardo.setUsuario(usuario);
        resguardo.setFechaAsignacion(LocalDate.now());
        resguardo.setObservaciones(dto.getObservaciones());
        resguardo.setConfirmado(false);

        BeanResguardo saved = resguardoRepository.save(resguardo);

        if (usuario.getTokenDispositivo() != null) {
            notificacionService.enviarNotificacion(
                    usuario.getTokenDispositivo(),
                    "Bien asignado",
                    "Se te ha asignado el bien: " + activo.getEtiquetaBien() +
                            ". Escanea el QR para confirmar el resguardo."
            );
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ResguardoResponseDTO verificarQR(Long activoId, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        BeanResguardo resguardo;

        if (isAdmin(actor)) {
            resguardo = resguardoRepository
                    .findTopByActivo_IdActivoAndConfirmadoFalseOrderByIdResguardoDesc(activoId)
                    .orElseThrow(() -> new RuntimeException("No tienes un resguardo pendiente para este activo"));
        } else {
            resguardo = resguardoRepository
                    .findTopByActivo_IdActivoAndUsuario_IdUsuarioAndConfirmadoFalseOrderByIdResguardoDesc(
                            activoId,
                            actor.getIdUsuario()
                    )
                    .orElseThrow(() -> new RuntimeException("No tienes un resguardo pendiente para este activo"));
        }

        return toResponse(resguardo);
    }

    @Transactional
    public ResguardoResponseDTO confirmar(ConfirmarResguardoDTO dto,
                                          List<MultipartFile> fotos,
                                          String correoAutenticado) throws IOException {

        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        BeanResguardo resguardo = resguardoRepository.findById(dto.getResguardoId())
                .orElseThrow(() -> new RuntimeException("Resguardo no encontrado"));
        assertTitularOAdmin(resguardo, actor);

        if (Boolean.TRUE.equals(resguardo.getConfirmado())) {
            throw new RuntimeException("Este resguardo ya fue confirmado");
        }

        BeanChecklist checklist = new BeanChecklist();
        checklist.setEnciende(dto.getEnciende());
        checklist.setPantallaFunciona(dto.getPantallaFunciona());
        checklist.setTieneCargador(dto.getTieneCargador());
        checklist.setDanios(dto.getDanios());
        checklist.setObservaciones(dto.getObservaciones());
        checklist.setResguardo(resguardo);
        BeanChecklist savedChecklist = checklistRepository.save(checklist);
        resguardo.setChecklists(savedChecklist);

        guardarFotos(fotos, resguardo, ENUM_TIPO_EVIDENCIA.RESGUARDO);

        resguardo.setConfirmado(true);
        resguardo.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.RESGUARDADO);
        activoRepository.save(resguardo.getActivo());

        historialService.registrar(
                resguardo.getActivo(),
                "DISPONIBLE",
                "RESGUARDADO",
                "Resguardo confirmado por empleado",
                resguardo.getUsuario()
        );

        BeanResguardo saved = resguardoRepository.save(resguardo);
        return toResponse(saved);
    }

    @Transactional
    public ResguardoResponseDTO devolver(DevolucionDTO dto,
                                         List<MultipartFile> fotos,
                                         String correoAutenticado) throws IOException {

        BeanUsuario actor = getActorOrThrow(correoAutenticado);

        if (fotos == null || fotos.isEmpty()) {
            throw new RuntimeException("Debes adjuntar al menos una foto para la devolución");
        }

        BeanResguardo resguardo = resguardoRepository.findById(dto.getResguardoId())
                .orElseThrow(() -> new RuntimeException("Resguardo no encontrado"));
        assertTitularOAdmin(resguardo, actor);

        if (!Boolean.TRUE.equals(resguardo.getConfirmado())) {
            throw new RuntimeException("Este resguardo no ha sido confirmado");
        }

        guardarFotos(fotos, resguardo, ENUM_TIPO_EVIDENCIA.DEVOLUCION);

        resguardo.setFechaDevolucion(LocalDate.now());
        resguardo.setObservaciones(dto.getObservaciones());

        resguardo.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
        activoRepository.save(resguardo.getActivo());

        historialService.registrar(
                resguardo.getActivo(),
                "RESGUARDADO",
                "DISPONIBLE",
                "Devolución de activo",
                resguardo.getUsuario()
        );

        BeanResguardo saved = resguardoRepository.save(resguardo);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ResguardoResponseDTO> listar(String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        boolean isAdmin = isAdmin(actor);
        List<BeanResguardo> resguardos = isAdmin
                ? resguardoRepository.findAllByOrderByIdResguardoDesc()
                : resguardoRepository.findByUsuario_IdUsuarioOrderByIdResguardoDesc(actor.getIdUsuario());

        return resguardos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ResguardoResponseDTO obtenerPorId(Long id, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));
        assertTitularOAdmin(resguardo, actor);
        return toResponse(resguardo);
    }

    @Transactional
    public ResguardoResponseDTO actualizar(Long id, ResguardoUpdateDTO dto, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));

        assertTitularOAdmin(resguardo, actor);

        boolean isAdmin = isAdmin(actor);
        ENUM_ESTATUS_ACTIVO estadoSolicitado = dto.getEstado();

        if (!isAdmin && estadoSolicitado != null && estadoSolicitado != ENUM_ESTATUS_ACTIVO.DEVOLUCION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un administrador puede modificar el estado del activo");
        }

        if (dto.getObservaciones() != null) {
            resguardo.setObservaciones(dto.getObservaciones());
        }

        if (dto.getConfirmado() != null) {
            boolean antes = Boolean.TRUE.equals(resguardo.getConfirmado());
            resguardo.setConfirmado(dto.getConfirmado());
            if (Boolean.TRUE.equals(dto.getConfirmado()) && !antes) {
                BeanActivo activo = resguardo.getActivo();
                if (activo == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resguardo sin activo asociado");
                }
                String anterior = activo.getEstatus() != null ? activo.getEstatus().name() : "";
                activo.setEstatus(ENUM_ESTATUS_ACTIVO.RESGUARDADO);
                activoRepository.save(activo);
                historialService.registrar(activo, anterior, "RESGUARDADO",
                        "Resguardo confirmado (API)", resguardo.getUsuario());
            }
        }

        if (estadoSolicitado != null) {
            BeanActivo activo = resguardo.getActivo();
            if (activo == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resguardo sin activo asociado");
            }
            String anterior = activo.getEstatus() != null ? activo.getEstatus().name() : "";
            activo.setEstatus(dto.getEstado());
            activoRepository.save(activo);
            historialService.registrar(activo, anterior, dto.getEstado().name(),
                    "Actualización de estatus del activo (resguardo)", actor);
        }

        BeanResguardo saved = resguardoRepository.save(resguardo);
        return toResponse(saved);
    }

    @Transactional
    public void eliminar(Long id, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        if (!isAdmin(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un administrador puede eliminar resguardos");
        }

        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));

        BeanActivo activo = resguardo.getActivo();
        if (activo != null && activo.getEstatus() == ENUM_ESTATUS_ACTIVO.DEVOLUCION) {
            String anterior = activo.getEstatus().name();
            activo.setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
            activoRepository.save(activo);
            historialService.registrar(
                    activo,
                    anterior,
                    ENUM_ESTATUS_ACTIVO.DISPONIBLE.name(),
                    "Devolucion aceptada por administrador",
                    actor
            );
        }

        evidenciaRepository.deleteByResguardo_IdResguardo(id);
        checklistRepository.deleteByResguardo_IdResguardo(id);
        resguardoRepository.delete(resguardo);
    }

    @Transactional
    public ResguardoResponseDTO solicitarDevolucion(Long id, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));

        assertTitularOAdmin(resguardo, actor);

        if (!Boolean.TRUE.equals(resguardo.getConfirmado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El resguardo no ha sido confirmado");
        }

        BeanActivo activo = resguardo.getActivo();
        if (activo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resguardo sin activo asociado");
        }

        String anterior = activo.getEstatus() != null ? activo.getEstatus().name() : "RESGUARDADO";
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.DEVOLUCION);
        activoRepository.save(activo);

        historialService.registrar(
                activo,
                anterior,
                ENUM_ESTATUS_ACTIVO.DEVOLUCION.name(),
                "Solicitud de devolución desde app móvil",
                actor
        );

        BeanResguardo saved = resguardoRepository.save(resguardo);
        return toResponse(saved);
    }

    @Transactional
    public ResguardoResponseDTO cancelarBaja(Long id, String correoAutenticado) {
        BeanUsuario actor = getActorOrThrow(correoAutenticado);
        if (!isAdmin(actor)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un administrador puede cancelar una solicitud de baja");
        }

        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));

        BeanActivo activo = resguardo.getActivo();
        if (activo == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resguardo sin activo asociado");
        }

        if (activo.getEstatus() != ENUM_ESTATUS_ACTIVO.SOLICITUD_DE_BAJA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El activo no tiene una solicitud de baja pendiente");
        }

        List<BeanSolicitud> pendientes = solicitudBajaRepository.findByActivoAndEstatusDesc(
                activo.getIdActivo(),
                ENUM_SOLICITUD_BAJA.PENDIENTE
        );

        BeanSolicitud solicitud = pendientes.isEmpty() ? null : pendientes.getFirst();
        if (solicitud != null) {
            solicitud.setEstatus(ENUM_SOLICITUD_BAJA.RECHAZADA);
            solicitud.setUsuarioAprobacion(actor);

            BeanMantenimiento mantenimiento = solicitud.getMantenimiento();
            if (mantenimiento != null) {
                mantenimiento.setEstatus(ENUM_MANTENIMIENTO.EN_PROCESO);
                mantenimiento.setFecha_fin(null);
                if (mantenimiento.getReporte() != null) {
                    mantenimiento.getReporte().setEstatus(com.example.integradora5d.models.reporte_danio.ENUM_REPORTEDANIO.EN_REVISION);
                }
            }

            solicitudBajaRepository.save(solicitud);
        }

        String anterior = activo.getEstatus().name();
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.MANTENIMIENTO);
        activoRepository.save(activo);

        historialService.registrar(
                activo,
                anterior,
                ENUM_ESTATUS_ACTIVO.MANTENIMIENTO.name(),
                "Solicitud de baja cancelada por administrador",
                actor
        );

        BeanResguardo saved = resguardoRepository.save(resguardo);
        return toResponse(saved);
    }

    @Transactional
    public Map<String, String> confirmarPorQr(Long idActivo) {
        BeanUsuario usuarioAutenticado = getUsuarioAutenticado();

        BeanActivo activo = activoRepository.findById(idActivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activo no encontrado"));

        if (resguardoRepository
                .findTopByUsuario_IdUsuarioAndActivo_IdActivoAndConfirmadoTrueOrderByIdResguardoDesc(
                        usuarioAutenticado.getIdUsuario(),
                        idActivo
                )
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Este bien ya fue confirmado previamente");
        }

        BeanResguardo resguardoPendiente = resguardoRepository
                .findTopByUsuario_IdUsuarioAndActivo_IdActivoAndConfirmadoFalseOrderByIdResguardoDesc(
                        usuarioAutenticado.getIdUsuario(),
                        idActivo
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Ese bien no fue asignado a este usuario"));

        resguardoPendiente.setConfirmado(true);
        activo.setEstatus(ENUM_ESTATUS_ACTIVO.RESGUARDADO);

        resguardoRepository.save(resguardoPendiente);
        activoRepository.save(activo);

        return Map.of("message", "Bien resguardado correctamente");
    }

    private BeanUsuario getActorOrThrow(String correoAutenticado) {
        return usuarioRepository.findByCorreoIgnoreCase(correoAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    private BeanUsuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        return usuarioRepository.findByCorreoIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    private boolean isAdmin(BeanUsuario actor) {
        return actor.getRol() != null && "ADMINISTRADOR".equals(actor.getRol().getNombre());
    }

    private void assertTitularOAdmin(BeanResguardo resguardo, BeanUsuario actor) {
        boolean isAdmin = isAdmin(actor);
        boolean esTitular = resguardo.getUsuario() != null
                && resguardo.getUsuario().getIdUsuario().equals(actor.getIdUsuario());
        if (!isAdmin && !esTitular) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a este resguardo");
        }
    }

    private void guardarFotos(List<MultipartFile> fotos,
                              BeanResguardo resguardo,
                              ENUM_TIPO_EVIDENCIA tipo) throws IOException {
        if (fotos == null || fotos.isEmpty()) return;

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        for (MultipartFile foto : fotos) {
            String nombreArchivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
            Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
            Files.write(ruta, foto.getBytes());

            BeanEvidencia evidencia = new BeanEvidencia();
            evidencia.setRuta_archivo(ruta.toString());
            evidencia.setFecha_subida(LocalDateTime.now());
            evidencia.setTipo(tipo);
            evidencia.setResguardo(resguardo);
            evidenciaRepository.save(evidencia);
        }
    }

    private ResguardoResponseDTO toResponse(BeanResguardo source) {
        if (source == null) return null;
        return new ResguardoResponseDTO(
                source.getIdResguardo(),
                source.getFechaAsignacion(),
                source.getConfirmado(),
                source.getObservaciones(),
                source.getFechaDevolucion(),
                mapUsuario(source.getUsuario()),
                mapChecklist(source.getChecklists()),
                mapActivo(source.getActivo())
        );
    }

    private ResguardoResponseDTO.UsuarioDTO mapUsuario(BeanUsuario source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.UsuarioDTO(
                source.getIdUsuario(),
                source.getNombre(),
                source.getCorreo(),
                source.getFechaNacimiento(),
                source.getCurp(),
                source.getNumeroEmpleado(),
                source.getArea(),
                source.getEstatus(),
                source.getPrimerAcceso(),
                source.getTokenDispositivo(),
                mapRol(source.getRol())
        );
    }

    private ResguardoResponseDTO.RolDTO mapRol(BeanRol source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.RolDTO(source.getIdRol(), source.getNombre());
    }

    private ResguardoResponseDTO.ChecklistDTO mapChecklist(BeanChecklist source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.ChecklistDTO(
                source.getIdChecklist(),
                source.getObservaciones(),
                enumName(source.getEnciende()),
                enumName(source.getPantallaFunciona()),
                enumName(source.getTieneCargador()),
                enumName(source.getDanios())
        );
    }

    private ResguardoResponseDTO.ActivoDTO mapActivo(BeanActivo source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.ActivoDTO(
                source.getIdActivo(),
                source.getEtiquetaBien(),
                source.getNumeroSerie(),
                source.getDescripcion(),
                source.getFechaAlta(),
                source.getCosto(),
                enumName(source.getEstatus()),
                mapAula(source.getAula()),
                mapProducto(source.getProducto())
        );
    }

    private ResguardoResponseDTO.AulaDTO mapAula(BeanAula source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.AulaDTO(
                source.getIdAula(),
                source.getNombre(),
                source.getDescripcion(),
                mapEdificio(source.getEdificio())
        );
    }

    private ResguardoResponseDTO.EdificioDTO mapEdificio(BeanEdificio source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.EdificioDTO(
                source.getIdEdificio(),
                source.getNombre(),
                mapCampus(source.getCampus())
        );
    }

    private ResguardoResponseDTO.CampusDTO mapCampus(BeanCampus source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.CampusDTO(source.getIdCampus(), source.getNombre());
    }

    private ResguardoResponseDTO.ProductoDTO mapProducto(BeanProducto source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.ProductoDTO(
                source.getId_producto(),
                source.getNombre(),
                source.getDescripcion(),
                enumName(source.getEstatus()),
                mapModelo(source.getModelo())
        );
    }

    private ResguardoResponseDTO.ModeloDTO mapModelo(BeanModelo source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.ModeloDTO(
                source.getId_modelo(),
                source.getNombre(),
                mapMarca(source.getMarca())
        );
    }

    private ResguardoResponseDTO.MarcaDTO mapMarca(BeanMarca source) {
        if (source == null) return null;
        return new ResguardoResponseDTO.MarcaDTO(source.getId_marca(), source.getNombre());
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
