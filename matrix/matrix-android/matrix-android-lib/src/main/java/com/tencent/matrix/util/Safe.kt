package com.tencent.matrix.util

/**
 * Created by Yves on 2021/11/15
 */

const val DEFAULT_TAG = "Matrix.Safe"

inline fun <T> T.safe(tag: String = DEFAULT_TAG, log: Boolean = true, unsafe: T.() -> Unit): T {
    try {
        unsafe()
    } catch (e: Throwable) {
        if (log) {
            MatrixLog.printErrStackTrace(tag, e, "")
        }
    }
    return this
}

inline fun <T, R> T.safe(
    tag: String = DEFAULT_TAG,
    log: Boolean = true,
    defVal: R?,
    unsafe: (T) -> R
): R? {
    return try {
        unsafe(this)
    } catch (e: Throwable) {
        if (log) {
            MatrixLog.printErrStackTrace(tag, e, "")
        }
        defVal
    }
}

inline fun <T, R> T.safeOrNull(
    tag: String = DEFAULT_TAG,
    log: Boolean = true,
    unsafe: (T) -> R
): R? = safe(tag, log, null, unsafe)