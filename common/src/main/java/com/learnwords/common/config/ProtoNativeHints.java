package com.learnwords.common.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/**
 * Global native reflection hints for every generated gRPC / protobuf class.
 *
 * <p>Registered via {@code META-INF/spring/aot.factories}, so every service that depends
 * on {@code common} picks it up automatically at AOT build time — no per-service wiring.
 *
 * <p>Protobuf message classes (and their {@code $Builder}), enums, and the {@code *Grpc}
 * stub / service classes are accessed reflectively during gRPC marshalling and net.devh
 * stub creation. Under GraalVM native image these reflective entry points must be declared,
 * otherwise calls fail with missing constructor/method errors or produce null stubs.
 *
 * <p>All generated proto classes live under {@code com.learnwords.<domain>.v1}; we scan that
 * namespace on the classpath and register the relevant members for each class.
 */
public class ProtoNativeHints implements RuntimeHintsRegistrar {

    private static final String PROTO_CLASS_PATTERN = "classpath*:com/learnwords/**/v1/**/*.class";

    private static final MemberCategory[] CATEGORIES = {
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.DECLARED_FIELDS
    };

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ClassLoader loader = classLoader != null ? classLoader : getClass().getClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        CachingMetadataReaderFactory readers = new CachingMetadataReaderFactory(loader);
        try {
            Resource[] resources = resolver.getResources(PROTO_CLASS_PATTERN);
            for (Resource res : resources) {
                try {
                    String className = readers.getMetadataReader(res).getClassMetadata().getClassName();
                    hints.reflection().registerTypeIfPresent(loader, className, CATEGORIES);
                } catch (Throwable ignored) {
                    // Skip entries that cannot be read/resolved.
                }
            }
        } catch (Exception ignored) {
            // No proto classes on the classpath — nothing to register.
        }
    }
}
