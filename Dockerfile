FROM eclipse-temurin:17-jre

# Tesseract + 언어 데이터 설치
RUN apt-get update \
 && apt-get install -y \
    tesseract-ocr \
    tesseract-ocr-kor \
    tesseract-ocr-eng \
 && rm -rf /var/lib/apt/lists/*

# 애플리케이션
COPY app.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
