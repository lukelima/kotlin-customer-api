package routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import models.orderStorage

fun Application.registerOrdersRoute() {
    routing {
        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()
    }
}

fun Route.listOrdersRoute() {
    get("/order") {
        if(orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        } else {
            call.respondText("No orders found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.getOrderRoute() {
    get("/order/{id}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Bad request",
            status = HttpStatusCode.BadRequest)
        val order = orderStorage.find {it.number == id} ?: return@get call.respondText(
            "No order found with id $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(order)
    }
}

fun Route.totalizeOrderRoute() {
    get("/order/{id}/total") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Bad request",
            status = HttpStatusCode.BadRequest)
        val order = orderStorage.find { it.number == id } ?: return@get call.respondText(
            "No order found with id $id",
            status = HttpStatusCode.NotFound
        )
        val total = order.contents.map { it.amount * it.price }.sum()
        call.respond(total)
    }
}