package org.example.redistest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.test.Test

@SpringBootTest(classes = [TestRedisConfiguration::class])
class RedisConcurrencyTest(

) {
    @Autowired
    private lateinit var stringRedisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var concurrentTaskExecutor: ConcurrentTaskExecutor

    @Autowired
    private lateinit var redissonClient: RedissonClient

    private val numberOfThreads = 10
    private val repeatCountPerThread = 1000

    @Test
    fun `증감 연산자만을 사용하면 동시성 이슈가 발생한다`() {
        // given
        var count = 0

        // when
        concurrentTaskExecutor.runConcurrentTasks(numberOfThreads, repeatCountPerThread) {
            count++
        }

        // then
        count shouldNotBe numberOfThreads * repeatCountPerThread
        println(count)
    }

    @Test
    fun `Redisson을 사용하면?`() {

        // given
        var count = 0

        // when
        concurrentTaskExecutor.runConcurrentTasks(numberOfThreads, repeatCountPerThread) {

            val lock: RLock = redissonClient.getLock("count_lock")
            val isLocked: Boolean = try {
                // waitTime: 락을 기다리는 최대 시간
                // leaseTime: 락이 자동으로 해제되기까지의 시간
                lock.tryLock(1, 2, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw RuntimeException("Lock 획득 중 인터럽트 발생", e)
            }

            if (isLocked) {
                try {
                    count++

                } finally {
                    lock.unlock()
                }
            } else {
                throw RuntimeException("Lock 획득 실패")
            }
        }

        println("count = $count")

        // then
        count shouldNotBe numberOfThreads * repeatCountPerThread

    }

    @Test
    fun `레디스 increment를 사용하면 동시성 이슈가 발생하지 않는다`() {
        // given
        val incrTestKey = "incr-test-key"
        stringRedisTemplate.delete(incrTestKey)

        // when
        concurrentTaskExecutor.runConcurrentTasks(numberOfThreads, repeatCountPerThread) {
            stringRedisTemplate.opsForValue().increment(incrTestKey)
        }

        // then
        val count = stringRedisTemplate.opsForValue().get(incrTestKey)!!.toInt()

        println("count = $count")
        count shouldBe numberOfThreads * repeatCountPerThread
    }

    @Test
    fun `레디스 decrement를 사용하면 동시성 이슈가 발생하지 않는다`() {
        // given
        val decrTestKey = "decr-test-key"
        stringRedisTemplate.delete(decrTestKey)

        // when
        concurrentTaskExecutor.runConcurrentTasks(numberOfThreads, repeatCountPerThread) {
            stringRedisTemplate.opsForValue().decrement(decrTestKey)
        }

        // then
        val count = stringRedisTemplate.opsForValue().get(decrTestKey)!!.toInt()

        println("count = $count")
        count shouldBe -(numberOfThreads * repeatCountPerThread)
    }

    @Test
    fun `레디스 incr와 decr를 사용하여 로직을 구현하면 동시성 이슈가 발생하지 않는다`() {
        // given
        val incrDecrTestKey = "incr-decr-test-key"
        val maxCount = 100
        stringRedisTemplate.delete(incrDecrTestKey)

        // when
        concurrentTaskExecutor.runConcurrentTasks(numberOfThreads, repeatCountPerThread) {
            val successIncrement = incrementIfPossible(incrDecrTestKey, maxCount) // 실제 사용할 로직과 동일한 구조로 실행

            if (successIncrement) {
                Thread.sleep(Random.nextLong(0, 300)) // 0 ~ 300ms 사이의 랜덤한 시간을 멈춤. 실제 로직이 들어갈 자리
            }
        }

        // then
        val currentCount = stringRedisTemplate.opsForValue().get(incrDecrTestKey)!!.toInt()
        println("count = $currentCount")
        currentCount shouldBe maxCount
    }

    private fun incrementIfPossible(key: String, maxCount: Int): Boolean {
        val incrementedCount = stringRedisTemplate.opsForValue().increment(key)!!.toInt()

        if (incrementedCount > maxCount) {
            decrementCount(key)
            return false
        }

        return true
    }

    private fun decrementCount(key: String) {
        stringRedisTemplate.opsForValue().decrement(key)
    }
}