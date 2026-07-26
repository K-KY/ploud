# Ploud

Ploud는 파일 업로드, 보관, 탐색을 위한 클라우드 스토리지 프로젝트입니다.

사용자가 자신의 파일과 디렉토리를 웹 환경에서 관리할 수 있도록, 파일 저장소와 메타데이터 관리를 분리해 구성한 스토리지 서비스입니다. 실제 파일 데이터는 객체 스토리지에 저장하고, 사용자가 보는 디렉토리 구조와 파일 정보는 별도의 메타데이터로 관리합니다.

## 프로젝트 소개

Ploud는 일반적인 클라우드 드라이브처럼 파일을 업로드하고, 폴더 단위로 정리하고, 필요한 파일을 다시 찾는 흐름을 목표로 합니다.

단순히 파일을 서버 디스크에 저장하는 방식이 아니라, 스토리지 계층과 애플리케이션 계층을 분리해 파일 관리 기능을 확장하기 쉬운 구조를 지향합니다. 사용자는 디렉토리 기반으로 파일을 탐색하고, 서비스는 내부적으로 파일 위치, 소유자, 이름, 크기, 타입 등의 정보를 메타데이터로 관리합니다.

## 주요 기능

### 사용자 인증

회원가입과 로그인을 통해 사용자별 파일 공간을 분리합니다. JWT 기반 인증을 사용해 파일 조회, 업로드, 디렉토리 관리 요청을 보호합니다.

<img src="ploud/ploud%20login.gif" alt="Ploud 로그인 흐름" width="900">

### 파일 업로드

presigned URL을 발급받아 클라이언트가 객체 스토리지로 파일을 직접 업로드합니다. 업로드 이후에는 파일명, 크기, 타입, 저장 위치 같은 메타데이터를 애플리케이션 서버에 저장합니다.

<img src="ploud/ploud%20file%20upload.gif" alt="Ploud 파일 업로드 흐름" width="900">

### 디렉토리 탐색

사용자는 폴더 구조를 따라 파일을 탐색할 수 있습니다. 실제 객체 스토리지는 디렉토리 개념이 약하지만, Ploud는 메타데이터를 통해 익숙한 폴더 기반 탐색 경험을 제공합니다.

<img src="ploud/ploud%20search%20directory%20system.gif" alt="Ploud 디렉토리 탐색 흐름" width="900">

### 파일 및 디렉토리 검색

파일명과 디렉토리명을 기준으로 원하는 항목을 검색할 수 있습니다. 많은 파일이 저장된 상황에서도 사용자가 필요한 자료를 빠르게 찾을 수 있도록 돕습니다.

<img src="ploud/ploud%20search.gif" alt="Ploud 파일 및 디렉토리 검색" width="900">

### 삭제 및 정리 흐름

파일 삭제 요청과 실제 스토리지 정리 흐름을 분리해 관리합니다.

<img src="ploud/ploud%20delete%20file%20and%20%20dir.gif" alt="Ploud 파일 및 디렉토리 삭제" width="900">


