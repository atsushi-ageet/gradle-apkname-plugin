package com.ageet.gradle.plugin.apkname

open class ApkNameExtension {

    var format = "${projectName}_${versionName}_$gitShortHash"

    val projectName: String
        get() = "#{projectName}"
    val applicationId: String
        get() = "#{applicationId}"
    val versionCode: String
        get() = "#{versionCode}"
    val versionName: String
        get() = "#{versionName}"
    val variantName: String
        get() = "#{variantName}"
    val buildType: String
        get() = "#{buildType}"
    val gitShortHash: String
        get() = "#{gitShortHash}"

    companion object {
        internal const val NAME = "apkName"
    }
}
