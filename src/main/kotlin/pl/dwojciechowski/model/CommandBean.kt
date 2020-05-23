package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.ProjectManager
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.ui.PLMPluginNotification
import pl.dwojciechowski.ui.PluginIcons
import reactor.core.Disposable
import reactor.core.Disposables
import java.time.LocalTime
import javax.swing.Icon

data class CommandBean(
    var name: String,
    var command: String,
    var executionTime: LocalTime = LocalTime.now(),
    private var _status: ExecutionStatus = ExecutionStatus.NONE,
    var response: ReplaySubject<String> = ReplaySubject.create(),
    var actualSubscription: Disposable = Disposables.never()
) : Cloneable {

    var status: ExecutionStatus
        get() = _status
        set(value) {
            _status = value
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
        CommandBean(name, command, LocalTime.now(), ExecutionStatus.NONE, ReplaySubject.create(), Disposables.never())

    enum class ExecutionStatus(val icon: Icon, val format: String) {
        RUNNING(PluginIcons.RUNNING, "Command %s started."),
        STOPPED(PluginIcons.ERROR, "Command %s failed."),
        COMPLETED(PluginIcons.CONFIRMATION, "Command %s finished successfully"),
        NONE(AllIcons.Debugger.Db_muted_breakpoint, "Command %s created");

        fun getMessage(command: CommandBean) = format.replace("%s", command.toString())
    }

}