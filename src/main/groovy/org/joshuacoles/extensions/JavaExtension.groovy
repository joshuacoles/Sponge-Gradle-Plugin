package org.joshuacoles.extensions

import com.google.common.base.Optional as GOptional
import com.google.common.base.Predicate as GPredicate
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.joshuacoles.extensions.support.ClosureGPredicate

import java.util.Optional as JOptional
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate as JPredicate
import java.util.function.Supplier
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import static com.google.common.base.Optional.fromNullable as gOpt

@SuppressWarnings("GroovyAssignabilityCheck")
class JavaExtension {
    static <T> GPredicate<T> toGPredicate(JPredicate<T> jPredicate) {
        return new ClosureGPredicate<T>({ jPredicate.test(it) })
    }

    static <T> boolean call(JPredicate<T> self, T t) { self.test(t) }

    static <T> T call(JOptional<T> self) { self.present ? self.get() : null }

    static <T> T call(JOptional<T> self, T orElse) { self.orElse(orElse) }

    static <T> GOptional<T> toGOptional(JOptional<T> self) { gOpt(self()) }

    static <T, R> R call(Function<T, R> self, T t) { self.apply(t) }

    static <R> R call(Supplier<R> self) { self.get() }

    static <T, U, R> R call(BiFunction<T, U, R> self, T t, U u) { self.apply(t, u) }

    static Iterable<ZipEntry> eachEntry(ZipFile self, Closure closure) {
        DefaultGroovyMethods.each(self.entries().iterator(), closure)
        return self.entries().iterable() as Iterable<ZipEntry>
    }

    static void remakeDir(File self) {
        self.deleteDir()
        self.mkdirs()
    }

}
