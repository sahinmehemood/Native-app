# DATABASE_SCHEMA.md — Hermes Android

Room (SQLite) with FTS5. Optional SQLCipher via build flag `useSqlCipher`.

## Entities (12)

### SessionEntity
```kotlin
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val profileId: String,
    val provider: String,
    val model: String,
    val pinned: Boolean = false,
    val archived: Boolean = false,
    val messageCount: Int = 0
)
```

### MessageEntity
```kotlin
@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(SessionEntity::class, ["id"], ["sessionId"], ForeignKey.CASCADE)]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val role: String,           // "user" | "assistant" | "system" | "tool"
    val content: String,
    val createdAt: Long,
    val model: String,
    val tokensIn: Int = 0,
    val tokensOut: Int = 0,
    val toolCalls: String = "[]",   // JSON array
    val status: String = "done"     // "streaming" | "done" | "error"
)
```

### ProfileEntity
```kotlin
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatar: String,         // emoji or color seed
    val createdAt: Long,
    val configPath: String,
    val isActive: Boolean = false,
    val serverMode: String      // "local" | "remote"
)
```

### SkillEntity
```kotlin
@Entity(tableName = "skills")
data class SkillEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val version: String,
    val category: String,
    val installed: Boolean,
    val source: String,
    val author: String
)
```

### ModelEntity
```kotlin
@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val provider: String,
    val modelId: String,
    val params: String,       // JSON
    val favorite: Boolean = false,
    val lastUsed: Long = 0
)
```

### MemoryEntity
```kotlin
@Entity(tableName = "memory")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val type: String,         // "MEMORY" | "USER"
    val content: String,
    val updatedAt: Long,
    val size: Int
)
```

### PersonaEntity
```kotlin
@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey val id: String,
    val profileId: String,
    val content: String,      // SOUL.md
    val updatedAt: Long
)
```

### ScheduleEntity
```kotlin
@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cron: String,
    val prompt: String,
    val targets: String,      // JSON array
    val enabled: Boolean,
    val lastRunAt: Long?,
    val nextRunAt: Long?
)
```

### ScheduleRunEntity
```kotlin
@Entity(tableName = "schedule_runs")
data class ScheduleRunEntity(
    @PrimaryKey val id: String,
    val scheduleId: String,
    val startedAt: Long,
    val finishedAt: Long?,
    val status: String,
    val output: String
)
```

### GatewayEntity
```kotlin
@Entity(tableName = "gateways")
data class GatewayEntity(
    @PrimaryKey val id: String,
    val platform: String,
    val name: String,
    val config: String,       // JSON
    val status: String,       // "stopped" | "running" | "error"
    val lastError: String?
)
```

### ToolEntity
```kotlin
@Entity(tableName = "tools")
data class ToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val toolset: String,
    val description: String,
    val enabled: Boolean
)
```

### SyncEntity
```kotlin
@Entity(tableName = "sync_queue")
data class SyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String,
    val entityId: String,
    val operation: String,    // "create" | "update" | "delete"
    val dirty: Boolean,
    val timestamp: Long
)
```

## FTS5 virtual tables
- `sessions_fts` over `sessions(title)` — MATCH query, join to sessions
- `messages_fts` over `messages(content)` — MATCH query
- `skills_fts` over `skills(name, description)`
- `memory_fts` over `memory(content)`

Set up via `@RawQuery` or migration with `CREATE VIRTUAL TABLE`.

## DAOs (8 + 2)
SessionDao, MessageDao, ProfileDao, SkillDao, ModelDao, MemoryDao, ScheduleDao,
GatewayDao, ToolDao, SyncDao. Each has insert/replace/getAll/getById/delete/observe (Flow).

## Migrations
Version 1 = initial. Add migrations for every schema change. Never use destructive migration in production.
