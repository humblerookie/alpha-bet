package com.github.humblerookie.alphabet.services

import com.github.humblerookie.alphabet.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
