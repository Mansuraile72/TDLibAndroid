package com.genius.tdlibandroid // यह सुनिश्चित करता है कि पैकेज का नाम सही है

data class Alarm(
    val id: Int,
    val time: String,
    val note: String,
    var isEnabled: Boolean
)