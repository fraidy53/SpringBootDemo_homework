# 프로젝트 구조/코드 설명 가이드

기준일: 2026-03-20

이 문서는 현재 `demo` 프로젝트의 폴더 구조와 각 코드의 역할을 빠르게 파악하기 위한 내부 가이드입니다.

## 1) 전체 구조

```text
demo/
├─ pom.xml
├─ mvnw, mvnw.cmd
├─ README.md
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/myspringbootlab/
│  │  │  ├─ MySpringbootLabApplication.java
│  │  │  ├─ advice/
│  │  │  │  ├─ DefaultExceptionAdvice.java
│  │  │  │  └─ ErrorObject.java
│  │  │  ├─ config/
│  │  │  │  ├─ ProdConfig.java
│  │  │  │  └─ TestConfig.java
│  │  │  ├─ controller/
│  │  │  │  └─ BookController.java
│  │  │  ├─ dto/
│  │  │  │  └─ BookDTO.java
│  │  │  ├─ entity/
│  │  │  │  ├─ Book.java
│  │  │  │  └─ BookDetail.java
│  │  │  ├─ env/
│  │  │  │  └─ MyEnvironment.java
│  │  │  ├─ exception/
│  │  │  │  └─ BusinessException.java
│  │  │  ├─ mapper/
│  │  │  │  └─ BookMapper.java
│  │  │  ├─ props/
│  │  │  │  └─ MyPropProperties.java
│  │  │  ├─ Repository/
│  │  │  │  ├─ BookRepository.java
│  │  │  │  └─ BookDetailRepository.java
│  │  │  ├─ runner/
│  │  │  │  └─ MyPropRunner.java
│  │  │  └─ service/
│  │  │     └─ BookService.java
│  │  └─ resources/
│  │     ├─ application.properties
│  │     ├─ application-prod.properties
│  │     ├─ application-test.properties
│  │     ├─ banner.txt
│  │     ├─ static/
│  │     └─ templates/
│  └─ test/java/com/example/myspringbootlab/
│     ├─ BookRepositoryTest.java
│     └─ MySpringbootLabApplicationTests.java
└─ target/
```

> 참고: 패키지명이 `Repository`(대문자 R)로 되어 있습니다. 일반 관례는 소문자 `repository`이지만, 현재 프로젝트는 기존 구조를 유지 중입니다.

## 2) 계층별 역할

### `controller`
- HTTP 요청을 받는 진입점입니다.
- 현재 `BookController`가 `/api/books` 하위 CRUD + 검색 API를 제공합니다.
- 입력 검증은 `@Valid`로 트리거되고, 비즈니스 처리는 `service`로 위임합니다.

### `service`
- 핵심 비즈니스 로직 계층입니다.
- `BookService`에서 조회/등록/수정/삭제, ISBN 중복 검사, 검색 로직을 처리합니다.
- 트랜잭션 경계(`@Transactional`)가 정의되어 있습니다.

### `Repository`
- DB 접근 계층(Spring Data JPA)입니다.
- `BookRepository`: ISBN/저자/제목 검색, 상세 fetch join 조회, 중복 체크.
- `BookDetailRepository`: 상세 단건/출판사 검색, 연관 Book fetch 조회.

### `entity`
- JPA 엔티티(테이블 매핑) 계층입니다.
- `Book`(기본 정보) <-> `BookDetail`(상세 정보) 1:1 연관관계가 구성되어 있습니다.
- `BookDetail`이 외래키(`book_id`)를 가지는 관계의 주인입니다.

### `dto`
- API 요청/응답 전용 데이터 모델입니다.
- `BookDTO.Request`, `BookDTO.BookDetailDTO`, `BookDTO.Response`, `BookDTO.BookDetailResponse`로 구성됩니다.
- 검증 애노테이션으로 잘못된 입력을 사전에 차단합니다.

### `mapper`
- DTO와 엔티티 간 변환을 담당합니다.
- 생성/수정 요청을 엔티티로 반영하고, 엔티티를 응답 DTO로 변환합니다.

### `advice` + `exception`
- 전역 예외 처리 계층입니다.
- `BusinessException`(도메인 예외), `DefaultExceptionAdvice`(공통 응답 변환), `ErrorObject`(에러 응답 포맷)로 구성됩니다.

