# Utiliser l'image de base OpenJDK 17 avec Alpine Linux
FROM openjdk:17-jdk-alpine

# Argument pour le fichier JAR
ARG JAR_FILE=target/jourferie-service-0.0.1-SNAPSHOT.jar

# Copier le fichier JAR dans l'image
COPY ${JAR_FILE} app.jar

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]
