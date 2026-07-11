package com.duoc.gestionguias.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String COLA_GUIAS = "guias.procesamiento";
    public static final String COLA_ERRORES = "guias.errores";

    public static final String EXCHANGE_GUIAS = "guias.exchange";
    public static final String EXCHANGE_ERRORES = "guias.errores.exchange";

    public static final String ROUTING_KEY_GUIAS = "guias.procesar";
    public static final String ROUTING_KEY_ERRORES = "guias.error";

    @Bean
    public DirectExchange exchangeGuias() {
        return new DirectExchange(EXCHANGE_GUIAS, true, false);
    }

    @Bean
    public DirectExchange exchangeErrores() {
        return new DirectExchange(EXCHANGE_ERRORES, true, false);
    }

    @Bean
    public Queue colaGuias() {
        return QueueBuilder.durable(COLA_GUIAS)
                .deadLetterExchange(EXCHANGE_ERRORES)
                .deadLetterRoutingKey(ROUTING_KEY_ERRORES)
                .build();
    }

    @Bean
    public Queue colaErrores() {
        return QueueBuilder.durable(COLA_ERRORES).build();
    }

    @Bean
    public Binding bindingGuias(
            Queue colaGuias,
            DirectExchange exchangeGuias
    ) {
        return BindingBuilder
                .bind(colaGuias)
                .to(exchangeGuias)
                .with(ROUTING_KEY_GUIAS);
    }

    @Bean
    public Binding bindingErrores(
            Queue colaErrores,
            DirectExchange exchangeErrores
    ) {
        return BindingBuilder
                .bind(colaErrores)
                .to(exchangeErrores)
                .with(ROUTING_KEY_ERRORES);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public ApplicationRunner declararInfraestructuraRabbit(
            RabbitAdmin rabbitAdmin
    ) {
        return args -> rabbitAdmin.initialize();
    }
}
