package pl.dwojciechowski.model

import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command
import reactor.core.Disposable
import reactor.core.Disposables
import java.time.LocalTime

data class CommandBean(
    var name: String,
    var command: String,
    var executionTime: LocalTime = LocalTime.MIN,
    var status: ExecutionStatus = ExecutionStatus.NONE,
    var response: ReplaySubject<String> = ReplaySubject.create(),
    var actualSubscription: Disposable = Disposables.never()
) {

    override fun toString(): String {
        return if (name.isNotEmpty()) name else command
    }

    fun getCommand(): Command {
        return Command.newBuilder()
            .setCommand(command)
            .build()
    }

    fun safeCopy() =
        CommandBean(name, command, LocalTime.now(), ExecutionStatus.NONE, ReplaySubject.create(), Disposables.never())

    enum class ExecutionStatus {
        RUNNING, STOPPED, COMPLETED, NONE
    }

}