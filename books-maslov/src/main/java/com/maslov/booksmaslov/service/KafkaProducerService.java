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

    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    private static final String TOPIC = "comments-topic";

    public void sendCommentEvent(CommentEvent request) {
        log.info("Sending event to Kafka for book ID: {}", request.bookId());

        // Метод send() работает асинхронно
        CompletableFuture<SendResult<String, CommentEvent>> future =
                kafkaTemplate.send(TOPIC, String.valueOf(request.bookId()), request);

        // Навешиваем коллбэки, чтобы логировать успех или ошибку отправки
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event successfully sent to topic [{}]. Offset: {}",
                        TOPIC, result.getRecordMetadata().offset());
            } else {
                log.error("Error sending event to Kafka for topic [{}]", TOPIC, ex);
            }
        });
    }
}
