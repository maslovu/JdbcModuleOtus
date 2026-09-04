# Используем легковесный образ JRE только для запуска приложения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Копируем готовый JAR-файл, который вы только что успешно собрали на компьютере
COPY books-maslov/target/books-maslov-*.jar app.jar

# Открываем порт микросервиса
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]