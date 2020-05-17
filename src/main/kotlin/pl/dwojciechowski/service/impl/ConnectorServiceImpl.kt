package pl.dwojciechowski.service.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import io.rsocket.RSocket
import io.rsocket.core.RSocketConnector
import io.rsocket.transport.netty.client.TcpClientTransport
import pl.dwojciechowski.configuration.PluginConfiguration
import pl.dwojciechowski.service.ConnectorService

class ConnectorServiceImpl(project: Project) : ConnectorService {

    private val config = ServiceManager.getService(project, PluginConfiguration::class.java)

    override fun establishConnection(): RSocket? {
        return RSocketConnector.create()
            .fragment(1024)
            .connect(TcpClientTransport.create(config.hostname, config.addonPort))
            .block()
    }

}