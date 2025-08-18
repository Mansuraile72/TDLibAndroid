// पथ: (रूट डायरेक्टरी)/build.gradle.kts

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // ⭐⭐⭐ यह लाइन सबसे महत्वपूर्ण है - इसे जोड़ा गया है ⭐⭐⭐
    alias(libs.plugins.hilt.android) apply false

}