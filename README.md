# kareer-server
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/be5a6b55-655c-4c49-8db4-3fe3c6a46211" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/4a60df5b-c5ab-4d56-b5a7-4f3b11c997a8" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/8bb20d3f-adf7-4000-9e11-fa26de85d116" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/ff387f13-b7ce-4aaa-8a17-5f70c7d76c81" />
<h1> 🐦 Kareer </h1>

${\textsf{\color{blue}한국 커리어 여정, Kareer와 함께 명확하게}}$

```
한국에서 커리어를 시작/전환하는 외국인 유학생을 위한 커리어·비자 가이드
Korea + Career = Kareer, 나만의 커리어 여정을 정리하다

ㄴ 전공/언어/비자 상태 기반으로 지원 가능한 직무,비자 옵션을 자동 제시
ㄴ 개인 맞춤 타임라인과 To-Do로 준비 단계를 한눈에 정리
```
## Contributors
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/jeong1112">
      <img width=140px src="https://github.com/jeong1112.png" alt=""/><br />
      <sub><b>👑이정연</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/eraser502">
      <img width=140px src="https://github.com/eraser502.png" alt=""/><br />
      <sub><b>김도훈</b></sub></a><br /></td>
      <td align="center"><a href="https://github.com/hyomee2">
      <img width=140px src="https://github.com/hyomee2.png" alt=""/><br />
      <sub><b>권형미</b></sub></a><br /></td>
    </tr>
  </tbody>
</table>

## 인프라 구조
<img width="785" height="664" alt="image" src="https://github.com/user-attachments/assets/6ae15100-82a0-417f-8d61-2c91a4562d53" />

## 시스템 아키텍처
```
kareer-server
├── deployment
│   ├── nginx
│   │   └── default.conf
│   ├── deploy.sh
│   └── docker-compose.yml
├── gradle
└── src
    └── main 
        └── java
            └── org.sopt.kareer
                ├── KareerApplication
                ├── domain
                │   └── jobposting
                │       ├── controller
                │       ├── dto
                │       ├── entity
                │       ├── exception
                │       ├── repository
                │       └── service
                │   └── member
                │       ├── controller
                │       ├── dto
                │       ├── entity
                │       ├── exception
                │       ├── repository
                │       └── service
                │   └── roadmap
                │       ├── controller
                │       ├── dto
                │       ├── entity
                │       ├── exception
                │       ├── repository
                │       └── service
                └── global
                    ├── annotation
                    ├── auth
                    ├── config
                    ├── entity
                    ├── exception
                    ├── external.ai
                    ├── jwt
                    ├── oauth
                    └── response
```

