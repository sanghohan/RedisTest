package org.example.redistest

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisServer
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@TestConfiguration
class TestRedisConfiguration {

    private val testRedisHost = "localhost"
    private val testRedisPort = 6379

    companion object {
        const val DEFAULT_DATABASE = 0
    }


    fun lettuceConnectionFactory(database: Int): LettuceConnectionFactory {
        val redisConfig = RedisStandaloneConfiguration().apply {
            this.hostName = testRedisHost
            this.port = testRedisPort
            this.database = database
        }

        return LettuceConnectionFactory(redisConfig).apply {
            this.afterPropertiesSet()
        }
    }

    @Bean
    fun testStringRedisTemplate(): StringRedisTemplate {
        return StringRedisTemplate().apply {
            this.setConnectionFactory(lettuceConnectionFactory(DEFAULT_DATABASE))
            this.afterPropertiesSet()
        }
    }
}