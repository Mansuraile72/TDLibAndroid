// पथ: app/src/main/java/com/genius/tdlibandroid/MyApp.kt
package com.genius.tdlibandroid

import androidx.multidex.MultiDexApplication
import com.genius.tdlibandroid.data.TgCore
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp : MultiDexApplication() {

    @Inject
    lateinit var core: TgCore

    override fun onCreate() {
        super.onCreate()

        // MultiDex.install(this) // MultiDexApplication इसे अपने आप संभालता है।

        // System.loadLibrary("tdjni") // यह काम अब TDLib क्लाइंट के अंदर होना चाहिए।
        // इसे यहाँ से हटाने से कोड साफ़ रहता है।

        core.initialize()
    }
}