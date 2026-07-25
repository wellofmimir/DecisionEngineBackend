package org.molokosoft.decisionengine.extensions

inline fun <T> Iterable<T>.hasDuplicatesBy(
    selector: (T) -> String
): Boolean =
    map {
        selector(it).trim().lowercase()
    }
    .distinct()
    .size != count()