package com.example.integradora5d.service.activo;

import com.example.integradora5d.models.activo.ActivoRepository;
import com.example.integradora5d.models.activo.BeanActivo;
import com.example.integradora5d.models.activo.ENUM_ESTATUS_ACTIVO;
import com.example.integradora5d.models.aula_laboratorio.AulaRepository;
import com.example.integradora5d.models.aula_laboratorio.BeanAula;
import com.example.integradora5d.models.campus.BeanCampus;
import com.example.integradora5d.models.campus.CampusRepository;
import com.example.integradora5d.models.edificio.BeanEdificio;
import com.example.integradora5d.models.edificio.EdificioRepository;
import com.example.integradora5d.models.marca.BeanMarca;
import com.example.integradora5d.models.marca.MarcaRepository;
import com.example.integradora5d.models.modelo.BeanModelo;
import com.example.integradora5d.models.modelo.ModeloRepository;
import com.example.integradora5d.models.producto.BeanProducto;
import com.example.integradora5d.models.producto.ENUM_ESTATUS_PRODUCTO;
import com.example.integradora5d.models.producto.ProductoRepository;
import com.example.integradora5d.dto.activo.ImportResultDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportacionService {

    private final ActivoRepository activoRepository;
    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
    private final CampusRepository campusRepository;
    private final EdificioRepository edificioRepository;
    private final AulaRepository aulaRepository;
    private final ActivoService activoService;

    public ImportacionService(ActivoRepository activoRepository,
                              ProductoRepository productoRepository,
                              MarcaRepository marcaRepository,
                              ModeloRepository modeloRepository,
                              CampusRepository campusRepository,
                              EdificioRepository edificioRepository,
                              AulaRepository aulaRepository,
                              ActivoService activoService) {
        this.activoRepository = activoRepository;
        this.productoRepository = productoRepository;
        this.marcaRepository = marcaRepository;
        this.modeloRepository = modeloRepository;
        this.campusRepository = campusRepository;
        this.edificioRepository = edificioRepository;
        this.aulaRepository = aulaRepository;
        this.activoService = activoService;
    }

    @Transactional
    public ImportResultDTO importarExcel(MultipartFile file) {
        int insertados = 0;
        int rechazados = 0;
        List<String> errores = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Saltar encabezado (fila 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String numeroSerie = getCellValue(row, 0);
                    String tipoNombre  = getCellValue(row, 1);
                    String marcaNombre = getCellValue(row, 2);
                    String modeloNombre = getCellValue(row, 3);
                    String campusNombre = getCellValue(row, 4);
                    String edificioNombre = getCellValue(row, 5);
                    String aulaNombre = getCellValue(row, 6);

                    // Validar número de serie duplicado
                    if (activoRepository.existsByNumeroSerie(numeroSerie)) {
                        errores.add("Fila " + (i + 1) + ": número de serie duplicado - " + numeroSerie);
                        rechazados++;
                        continue;
                    }

                    // Resolver o crear marca
                    BeanMarca marca = marcaRepository.findByNombre(marcaNombre)
                            .orElseGet(() -> {
                                BeanMarca nueva = new BeanMarca();
                                nueva.setNombre(marcaNombre);
                                return marcaRepository.save(nueva);
                            });

                    // Resolver o crear modelo
                    BeanModelo modelo = modeloRepository
                            .findByNombreAndMarca_Id_marca(modeloNombre, marca.getId_marca())
                            .orElseGet(() -> {
                                BeanModelo nuevo = new BeanModelo();
                                nuevo.setNombre(modeloNombre);
                                nuevo.setMarca(marca);
                                return modeloRepository.save(nuevo);
                            });

                    // Resolver o crear producto
                    BeanProducto producto = productoRepository.findByNombre(tipoNombre)
                            .orElseGet(() -> {
                                BeanProducto nuevo = new BeanProducto();
                                nuevo.setNombre(tipoNombre);
                                nuevo.setModelo(modelo);
                                nuevo.setEstatus(ENUM_ESTATUS_PRODUCTO.ACTIVO);
                                return productoRepository.save(nuevo);
                            });

                    // Resolver o crear campus
                    BeanCampus campus = campusRepository.findByNombre(campusNombre)
                            .orElseGet(() -> {
                                BeanCampus nuevo = new BeanCampus();
                                nuevo.setNombre(campusNombre);
                                return campusRepository.save(nuevo);
                            });

                    // Resolver o crear edificio
                    BeanEdificio edificio = edificioRepository
                            .findByNombreAndCampus_IdCampus(edificioNombre, campus.getIdCampus())
                            .orElseGet(() -> {
                                BeanEdificio nuevo = new BeanEdificio();
                                nuevo.setNombre(edificioNombre);
                                nuevo.setCampus(campus);
                                return edificioRepository.save(nuevo);
                            });

                    // Resolver o crear aula
                    BeanAula aula = aulaRepository
                            .findByNombreAndEdificio_IdEdificio(aulaNombre, edificio.getIdEdificio())
                            .orElseGet(() -> {
                                BeanAula nueva = new BeanAula();
                                nueva.setNombre(aulaNombre);
                                nueva.setEdificio(edificio);
                                return aulaRepository.save(nueva);
                            });

                    // Crear activo
                    BeanActivo activo = new BeanActivo();
                    activo.setNumeroSerie(numeroSerie);
                    activo.setProducto(producto);
                    activo.setAula(aula);
                    activo.setFechaAlta(LocalDate.now());
                    activo.setEstatus(ENUM_ESTATUS_ACTIVO.DISPONIBLE);
                    activo.setEtiquetaBien(activoService.generarEtiquetaPublica(producto, aula));

                    activoRepository.save(activo);
                    insertados++;

                } catch (Exception e) {
                    errores.add("Fila " + (i + 1) + ": " + e.getMessage());
                    rechazados++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage());
        }

        return new ImportResultDTO(insertados, rechazados, errores);
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}
