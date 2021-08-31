import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.Customer
import models.customerStorage

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}

fun Route.customerRouting(){
    route("/customer") {
        get {
            if(customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respondText("No costumers found", status=HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val customer = customerStorage.find { it -> it.id == id } ?: return@get call.respondText(
                "No customer found with id $id",
                status = HttpStatusCode.NotFound
            )
            call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status=HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if(customerStorage.removeIf {it -> it.id == id}) {
                call.respondText("Customer removed correctly", status=HttpStatusCode.Accepted)
            } else {
                call.respondText("Customer not found", status=HttpStatusCode.NotFound)
            }
        }
    }
}