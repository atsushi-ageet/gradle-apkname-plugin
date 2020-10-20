package com.ageet.gradle.plugin.apkname

import com.android.build.gradle.api.ApplicationVariant

class ApkNameFormatter(val projectName: String, val variant: ApplicationVariant, val gitShortHash: String) {
    val applicationId: String get() = variant.mergedFlavor.applicationId.orEmpty()
    val versionCode: Int get() = variant.mergedFlavor.versionCode ?: 0
    val versionName: String get() = variant.mergedFlavor.versionName.orEmpty()
    val variantName: String get() = variant.name
    val flavorName: String get() = variant.flavorName
    val flavorNames: List<String> get() = variant.productFlavors.map { it.name }
    val buildType: String get() = variant.buildType.name
}
