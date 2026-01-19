package com.learnwords.koogservice

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<KoogServiceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
