# =================================================================
#  Étape 1 : L'étape de construction (Build Stage)
#  Utilise une image Maven pour construire notre projet
# =================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

# Définit le répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# Copie d'abord le pom.xml pour tirer parti du cache de couches de Docker
COPY pom.xml .

# Télécharge toutes les dépendances du projet
RUN mvn dependency:go-offline

# Copie le reste du code source du projet
COPY src ./src

# Construit l'application, en sautant les tests car nous les exécutons dans le CI
RUN mvn clean package -DskipTests


# =================================================================
#  Étape 2 : L'étape d'exécution (Runtime Stage)
#  Utilise une image Java légère pour exécuter notre application
# =================================================================
FROM eclipse-temurin:21-jre-jammy

# Définit le répertoire de travail
WORKDIR /app

# Copie le fichier .jar exécutable de l'étape de construction vers cette nouvelle étape
COPY --from=build /app/target/maVille-1.0-SNAPSHOT.jar app.jar

# Expose le port sur lequel l'application Javalin s'exécute
# (Ajustez si votre application utilise un autre port)
EXPOSE 7070

# La commande pour lancer l'application lorsque le conteneur démarre
ENTRYPOINT ["java", "-jar", "app.jar"]