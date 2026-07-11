package com.duoc.gestionguias.controller;

import com.duoc.gestionguias.dto.ActualizarGuiaRequest;
import com.duoc.gestionguias.dto.GuiaMensaje;
import com.duoc.gestionguias.dto.GuiaRequest;
import com.duoc.gestionguias.service.GuiaProductorService;
import com.duoc.gestionguias.service.GuiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaService guiaService;
    private final GuiaProductorService guiaProductorService;

    public GuiaController(
            GuiaService guiaService,
            GuiaProductorService guiaProductorService
    ) {
        this.guiaService = guiaService;
        this.guiaProductorService = guiaProductorService;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearGuia(@RequestBody GuiaRequest request) {
        try {
            String nombreArchivo = guiaService.crearGuiaTemporal(request);
            GuiaMensaje mensajeEncolado =
                    guiaProductorService.encolarGuia(request, nombreArchivo);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Guía generada en EFS y enviada a RabbitMQ",
                    "archivo", nombreArchivo,
                    "idMensaje", mensajeEncolado.getIdMensaje(),
                    "cola", "guias.procesamiento"
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "No se pudo generar la guía",
                    "detalle", e.getMessage()
            ));
        }
    }

    @PostMapping("/subir")
    public ResponseEntity<?> subirGuia(
            @RequestParam String archivo,
            @RequestParam String transportista
    ) {
        try {
            String s3Key = guiaService.subirGuiaAS3(archivo, transportista);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Guía subida correctamente a S3",
                    "s3Key", s3Key
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo subir la guía a S3",
                    "detalle", e.getMessage()
            ));
        }
    }

    @GetMapping("/descargar")
public ResponseEntity<?> descargarGuia(@RequestParam String key) {
    try {
        String contenido = guiaService.descargarGuiaDesdeS3(key);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Guía descargada correctamente desde S3",
                "contenido", contenido
        ));
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "No se pudo descargar la guía",
                "detalle", e.getMessage()
        ));
    }
}

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarGuia(@RequestBody ActualizarGuiaRequest request) {
        try {
            String keyActualizada = guiaService.actualizarGuiaEnS3(
                    request.getKey(),
                    request.getNuevoContenido()
            );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Guía actualizada correctamente en S3",
                    "s3Key", keyActualizada
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo actualizar la guía",
                    "detalle", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarGuia(@RequestParam String key) {
        try {
            String keyEliminada = guiaService.eliminarGuiaDeS3(key);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Guía eliminada correctamente de S3",
                    "s3Key", keyEliminada
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo eliminar la guía",
                    "detalle", e.getMessage()
            ));
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> consultarHistorial(
            @RequestParam String fecha,
            @RequestParam String transportista
    ) {
        try {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Historial consultado correctamente",
                    "resultados", guiaService.consultarHistorial(fecha, transportista)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo consultar el historial",
                    "detalle", e.getMessage()
            ));
        }
    }
}