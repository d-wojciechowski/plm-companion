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
            .sessionDuration(Duration.ofMinutes(5))
            .retry(retryByConfig().doAfterRetry { retryAction(it) })
        try {
            return RSocketConnector.create()
                .reconnect(retryByConfig())
                .resume(resumeStrategy)
                .fragment(1024)
                .connect(TcpClientTransport.create(config.hostname, config.addonPort))
                .block() ?: throw Exception()
        } catch (e: Exception) {
            throw Exceptions.retryExhausted("Could not connect to server, 2 retries made", null)
        }

    }

    private fun retryByConfig(): RetryBackoffSpec {
        return Retry.fixedDelay(
            config.timeout / config.refreshRate.toLong(), Duration.ofMillis(config.refreshRate.toLong())
        )
    }

    private fun retryAction(it: Retry.RetrySignal) {
        if (it.failure() != null && it.totalRetriesInARow() % 10 == 0L) {
            PLMPluginNotification.notify(
                project, "Addon connection lost, attempt to reconnect", PluginIcons.WARNING
            )
        } else if (it.failure() == null) {
            PLMPluginNotification.notify(project, "Addon connection recovered.", PluginIcons.CONFIRMATION)
        }
    }

}