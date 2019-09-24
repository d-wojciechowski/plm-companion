package pl.dwojciechowski.service.impl

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService

class HttpServiceImpl : HttpService {

    override fun getStatus(targetUrl: String, login: String, password: String): ServerStatus {
        return try {
            val response = targetUrl.httpGet()
                .timeout(2000)
                .authentication()
                .basic(login, password)
                .response().second
            when (response.statusCode) {
                200 -> ServerStatus.RUNNING
                else -> ServerStatus.DOWN
            }
        } catch (e: Exception) {
            ServerStatus.DOWN
        }
    }

}