package org.example.redistest.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ReserveController {

    @GetMapping("/api/test/reserves")
    fun getReserves() {

    }

}