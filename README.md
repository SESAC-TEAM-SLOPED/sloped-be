
### Getting Started - Local

1-1. AMD64 아키텍처 이미지 빌드 
```
docker build --platform linux/amd64 -t postgis-sloped .
```

1-2. ARM64 아키텍처 이미지 빌드 (Mac M1)
```
docker build --platform linux/arm64 -t postgis-sloped .
```

2. Docker-compose 명령어
```
docker-compose -f docker-compose.local.yml --env-file .env.local up -d --build
```
