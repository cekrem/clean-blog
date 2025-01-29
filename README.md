src/main/kotlin/
├── domain/                 # Enterprise business rules
│   └── model/             # Domain entities
│       ├── Content.kt
│       ├── ContentBlock.kt
│       ├── ContentSummary.kt
│       ├── ContentType.kt
│       └── Metadata.kt
│
├── application/           # Application business rules
│   ├── port/             # Inbound/outbound ports (interfaces)
│   │   └── ContentRepository.kt
│   └── usecase/          # Use case interfaces
│       ├── UseCase.kt
│       ├── GetContentUseCase.kt
│       ├── ListContentsByTypeUseCase.kt
│       └── GetContentTypesUseCase.kt
│
├── adapter/              # Interface adapters
│   ├── usecase/         # Use case implementations
│   │   ├── GetContentUseCaseImpl.kt
│   │   ├── ListContentsByTypeUseCaseImpl.kt
│   │   └── GetContentTypesUseCaseImpl.kt
│   │
│   ├── persistence/     # Repository implementations
│   │   ├── ContentGateway.kt
│   │   ├── FileContentGateway.kt
│   │   ├── RssContentGateway.kt
│   │   ├── MockContentGateway.kt
│   │   └── parser/
│   │       ├── ContentParser.kt
│   │       └── MarkdownContentParser.kt
│   │
│   └── web/            # Web-related adapters
│       ├── Routes.kt
│       ├── Server.kt
│       ├── Configuration.kt
│       └── response/
│           ├── ContentResponse.kt
│           ├── ContentBlockResponse.kt
│           └── ContentTypeResponse.kt
│
└── infrastructure/      # Frameworks & drivers
└── config/         # Framework configurations
└── KtorConfig.kt
