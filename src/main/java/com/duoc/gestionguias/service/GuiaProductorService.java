package com.duoc.gestionguias.service;

import com.duoc.gestionguias.config.RabbitMQConfig;
import com.duoc.gestionguias.dto.GuiaMensaje;
import com.duoc.gestionguias.dto.GuiaRequest;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class GuiaProductorService {

    private final RabbitTemplate rabbitTemplate;

    public GuiaProductorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public GuiaMensaje encolarGuia(
            GuiaRequest request,
            String nombreArchivo
    ) {
        GuiaMensaje mensaje = new GuiaMensaje(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                nombreArchivo,
                request.getTransportista(),
                request.getCliente(),
                request.getDireccionDestino(),
                request.getProducto(),
                request.getCantidad(),
                request.getUsuarioAutorizado()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_GUIAS,
                RabbitMQConfig.ROUTING_KEY_GUIAS,
                mensaje,
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    message.getMessageProperties()
                            .setMessageId(mensaje.getIdMensaje());

                    return message;
                }
        );

        return mensaje;
    }
}
