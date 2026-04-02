# ---- Etapa de build ----
FROM gradle:9.4-jdk17-alpine AS build

WORKDIR /app

# Copia os arquivos de configuração do Gradle primeiro para aproveitar o cache de dependências
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# Copia o restante do código-fonte e compila
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ---- Etapa de runtime ----
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Copia o JAR gerado na etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Ajusta permissões
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]