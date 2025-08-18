// पथ: app/src/main/java/com/genius/tdlibandroid/di/AppModule.kt
package com.genius.tdlibandroid.di

import com.genius.tdlibandroid.data.TelegramClient
import com.genius.tdlibandroid.data.TelegramClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindTelegramClient(impl: TelegramClientImpl): TelegramClient

    // नोट: TgCore में @Inject constructor का उपयोग होता है,
    // इसलिए हमें उसके लिए @Provides फंक्शन की आवश्यकता नहीं है।
    // यदि आपने पहले provideTgCore(...) जोड़ा था, तो उसे हटा दें।
}