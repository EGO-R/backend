package org.mirea.backend.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
//class CoroutinesConfig {
//
//    @Bean(destroyMethod = "cancel")
//    fun applicationScope(): CoroutineScope =
//        CoroutineScope(SupervisorJob() + Dispatchers.Default)
//
//}
