package pl.dwojciechowski.model

import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command

data class CommandBean(
    var name: String,
    var command: String,
    var response : ReplaySubject<String> = ReplaySubject.create()
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

    fun safeCopy() = CommandBean(name, command, ReplaySubject.create())

}