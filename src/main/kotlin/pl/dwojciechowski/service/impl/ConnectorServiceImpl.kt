package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocket
import io.rsocket.core.RSocketConnector
import io.rsocket.core.Resume
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.ConnectorService
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import reactor.core.Exceptions
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.time.Duration
import javax.swing.Icon

class ConnectorServiceImpl(private val project: Project) : ConnectorService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)
    private lateinit var rSocket: RSocket

    override fun getConnection(): RSocket {
        if (!this::rSocket.isInitialized || rSocket.isDisposed) {
            rSocket = establishConnection()
        }
        return rSocket
    }

    private fun establishConnection(): RSocket {
        val resumeStrategy = Resume()
            .sessionDuration(Duration.ofHours(1))
            .retry(retryByConfig("Connection lost", PluginIcons.WARNING))
        try {
            return RSocketConnector.create()
                .reconnect(retryByConfig("Could not connect to server", PluginIcons.WARNING))
                .resume(resumeStrategy)
                .fragment(1024)
                .connect(TcpClientTransport.create(config.hostname, config.addonPort))
                .block() ?: throw Exception()
        } catch (e: Exception) {
            throw Exceptions.retryExhausted("Could not connect to server, ${maxAttempts()} retries made", null)
        }

    }

    private fun retryByConfig(message: String, icon: Icon): RetryBackoffSpec {
        return Retry.fixedDelay(maxAttempts(), Duration.ofMillis(config.refreshRate.toLong()))
            .doBeforeRetry {
                notification(message, it, icon)
            }
    }

    private fun maxAttempts() = config.timeout / config.refreshRate.toLong()

    private fun notification(message: String, it: Retry.RetrySignal, icon: Icon) {
        PLMPluginNotification.notify(
            project, "$message, retry number ${it.totalRetriesInARow() + 1}", icon
        )
    }

}
