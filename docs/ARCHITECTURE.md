# ARCHITECTURE.md — Hermes Android

## Layers

```
PRESENTATION (Compose)
  Screens → ViewModels (MVI) → UiState (sealed) → Components (Design System)
       │
  Navigation (type-safe Routes, single NavHost, single Activity)
       │
DOMAIN (pure Kotlin)
  UseCases → Models → Repository interfaces
       │
DATA
  Repository impls → (Local: Room/SQLCipher) + (Remote: Ktor HTTP/SSE/WS) + (Prefs: Encrypted)
```

## Dependency rule
Presentation depends on Domain (via interfaces). Data implements Domain interfaces.
Domain knows nothing about Android/Compose. This is enforced by package structure + Hilt.

## MVI contract (every screen)
```kotlin
// state/xxxUiState.kt
sealed interface XxxUiState {
    data object Loading : XxxUiState
    data object Empty : XxxUiState
    data class Loaded(val ...) : XxxUiState
    data class Error(val message: String) : XxxUiState
}

// viewmodel/XxxViewModel.kt
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val useCase: XxxUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<XxxUiState>(XxxUiState.Loading)
    val state: StateFlow<XxxUiState> = _state.asStateFlow()
    private val _events = MutableSharedFlow<XxxEvent>()
    val events = _events.asSharedFlow()

    fun onEvent(e: XxxEvent) { /* reduce */ }
}

// screen/XxxScreen.kt
@Composable
fun XxxScreen(viewModel: XxxViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.events.collect { /* handle */ } }
    when (state) { /* Loading/Empty/Loaded/Error */ }
}
```

## DI modules (Hilt)
- `AppModule` — Application, CoroutineScope, Dispatchers
- `NetworkModule` — Ktor `HttpClient`, `SseClient`, auth interceptor, base URL provider
- `DatabaseModule` — `HermesDatabase`, DAOs
- `RepositoryModule` — binds `XxxRepository` (interface) → `XxxRepositoryImpl`
- `UseCaseModule` — provides `XxxUseCase`

## Data flow: Chat streaming
```
User sends → ChatViewModel.send() → ChatUseCase.execute()
  → ChatRepository.stream(sessionId, message)
    → SseClient.postChat() returns Flow<ChatEvent>
      → map to domain Message + ToolCall + TokenUsage
        → persist to Room (MessageEntity)
          → emit to StateFlow → ChatScreen renders
```

## Naming conventions
- Screens: `XxxScreen.kt`
- ViewModels: `XxxViewModel.kt`
- UiState: `XxxUiState.kt`
- Repository iface: `XxxRepository` ; impl: `XxxRepositoryImpl`
- UseCase: `DoXxxUseCase` or `XxxUseCase`
- DAO: `XxxDao`
- Entity: `XxxEntity`
- DTO: `XxxDto` in `data/api/dto`
- Components: `HermesXxx.kt`

## Error handling
- `HermesException(message, cause, code)` extends `Exception`
- `Result<out T>` sealed (Success/Error)
- All suspends wrapped in `runCatching` → `Result`
- ViewModel exposes error via `UiState.Error` + event `ShowSnackbar`

## Concurrency
- `viewModelScope` for UI-bound
- `CoroutineScope(SupervisorJob() + Dispatchers.Default)` for app-level (sync worker)
- Flows with `stateIn`/`shareIn` where appropriate
- `collectAsStateWithLifecycle()` in Compose (lifecycle-runtime-compose)
