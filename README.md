<div align=center>

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


