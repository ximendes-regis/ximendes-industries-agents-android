# Ximendes Industries Agents

Aplicativo Android nativo para interagir com agentes de IA via chat. Liste os agentes disponíveis e converse com eles através de uma interface moderna construída com Jetpack Compose.

## 📱 Funcionalidades

- **Menu de Agentes**: visualize todos os agentes disponíveis em cards
- **Chat**: envie mensagens e receba respostas do agente selecionado
- **Sessões**: gerencie múltiplas conversas e alterne entre elas
- **UI moderna**: interface Material Design 3 com animações suaves

## 🛠 Stack Técnica

| Tecnologia             | Uso                    |
| ---------------------- | ---------------------- |
| **Kotlin**             | Linguagem principal    |
| **Jetpack Compose**    | Interface declarativa  |
| **Material 3**         | Componentes visuais    |
| **Hilt**               | Injeção de dependência |
| **Retrofit + OkHttp**  | Chamadas de API REST   |
| **Moshi**              | Serialização JSON      |
| **Coroutines**         | Programação assíncrona |
| **Navigation Compose** | Navegação entre telas  |

## 📋 Requisitos

- Android Studio Ladybug (2024.2.1) ou superior
- JDK 17
- Android SDK 35
- minSdk 24
- targetSdk 35

## 🚀 Como Executar

### 1. Clone o repositório

```bash
git clone https://github.com/ximendes-regis/ximendes-industries-agents-android.git
cd xiagents
```

### 2. Configure a URL da API

Antes de rodar o app, configure a URL base do backend em:

```
app/src/main/java/br/com/ximendesindustries/xiagents/di/NetworkModule.kt
```

Substitua `"your_base_url_here"` pela URL real da API (ex: `"https://api.exemplo.com/"`).

> ⚠️ **Importante**: A URL deve terminar com `/`.

### 3. Execute o projeto

Abra o projeto no Android Studio e execute (Run) em um emulador ou dispositivo físico.

Ou via terminal:

```bash
./gradlew installDebug
```

## 🏗 Arquitetura

O projeto segue uma arquitetura em camadas:

```
app/src/main/java/br/com/ximendesindustries/xiagents/
├── data/           # Dados e fontes externas
│   ├── api/        # Interfaces Retrofit
│   ├── datasource/ # Fontes de dados remotas
│   ├── model/      # DTOs (request/response)
│   └── repository/ # Implementações do repositório
├── domain/         # Regras de negócio
│   ├── model/      # Modelos de domínio
│   ├── repository/ # Contratos do repositório
│   └── usecase/    # Casos de uso
├── ui/             # Camada de apresentação
│   ├── screen/     # Telas Compose
│   │   ├── agentchat/
│   │   └── agentsmenu/
│   └── theme/      # Tema e estilos
├── core/           # Utilitários e modelos compartilhados
└── di/             # Módulos Hilt
```

## 🔌 API Backend

O app espera os seguintes endpoints:

| Método | Endpoint                           | Descrição                 |
| ------ | ---------------------------------- | ------------------------- |
| GET    | `/agents`                          | Lista todos os agentes    |
| POST   | `/agents/chat/{agentId}`           | Envia mensagem ao agente  |
| GET    | `/agents/chat/pixel/sessions`      | Lista sessões de conversa |
| GET    | `/agents/chat/pixel/sessions/{id}` | Detalhes de uma sessão    |

## 🧪 Testes

Execute os testes unitários:

```bash
./gradlew test
```
