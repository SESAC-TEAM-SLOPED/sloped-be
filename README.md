
## 함께가길💙 - 교통약자 대상 장애 친화 공간 리뷰 서비스
<div align="center">
<img src="https://github.com/user-attachments/assets/5b87496b-d607-4403-96a0-3c6b41576f83" width="100%"></img>
</div>


> ## ⚑ 목차
> 1. [문서화 링크](#문서화-링크)
> 2. [팀원 소개](#팀원-소개)
> 3. [프로젝트 기획 의도](#프로젝트-기획-의도)
> 4. [기존 서비스와의 차별점](#기존-서비스와의-차별점)
> 5. [기능 요구사항](#기능-요구사항)
> 6. [유저 시나리오](#유저-시나리오)
> 7. [기술 아키텍처](#기술-아키텍처)
> 8. [핵심 기술](#핵심-기술)
> 9. [ERD](#ERD)
> 10. [API 명세](#API-명세)
> 11. [트러블 슈팅](#트러블-슈팅)
> 12. [협업 및 커뮤니케이션](#협업-및-커뮤니케이션)
> 13. [로컬 실행 방법 (.env.example)](#실행-방법)

<br/>

## 문서화 링크
1. [프로젝트 최종 발표 자료(시연영상 포함)](https://docs.google.com/presentation/d/14T0QtWKZBkjFt-nX-zfKB_DEmuK7uNXYibY4Nzpn8ZM/edit?usp=sharing)
<details>
  <summary> <b>2. 웹서비스 시나리오</b>  </summary>
  
  - [웹서비스 시나리오](https://docs.google.com/document/d/1Y0vVhyDDkdu6VSOUiUGBsDKCOtl517b1bl9M0PJDX-c/edit?usp=sharing)
<img src="https://github.com/user-attachments/assets/b7d36788-b939-484c-ad08-99e5557ebf42">
</details>
<details>
<summary><b>3. 요구사항 정의서</b></summary>
  
  - [요구사항 정의서](https://docs.google.com/spreadsheets/d/1cn4EJACK-sohvccxQBbIdtD8Avkok8BlrEVkyOJ38Qc/edit?usp=sharing)
  <img src="https://github.com/user-attachments/assets/a1ad2d85-ec33-4302-97e2-5fabc608848e" width="80%">
</details>
<details>
<summary><b>4. 함께가길 UI</b></summary>
  
  - [Figma 링크](https://www.figma.com/design/KbJdqPT08pLrIRZ2hKQSVE/%EA%B2%BD%EC%82%AC%EB%82%AC%EB%84%A4-UI?node-id=0-1&t=rsWkjY4klQHroDdx-1)
  ![image](https://github.com/user-attachments/assets/caf18215-1af8-4ab1-80e7-dbd0c3621758)
</details>

<br/>
<br/>


## 팀원 소개

<img src="https://github.com/user-attachments/assets/8428943e-a511-4d28-b7bc-8844dd8ba3d6" width="80%">

| 팀원 | 역할 | 팀원 | 역할|
| --- | --- | --- | --- |
| [정지영](https://github.com/Younngg) | - 즐겨찾기 관련 API 개발<br> - 통행 불편 제보&시설 리뷰의 이미지 캡셔닝 <br> - 지도 API 연결 및 마커 기능 구현 <br> - 프론트 전반 담당 | [배서진](https://github.com/bsjin1122) | - 도로 관련 API, 프론트 개발 <br> - 전국이동지원센터&콜택시 데이터 정제 <br> - PostGIS 설정 <br> - Notion 문서화, README, PPT 작성|
| [서창호](https://github.com/ChangHoSe) | - 회원 관련 API, 프론트 개발 <br> - JWT Config 작성 <br> - 소셜 로그인 구현(Google, Naver, Kakao) <br> - 시설 리뷰 API 개발 <br> - 프로젝트 발표 | [이서연](https://github.com/sylee6529) | - Docker 설정/배포 <br> - 시설 관련 API, 프론트 개발 <br> - Jira & Github Issue 연동 <br> - 시설 리뷰에 RAG 활용한 장소 추천 기능 구현 |

<br>

## 프로젝트 기획 의도

<img src="https://github.com/user-attachments/assets/a233c68d-a71e-4967-8fee-7a51e34669d3" width="80%">


## 기존 서비스와의 차별점

<img src="https://github.com/user-attachments/assets/11c0ed64-5d78-4ef3-a058-c02d52ef9d60" width="80%">

## 기능 요구사항

<img src="https://github.com/user-attachments/assets/4d64b07f-291c-4436-87e1-cebe37368bda" width="80%">


## 유저 시나리오
<img src="https://github.com/user-attachments/assets/1f17135b-8657-4643-9cf2-670b5341b03d" width="80%">


## 기술 아키텍처
<img src="https://github.com/user-attachments/assets/e558e72a-ed53-4d8b-89ae-521a3bff797a" width="80%">

## 핵심 기술 
<img src="https://github.com/user-attachments/assets/b3c510ad-b0e4-4baa-9616-94641296ff48" width="80%">


### 프로젝트 환경
| Stack                                                                                                        | Version           |
|:------------------------------------------------------------------------------------------------------------:|:-----------------:|
| ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) | Spring Boot 3.3.x |
| ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)    | Gradle 8.7       |
| ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)    | JDK 17           |
| ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)       | PostgreSQL 16.3     |
| ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)    | Redis 6.0        |


## ERD
<details>
  <summary><b>ERD</b></summary>

  ![image](https://github.com/user-attachments/assets/45ccf1e9-a5bf-492d-805b-ed5f188410de)

  ![image](https://github.com/user-attachments/assets/36f86d66-b6ea-481b-8d95-a1cda6422aed)

</details>

<br/>

## API 명세 
<details><summary><b>추후 Postman/마크다운 반영 예정입니다.</b></summary>

![image](https://github.com/user-attachments/assets/da76f790-183f-4523-99d3-50458ab8997d)
![image](https://github.com/user-attachments/assets/a8e914d5-f7ac-44ff-b621-8949fb51e315)

</details>

<br>

## 트러블 슈팅
<details>
  <summary><b>💥 1. 소셜 로그인 설정(OAuth2Login), Spring Security 충돌</b></summary>

![image](https://github.com/user-attachments/assets/33bfccdc-dbb1-4733-8713-a8e3e79c3019)
![image](https://github.com/user-attachments/assets/dbf7fed5-f946-466b-9863-ff60dabc796d)

</details>

<details>
  <summary><b>💦 2. Image Captioning - 이미지 분석 속도 이슈 </b></summary>

![image](https://github.com/user-attachments/assets/5a2c5597-1e41-4cb4-aafe-c27091756e9b)

</details>

<details>
  <summary><b>🚗 3. 도로 - 민원기관 및 장애인콜택시 데이터 정제</b></summary>

![image](https://github.com/user-attachments/assets/f0d5ce2b-7033-4d2e-8e57-e6aeab4e97a5)


</details>

<details><summary><b>💫 4. 시설 - 내 근처의 교통약자 친화 장소 추천(RAG)</b></summary>

![image](https://github.com/user-attachments/assets/390e397c-8edb-4c60-a31f-e7e44a9c41f8)
![image](https://github.com/user-attachments/assets/35b0cbff-3805-4c63-a85b-9072f9216f8f)

</details>


<br>

## 협업 및 커뮤니케이션
<details><summary><b>🏎 Jira & Github 이슈 트래킹</b></summary>

![image](https://github.com/user-attachments/assets/3d0a65ec-3af6-4e21-b70e-5df3503b09f0)

![image](https://github.com/user-attachments/assets/f6f63f3e-60bf-4077-abbd-bdd673f3bfc7)

![image](https://github.com/user-attachments/assets/7b1c5098-e972-4cb8-b01e-9995b894b8f8)


</details>

<details><summary><b>📝 notion으로 회의록 & 모르는 것 공유하기<b></summary>

  ![image](https://github.com/user-attachments/assets/51fa7f60-0301-41cd-bf6e-9d32fa6730d1)
 
  ![image](https://github.com/user-attachments/assets/e11f39ff-a455-4135-a23a-1a31ae4147e2)


</details>

<br>

## 실행 방법

<details><summary><b> env.local(example)</b></summary>


![image](https://github.com/user-attachments/assets/bc6e0b3b-2935-4f23-9583-bbe8ffbbef35)


</details>

### Getting Started - Local
```
docker-compose -f docker-compose.local.yml --env-file .env.local up -d --build
```
