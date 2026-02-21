package br.com.ximendesindustries.xiagents.di

import br.com.ximendesindustries.xiagents.data.api.AgentsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): AgentsApi {
        return retrofit.create(AgentsApi::class.java)
    }
}
