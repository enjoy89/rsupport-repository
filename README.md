# 🗒️ 공지사항 관리 REST API

> RSupport 백엔드 과제 전형 제출물 <br>
> 2024.04.17 ~ 2024.04.20
>
<br>

- [작업 기록 노션 페이지](https://jeondui.notion.site/9ae88293619b4aa3ab1aec315a5f26e7?pvs=74) : API 명세서, 데이터베이스 ERD 등 작업, 트러블 슈팅 등 작업 기록을 상세히 작성하였습니다.

<br>

## 👩🏻‍💻 주요 기능
- 회원가입, 로그인
- 공지사항 등록, 수정, 조회, 삭제

<br>

## 👏 작업 흐름 (Feat. Branch 전략)
> main branch : 최종 병합 버전 <br>
> devlop branch : 기능 개발
>
1. [이슈 등록](https://github.com/enjoy89/rsupport-repository/issues?q=is%3Aissue+is%3Aclosed) : 작업할 기능 계획 간략하게 작성
2. 기능 개발 : `develop` 브랜치에서 작업
3. [Pull Request](https://github.com/enjoy89/rsupport-repository/pulls?q=is%3Apr+is%3Aclosed) : `develop` -> `main` 병합 전, 작업 과정을 상세히 작성
4. main merge : 최종 검토 후 `main` 브랜치에 병합

**모든 작업 과정을 문서화 하는 것에 초점을 두었습니다!** <br>

<br>

## ✏️ 커밋 컨벤션 
|타입 이름|내용|
|------|---|
|**Feat**|새로운 기능에 대한 커밋|
|**Fix**|버그 수정에 대한 커밋|
|**Refactor**|코드 리팩토링에 대한 커밋|
|**Test**|테스트 코드 수정에 대한 커밋|
|**Docs**|문서 수정에 대한 커밋|
|**Chore**|그 외 자잘한 수정에 대한 커밋|


<br>

## ⚒️ 사용 기술

### Backend
<div>
<img src="https://img.shields.io/badge/Java 17-FF9E0F?style=for-the-badge&logo=Java&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/Spring Boot 3.1.10 -6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white">
</div>

### Database & Storage
<div>
<img src="https://img.shields.io/badge/Spring Data JPA-20336B?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/aws s3-FFB71B?style=for-the-badge&logo=Amazon aws&logoColor=white">
</div>

### Tools
<div>
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/Intellij idea-3B00B9?style=for-the-badge&logo=intellijidea&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">
</div>

### Deploy
<div>
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/aws ec2-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
</div>

<br>

## 🙌 예상 동작 시나리오 (Feat. 기능 상세 설명)
> 기능 요구사항을 모두 만족하며, 제가 예상하는 API 호출 시나리오입니다.
>
1. 회원가입 진행
   - 이메일, 비밀번호 등 회원가입에 필요한 정보를 입력하여 회원가입을 진행합니다.
2. 로그인 진행
   - 이메일, 비밀번호를 입력하여 로그인을 합니다.
   - 로그인 시 서비스를 이용할 수 있는 인증 토큰이 발급됩니다.
3. 공지사항 등록
   - 로그인 이후, 공지사항을 등록할 수 있습니다.
   - 공지사항의 제목, 내용, 시작 일시, 종료 일시, 첨부파일을 입력하여 등록할 수 있습니다.
4. 공지사항 조회
   - 비회원 / 회원 모두, 공지사항을 조회할 수 있습니다.
   - 공지사항 전체 개수와 페이지 수를 포함하는 전체 조회가 가능합니다.
   - 공지사항 제목 혹은 id 값을 클릭하여 상세 화면으로 이동한다고 예상하여, 공지사항 상세 조회 API를 따로 만들었습니다.
   - 공지사항 상세 화면에는 자세한 내용이 확인 가능하며, 사용자가 이를 확인할 시 조회수가 증가합니다.
5. 공지사항 수정
    - 로그인 이후, 공지사항을 등록한 작성자의 권한이 확인되면 글 수정이 가능합니다.
    - 공지사항 내용과 더 나아가서 첨부파일을 삭제하거나, 새로 등록할 수 있습니다.
6. 공지사항 삭제
    - 수정과 마찬가지로, 공지사항의 작성자의 권한이 확인되면 글 삭제가 가능합니다.

<br>

## 💁🏻‍♀️ 실행 방법
1. Github 저장소 코드 클론
   ```
   git clone https://github.com/enjoy89/rsupport-repository.git
   ```
2. 프로젝트 빌드
   ```
   ./gradlew clean build
   ```
4. Spring Boot 애플리케이션 실행
   ```
   java -jar rsupport-noice-0.0.1-SNAPSHOT.jar
   ```
