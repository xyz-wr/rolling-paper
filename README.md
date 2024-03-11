<h2> [ Rolling Paper ] 롤링페이퍼 작성 서비스 </h2>

<p align="middle">  Spring Boot를 사용하여 웹 개발을 학습하기 위해 진행한 개인 프로젝트입니다.
  <br/> 전체 공개는 로그인한 모든 유저가 해당 롤링페이퍼에 메시지를 작성할 수 있고, 
  <br/> 친구 공개는 롤링페이퍼 작성자와 초대 요청을 수락한 유저들만 메시지를 작성할 수 있습니다. 
</p>
</div>

</br>
</br>

## 📃 프로젝트 개요
- `프로젝트명` : Rolling Paper
- `프로젝트 기간` : 2024.01 ~ 2024.03
- `배포 링크` : [rolling-paper](http://ec2-43-200-251-136.ap-northeast-2.compute.amazonaws.com:8080/)
- `프로젝트 문서` : [Rolling Paper 서비스 소개](https://www.notion.so/Rolling-Paper-54274111cf854ddbb9b7ff92d2f35387)  

</br>
</br>

## ⚒️ 사용 기술 및 프로젝트 아키텍처
### 사용 기술
| 언어 | 내용 |
| --- | --- |
| 언어 | Java 17 |
| 프레임워크 및 라이브러리 | Spring Boot 3.0.2, Spring Security, Spring Data Jpa, JUnit |
| 보안 및 인증 관련 | JWT, OAUTH 2.0 |
| 데이터베이스 | MariaDB, Redis |
| 인프라 및 배포 관련 | AWS EC2, Docker |
| 개발 도구 및 기타 서비스 | GitHub Actions, Thymeleaf, Git |
</br>

### 프로젝트 아키텍처
#### 1) 서비스 구조
<div align=center>
<img width="720" alt="Architecture_Diagram" src="https://github.com/xyz-wr/rolling-paper/assets/63355903/2eb1aa0e-31fc-4227-880a-ff721754fbf5">
</div>
</br>

#### 2) 인증 서버 구조(JWT)
<div align=center>
<img width="720" alt="Architecture_Diagram" src="https://github.com/xyz-wr/rolling-paper/assets/63355903/c0d5ae19-4499-4ec8-9bbd-531b784b515c">
</div>
</br>

#### 3) 인증 서버 구조(OAuth2.0)
<div align=center>
<img width="720" alt="Architecture_Diagram" src="https://github.com/xyz-wr/rolling-paper/assets/63355903/6acb7a26-042c-4ee0-8c6c-1e717a8770c9">
</div>
</br>

## 📦 ERD 
`dbdiagram` : [dbdiagram](https://dbdiagram.io/d/643ba6606b31947051aa49eb)
<div align=center>
<img width="720" alt="Architecture_Diagram" src="https://github.com/xyz-wr/rolling-paper/assets/63355903/0213c880-81b4-40cc-b7e6-89087b5f051f">
</div>
</br>

## ✅ 주요 기능 및 사용 방법
### 유저
<div align=center>
  
|![회원가입 및 로그인](https://github.com/xyz-wr/rolling-paper/assets/63355903/c735afb9-bec4-4254-aa18-621aa14ba935) |![OAuth 2 0](https://github.com/xyz-wr/rolling-paper/assets/63355903/b0344f31-0594-4655-8708-3c2f41478d25)|![프로필 수정](https://github.com/xyz-wr/rolling-paper/assets/63355903/746684d4-04a0-4d7d-9520-ab7d06822c7d) |![유저 정보 리스트](https://github.com/xyz-wr/rolling-paper/assets/63355903/7ff3a637-ba37-4704-ad5f-ce8200b0d0f6)|
|:---:|:---:|:---:|:---:|
| 회원가입 및 로그인 | OAuth 2.0 로그인 | 프로필 수정 | 유저 정보 리스트 |
</div>
</br>

### 롤링페이퍼
<div align=center>
  
|![롤링페이퍼 작성](https://github.com/xyz-wr/rolling-paper/assets/63355903/c7c7635d-85ef-4dca-826b-a79158e3a7c8) |![롤링페이퍼 수정 및 삭제](https://github.com/xyz-wr/rolling-paper/assets/63355903/53a2171c-ec7d-4ad6-9a42-3058f9663149)|![롤링페이퍼 검색](https://github.com/xyz-wr/rolling-paper/assets/63355903/fe2c2819-9cc8-4381-929d-c5985c9e4938) |![롤링페이퍼 탈퇴](https://github.com/xyz-wr/rolling-paper/assets/63355903/c2f00e0c-f884-4694-88a3-6cbd39aa2f2e)|![페이지네이션](https://github.com/xyz-wr/rolling-paper/assets/63355903/5b79d9df-82bd-4ed4-9a9d-3acb5049ab2a)|![이용권 구매](https://github.com/xyz-wr/rolling-paper/assets/63355903/74028a45-4661-4536-824f-c2a1cc01a0dd)|
|:---:|:---:|:---:|:---:|:---:|:---:|
| 롤링페이퍼 작성 | 롤링페이퍼 수정 및 삭제 | 롤링페이퍼 검색 | 롤링페이퍼 탈퇴 | 페이지네이션 | 이용권 구매|
</div>
</br>

### 롤링페이퍼 메시지
<div align=center>
  
|![메시지 작성](https://github.com/xyz-wr/rolling-paper/assets/63355903/80fed528-d9a1-4ed1-8eb8-c7f503fa0a77) |![메시지 수정 및 삭제](https://github.com/xyz-wr/rolling-paper/assets/63355903/c5803bda-6fe4-4e4f-a4b3-92e8e9d66e8d)|
|:---:|:---:|
| 롤링페이퍼 메시지 작성 | 롤링페이퍼 메시지 수정 및 삭제 | 
</div>
</br>

### 초대장
<div align=center>
  
|![초대장 발송](https://github.com/xyz-wr/rolling-paper/assets/63355903/14418764-81f5-4b7b-8f84-fd04a1d953f3) |![초대 수락](https://github.com/xyz-wr/rolling-paper/assets/63355903/d21bf6d0-f211-4348-aafd-3223b8ef2aa4)| ![초대 수락 유저 리스트 및 탈퇴](https://github.com/xyz-wr/rolling-paper/assets/63355903/032a3583-6951-4fb6-bb34-e89bff731978)|
|:---:|:---:|:---:|
| 초대장 발송 | 초대 수락 | 초대 수락 유저 리스트 및 탈퇴 |
</div>
</br>

### 좋아요
<div align=center>
  
|![좋아요](https://github.com/xyz-wr/rolling-paper/assets/63355903/487db44f-b488-4dd3-a82f-0db20840648c)|
|:---:|
</div>
</br>

## API 설계
### 1) RESTful API
[RESTful API](https://docs.google.com/spreadsheets/d/1rr44hdJHzL1d2XegidktHY1Tk-6BB-2xd7IgAdPji-8/edit#gid=0)

### 2) MVC
[MVC](https://docs.google.com/spreadsheets/d/1tPKW0C0OMZWPcnCPozfFfCAAVe35cQb18xEZXdAlEls/edit#gid=992728778)

## 배포 방식
EC2에서 Docker를 활용하여 배포를 진행하였습니다. </br>

[AWS EC2, Docker Compose를 이용한 프로젝트 배포](https://www.notion.so/AWS-EC2-Docker-Compose-258e22a1a5a3440f98793943ddd3c52c)
</br>

## CI/CD
Github Actions를 사용하여 CI/CD를 구축하였습니다. </br>
Github Actions를 사용하여 Docker Image를 빌드하고 EC2에 반영합니다. </br>

[GitHub Actions CI/CD](https://www.notion.so/GitHub-Actions-CI-CD-ab1f0e8da0064aeba2dfcc45df5d1f68)
</p>

## 트러블 슈팅
❗[AWS EC2 메모리 부족 현상 해결](https://www.notion.so/AWS-EC2-ef2595c50bfb4d4ba2ab0aff1e60a092)
</br>
❗[invitation과 paper 연결 후 paper delete시 발생한 문제 해결](https://www.notion.so/invitation-paper-paper-delete-9421b8b6896a4a3bb9b47faf06074da0)
</br>
❗[이미 이메일이 존재하는 경우에도 회원가입 시 인증번호가 발송되는 문제](https://www.notion.so/77710ceca90b4c9e8140fa86fffef8f7)
</br>


