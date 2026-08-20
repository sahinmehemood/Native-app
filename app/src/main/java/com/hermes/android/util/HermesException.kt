package com.hermes.android.util

/** Typed error for the data/domain layers. */
class HermesException(
    message: String,
    cause: Throwable? = null,
    val code: Int? = null,
    val kind: Kind = Kind.Unknown
) : Exception(message, cause) {
    enum class Kind { Network, Auth, NotFound, Validation, Server, Timeout, Unknown }
}
