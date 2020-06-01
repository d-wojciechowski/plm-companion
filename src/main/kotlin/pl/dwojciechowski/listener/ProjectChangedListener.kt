package pl.dwojciechowski.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import pl.dwojciechowski.service.IdeControlService
import pl.dwojciechowski.service.NotificationService
import pl.dwojciechowski.service.RemoteService
import pl.dwojciechowski.service.StatusService

class ProjectChangedListener : ProjectManagerListener {


    override fun projectOpened(project: Project) {
        //Just init on project open
        ServiceManager.getService(project, NotificationService::class.java)
        ServiceManager.getService(project, RemoteService::class.java)
        ServiceManager.getService(project, StatusService::class.java)

        val ideService = ServiceManager.getService(project, IdeControlService::class.java)
        ApplicationManager.getApplication().invokeLater {
            Thread.sleep(500)
            //TODO INIT OF COMMAND PANE
            ideService.switchToCommandTab()
            ideService.withCommandTab { toolWindow, _, _ ->
                toolWindow.hide()
            }
        }
    }

}