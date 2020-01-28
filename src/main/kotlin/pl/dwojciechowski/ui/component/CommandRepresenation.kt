package pl.dwojciechowski.ui.component

import pl.dwojciechowski.proto.commands.Command

data class CommandRepresenation(
    var name: String,
    var command: String
) {
    override fun toString(): String {
//        return if (name.isNotEmpty()) name else command
        return command
    }

    fun getCommand(): Command {
        val split = command.split(' ', limit = 1)
        return Command.newBuilder()
            .setCommand(split[0])
            .setArgs(if (split.size > 1) split[1] else "")
            .build()
    }
}