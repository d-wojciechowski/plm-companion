package pl.dwojciechowski.model

import reactor.core.Disposable
import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command
import reactor.core.Disposables
import java.time.LocalTime

data class CommandBean(
    var name: String,
    var command: String,
    var executionTime : LocalTime = LocalTime.MIN,
    var status: ExecutionStatus = ExecutionStatus.NONE,
    var response: ReplaySubject<String> = ReplaySubject.create(),
    var actualSubscription: Disposable = Disposables.never()
) {

    override fun toString(): String {
        return if (name.isNotEmpty()) name else command
    }

    fun getCommand(): Command {
        val split = command.split(' ', limit = 1)
        return Command.newBuilder()
            .setCommand(split[0])
            .setArgs(if (split.size > 1) split[1] else "")
            .build()
    }

    fun safeCopy() = CommandBean(name, command, LocalTime.now(), ExecutionStatus.NONE, ReplaySubject.create(), actualSubscription)

    enum class ExecutionStatus {
        RUNNING, STOPPED, COMPLETED, NONE
    }

}