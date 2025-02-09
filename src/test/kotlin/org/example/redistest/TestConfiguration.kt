package org.example.redistest

import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisServer
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.redisson.config.Config
import org.redisson.Redisson

@TestConfiguration
class TestRedisConfiguration {

    private val testRedisHost = "localhost"
    private val testRedisPort = 6379

    companion object {
        const val DEFAULT_DATABASE = 0
    }

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            useSingleServer().address = "redis://localhost:6379"
        }
        return Redisson.create(config)
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