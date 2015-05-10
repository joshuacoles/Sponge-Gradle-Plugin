package org.joshuacoles.gradle.sponge

import org.gradle.api.Project

class PluginConstants {
    private Project project

    final File BUILD_DIR = new File(project.buildDir, 'sponge')
    final File TEMP_SOURCES_DIR = new File(BUILD_DIR, 'sources')

    PluginConstants(Project project) {
        this.project = project
        this.metaClass.properties.each {
            if (it.type == File && it.name.contains('DIR')) {
                (it.getProperty(this) as File).mkdirs()
            }
        }
    }

    void remake() {
        clean()
        make()
    }

    void clean() {
        this.metaClass.properties.each {
            if (it.type == File && it.name.contains('DIR')) {
                (it.getProperty(this) as File).deleteDir()
            }
        }
    }

    void make() {
        this.metaClass.properties.each {
            if (it.type == File && it.name.contains('DIR')) {
                (it.getProperty(this) as File).mkdirs()
            }
        }
    }
}
