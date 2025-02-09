package org.example.redistest

import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class ConcurrentTaskExecutor {
    fun runConcurrentTasks(numberOfThreads: Int, repeatCountPerThread: Int, task: () -> Unit) {
        val startLatch = CountDownLatch(1) // 모든 쓰레드가 준비될 때까지 대기시키기 위한 용도
        val doneLatch = CountDownLatch(numberOfThreads)

        val executor = Executors.newFixedThreadPool(numberOfThreads)

        repeat(numberOfThreads) {
            executor.submit() {
                try {
                    startLatch.await() // 모든 스레드가 준비될 때까지 대기
                    repeat(repeatCountPerThread) {
                        task()
                    }
                } finally {
                    doneLatch.countDown() // 작업 완료 처리
                }
            }
        }

        startLatch.countDown() // 모든 스레드가 동시에 작업을 시작하도록 함
        doneLatch.await() // 모든 스레드가 작업을 마칠 때까지 대기

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.MINUTES)
    }
}