package com.jconf.demo.observability;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ObservedContentRetrieverConfig {

        @Bean
        public ContentRetriever observedContentRetriever(
                EmbeddingStore<TextSegment> localEmbeddingStore,
                EmbeddingModel ollamaEmbeddingModel,          // usa tu bean existente
                ObservationRegistry observationRegistry) {

            ContentRetriever delegate = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(localEmbeddingStore)
                    .embeddingModel(ollamaEmbeddingModel)
                    .maxResults(2)
                    .build();

            return new ContentRetriever() {
                @Override
                public List<Content> retrieve(Query query) {

                    Observation obs = Observation
                            .start("rag.retrieve", observationRegistry)
                            .lowCardinalityKeyValue("rag.stage", "retrieve");

                    try (Observation.Scope scope = obs.openScope()) {

                        List<Content> contents = delegate.retrieve(query);

                        String preview = contents.stream()
                                .map(content -> {
                                    if (content == null || content.textSegment() == null) {
                                        return "";
                                    }
                                    String text = content.textSegment().text();
                                    if (text == null) return "";
                                    int limit = Math.min(200, text.length());
                                    return text.substring(0, limit);
                                })
                                .filter(s -> !s.isBlank())
                                .collect(Collectors.joining("\n---\n"));

                        obs.highCardinalityKeyValue("rag.context.size",
                                String.valueOf(contents.size()));
                        obs.highCardinalityKeyValue("rag.context.preview", preview);

                        String sources = contents.stream()
                                .map(content -> {
                                    if (content == null || content.textSegment() == null) return "desconocido";
                                    var meta = content.textSegment().metadata();
                                    if (meta == null) return "desconocido";
                                    String src = meta.getString("source");
                                    return src != null ? src : "desconocido";
                                })
                                .distinct()
                                .collect(Collectors.joining(","));

                        obs.highCardinalityKeyValue("rag.context.sources", sources);

                        return contents;
                    } finally {
                        obs.stop();
                    }
                }
            };
        }
    }