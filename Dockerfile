# Usa imagem leve do Java 21
FROM eclipse-temurin:21-jdk

# Define diretório de trabalho
WORKDIR /app

# Copia o JAR gerado para dentro do container
COPY target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
