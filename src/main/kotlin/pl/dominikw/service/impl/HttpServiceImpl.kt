package pl.dominikw.service.impl

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import pl.dominikw.model.ServerStatus
import pl.dominikw.service.HttpService

class HttpServiceImpl : HttpService {
    override fun getStatus(targetUrl: String, login: String, password: String): ServerStatus {
        return try {
            val (request, response, result) = targetUrl.httpGet()
                .timeout(2000)
                .authentication()
                .basic(login, password)
                .response()
             when (response.statusCode) {
                200 -> ServerStatus.RUNNING
                404, -1 -> ServerStatus.DOWN
                else -> ServerStatus.STARTING
            }
        } catch (e: Exception) {
            ServerStatus.DOWN
        }
    }

}