package org.joshuacoles.gradle.sponge

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.joshuacoles.gradle.common.tasks.SourceCopyTask

class SpongePlugin implements Plugin<Project> {
    @Delegate
    PluginConstants constants

    void apply(Project project) {
        this.constants = new PluginConstants(project)
        constants.remake()

        project.repositories.maven {
            url 'http://repo.spongepowered.org/maven'
        }

        project.repositories.mavenCentral()

        project.extensions.create("sponge", SpongeExtension)
        project.extensions.create("plugin", SpongePluginExtension)

        project.dependencies {
            compile "org.spongepowered:spongeapi:${extension(project, SpongeExtension).version}"
        }

        replaceSourceTokens(project)

        project.configurations {
            plguin_before
            plguin_required_before
            plguin_after
            plguin_required_after
        }

        project.task('spongeLicense') {
            def l = new File('LICENSE')
            l.createNewFile()
            l.text = 'https://raw.githubusercontent.com/SpongePowered/SpongeAPI/master/LICENSE.txt'.toURL().text
        }
    }

    private static <T> T extension(Project project, Class<T> c) {
        project.extensions.getByType(c) as T
    }

    private final void replaceSourceTokens(Project project) {
        SourceSet main = (project.convention.plugins.get("java") as JavaPluginConvention)
                .sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        File dir = projectFile TEMP_SOURCES_DIR, project

        SourceCopyTask task = makeTask project, "replaceSourceTokens", SourceCopyTask
        task.source = main.java
        task.output = dir

        JavaCompile compile = (JavaCompile) project.tasks.getByName(main.getCompileJavaTaskName());
        compile.dependsOn "replaceSourceTokens"
        compile.source = dir

        (project.getTasksByName('replaceSourceTokens', false)[0] as SourceCopyTask)
                .replace(['@[plugin.version]': project.version,
                          '@[plugin.id]'     : extension(project, SpongePluginExtension).id,
                          '@[plugin.name]'   : extension(project, SpongePluginExtension).name])
    }

    private static File projectFile(String path, Project project) {
        return project.file(path)
    }

    private static File projectFile(File path, Project project) {
        return project.file(path.canonicalPath)
    }

    public static <T extends Task> T makeTask(Project proj, String name, Class<T> type) {
        return (T) proj.task([name: name, type: type], name);
    }

    /*
    * todo
    *
    * plugin dependency config for thingy so you can add plugins from some gradle/file loc and add it too dependency string
    * different language support w/ auto add lang-x plugin to dependencies
    * run for diff impl
    * gen default plugin setup
    * */
}
