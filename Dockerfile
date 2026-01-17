FROM gradle:8.7-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew \
 && ./gradlew --no-daemon clean bootJar \
 && cp "$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" app.jar

FROM eclipse-temurin:17-jre
RUN apt-get update \
 && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-kor \
    tesseract-ocr-eng \
 && rm -rf /var/lib/apt/lists/*
COPY --from=builder /workspace/app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
