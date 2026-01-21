FROM eclipse-temurin:21-jre-jammy AS artifacts
WORKDIR /workspace
COPY build/libs ./libs
RUN cp "$(find libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)" app.jar

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
 && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-kor \
    tesseract-ocr-eng \
 && rm -rf /var/lib/apt/lists/*
COPY --from=artifacts /workspace/app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
