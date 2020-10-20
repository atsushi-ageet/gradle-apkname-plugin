package com.ageet.gradle.plugin.apkname

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.tasks.FinalizeBundleTask
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

    fun Project.onEvaluate() {
        if (!hasAppPlugin) throw RuntimeException("This project is not android project")
        apkNameExtension = android.extensions.create(EXTENSION_NAME, ApkNameExtension::class.java)
        android.applicationVariants.all { variant ->
            if (!apkNameExtension.releaseOnly || variant.buildType.name == "release") {
                generateApkNameFrom(variant)
            }
        }
    }

    private fun Project.generateApkNameFrom(variant: ApplicationVariant) {
        logger.info("Generate apk name variant = ${variant.name}, format = ${apkNameExtension.format}")
        val formatter = ApkNameFormatter(name, variant, gitShortHash)
        val format = apkNameExtension.format
        val suffix = if (variant.isSigningReady) "" else "-unsigned"
        val baseName = formatter.format()
        val apkName = "$baseName$suffix.apk"
        variant.outputs.mapNotNull { it as? ApkVariantOutput }.forEach {
            it.outputFileName = apkName
        }
        logger.info("apkName = $apkName")

        afterEvaluate {
            val task = tasks.getByName("sign${variant.name.capitalize()}Bundle") as FinalizeBundleTask
            val bundleDir = task.finalBundleFile.get().asFile.parent
            val bundleFile = File(bundleDir, "$baseName.aab")
            task.finalBundleFile.set(bundleFile)
            logger.info("bundleFile = $bundleFile")
        }
    }

    private fun Project.openGit(gitDir: File): Git = try {
        Git.open(gitDir)
    } catch (e: RepositoryNotFoundException) {
        gitDir.parentFile?.let{ openGit(it) } ?: throw RepositoryNotFoundException(projectDir)
    }

    private val Project.android: BaseAppModuleExtension
        get() = extensions.getByName("android") as BaseAppModuleExtension

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
