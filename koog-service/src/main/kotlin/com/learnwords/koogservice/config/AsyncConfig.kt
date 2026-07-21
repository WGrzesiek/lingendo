package com.learnwords.koogservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

/**
 * Asynchroniczne przetwarzanie generowania zdań AI.
 *
 * Listener Kafka MUSI wracać natychmiast (commit offsetu). Właściwa praca (wywołania OpenAI,
 * minuty dla dużej talii) leci tutaj, poza wątkiem konsumenta — dzięki temu czas przetwarzania
 * nigdy nie przekracza max.poll.interval.ms i Kafka nie robi rebalance/redelivery (co wcześniej
 * generowało tysiące zduplikowanych jobów).
 */
@Configuration
@EnableAsync
class AsyncConfig {

    @Bean("koogTaskExecutor")
    fun koogTaskExecutor(): TaskExecutor = ThreadPoolTaskExecutor().apply {
        corePoolSize = 1
        maxPoolSize = 2
        queueCapacity = 100
        setThreadNamePrefix("koog-ai-")
        initialize()
    }
}
