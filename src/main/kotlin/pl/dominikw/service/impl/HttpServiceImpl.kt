package pl.dominikw.service.impl

import com.github.kittinunf.fuel.httpGet
import pl.dominikw.model.ServerStatus
import pl.dominikw.service.HttpService

class HttpServiceImpl : HttpService {

    override fun getStatus(targetUrl: String): ServerStatus {
        return try {
            val (request, response, result) = targetUrl.httpGet().response()
            when (response.statusCode) {
                200 -> ServerStatus.RUNNING
                404 -> ServerStatus.DOWN
                else -> ServerStatus.STARTING
            }
        } catch (e: Exception) {
            ServerStatus.DOWN
        }
    }

}