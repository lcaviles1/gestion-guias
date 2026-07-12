package com.duoc.gestionguias.service;

import com.duoc.gestionguias.dto.GuiaMensaje;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class GuiaPersistenciaService {

    private final JdbcTemplate jdbcTemplate;

    public GuiaPersistenciaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void guardarGuiaProcesada(GuiaMensaje mensaje) {
        String sql = """
                INSERT INTO guias_procesadas (
                    id_mensaje,
                    fecha_encolado,
                    archivo,
                    transportista,
                    cliente,
                    direccion_destino,
                    producto,
                    cantidad,
                    usuario_autorizado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                mensaje.getIdMensaje(),
                mensaje.getFechaEncolado(),
                mensaje.getArchivo(),
                mensaje.getTransportista(),
                mensaje.getCliente(),
                mensaje.getDireccionDestino(),
                mensaje.getProducto(),
                mensaje.getCantidad(),
                mensaje.getUsuarioAutorizado()
        );
    }

    public Integer contarGuiasProcesadas() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM guias_procesadas",
                Integer.class
        );
    }
}
