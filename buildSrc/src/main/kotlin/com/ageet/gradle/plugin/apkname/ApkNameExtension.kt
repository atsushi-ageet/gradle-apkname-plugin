package com.ageet.gradle.plugin.apkname

/**
 * @property format format of apk name.
 * @property projectName gradle project name.
 * @property applicationId android applicationId.
 * @property versionCode android versionCode.
 * @property versionName android versionName.
 * @property variantName android variantName.
 * @property buildType android buildType.
 * @property gitShortHash git short commit hash.
 */
open class ApkNameExtension {

    var format = "${projectName}_${versionName}_$gitShortHash"
    var releaseOnly = true

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
    val flavorName: String
        get() = "#{flavorName}"
    val buildType: String
        get() = "#{buildType}"
    val gitShortHash: String
        get() = "#{gitShortHash}"
}
