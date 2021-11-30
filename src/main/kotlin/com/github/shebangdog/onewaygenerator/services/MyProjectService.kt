package com.github.shebangdog.onewaygenerator.services

import com.intellij.openapi.project.Project
import com.github.shebangdog.onewaygenerator.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
