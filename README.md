# ACS 7th Final Project Backend

This is aws final project, MOAI!


# MSA Architecture
![acs-final-project-msa-architecture drawio](https://github.com/user-attachments/assets/b59f3933-e994-47b7-a6c1-53d0558e05b2)

## 4개의 도메인으로 구분하여 MSA 환경 구축
- 동화책
- 독후감
- 책방
- 회원

전체 프로젝트 내에서 각 도메인은 독립적은 모듈로 구성되어 서로 간의 결합도를 낮추고 빌드 과정 또한 독립적으로 수행됩니다. 

각 도메인이 공통적으로 사용하는 요소들은 ```common``` 도메인에 포함시켜 상속받도록 하였습니다


의존성 또한 공통적으로 필요로 하는 ```lombok```, ```jpa```, ```webmvc``` 같은 것들은 프로젝트 레벨의 ```build.gradle``` 에 작성하였습니다. 

동화책 도메인의 경우 AWS 서비스 API들을 호출해야 하므로 필요한 의존성을 모듈 레벨의 ```build.gradle``` 에 작성해주었습니다.

## API-Gateway
프론트엔드와 api 통신을 하는데 있어 공통된 엔드포인트 주소를 사용하고 보안성을 높이기 위해 ```spring-cloud-starter-gateway``` 라이브러리를 이용하여 API-Gateway 모듈을 추가적으로 구성하였습니다.

## Docker 빌드
도커 이미지로 빌드 시 이미지 경량화를 위해 2 stage로 빌드 하였고 모듈 단위로 이미지가 생성되도록 하였습니다.

```docker
COPY fairytale/build/libs/fairytale-0.0.1-SNAPSHOT.jar app.jar
```

