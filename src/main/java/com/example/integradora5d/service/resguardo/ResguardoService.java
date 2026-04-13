package com.example.integradora5d.service.resguardo;

import com.example.integradora5d.dto.resguardo.ConfirmarResguardoDTO;
import com.example.integradora5d.dto.resguardo.CreateResguardoDTO;
import com.example.integradora5d.dto.resguardo.DevolucionDTO;
import com.example.integradora5d.dto.resguardo.ResguardoUpdateDTO;
import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.checklist_resguardo.BeanChecklist;
import com.example.integradora5d.models.checklist_resguardo.ChecklistRepository;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_EVIDENCIA;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import com.example.integradora5d.models.resguardo.BeanResguardo;
import com.example.integradora5d.models.resguardo.ResguardoRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.service.historial_activo.HistorialService;
import com.example.integradora5d.service.notificacion.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ResguardoService {

    private final ResguardoRepository resguardoRepository;
    private final ActivoRepository activoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final ChecklistRepository checklistRepository;
    private final NotificacionService notificacionService;
    private final HistorialService historialService;

    // Carpeta donde se guardan las fotos
    private static final String UPLOAD_DIR = "uploads/evidencias/";

    public ResguardoService(ResguardoRepository resguardoRepository,
                            ActivoRepository activoRepository,
                            UsuarioRepository usuarioRepository,
                            EvidenciaRepository evidenciaRepository,
                            ChecklistRepository checklistRepository,
                            NotificacionService notificacionService, HistorialService historialService) {
        this.resguardoRepository = resguardoRepository;
        this.activoRepository = activoRepository;
        this.usuarioRepository = usuarioRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.checklistRepository = checklistRepository;
        this.notificacionService = notificacionService;
        this.historialService = historialService;
    }

    // --- ADMIN: Asignar activo a empleado ---
    @Transactional
    public BeanResguardo asignar(CreateResguardoDTO dto) {

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

        resguardoRepository.save(resguardo);

        // Notificación push al empleado
        if (usuario.getTokenDispositivo() != null) {
            notificacionService.enviarNotificacion(
                    usuario.getTokenDispositivo(),
                    "Bien asignado",
                    "Se te ha asignado el bien: " + activo.getEtiquetaBien() +
                            ". Escanea el QR para confirmar el resguardo."
            );
        }

        return resguardo;
    }

    // --- MÓVIL: Verificar QR antes de mostrar formulario ---
    @Transactional(readOnly = true)
    public BeanResguardo verificarQR(Long activoId) {
        return resguardoRepository.findByActivo_IdActivoAndConfirmadoFalse(activoId)
                .orElseThrow(() -> new RuntimeException("No tienes un resguardo pendiente para este activo"));
    }

    // --- MÓVIL: Confirmar resguardo con checklist y fotos ---
    @Transactional
    public BeanResguardo confirmar(ConfirmarResguardoDTO dto,
                                   List<MultipartFile> fotos) throws IOException {

        BeanResguardo resguardo = resguardoRepository.findById(dto.getResguardoId())
                .orElseThrow(() -> new RuntimeException("Resguardo no encontrado"));

        if (resguardo.getConfirmado()) {
            throw new RuntimeException("Este resguardo ya fue confirmado");
        }

        // Guardar checklist
        BeanChecklist checklist = new BeanChecklist();
        checklist.setEnciende(dto.getEnciende());
        checklist.setPantallaFunciona(dto.getPantallaFunciona());
        checklist.setTieneCargador(dto.getTieneCargador());
        checklist.setDanios(dto.getDanios());
        checklist.setObservaciones(dto.getObservaciones());
        checklist.setResguardo(resguardo);
        checklistRepository.save(checklist);

        // Guardar fotos
        guardarFotos(fotos, resguardo, ENUM_TIPO_EVIDENCIA.RESGUARDO);

        // Confirmar resguardo
        resguardo.setConfirmado(true);

        // Cambiar estatus del activo
        resguardo.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.RESGUARDADO);
        activoRepository.save(resguardo.getActivo());

        historialService.registrar(
                resguardo.getActivo(),
                "DISPONIBLE",
                "RESGUARDADO",
                "Resguardo confirmado por empleado",
                resguardo.getUsuario()
        );

        return resguardoRepository.save(resguardo);
    }

    // --- MÓVIL: Devolución ---
    @Transactional
    public BeanResguardo devolver(DevolucionDTO dto,
                                  List<MultipartFile> fotos) throws IOException {

        if (fotos == null || fotos.isEmpty()) {
            throw new RuntimeException("Debes adjuntar al menos una foto para la devolución");
        }

        BeanResguardo resguardo = resguardoRepository.findById(dto.getResguardoId())
                .orElseThrow(() -> new RuntimeException("Resguardo no encontrado"));

        if (!resguardo.getConfirmado()) {
            throw new RuntimeException("Este resguardo no ha sido confirmado");
        }

        // Guardar fotos de devolución
        guardarFotos(fotos, resguardo, ENUM_TIPO_EVIDENCIA.DEVOLUCION);

        // Registrar devolución
        resguardo.setFechaDevolucion(LocalDate.now());
        resguardo.setObservaciones(dto.getObservaciones());

        // Cambiar estatus del activo a disponible
        resguardo.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
        activoRepository.save(resguardo.getActivo());

        historialService.registrar(
                resguardo.getActivo(),
                "RESGUARDADO",
                "DISPONIBLE",
                "Devolución de activo",
                resguardo.getUsuario()
        );

        return resguardoRepository.save(resguardo);
    }

    /**
     * Lista resguardos: ADMINISTRADOR ve todos; el resto solo los propios.
     */
    @Transactional(readOnly = true)
    public List<BeanResguardo> listar(String correoAutenticado) {
        BeanUsuario actor = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        boolean isAdmin = actor.getRol() != null && "ADMINISTRADOR".equals(actor.getRol().getNombre());
        if (isAdmin) {
            return resguardoRepository.findAllByOrderByIdResguardoDesc();
        }
        return resguardoRepository.findByUsuario_IdUsuarioOrderByIdResguardoDesc(actor.getIdUsuario());
    }

    /** Consulta un resguardo por ID (titular del resguardo o ADMINISTRADOR). */
    @Transactional(readOnly = true)
    public BeanResguardo obtenerPorId(Long id, String correoAutenticado) {
        BeanUsuario actor = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));
        assertTitularOAdmin(resguardo, actor);
        return resguardo;
    }

    /**
     * Actualización parcial por ID (REST PUT con semántica de patch: solo campos presentes en el JSON).
     * Respuesta: misma entidad que POST/GET de resguardo ({@link BeanResguardo} serializada a JSON).
     */
    @Transactional
    public BeanResguardo actualizar(Long id, ResguardoUpdateDTO dto, String correoAutenticado) {
        BeanUsuario actor = usuarioRepository.findByCorreo(correoAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        BeanResguardo resguardo = resguardoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resguardo no encontrado"));

        assertTitularOAdmin(resguardo, actor);

        boolean isAdmin = actor.getRol() != null && "ADMINISTRADOR".equals(actor.getRol().getNombre());

        if (!isAdmin && dto.getEstado() != null) {
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

        if (isAdmin && dto.getEstado() != null) {
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

        return resguardoRepository.save(resguardo);
    }

    private void assertTitularOAdmin(BeanResguardo resguardo, BeanUsuario actor) {
        boolean isAdmin = actor.getRol() != null && "ADMINISTRADOR".equals(actor.getRol().getNombre());
        boolean esTitular = resguardo.getUsuario() != null
                && resguardo.getUsuario().getIdUsuario().equals(actor.getIdUsuario());
        if (!isAdmin && !esTitular) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a este resguardo");
        }
    }

    // --- Helper: guardar fotos en disco ---
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


}
