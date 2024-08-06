package com.isu.apitracker.di

import android.content.Context
import androidx.room.Room
import com.isu.apitracker.AppDatabase
import com.isu.apitracker.TransactionDataDao
import com.isu.apitracker.data.RepositoryImplementation
import com.isu.apitracker.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDao(@ApplicationContext context: Context): TransactionDataDao {
        val db= Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                "app_database"
            )
            .fallbackToDestructiveMigration().build()
        val dao=db.transactionDataDao()
        return dao
    }
    @Provides
    @Singleton
    fun provideRepository(dao: TransactionDataDao): Repository {
        val repository=RepositoryImplementation(dao)
        return repository
    }
}