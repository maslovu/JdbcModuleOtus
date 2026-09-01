package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dto.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    // Spring Boot автоматически создаст и внедрит этот бин на основе yaml-настроек
    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    // Имя топика, в который будут лететь сообщения
    private static final String TOPIC = "comments-topic";

    public void sendCommentEvent(CommentEvent request) {
        log.info("Отправка события в Kafka для книги ID: {}", request.getBookId());

        // Метод send() работает асинхронно и возвращает CompletableFuture (в Spring Boot 3.x)
        CompletableFuture<SendResult<String, CommentEvent>> future =
                kafkaTemplate.send(TOPIC, String.valueOf(request.getBookId()), request);

        // Навешиваем коллбэки, чтобы логировать успех или ошибку отправки
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Событие успешно отправлено в топик [{}]. Смещение (Offset): {}",
                        TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("Ошибка отправки события в Kafka для топика [{}]", TOPIC, ex);
            }
        });
    }
}
