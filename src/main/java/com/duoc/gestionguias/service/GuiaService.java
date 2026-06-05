package com.duoc.gestionguias.service;

import com.duoc.gestionguias.dto.GuiaRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GuiaService {

    @Value("${app.efs.path}")
    private String efsPath;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    public GuiaService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String crearGuiaTemporal(GuiaRequest request) throws IOException {
        Files.createDirectories(Path.of(efsPath));

        String id = UUID.randomUUID().toString().substring(0, 8);
        String nombreArchivo = "guia-" + id + ".txt";
        Path rutaArchivo = Path.of(efsPath, nombreArchivo);

        String contenido = """
                GUIA DE DESPACHO
                =========================
                ID GUIA: %s
                FECHA CREACION: %s
                TRANSPORTISTA: %s
                CLIENTE: %s
                DIRECCION DESTINO: %s
                PRODUCTO: %s
                CANTIDAD: %d
                USUARIO AUTORIZADO: %s
                FECHA CARPETA S3: %s
                =========================
                Archivo generado temporalmente en EFS.
                """.formatted(
                id,
                LocalDateTime.now(),
                request.getTransportista(),
                request.getCliente(),
                request.getDireccionDestino(),
                request.getProducto(),
                request.getCantidad(),
                request.getUsuarioAutorizado(),
                LocalDate.now()
        );

        Files.writeString(rutaArchivo, contenido);

        return nombreArchivo;
    }

    public String subirGuiaAS3(String nombreArchivo, String transportista) {
        Path rutaArchivo = Path.of(efsPath, nombreArchivo);

        if (!Files.exists(rutaArchivo)) {
            throw new RuntimeException("No existe la guía temporal: " + nombreArchivo);
        }

        String fecha = LocalDate.now().toString();
        String transportistaLimpio = transportista.trim().replace(" ", "_");

        String s3Key = fecha + "/" + transportistaLimpio + "/" + nombreArchivo;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(rutaArchivo));

        return s3Key;
    }

    public String descargarGuiaDesdeS3(String key, String usuario) {
        if (!"lucas".equalsIgnoreCase(usuario)) {
            throw new SecurityException("Usuario sin permisos para descargar esta guía");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        return objectBytes.asUtf8String();
    }

    public String actualizarGuiaEnS3(String key, String nuevoContenido) {
        String contenidoActualizado = """
                GUIA DE DESPACHO ACTUALIZADA
                =========================
                FECHA ACTUALIZACION: %s
                CONTENIDO:
                %s
                =========================
                Archivo actualizado en AWS S3.
                """.formatted(LocalDateTime.now(), nuevoContenido);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(contenidoActualizado));

        return key;
    }

    public String eliminarGuiaDeS3(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return key;
    }

    public List<String> consultarHistorial(String fecha, String transportista) {
        String transportistaLimpio = transportista.trim().replace(" ", "_");
        String prefix = fecha + "/" + transportistaLimpio + "/";

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        return s3Client.listObjectsV2(listRequest)
                .contents()
                .stream()
                .map(S3Object::key)
                .toList();
    }
}