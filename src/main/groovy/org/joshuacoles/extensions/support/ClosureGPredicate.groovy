package org.joshuacoles.extensions.support

import com.google.common.base.Predicate
import groovy.transform.Immutable

@Immutable
class ClosureGPredicate<T> implements Predicate<T> {
    Closure<Boolean> closure

    @Override
    boolean apply(T t) {
        return closure(t)
    }
}
