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
    var format: ApkNameFormatter.()->CharSequence = { "${projectName}_${versionName}_${gitShortHash}" }
    var releaseOnly = true
}
