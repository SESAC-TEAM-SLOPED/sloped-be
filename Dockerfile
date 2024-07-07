# 기본 이미지로 PostgreSQL 16을 사용
FROM postgres:16

# 필요한 패키지 설치
RUN apt-get update && \
    apt-get install -y postgis postgresql-16-postgis-3

# 기본 설정 및 확장 설치
RUN mkdir -p /docker-entrypoint-initdb.d
COPY ./init-db.sh /docker-entrypoint-initdb.d/

# 스크립트 파일 권한 설정
RUN chmod +x /docker-entrypoint-initdb.d/init-db.sh