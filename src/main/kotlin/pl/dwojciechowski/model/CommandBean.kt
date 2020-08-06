package pl.dwojciechowski.model

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.ProjectManager
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import pl.dwojciechowski.proto.commands.Command
import pl.dwojciechowski.ui.PLMPluginNotification
import reactor.core.Disposable
import reactor.core.Disposables
import java.time.LocalTime
import javax.swing.Icon

data class CommandBean(
    var name: String,
    var command: String,
    var type: Type = Type.COMMAND,
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
        CommandBean(name, command, type, LocalTime.now(), ReplaySubject.create(), Disposables.never())

    enum class Type(val icon: Icon) {
        COMMAND(AllIcons.Xml.Css_class), PROPERTY_NAME(AllIcons.Nodes.Property)
    }

}