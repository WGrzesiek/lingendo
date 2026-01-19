package com.learnwords.koogservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KoogServiceApplication

fun main(args: Array<String>) {
    runApplication<KoogServiceApplication>(*args)
}
