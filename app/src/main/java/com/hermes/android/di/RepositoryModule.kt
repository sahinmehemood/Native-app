package com.hermes.android.di

import com.hermes.android.data.repository.AgentRepositoryImpl
import com.hermes.android.data.repository.ChatRepositoryImpl
import com.hermes.android.data.repository.ConnectionRepositoryImpl
import com.hermes.android.data.repository.SkillRepositoryImpl
import com.hermes.android.domain.repository.AgentRepository
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.ConnectionRepository
import com.hermes.android.domain.repository.SkillRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChat(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindAgent(impl: AgentRepositoryImpl): AgentRepository

    @Binds
    @Singleton
    abstract fun bindSkill(impl: SkillRepositoryImpl): SkillRepository

    @Binds
    @Singleton
    abstract fun bindConnection(impl: ConnectionRepositoryImpl): ConnectionRepository
}
