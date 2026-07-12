package com.duoc.gestionguias.service;

import com.duoc.gestionguias.config.RabbitMQConfig;
import com.duoc.gestionguias.dto.GuiaErrorMensaje;
import com.duoc.gestionguias.dto.GuiaMensaje;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GuiaConsumidorService {

    private final RabbitTemplate rabbitTemplate;
    private final GuiaPersistenciaService guiaPersistenciaService;

    public GuiaConsumidorService(
            RabbitTemplate rabbitTemplate,
            GuiaPersistenciaService guiaPersistenciaService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.guiaPersistenciaService = guiaPersistenciaService;
    }

    public GuiaMensaje consumirYGuardar() {
        Object recibido = rabbitTemplate.receiveAndConvert(
                RabbitMQConfig.COLA_GUIAS,
                3000
        );

        if (recibido == null) {
            return null;
        }

        GuiaMensaje mensaje = (GuiaMensaje) recibido;

        try {
            guiaPersistenciaService.guardarGuiaProcesada(mensaje);
            return mensaje;
        } catch (RuntimeException e) {
            GuiaErrorMensaje mensajeError = new GuiaErrorMensaje(
                    LocalDateTime.now().toString(),
                    e.getMessage(),
                    mensaje
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_ERRORES,
                    RabbitMQConfig.ROUTING_KEY_ERRORES,
                    mensajeError
            );

            throw new RuntimeException(
                    "La guía no pudo guardarse y fue enviada a la cola de errores",
                    e
            );
        }
    }

    public Integer contarProcesadas() {
        return guiaPersistenciaService.contarGuiasProcesadas();
    }
}
