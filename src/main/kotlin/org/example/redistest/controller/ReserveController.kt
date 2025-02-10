package org.example.redistest.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class ReserveController {

    @GetMapping("/api/test/reserves")
    fun getReserves() {

        logger.debug { "degbug test" }

        throw Exception("error throw!!")

    }

}