package com.learnwords.koogservice

import com.learnwords.koogservice.config.KoogNativeHints
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints

@ImportRuntimeHints(KoogNativeHints::class)
@SpringBootApplication
class KoogServiceApplication

fun main(args: Array<String>) {
    runApplication<KoogServiceApplication>(*args)
}
