/*
 *  No password for you
 *  Copyright (c) 2024 . All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,either version 3 of the License,or
 * (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not,see <http://www.gnu.org/licenses/>.
 */

package com.leo.nopasswordforyou.di

import android.content.Context
import androidx.room.Room
import com.leo.nopasswordforyou.database.Database
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passlist.PassListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "NOPASSFORYOU_DATA")
            .build()
    }


    @Provides
    @Singleton
    fun providePassListDao(database: Database): PassListDao {
        return database.passListDao()
    }

    @Provides
    @Singleton
    fun providePassesDao(database: Database): PassesDao {
        return database.passesDao()
    }

    @Provides
    @Singleton
    fun provideAliasDao(database: Database): AliasDao {
        return database.aliasDao()
    }

}