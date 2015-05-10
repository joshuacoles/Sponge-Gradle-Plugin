package org.joshuacoles.gradle.common

import org.apache.commons.io.IOUtils
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.UncheckedIOException
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.file.RelativePath
import org.gradle.api.internal.file.collections.MinimalFileTree

import java.util.concurrent.atomic.AtomicBoolean
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import static org.gradle.util.SingleMessageLogger.nagUserOfDeprecatedBehaviour as nag

class ZipFileTree implements MinimalFileTree {
    private final File zipFile

    public ZipFileTree(File zipFile) {
        this.zipFile = zipFile
    }

    public String getDisplayName() { return "ZIP '$zipFile.name'" }

    public void visit(FileVisitor visitor) {
        if (!zipFile.exists()) {
            nag "The specified zip file $displayName does not exist and will be silently ignored"
            return
        }

        if (!zipFile.isFile()) throw new InvalidUserDataException("Cannot expand $displayName as it is not a file.")

        AtomicBoolean stopFlag = new AtomicBoolean()

        try {
            ZipFile zip = new ZipFile(zipFile)
            zip.withCloseable {
                (zip.entries().asList().map({ it.name }, { it }))
                        .values()
                        .iterator()
                        .forEachRemaining
                        { ZipEntry entry ->
                            if (!stopFlag.get()) {
                                entry.directory ?
                                        visitor.visitDir(new DetailsImpl(entry, zip, stopFlag)) :
                                        visitor.visitFile(new DetailsImpl(entry, zip, stopFlag))
                            }
                        }
            }
        } catch (Exception e) {
            throw new GradleException("Could not expand $displayName.", e)
        }
    }

    private class DetailsImpl implements FileVisitDetails {
        private final ZipEntry entry
        private final ZipFile zip
        private final AtomicBoolean stopFlag
        private File file

        public DetailsImpl(ZipEntry entry, ZipFile zip, AtomicBoolean stopFlag) {
            this.entry = entry
            this.zip = zip
            this.stopFlag = stopFlag
        }

        public String getDisplayName() { return String.format("zip entry %s!%s", zipFile, entry.getName()) }

        public void stopVisiting() { stopFlag.set(true) }

        /**
         * Changed this to return a broken value! Be warned! Will not be a valid file, do not read it.
         * Standard Jar/Zip tasks don't care about this, even though they call it.
         */
        public File getFile() {
            if (file == null) file = new File(entry.name)
            return file
        }

        public long getLastModified() { return entry.time }

        public boolean isDirectory() { return entry.directory }

        public long getSize() { return entry.size }

        public InputStream open() {
            try {
                return zip.getInputStream(entry)
            } catch (IOException e) {
                throw new UncheckedIOException(e)
            }
        }

        public RelativePath getRelativePath() { new RelativePath(!entry.directory, entry.name.split("/")) }

        public String toString() { return getDisplayName() }

        public String getName() { return getRelativePath().getLastName() }

        public String getPath() { return getRelativePath().getPathString() }

        public void copyTo(OutputStream outstr) {
            try {
                open().withCloseable(IOUtils.&copy.rcurry(outstr))
            } catch (IOException e) {
                throw new UncheckedIOException(e)
            }
        }

        public boolean copyTo(File target) {
            validateTimeStamps()
            try {
                if (isDirectory()) target.mkdirs()
                else {
                    target.getParentFile().mkdirs()
                    copyFile(target)
                }
                return true
            } catch (Exception e) {
                throw new GradleException("Could not copy $displayName to '$target'.", e)
            }
        }

        private void validateTimeStamps() {
            if (lastModified < 0L) throw new GradleException("Invalid Timestamp ${lastModified} for '$displayName'.")
        }

        private void copyFile(File target) throws IOException {
            new FileOutputStream(target).withCloseable(this.&copyTo)
        }

        public int getMode() { directory ? 493 : 420 }
    }
}