#### 왜 `BusinessException`이 따로 필요한가?
- `BusinessException`은 **"무슨 문제인지"(비즈니스 의미)**를 담는 예외 클래스입니다.
  - 예: "해당 ID의 도서를 찾을 수 없음", "ISBN 중복" 등
  - `HttpStatus`를 함께 담아, 서비스 계층에서 의도를 명확히 표현합니다.
- `DefaultExceptionAdvice`는 **"그 예외를 HTTP 응답으로 어떻게 포장할지"**를 담당합니다.
  - 즉, 예외를 잡아서 `ErrorObject` 형태(JSON)로 변환하는 역할입니다.

정리하면:
- `exception` 패키지: 예외의 **의미/종류 정의**
- `advice` 패키지: 예외의 **응답 변환/표준화 처리**

두 역할이 분리되어야 좋은 이유:
- 서비스 로직에서 응답 포맷(JSON 구조)까지 신경 쓰지 않아도 됩니다.
- 예외 응답 형식을 바꿔도(`ErrorObject` 필드 변경 등) 비즈니스 로직 수정이 최소화됩니다.
- 테스트/유지보수가 쉬워집니다 (비즈니스 검증과 응답 포맷 검증을 분리 가능).

#### 현재 프로젝트에서 실제 동작 흐름
1. `BookService`에서 비즈니스 조건 실패 시 `throw new BusinessException(...)`
2. 컨트롤러까지 예외가 전파됨
3. `DefaultExceptionAdvice`의 `@ExceptionHandler(BusinessException.class)`가 예외를 처리
4. `ErrorObject`로 변환하여 일관된 HTTP 오류 응답 반환

### `config` + `env` + `props` + `runner`
- 실행환경/설정 관리 계층입니다.
- `ProdConfig`, `TestConfig`: 프로파일별 환경 빈 제공.
- `MyEnvironment`: 환경 모델 객체.
- `MyPropProperties`: `application*.properties` 바인딩.
- `MyPropRunner`: 애플리케이션 시작 시 설정값 로그 출력.

## 3) 현재 핵심 도메인 모델

### Book (기본 도서 정보)
- 필드: `id`, `title`, `author`, `isbn`, `price`, `publishDate`
- 제약: `isbn` 유니크
- 관계: `BookDetail`과 1:1 (`mappedBy = "book"`, `cascade = ALL`, `orphanRemoval = true`)

### BookDetail (도서 상세 정보)
- 필드: `description`, `language`, `pageCount`, `publisher`, `coverImageUrl`, `edition`
- 관계: `@OneToOne(fetch = LAZY)` + `@JoinColumn(name = "book_id", unique = true)`

## 4) 요청 처리 흐름

```text
Client
  -> BookController (요청 수신/검증)
  -> BookService (비즈니스 로직/트랜잭션)
  -> BookRepository, BookDetailRepository (DB 접근)
  -> BookMapper (엔티티 <-> DTO 변환)
  -> Response 반환

예외 발생 시
  -> DefaultExceptionAdvice
  -> ErrorObject 형태로 일관 응답
```

## 5) 주요 API

- `GET /api/books`
- `GET /api/books/{id}`
- `GET /api/books/isbn/{isbn}`
- `GET /api/books/search/author?author={author}`
- `GET /api/books/search/title?title={title}`
- `POST /api/books`
- `PUT /api/books/{id}`
- `DELETE /api/books/{id}`

## 6) 테스트 파일 역할

### `BookRepositoryTest`
- `@DataJpaTest` 기반으로 JPA 레이어만 집중 검증합니다.
- 1:1 연관관계 저장/조회, ISBN 조회, 저자 검색 등의 레포지토리 동작을 확인합니다.

### `MySpringbootLabApplicationTests`
- 애플리케이션 컨텍스트 로딩 여부를 확인하는 스모크 테스트입니다.

## 7) 빠른 확인 명령

```powershell
cd "C:\4. Java_Spring\3. SpringBoot\demo"
.\mvnw.cmd -q -Dtest=BookRepositoryTest test
.\mvnw.cmd -q -DskipTests compile
```

```powershell
cd "C:\4. Java_Spring\3. SpringBoot\demo"
.\mvnw.cmd spring-boot:run
```
