package com.jetbrains.buildtrigger.mock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

import java.util.function.BiFunction;

/**
 * Позволяет описать ответ стаба в императивном стиле, формат ответа - всегда строка.
 */
public class ToStringResponseTransformer extends ResponseTransformer {

    /**
     * Имя трансформера
     */
    public static final String NAME = "ToStringResponseTransformer";

    /**
     * Имя параметра, в котором лежит процессор ответа
     */
    public static final String PROCESSOR_PARAM = "processor";

    @Override
    @SuppressWarnings("unchecked")
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        BiFunction<Request, Response, Object> processor = (BiFunction<Request, Response, Object>) parameters.get(PROCESSOR_PARAM);
        Object result = processor.apply(request, response);
        return Response.Builder.like(response).body(result.toString()).build();
    }

    @Override
    public boolean applyGlobally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
