package pl.dwojciechowski.model

import com.intellij.openapi.project.ProjectManager
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.ui.PLMPluginNotification
import reactor.core.Disposable
import reactor.core.Disposables
import java.time.LocalTime

data class CommandBean(
    var name: String,
    var command: String,
    var executionTime: LocalTime = LocalTime.now(),
    var response: ReplaySubject<String> = ReplaySubject.create(),
    var actualSubscription: Disposable = Disposables.never()
) : Cloneable {

    var status: ExecutionStatus = ExecutionStatus.NONE
        set(value) {
            field = value
            subscription.onNext(this)
        }

    private val subscription = PublishSubject.create<CommandBean>()

    init {
        subscription.subscribe {
            ProjectManager.getInstance().openProjects.forEach { project ->
                PLMPluginNotification.notify(project, it.status.getMessage(it), status.icon)
            }
        }
    }

    override fun toString(): String {
        return if (name.isNotEmpty()) name else command
    }

    fun getCommand(): Command {
        return Command.newBuilder()
            .setCommand(command)
            .build()
    }

    public override fun clone() =
        CommandBean(name, command, LocalTime.now(), ReplaySubject.create(), Disposables.never())

}