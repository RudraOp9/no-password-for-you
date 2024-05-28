/*
 *  No password for you
 *  Created by RudraOp9
 *  Modified on 27/05/24, 11:28 am
 *  Copyright (c) 2024 . All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.leo.nopasswordforyou.di

import android.content.Context
import androidx.room.Room
import com.leo.nopasswordforyou.App
import com.leo.nopasswordforyou.database.Database
import com.leo.nopasswordforyou.database.alias.AliasDao
import com.leo.nopasswordforyou.database.passes.PassesDao
import com.leo.nopasswordforyou.database.passlist.PassListDao
import com.leo.nopasswordforyou.database.passlist.PassListEntity
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
    /*  suspend fun addPassInList(
          title: String,
          description: String,
          alias: String,
          passId: String,
          @ApplicationContext context: ApplicationContext
      ) {
          App.database.passListDao().insertNewPass(
              PassListEntity(
                  title = title,
                  desc = description,
                  alias = alias,
                  passId = passId
              )
          )

      }*/
}