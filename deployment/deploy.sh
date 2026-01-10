#!/bin/bash
set -e  # 명령어 실패시 스크립트 즉시 종료

DEFAULT_CONF="/etc/nginx/conf.d/service-url.inc"
MAX_RETRY=10
SLEEP_SEC=10

# 1. 현재 떠있는 컨테이너 확인
IS_BLUE=$(docker ps --format '{{.Names}}' | grep -w kareer-blue || true)

if [ -z "$IS_BLUE" ]; then
  TARGET_CONTAINER="kareer-green"
  TARGET_PORT=8081
  STOP_CONTAINER="kareer-blue"
else
  TARGET_CONTAINER="kareer-blue"
  TARGET_PORT=8080
  STOP_CONTAINER="kareer-green"
fi

echo "### 현재 실행 중 컨테이너: $STOP_CONTAINER"
echo "### 새로 배포될 컨테이너: $TARGET_CONTAINER (port: $TARGET_PORT)"

# 2. ECR 로그인
echo "### 🔑 ECR 로그인"
aws ecr get-login-password --region ap-northeast-2 \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY"

# 3. 이미지 Pull 및 실행
echo "### ☁️ 1. 이미지 Pull"
docker compose pull $TARGET_CONTAINER

echo "### 📦 2. 컨테이너 실행"
docker compose up -d $TARGET_CONTAINER

# 3. 헬스 체크
echo "### 3. 🔆 헬스 체크"
HEALTH_CHECK_PASSED=false

for ((i=1; i<=MAX_RETRY; i++)); do
  HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" \
    http://127.0.0.1:$TARGET_PORT/actuator/health || true)

  if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "### ✅ 헬스 체크 성공"
    HEALTH_CHECK_PASSED=true
    break
  else
    echo "### ⏰ 헬스 체크 대기중 ($i/$MAX_RETRY)"
  fi

  sleep "$SLEEP_SEC"
done

if [ "$HEALTH_CHECK_PASSED" = false ]; then
  echo "### ❌ 헬스 체크 실패 -> 배포 중단"
  docker compose stop $TARGET_CONTAINER
  docker compose rm -f $TARGET_CONTAINER
  exit 1
fi

# 4. Nginx 트래픽 스위치
echo "### 🔜 4. Nginx 포트 스위치"

sudo tee "$DEFAULT_CONF" > /dev/null <<EOF
# managed by deploy.sh (이 파일은 자동 생성되며, 사람이 수동 수정하면 안 됨)
set \$service_url http://127.0.0.1:$TARGET_PORT;
EOF

if ! sudo nginx -s reload; then
  echo "### ❌ Nginx reload 실패"
  echo "### 🔄 새 컨테이너 중지 및 롤백"
  docker compose stop $TARGET_CONTAINER || true
  docker compose rm -f $TARGET_CONTAINER || true
  exit 1
fi

# 5. 기존 컨테이너 종료
echo "### 🌀 5. 기존 컨테이너 종료"
docker compose stop $STOP_CONTAINER || true
docker compose rm -f $STOP_CONTAINER || true
docker image prune -f --filter "dangling=true"

echo "### 🎉 배포 성공"