```text
ploud
├── Dockerfile
├── README.md
├── build.gradle
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com/java/ploud
    │   │       ├── PloudApplication.java
    │   │       ├── auth
    │   │       │   ├── auth.md
    │   │       │   ├── controller
    │   │       │   │   ├── SignUpController.java
    │   │       │   │   └── UserController.java
    │   │       │   ├── dto
    │   │       │   │   ├── AuthDto.java
    │   │       │   │   ├── AuthedUserDetail.java
    │   │       │   │   └── UserDto.java
    │   │       │   ├── entity
    │   │       │   │   └── User.java
    │   │       │   ├── repository
    │   │       │   │   └── UserRepository.java
    │   │       │   └── service
    │   │       │       ├── PasswordEncryptor.java
    │   │       │       ├── TempUserService.java
    │   │       │       ├── UserService.java
    │   │       │       └── mail
    │   │       │           └── MailService.java
    │   │       ├── config
    │   │       │   ├── AppConfig.java
    │   │       │   ├── AuthConfig.java
    │   │       │   ├── MailConfig.java
    │   │       │   ├── MinioConfig.java
    │   │       │   ├── RedisConfig.java
    │   │       │   ├── ScheduleConfig.java
    │   │       │   ├── SecurityConfig.java
    │   │       │   └── WebConfig.java
    │   │       ├── exceptions
    │   │       │   └── RootNotFoundException.java
    │   │       ├── jwt
    │   │       │   ├── JwtProvider.java
    │   │       │   ├── controller
    │   │       │   │   └── JwtController.java
    │   │       │   ├── dto
    │   │       │   │   └── JwtDto.java
    │   │       │   ├── filter
    │   │       │   │   ├── JwtAuthenticationFilter.java
    │   │       │   │   ├── JwtFilter.java
    │   │       │   │   └── RequestDebugFilter.java
    │   │       │   └── service
    │   │       │       └── JwtService.java
    │   │       ├── metadb
    │   │       │   ├── controller
    │   │       │   │   ├── DirController.java
    │   │       │   │   ├── FileController.java
    │   │       │   │   └── SearchController.java
    │   │       │   └── service
    │   │       │       ├── DeleteQueueService.java
    │   │       │       ├── DirectoryService.java
    │   │       │       ├── DirectoryTransactionService.java
    │   │       │       ├── FileService.java
    │   │       │       ├── SearchService.java
    │   │       │       ├── StorageCleanService.java
    │   │       │       ├── dto
    │   │       │       │   ├── DirDto.java
    │   │       │       │   ├── DirectoryDto.java
    │   │       │       │   ├── DirectoryPathDto.java
    │   │       │       │   ├── ExploreDto.java
    │   │       │       │   ├── FileChangeDto.java
    │   │       │       │   ├── FileDto.java
    │   │       │       │   ├── MetaDataDto.java
    │   │       │       │   ├── PathDecryptDto.java
    │   │       │       │   ├── QueueMessage.java
    │   │       │       │   └── SearchDto.java
    │   │       │       ├── entity
    │   │       │       │   ├── DeleteQueue.java
    │   │       │       │   ├── Directory.java
    │   │       │       │   ├── Files.java
    │   │       │       │   ├── StorageCleanQueue.java
    │   │       │       │   └── TargetTypes.java
    │   │       │       ├── queue
    │   │       │       │   ├── DelConsumer.java
    │   │       │       │   ├── DelProducer.java
    │   │       │       │   ├── DirQueueHandler.java
    │   │       │       │   ├── FileQueueHandler.java
    │   │       │       │   ├── QueueHandleManager.java
    │   │       │       │   ├── QueueHandler.java
    │   │       │       │   ├── StorageQueueHandler.java
    │   │       │       │   └── StreamInitializer.java
    │   │       │       └── repository
    │   │       │           ├── DeleteQueueRepository.java
    │   │       │           ├── DirectoryRepository.java
    │   │       │           ├── FileRepository.java
    │   │       │           └── StorageCleanRepository.java
    │   │       └── storage
    │   │           ├── dev.md
    │   │           ├── controller
    │   │           │   └── StorageController.java
    │   │           └── service
    │   │               ├── MinioService.java
    │   │               └── dto
    │   │                   ├── FileDeleteDto.java
    │   │                   ├── FileUploadDto.java
    │   │                   ├── PreSignedUrlDto.java
    │   │                   └── StorageDto.java
    │   └── resources
    │       ├── application-local.properties
    │       ├── application-prod.properties
    │       ├── application-test.properties
    │       └── application.properties
    └── test
```

## 기술 스택

### Backend

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### Database & Storage

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)
![MinIO](https://img.shields.io/badge/MinIO-C72E49?style=for-the-badge&logo=minio&logoColor=white)

### Infrastructure & Test

![Docker](https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![k6](https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white)
