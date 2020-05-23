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
            .retry(
                Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(1))
                    .doBeforeRetry {
                        PLMPluginNotification.notify(
                            project,
                            "Addon connection lost, attempt to reconnect",
                            PluginIcons.WARNING
                        )
                    }
            )
        try {
            return RSocketConnector.create()
                .reconnect(Retry.fixedDelay(2L, Duration.ofSeconds(1)))
                .resume(resumeStrategy)
                .fragment(1024)
                .connect(TcpClientTransport.create(config.hostname, config.addonPort))
                .block() ?: throw Exception()
        } catch (e: Exception) {
            throw Exceptions.retryExhausted("Could not connect to server, 2 retries made", null)
        }

    }


}