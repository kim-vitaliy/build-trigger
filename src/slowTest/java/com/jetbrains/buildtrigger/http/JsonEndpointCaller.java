package com.jetbrains.buildtrigger.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.buildtrigger.command.CommandResult;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Исполнитель JSON-команд.
 * Отправляет запрос компоненту, разбирает и возвращает ответ.
 *
 * @author Vitaliy Kim
 * @since 21.01.2023
 */
public class JsonEndpointCaller {

    private static final Logger log = LoggerFactory.getLogger(JsonEndpointCaller.class);

    private final HttpClient httpClient;
    private final URI baseUri;
    private final ObjectMapper objectMapper;

    private final Map<String, URI> requestUriCache = Collections.synchronizedMap(new HashMap<>());

    public JsonEndpointCaller(@Nonnull HttpClient httpClient,
                              @Nonnull String baseUri,
                              @Nonnull ObjectMapper objectMapper) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
        Objects.requireNonNull(baseUri, "baseUri");
        this.baseUri = baseUri.endsWith("/") ? URI.create(baseUri) : URI.create(baseUri + '/');
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    }

    /**
     * Вызов команды GET-запросом
     *
     * @param command команда
     * @param <SuccessT> класс успешного ответа
     * @param <ErrorT> класс ответа с ошибкой бизнес-логики
     *
     * @return результат выполнения команды
     */
    public <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> get(@Nonnull CommandEndpoint<SuccessT, ErrorT> command) {
        ClassicHttpRequest request = ClassicRequestBuilder.get()
                .setUri(createUri(command))
                .build();

        return call(request, command);
    }

    /**
     * Вызов команды с телом запроса через POST.
     *
     * @param request запрос
     * @param command команда
     * @param <RequestT> класс запроса
     * @param <SuccessT> класс успешного ответа
     * @param <ErrorT> класс ответа с ошибкой бизнес-логики
     * @return результат выполнения команды
     */
    public <RequestT, SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> postWithBody(@Nonnull RequestT request,
                                                                                     @Nonnull CommandEndpoint<SuccessT, ErrorT> command) {

        ClassicHttpRequest classicHttpRequest;
        try {
            classicHttpRequest = ClassicRequestBuilder.post()
                    .setUri(createUri(command))
                    .setEntity(objectMapper.writeValueAsBytes(request), ContentType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse request to json", e);
        }

        return call(classicHttpRequest, command);
    }

    /**
     * Вызов команды с телом запроса через PUT.
     *
     * @param request запрос
     * @param command команда
     * @param <RequestT> класс запроса
     * @param <SuccessT> класс успешного ответа
     * @param <ErrorT> класс ответа с ошибкой бизнес-логики
     * @return результат выполнения команды
     */
    public <RequestT, SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> putWithBody(@Nonnull RequestT request,
                                                                                    @Nonnull CommandEndpoint<SuccessT, ErrorT> command) {

        ClassicHttpRequest classicHttpRequest;
        try {
            classicHttpRequest = ClassicRequestBuilder.put()
                    .setUri(createUri(command))
                    .setEntity(objectMapper.writeValueAsBytes(request), ContentType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse request to json", e);
        }

        return call(classicHttpRequest, command);
    }

    /**
     * Вызов команды DELETE-запросом
     *
     * @param command команда
     * @param <SuccessT> класс успешного ответа
     * @param <ErrorT> класс ответа с ошибкой бизнес-логики
     *
     * @return результат выполнения команды
     */
    public <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> delete(@Nonnull CommandEndpoint<SuccessT, ErrorT> command) {
        ClassicHttpRequest request = ClassicRequestBuilder.delete()
                .setUri(createUri(command))
                .build();

        return call(request, command);
    }

    /**
     * Вызов команды c multipart/form-data
     *
     * @param params параметры запроса
     * @param command команда
     * @param <SuccessT> класс успешного ответа
     * @param <ErrorT> класс ответа с ошибкой бизнес логики
     * @return результат выполнения команды
     */
    public <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> postWithMultipart(@Nonnull Map<String, ? extends ContentBody> params,
                                                                                @Nonnull CommandEndpoint<SuccessT, ErrorT> command) {

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        params.forEach(multipartEntityBuilder::addPart);

        ClassicHttpRequest httpRequest = ClassicRequestBuilder.post()
                .setUri(createUri(command))
                .setEntity(multipartEntityBuilder.build())
                .build();

        return call(httpRequest, command);
    }

    /**
     * Сформировать полный URI запроса
     *
     * @param command команда для запроса
     * @return URI
     */
    @Nonnull
    private URI createUri(@Nonnull CommandEndpoint<?, ?> command) {
        return requestUriCache.computeIfAbsent(command.getRelativePath(), this::computeUri);
    }

    private URI computeUri(String relativePath) {
        String relativePathPart = relativePath.startsWith("/")
                ? relativePath.substring(1)
                : relativePath;

        try {
            URIBuilder uriBuilder = new URIBuilder(baseUri);
            uriBuilder.setPath(baseUri.getPath() + relativePathPart);
            return uriBuilder.build();
        } catch (URISyntaxException exc) {
            throw new RuntimeException("Unexpected by test", exc);
        }
    }

    private <SuccessT, ErrorT> CommandResult<SuccessT, ErrorT> call(@Nonnull ClassicHttpRequest request,
                                                                    @Nonnull CommandEndpoint<SuccessT, ErrorT> command) {
        try {
            return httpClient.execute(request, httpResponse -> getResultFromResponse(httpResponse, command));
        } catch (Exception e) {
            log.error("Unable to call: commandRelativePath={}", command.getRelativePath(), e);
            throw new RuntimeException(e);
        }
    }

    private <ErrorT, SuccessT> CommandResult<SuccessT, ErrorT> getResultFromResponse(
            @Nonnull ClassicHttpResponse httpResponse,
            @Nonnull CommandEndpoint<SuccessT, ErrorT> command) throws IOException {

        int httpResponseCode = httpResponse.getCode();

        HttpEntity httpEntity = httpResponse.getEntity();
        try {
            if (httpResponseCode == HttpStatus.SC_OK) {
                if (command.getSuccessClass().isAssignableFrom(Void.class)) {
                    return (CommandResult<SuccessT, ErrorT>) CommandResult.successEmpty();
                }
                if (command.getSuccessClass().isAssignableFrom(ResponseInputStream.class)) {
                    ResponseInputStream.Builder builder = ResponseInputStream.builder()
                            .inputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));
                    for (Header header : httpResponse.getHeaders()) {
                        if (header.getName().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE)) {
                            builder.contentType(header.getValue());
                        } else if (header.getName().equalsIgnoreCase(HttpHeaders.CONTENT_DISPOSITION)) {
                            builder.contentDisposition(header.getValue());
                        }
                    }
                    return (CommandResult<SuccessT, ErrorT>) CommandResult.success(builder.build());
                }
                SuccessT success = objectMapper.readValue(httpEntity.getContent(), command.getSuccessClass());
                return CommandResult.success(success);
            }

            if (httpResponseCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
                ErrorT error = objectMapper.readValue(httpEntity.getContent(), command.getErrorClass());
                return CommandResult.error(error);
            }
        } finally {
            EntityUtils.consume(httpEntity);
        }
        throw new IllegalArgumentException(
                String.format("Command return unprocessed http code: commandPath=%s, httpResponseCode=%d",
                        createUri(command), httpResponseCode));
    }
}
