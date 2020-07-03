package com.ageet.gradle.plugin.apkname

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer
import java.io.File

class ApkNamePlugin : Plugin<Project> {

    private lateinit var apkNameExtension: ApkNameExtension

    override fun apply(project: Project?) {
        project?.onEvaluate()
    }

    private fun Project.onEvaluate() {
        afterEvaluate { onAfterEvaluate() }
        apkNameExtension = android.extensions.create(EXTENSION_NAME, ApkNameExtension::class.java)
    }

    private fun Project.onAfterEvaluate() {
        if ( !hasAppPlugin ) throw RuntimeException("This project is not android project")
        android.applicationVariants.filter { variant ->
            !apkNameExtension.releaseOnly || variant.buildType.name == "release"
        }.forEach { variant->
            generateApkNameFrom(variant)
        }
    }

    private fun Project.generateApkNameFrom(variant: ApplicationVariant) {
        logger.info("Generate apk name variant = ${variant.name}, format = ${apkNameExtension.format}")
        val format = apkNameExtension.format
        val suffix = if (variant.isSigningReady) "" else "-unsigned"
        val apkName = """#\{\w+}""".toRegex().replace(format) { a ->
            when (a.value) {
                apkNameExtension.projectName -> name
                apkNameExtension.applicationId -> variant.mergedFlavor.applicationId
                apkNameExtension.versionCode -> variant.mergedFlavor.versionCode.toString()
                apkNameExtension.versionName -> variant.mergedFlavor.versionName
                apkNameExtension.variantName -> variant.name
                apkNameExtension.flavorName -> variant.flavorName
                apkNameExtension.buildType -> variant.buildType.name
                apkNameExtension.gitShortHash -> gitShortHash
                else -> ""
            }
        } + "$suffix.apk"
        variant.outputs.mapNotNull { it as? ApkVariantOutput }.forEach {
            it.outputFileName = apkName
        }
        logger.info("apkName = $apkName")
    }

    private fun Project.openGit(gitDir: File): Git = try {
        Git.open(gitDir)
    } catch (e: RepositoryNotFoundException) {
        gitDir.parentFile?.let{ openGit(it) } ?: throw RepositoryNotFoundException(projectDir)
    }

    private val Project.android: AppExtension
        get() = extensions.getByName("android") as AppExtension

    private val Project.hasAppPlugin: Boolean
        get() = plugins.hasPlugin("com.android.application")

    private val Project.gitShortHash: String
        get() = openGit(projectDir).shortHash

    private val Git.shortHash: String
        get() = repository.exactRef("HEAD").objectId.name.substring(0, GIT_SHORT_HASH_LENGTH)

    private val AppExtension.extensions: ExtensionContainer
        get() = (this as ExtensionAware).extensions

    private companion object {
        private const val GIT_SHORT_HASH_LENGTH = 7
        private const val EXTENSION_NAME = "apkName"
    }
}
