# === ЭТАП 1: Сборка приложения ===
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем абсолютно ВСЕ файлы проекта (включая корневой pom, все подмодули и их src)
COPY . .

# Собираем весь проект, пропуская тесты.
# Ограничиваем использование памяти для Docker-демона флагом -B (Batch mode)
RUN mvn clean package -DskipTests -B

# === ЭТАП 2: Запуск приложения ===
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Копируем готовый JAR-файл из папки target ЗАПУСКАЕМОГО подмодуля.
# ВНИМАНИЕ: Замените "books-maslov" на точное имя папки вашего подмодуля, если оно отличается,
# и проверьте версию JAR-файла (2.1.0-SNAPSHOT или другую).
COPY --from=builder /app/books-maslov/target/books-maslov-*.jar app.jar

# Открываем порт (тот, что прописан у вас в application.yml, обычно 8080)
EXPOSE 8080

# Команда для запуска JAR-файла
ENTRYPOINT ["java", "-jar", "app.jar"]