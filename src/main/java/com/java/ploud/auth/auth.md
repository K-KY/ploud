# 사용자 인증 / 로그인 기능 명세서

## 1. 개요

본 시스템은 사용자 인증(Authentication)과 권한 인가(Authorization)를 담당하며,
JWT 토큰 또는 세션 기반 인증을 통해 보안이 필요한 API 접근을 제어한다.

* 인증 처리: **Filter**
* 회원 관리: **Controller / Service**
* 권한 검증: **Spring Security 기반**

---

## 2. 인증 흐름 전체 구조

```
[Client]
   ↓
[Login Request]
   ↓
[Authentication Filter]
   ↓
[UserDetailsService]
   ↓
[AuthenticationManager]
   ↓
[JWT / Session 발급]
   ↓
[Client 저장]
```

---

## 3. 기능 명세

## 3.1 회원가입

### 설명

신규 사용자를 등록한다.

### 처리 계층

* **Controller**
* **Service**
* **Repository**

### API

```
POST /api/users/signup
```

### 요청 값

| 필드명      | 타입     | 필수 | 설명      |
| -------- | ------ | -- | ------- |
| email    | String | O  | 로그인 ID  |
| password | String | O  | 평문 비밀번호 |
| name     | String | O  | 사용자 이름  |

### 처리 로직

1. 이메일 중복 체크
2. 비밀번호 암호화 (BCrypt)
3. 기본 권한(Role.USER) 부여
4. User 저장

### 결과

* 성공: 201 Created
* 실패: 409 Conflict (중복 이메일)

---

## 3.2 로그인

### 설명

아이디/비밀번호를 검증하고 인증 정보를 생성한다.

### 처리 계층

* **Filter (UsernamePasswordAuthenticationFilter 확장)**

### API

```
POST /api/auth/login
```

### 요청 값

| 필드명      | 타입     |
| -------- | ------ |
| email    | String |
| password | String |

### 처리 로직

1. Filter에서 요청 가로챔
2. AuthenticationManager로 인증 위임
3. 인증 성공 시:

  * JWT 발급 **또는**
  * 세션 생성
4. 인증 실패 시 401 반환

---

## 3.3 비밀번호 변경

### 설명

로그인한 사용자가 기존 비밀번호를 변경한다.

### 인증

* 필요 (JWT / Session)

### API

```
PUT /api/users/password
```

### 요청 값

| 필드명         | 설명      |
| ----------- | ------- |
| oldPassword | 기존 비밀번호 |
| newPassword | 새 비밀번호  |

### 처리 로직

1. 기존 비밀번호 검증
2. 새 비밀번호 암호화
3. DB 업데이트

---

## 3.4 비밀번호 찾기 (재설정)

### 설명

비밀번호 분실 시 임시 토큰을 발급하여 재설정한다.

### API

```
POST /api/users/password/reset-request
POST /api/users/password/reset
```

### 흐름

1. 이메일 입력
2. 임시 토큰 생성 (만료 시간 포함)
3. 이메일 발송
4. 토큰 검증 후 비밀번호 재설정

---

## 3.5 토큰 / 세션 저장

### 선택 1: JWT 기반

* 저장 위치: Client(LocalStorage / Cookie)
* 서버 상태: Stateless

### 선택 2: 세션 기반

* 저장 위치: 서버 (Redis 권장)
* JSESSIONID 사용

---

## 3.6 권한 관리 (Authorization)

### 설명

사용자의 Role에 따라 접근 가능한 API를 제한한다.

### 처리 계층

* **Spring Security**
* **@PreAuthorize / @Secured**

### 예시

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin")
public ResponseEntity<?> adminApi() {}
```

---

## 4. 테이블 명세

## 4.1 User 테이블

| 컬럼명        | 타입       | 설명       |
| ---------- | -------- | -------- |
| user_id    | BIGINT   | PK       |
| email      | VARCHAR  | 로그인 ID   |
| password   | VARCHAR  | 암호화 비밀번호 |
| name       | VARCHAR  | 사용자 이름   |
| enabled    | BOOLEAN  | 활성 여부    |
| created_at | DATETIME | 생성일      |

---

## 4.2 Role 테이블

| 컬럼명       | 타입      | 설명                     |
| --------- | ------- | ---------------------- |
| role_id   | BIGINT  | PK                     |
| role_name | VARCHAR | ROLE_USER / ROLE_ADMIN |

---

## 4.3 User_Role (N:M)

| 컬럼명     | 설명 |
| ------- | -- |
| user_id | FK |
| role_id | FK |

---

## 5. ERD 관계

```
User 1 --- N User_Role N --- 1 Role
```

---

## 6. 보안 고려 사항

* 비밀번호는 반드시 **BCrypt**
* 로그인 실패 횟수 제한 가능
* JWT 만료 시간 + Refresh Token 권장
* 관리자 권한 API는 별도 필터 적용

---

## 7. 책임 분리 요약

| 계층         | 책임        |
| ---------- | --------- |
| Controller | 회원 관리     |
| Filter     | 로그인 인증    |
| Service    | 비즈니스 로직   |
| Security   | 권한 검증     |
| DB         | 사용자 상태 관리 |