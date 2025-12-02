package com.jconf.demo.ai.retrievers;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.List;

public class ObservedContentRetriever implements ContentRetriever {

    private final ContentRetriever delegate;
    private final ObservationRegistry registry;
    private final String name;

    public ObservedContentRetriever(ContentRetriever delegate,
                                    ObservationRegistry registry,
                                    String name) {
        this.delegate = delegate;
        this.registry = registry;
        this.name = name;
    }

    @Override
    public List<Content> retrieve(Query query) {
        // Nombre de la métrica/traza; cámbialo si quieres algo más específico
        return Observation
                .createNotStarted("rag.content.retriever", registry)
                .contextualName(name) // aparecerá como span / timer con este nombre
                .observe(() -> delegate.retrieve(query));
    }
}
