package com.jconf.demo.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

/**
 * Acto 1:
 * - Vector store en memoria (InMemoryEmbeddingStore).
 * - Ingesta de algunos documentos bancarios de ejemplo.
 */
@Configuration
public class LocalEmbeddingStoreConfig {


    @Bean
    public EmbeddingStore<TextSegment> localEmbeddingStore() {
        return new dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore<>();
    }

    @Bean
    public CommandLineRunner ingestSampleDocs(EmbeddingModel embeddingModel,
                                              EmbeddingStore<TextSegment> localEmbeddingStore) {
        return args -> {

            // Leer cada .md desde src/main/resources/docs/
            Document politicasCuentaAhorro = loadMarkdownAsDocument(
                    "docs/politicas-cuenta-ahorro-plus.md",
                    "politicas-cuenta-ahorro-plus"
            );

            Document retirosCajero = loadMarkdownAsDocument(
                    "docs/retiros-en-cajeros.md",
                    "retiros-en-cajeros"
            );

            Document tarjetaCredito = loadMarkdownAsDocument(
                    "docs/tarjeta-credito-classic.md",
                    "tarjeta-credito-classic"
            );

            Document transferencias = loadMarkdownAsDocument(
                    "docs/transferencias-interbancarias.md",
                    "transferencias-interbancarias"
            );

            Document seguridadFraudes = loadMarkdownAsDocument(
                    "docs/seguridad-y-fraudes.md",
                    "seguridad-y-fraudes"
            );

            DocumentSplitter splitter = DocumentSplitters.recursive(
                    300,   // tamaño de chunk
                    30     // overlap entre chunks
            );

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                   // .documentSplitter(splitter)
                    .embeddingModel(embeddingModel)
                    .embeddingStore(localEmbeddingStore)
                    .build();

            ingestor.ingest(politicasCuentaAhorro);
            ingestor.ingest(retirosCajero);
            ingestor.ingest(tarjetaCredito);
            ingestor.ingest(transferencias);
            ingestor.ingest(seguridadFraudes);
        };
    }

    /**
     * Carga un archivo .md del classpath y lo convierte en Document
     * agregando metadata con el "source".
     */
    private Document loadMarkdownAsDocument(String classpathLocation, String sourceName) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        return Document.from(
                text,
                Metadata.from(
                        "source", sourceName
                )
        );
    }
}