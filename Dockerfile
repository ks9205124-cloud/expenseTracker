# Stage 1: Build the Vite Frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend

# Copy package files first to leverage Docker cache for dependencies
COPY frontend/package*.json ./
RUN npm install

# Copy the rest of the frontend source code and build it
COPY frontend/ .
RUN npm run build

# Stage 2: Build the Spring Boot Backend and embed the frontend
FROM eclipse-temurin:17-jdk-alpine AS backend-build
WORKDIR /app

# Copy the entire backend project
COPY . .

# Copy the Vite build output (dist folder) directly into Spring Boot's static directory
COPY --from=frontend-build /app/frontend/dist /app/src/main/resources/static

# Grant execution permissions to Maven wrapper and build the jar
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Stage 3: Run the final unified application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the backend build stage
COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]