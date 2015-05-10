package org.joshuacoles.extensions

import com.google.common.base.Optional as GOptional
import com.google.common.base.Predicate as GPredicate

import java.util.Optional as JOptional
import java.util.function.Predicate as JPredicate

import static java.util.Optional.ofNullable as jOpt

class GuavaExtension {
    static <T> JPredicate<T> toJPredicate(GPredicate<T> gPredicate) { return { gPredicate.apply(it) } }

    static <T> boolean call(GPredicate<T> self, T t) { self.apply(t) }

    static <T> T call(GOptional<T> self) { self.get() }

    static <T> T call(GOptional<T> self, T orElse) { self.or(orElse) }

    static <T> JOptional<T> toJOptional(GOptional<T> self) { jOpt(self.orNull()) }
}
