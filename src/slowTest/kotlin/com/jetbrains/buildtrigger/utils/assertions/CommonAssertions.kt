package com.jetbrains.buildtrigger.utils.assertions

import org.amshove.kluent.should

private data class Failure(val index: Int, val throwable: Throwable)

/**
 * Вызывает все переданные проверки, вне зависимости от успешности выполнения каждой из них
 */
infix fun <T> T.shouldSoftAssert(assertions: TestContainer<T>.() -> Unit): T {
    val provider = TestContainer<T>()
    provider.assertions()

    val failures = ArrayList<Failure>()
    for ((index, assertion) in provider.assertions.withIndex()) {
        try {
            assertion(this)
        } catch (expected: Throwable) {
            failures += Failure(index, expected)
        }
    }

    val joinedFailures = failures.joinToString("\n") { "\tAssertion ${it.index}: ${it.throwable.message}" }
    val message = "Multiple failures (${failures.size}):\n$joinedFailures"

    return this.should(message) { failures.isEmpty() }
}
