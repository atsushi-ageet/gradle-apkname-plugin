package com.ageet.gradle.plugin.apkname

/**
 * @property format format of apk name.
 */
open class ApkNameExtension {
    var format: ApkNameFormatter.()->CharSequence = { "${projectName}_${versionName}_${gitShortHash}" }
    var releaseOnly = true
}
