package com.learnwords.koogservice.config

import org.flywaydb.core.extensibility.Plugin
import org.springframework.aot.hint.BindingReflectionHintsRegistrar
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.classreading.CachingMetadataReaderFactory
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

    private val binding = BindingReflectionHintsRegistrar()

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        val loader = classLoader ?: KoogNativeHints::class.java.classLoader
        ServiceLoader.load(Plugin::class.java, loader).forEach { plugin ->
            val type = plugin.javaClass
            // Flyway invokes plugin getters/setters reflectively when copying config...
            hints.reflection().registerType(
                type,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.DECLARED_FIELDS
            )
            // ...and ConfigurationExtension.copy() serializes the whole extension graph with
            // Jackson, so register every reachable nested model (e.g. TransactionalModel) for binding.
            binding.registerReflectionHints(hints.reflection(), type)
        }

        // Hibernate resolves these named strategy classes reflectively via their no-arg
        // constructor (StrategySelectorImpl). The native image lacks metadata for some of
        // them under Spring Boot 4 / Hibernate 7 (e.g. ColumnOrderingStrategyStandard).
        listOf(
            "org.hibernate.boot.model.relational.ColumnOrderingStrategyStandard",
            "org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl",
            "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl",
            "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl",
            "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
        ).forEach { fqn ->
            hints.reflection().registerTypeIfPresent(
                loader, fqn,
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
            )
        }

        // JBoss Logging typed loggers: Hibernate calls Logger.getMessageLogger(X.class) which
        // does Class.forName("X_$logger"). Native strips those generated impls unless registered.
        // Scan every Hibernate *_$logger / *_$bundle impl and register their constructors at once.
        val resolver = PathMatchingResourcePatternResolver(loader)
        val readerFactory = CachingMetadataReaderFactory(loader)
        listOf(
            "classpath*:org/hibernate/**/*_\$logger.class",
            "classpath*:org/hibernate/**/*_\$bundle.class",
            // PostgreSQLDialect.contributePostgreSQLTypes loads these via Class.forName
            // (PgJdbcHelper): PostgreSQLInetJdbcType, ...IntervalSecondJdbcType, ...StructJdbcType, etc.
            "classpath*:org/hibernate/dialect/type/**/*.class",
            // Hibernate 7 hibernate-models: OrmAnnotationDescriptor$DynamicCreator reflectively
            // instantiates every *Annotation wrapper via its (ModelsContext) constructor.
            "classpath*:org/hibernate/boot/models/annotations/**/*.class",
            // DialectOverridesAnnotationHelper introspects org.hibernate.annotations.* (and the
            // nested DialectOverride.SQLInsert/... forms) reflectively to build the override map.
            "classpath*:org/hibernate/annotations/**/*.class"
        ).forEach { pattern ->
            runCatching { resolver.getResources(pattern) }.getOrDefault(emptyArray()).forEach { res ->
                runCatching {
                    val className = readerFactory.getMetadataReader(res).classMetadata.className
                    hints.reflection().registerTypeIfPresent(
                        loader, className,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_METHODS
                    )
                }
            }
        }

        // DialectOverridesAnnotationHelper enumerates DialectOverride's nested annotation
        // classes via getDeclaredClasses(); register that reflective access explicitly.
        hints.reflection().registerTypeIfPresent(
            loader, "org.hibernate.annotations.DialectOverride",
            MemberCategory.DECLARED_CLASSES,
            MemberCategory.INVOKE_DECLARED_METHODS
        )
    }
}
