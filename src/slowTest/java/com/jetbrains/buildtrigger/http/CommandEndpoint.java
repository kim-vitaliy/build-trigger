package com.jetbrains.buildtrigger.http;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Реализация описания вызова команды по умолчанию
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public class CommandEndpoint<SuccessT, ErrorT> {

    /**
     * Относительный путь к команде. (например - /general/create/custom)
     */
    @Nonnull
    private final String relativePath;

    @Nonnull
    private final Class<SuccessT> successClass;

    @Nonnull
    private final Class<ErrorT> errorClass;

    private CommandEndpoint(@Nonnull String relativePath,
                            @Nonnull Class<SuccessT> successClass,
                            @Nonnull Class<ErrorT> errorClass) {
        this.relativePath = Objects.requireNonNull(relativePath, "relativePath");
        this.successClass = Objects.requireNonNull(successClass, "successClass");
        this.errorClass = Objects.requireNonNull(errorClass, "errorClass");
    }

    public static <SuccessT, ErrorT> CommandEndpoint<SuccessT, ErrorT> create(
            @Nonnull String relativePath,
            @Nonnull Class<SuccessT> successClass,
            @Nonnull Class<ErrorT> errorClass) {
        return new CommandEndpoint<>(relativePath, successClass, errorClass);
    }

    @Nonnull
    public String getRelativePath() {
        return relativePath;
    }

    @Nonnull
    public Class<SuccessT> getSuccessClass() {
        return successClass;
    }

    @Nonnull
    public Class<ErrorT> getErrorClass() {
        return errorClass;
    }
}
