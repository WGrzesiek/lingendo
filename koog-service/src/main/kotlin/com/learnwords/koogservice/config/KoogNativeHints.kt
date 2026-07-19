package com.learnwords.koogservice.config

import org.flywaydb.core.extensibility.Plugin
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import java.util.ServiceLoader

/**
 * Native reflection hints for Flyway plugins/config extensions.
 *
 * Flyway's PluginRegister.getCopy() reflectively invokes getters/setters on every
 * registered [Plugin] (including ConfigurationExtension implementations such as
 * PrepareScriptFilenameConfigurationExtension). Spring Boot's Flyway hints do not
 * cover all of these methods, so the native image throws MissingReflectionRegistrationError.
 *
 * We enumerate all Flyway plugins via ServiceLoader at AOT build time and register their
 * declared members for reflection — version-independent, no whack-a-mole per method.
 */
class KoogNativeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        val loader = classLoader ?: KoogNativeHints::class.java.classLoader
        ServiceLoader.load(Plugin::class.java, loader).forEach { plugin ->
            hints.reflection().registerType(
                plugin.javaClass,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS
            )
        }
    }
}
