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

## 📦 ERD 
`dbdiagram` : [dbdiagram](https://dbdiagram.io/d/643ba6606b31947051aa49eb)
<div align=center>
<img width="720" alt="Architecture_Diagram" src="https://github.com/xyz-wr/rolling-paper/assets/63355903/0213c880-81b4-40cc-b7e6-89087b5f051f">
</div>
</br>

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
[AWS EC2 메모리 부족 현상 해결](https://www.notion.so/AWS-EC2-ef2595c50bfb4d4ba2ab0aff1e60a092)
</br>
[invitation과 paper 연결 후 paper delete시 발생한 문제 해결](https://www.notion.so/invitation-paper-paper-delete-9421b8b6896a4a3bb9b47faf06074da0)
</br>
[MessageRepositoryTest에서 testFindByPaperIdAndId가 전체 테스트에서는 통과되지 않는 문제 해결](https://www.notion.so/MessageRepositoryTest-testFindByPaperIdAndId-52bd396b2a1d44568743f07567b1a43f)
</br>
[이메일 인증번호 구현 시 The sender address is unauthorized NkEL1BlcQ4yKzxc1hmQMQg - nsmtp 문제 해결](https://www.notion.so/The-sender-address-is-unauthorized-NkEL1BlcQ4yKzxc1hmQMQg-nsmtp-18781c6a95914b55b374d08ff73ee79f)
</br>
[Dockerfile 작성 시 failed to compute cache key 문제 해결](https://www.notion.so/Dockerfile-failed-to-compute-cache-key-12bfa8be67d14df4a0cf21e6a58472bb)
</br>


