package com.example.integradora5d.service.mantenimiento;

import com.example.integradora5d.dto.mantenimiento.AsignarMantenimientoDTO;
import com.example.integradora5d.dto.mantenimiento.AtenderMantenimientoDTO;
import com.example.integradora5d.dto.mantenimiento.CerrarMantenimientoDTO;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.evidencia.BeanEvidencia;
import com.example.integradora5d.models.evidencia.ENUM_TIPO_EVIDENCIA;
import com.example.integradora5d.models.evidencia.EvidenciaRepository;
import com.example.integradora5d.models.mantenimiento.BeanMantenimiento;
import com.example.integradora5d.models.mantenimiento.ENUM_MANTENIMIENTO;
import com.example.integradora5d.models.mantenimiento.MantenimientoRepository;
import com.example.integradora5d.models.prioridad.BeanPrioridad;
import com.example.integradora5d.models.prioridad.PrioridadRepository;
import com.example.integradora5d.models.reporte_danio.BeanReporte;
import com.example.integradora5d.models.reporte_danio.ENUM_REPORTEDANIO;
import com.example.integradora5d.models.reporte_danio.ReporteRepository;
import com.example.integradora5d.models.solicitud_baja.BeanSolicitud;
import com.example.integradora5d.models.solicitud_baja.ENUM_SOLICITUD_BAJA;
import com.example.integradora5d.models.solicitud_baja.SolicitudBajaRepository;
import com.example.integradora5d.models.usuario.BeanUsuario;
import com.example.integradora5d.models.usuario.UsuarioRepository;
import com.example.integradora5d.service.historial_activo.HistorialService;
import com.example.integradora5d.service.notificacion.NotificacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MantenimientoService {

    private final MantenimientoRepository mantenimientoRepository;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrioridadRepository prioridadRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final SolicitudBajaRepository solicitudBajaRepository;
    private final NotificacionService notificacionService;
    private final HistorialService historialService;

    private static final String UPLOAD_DIR = "uploads/mantenimientos/";

    public MantenimientoService(MantenimientoRepository mantenimientoRepository,
                                ReporteRepository reporteRepository,
                                UsuarioRepository usuarioRepository,
                                PrioridadRepository prioridadRepository,
                                EvidenciaRepository evidenciaRepository,
                                SolicitudBajaRepository solicitudBajaRepository,
                                NotificacionService notificacionService, HistorialService historialService) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
        this.prioridadRepository = prioridadRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.solicitudBajaRepository = solicitudBajaRepository;
        this.notificacionService = notificacionService;
        this.historialService = historialService;
    }

    // WEB - Admin obtiene todos los reportes abiertos
    @Transactional(readOnly = true)
    public List<BeanReporte> getReportesAbiertos() {
        return reporteRepository.findByEstatus(ENUM_REPORTEDANIO.ABIERTO);
    }

    // MÓVIL - Técnico obtiene sus mantenimientos
    @Transactional(readOnly = true)
    public List<BeanMantenimiento> getByTecnico(Long tecnicoId) {
        return mantenimientoRepository.findByTecnico_IdUsuario(tecnicoId);
    }

    @Transactional(readOnly = true)
    public BeanMantenimiento getById(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
    }

    // WEB - Admin asigna técnico al reporte
    @Transactional
    public BeanMantenimiento asignar(AsignarMantenimientoDTO dto) {

        BeanReporte reporte = reporteRepository.findById(dto.getReporteId())
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        BeanUsuario tecnico = usuarioRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado"));

        BeanPrioridad prioridad = prioridadRepository.findById(dto.getPrioridadId())
                .orElseThrow(() -> new RuntimeException("Prioridad no encontrada"));

        BeanMantenimiento mantenimiento = new BeanMantenimiento();
        mantenimiento.setReporte(reporte);
        mantenimiento.setTecnico(tecnico);
        mantenimiento.setTipo(dto.getTipo());
        mantenimiento.setPrioridad(prioridad);
        mantenimiento.setFecha_inicio(LocalDate.now());
        mantenimiento.setEstatus(ENUM_MANTENIMIENTO.PENDIENTE);

        mantenimientoRepository.save(mantenimiento);

        // Cambiar estatus del reporte
        reporte.setEstatus(ENUM_REPORTEDANIO.EN_REVISION);
        // Cambiar estatus del activo a MANTENIMIENTO
        reporte.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.MANTENIMIENTO);
        reporteRepository.save(reporte);

        historialService.registrar(
                reporte.getActivo(),
                "DISPONIBLE",
                "MANTENIMIENTO",
                "Activo enviado a mantenimiento",
                tecnico
        );

        // Notificar al técnico
        if (tecnico.getTokenDispositivo() != null) {
            notificacionService.enviarNotificacion(
                    tecnico.getTokenDispositivo(),
                    "Nuevo mantenimiento asignado",
                    "Se te asignó el mantenimiento del bien: " +
                            reporte.getActivo().getEtiquetaBien()
            );
        }

        return mantenimiento;
    }

    // MÓVIL - Técnico atiende el mantenimiento con evidencia
    @Transactional
    public BeanMantenimiento atender(AtenderMantenimientoDTO dto,
                                     List<MultipartFile> fotos) throws IOException {

        BeanMantenimiento mantenimiento = mantenimientoRepository.findById(dto.getMantenimientoId())
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        mantenimiento.setDiagnostico(dto.getDiagnostico());
        mantenimiento.setAccionesRealizadas(dto.getAccionesRealizadas());
        mantenimiento.setPiezasUtilizadas(dto.getPiezasUtilizadas());
        mantenimiento.setEstatus(ENUM_MANTENIMIENTO.EN_PROCESO);

        // Guardar fotos de evidencia
        if (fotos != null && !fotos.isEmpty()) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            for (MultipartFile foto : fotos) {
                String nombreArchivo = UUID.randomUUID() + "_" + foto.getOriginalFilename();
                Path ruta = Paths.get(UPLOAD_DIR + nombreArchivo);
                Files.write(ruta, foto.getBytes());

                BeanEvidencia evidencia = new BeanEvidencia();
                evidencia.setRuta_archivo(ruta.toString());
                evidencia.setFecha_subida(LocalDateTime.now());
                evidencia.setTipo(ENUM_TIPO_EVIDENCIA.MANTENIMIENTO);
                evidencia.setMantenimiento(mantenimiento);
                evidenciaRepository.save(evidencia);
            }
        }

        return mantenimientoRepository.save(mantenimiento);
    }

    // MÓVIL/WEB - Cerrar mantenimiento
    @Transactional
    public BeanMantenimiento cerrar(CerrarMantenimientoDTO dto) {

        BeanMantenimiento mantenimiento = mantenimientoRepository.findById(dto.getMantenimientoId())
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));

        // Validar que tenga diagnóstico y evidencia
        if (mantenimiento.getDiagnostico() == null || mantenimiento.getDiagnostico().isBlank()) {
            throw new RuntimeException("Debe existir un diagnóstico antes de cerrar");
        }

        boolean tieneEvidencia = evidenciaRepository
                .existsByMantenimiento_Id_mantenimiento(mantenimiento.getId_mantenimiento());

        if (!tieneEvidencia) {
            throw new RuntimeException("Debe existir evidencia antes de cerrar");
        }

        if (dto.getEstatusFinal() != ENUM_MANTENIMIENTO.REPARADO &&
                dto.getEstatusFinal() != ENUM_MANTENIMIENTO.IRREPARABLE) {
            throw new RuntimeException("El estatus final debe ser REPARADO o IRREPARABLE");
        }

        mantenimiento.setEstatus(dto.getEstatusFinal());
        mantenimiento.setFecha_fin(LocalDate.now());

        BeanReporte reporte = mantenimiento.getReporte();

        if (dto.getEstatusFinal() == ENUM_MANTENIMIENTO.REPARADO) {
            // Activo vuelve a disponible
            reporte.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
            reporte.setEstatus(ENUM_REPORTEDANIO.ATENDIDO);
            historialService.registrar(
                    reporte.getActivo(),
                    "MANTENIMIENTO",
                    "DISPONIBLE",
                    "Mantenimiento completado: REPARADO",
                    mantenimiento.getTecnico()
            );

        } else {
            // IRREPARABLE: activo pasa a BAJA y se crea solicitud
            reporte.getActivo().setEstatus(ENUM_ESTATUS_ACTIVO.BAJA);
            reporte.setEstatus(ENUM_REPORTEDANIO.ATENDIDO);

            historialService.registrar(
                    reporte.getActivo(),
                    "MANTENIMIENTO",
                    "BAJA",
                    "Activo irreparable, enviado a baja",
                    mantenimiento.getTecnico()
            );

            BeanSolicitud solicitud = new BeanSolicitud();
            solicitud.setActivo(reporte.getActivo());
            solicitud.setMantenimiento(mantenimiento);
            solicitud.setMotivo("Activo irreparable: " + dto.getObservaciones());
            solicitud.setFecha_solicitud(LocalDate.now());
            solicitud.setEstatus(ENUM_SOLICITUD_BAJA.PENDIENTE);
            solicitudBajaRepository.save(solicitud);
        }

        reporteRepository.save(reporte);
        return mantenimientoRepository.save(mantenimiento);
    }
}
