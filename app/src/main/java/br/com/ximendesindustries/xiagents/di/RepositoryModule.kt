package br.com.ximendesindustries.xiagents.di

import br.com.ximendesindustries.xiagents.data.repository.AgentsRepositoryImpl
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
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
    abstract fun bindAgentsRepository(
        agentsRepositoryImpl: AgentsRepositoryImpl
    ): AgentsRepository
}
