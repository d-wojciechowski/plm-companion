package pl.dwojciechowski.service.impl

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.httpGet
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.model.HttpStatusConfig
import pl.dwojciechowski.model.ServerStatus
import pl.dwojciechowski.service.StatusService

class StatusServiceImpl(project: Project) : StatusService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private val commandSubject = PublishSubject.create<ServerStatus>()

    init {
        GlobalScope.launch {
            while (true) {
                commandSubject.onNext(
                    if (config.scanWindchill) {
                        getStatus(HttpStatusConfig(config))
                    } else {
                        ServerStatus.NOT_SCANNING
                    }
                )

                delay(config.refreshRate.toLong())
            }
        }
    }

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
                503 -> ServerStatus.AVAILABLE
                else -> ServerStatus.DOWN
            }
        } catch (e: Exception) {
            ServerStatus.DOWN
        }

    override fun getOutputSubject(): Subject<ServerStatus> = commandSubject

}