// Файл содержит только утилитные функции для тестирования. Top-level класс с именем файла не нужен.
@file:Suppress("MatchingDeclarationName")
package com.jetbrains.buildtrigger.utils.assertions

import org.amshove.kluent.should
import java.util.Optional

/**
 * Проверяет, что данный [Optional] не содержит значения.
 */
fun Optional<*>.shouldBeEmpty() = this.should("Should be an empty optional but given $this") { !isPresent }

/**
 * Проверяет, что данный [Optional] содержит некоторое значение.
 */
fun <T> Optional<T>.shouldBePresent() = this.should("Should contain some value but given empty") { isPresent }
