package pl.dwojciechowski.run.state

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.components.ServiceManager
import pl.dwojciechowski.service.RemoteService

class RemoteCommandState(private val environment: ExecutionEnvironment) : RunProfileState {

    private val remoteServiceManager = ServiceManager.getService(environment.project, RemoteService::class.java)

    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        TODO("Not yet implemented")
    }

}