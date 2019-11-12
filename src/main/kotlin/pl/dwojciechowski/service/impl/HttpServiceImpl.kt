package pl.dwojciechowski.service.impl

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.HttpService

class HttpServiceImpl : HttpService {

    override fun getStatus(config: HttpStatusConfig): ServerStatus =
        try {
            val response = config.url.httpGet()
                .timeout(config.timeout)
                .authentication()
                .basic(config.login, config.password)
                .response().second
            when (response.statusCode) {
                200 -> ServerStatus.RUNNING
                401 -> ServerStatus.UNAUTHORIZED
                503 -> ServerStatus.REACHABLE
                else -> ServerStatus.DOWN
            }
        } catch (e: Exception) {
            ServerStatus.DOWN
        }

}