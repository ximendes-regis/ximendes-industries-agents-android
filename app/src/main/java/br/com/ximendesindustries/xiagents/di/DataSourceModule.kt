package br.com.ximendesindustries.xiagents.di

import br.com.ximendesindustries.xiagents.data.datasource.AgentsRemoteDataSource
import br.com.ximendesindustries.xiagents.data.datasource.AgentsRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAgentsRemoteDataSource(
        impl: AgentsRemoteDataSourceImpl
    ): AgentsRemoteDataSource
}
