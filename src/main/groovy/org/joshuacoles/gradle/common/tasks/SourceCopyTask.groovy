package org.joshuacoles.gradle.common.tasks

import com.google.common.base.Charsets
import com.google.common.io.Files
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryTree
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet

import java.util.function.Supplier
import java.util.regex.Pattern

class SourceCopyTask extends DefaultTask {
    @InputFiles
    SourceDirectorySet source;

    @Input
    HashMap<String, Object> replacements = new HashMap<String, Object>();

    @Input
    ArrayList<String> includes = new ArrayList<String>();

    @OutputDirectory
    File output;

    @TaskAction
    public void doTask() throws IOException {
        logger.debug("INPUTS >> " + source);
        logger.debug("OUTPUTS >> " + this.output);

        // get the include/exclude patterns from the source (this is different than what's returned by getFilter)
        PatternSet patterns = new PatternSet()

        patterns.includes = source.includes
        patterns.excludes = source.excludes

        // get output
        File output = this.output
        project.delete(output)
        project.mkdir(output)
        output = output.canonicalFile

        // resolve replacements
        Map<String, String> repl = (replacements.findAll { it.key != null && it.value != null })
                .map({ k, _ -> Pattern.quote(k) }, { _, v -> v.toString() })

        logger.debug("REPLACE >> " + repl);

        // start traversing tree
        source.srcDirTrees.findAll { it.dir.exists() && it.dir.directory }
                .map({ it.dir }, { project.fileTree(it).matching(source.filter).matching(patterns) })
                .each
                { entry ->
                    entry.value.each { File file ->
                        File dest = getDest(file, entry.key, output)
                        dest.parentFile.mkdirs()
                        dest.createNewFile()
                        if (isIncluded(file)) {
                            logger.debug("PARSING FILE IN >> $file")
                            repl.each { r -> file.text.replaceAll(r.key, r.value) }
                        }
                    }
                }
    }

    private traverseCallable(Object o) {
        if (o instanceof Supplier) traverseCallable(o())
        else if (o instanceof Closure) {
            if (o.maximumNumberOfParameters == 0) traverseCallable(o())
            else o
        }
    }

    private static File getDest(File input, File base, File baseOut) throws IOException {
        String relative = input.getCanonicalPath().replace(base.getCanonicalPath(), "");
        return new File(baseOut, relative);
    }

    private boolean isIncluded(File file) throws IOException {
        if (includes.isEmpty()) return true;

        String path = file.getCanonicalPath().replace('\\', '/');
        for (String include : includes) {
            if (path.endsWith(include.replace('\\', '/'))) return true;
        }

        return false;
    }

    public void replace(String key, Object val) { replacements.put(key, val); }

    public void replace(Map<String, Object> map) { replacements.putAll(map); }

    public HashMap<String, Object> getReplacements() { return replacements; }

    public void include(String str) { includes.add(str); }

    public void include(List<String> strs) { includes.addAll(strs); }
}