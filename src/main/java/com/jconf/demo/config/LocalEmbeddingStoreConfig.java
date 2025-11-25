package com.jconf.demo.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Acto 1:
 * - Vector store en memoria (InMemoryEmbeddingStore).
 * - Ingesta de algunos documentos bancarios de ejemplo.
 */
@Configuration
public class LocalEmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> localEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public CommandLineRunner ingestSampleDocs(EmbeddingModel embeddingModel,
                                              EmbeddingStore<TextSegment> localEmbeddingStore) {
        return args -> {

            // Aquí luego puedes cambiar por cargar PDFs desde resources.
            Document politicasCuentaAhorro = Document.from("""
                La Cuenta Ahorro Plus no tiene comisión de mantenimiento
                si el cliente mantiene un saldo promedio mensual mayor a 1000 soles.
                En caso contrario, la comisión mensual es de 10 soles.
                """);

            Document retirosCajero = Document.from("""
                El retiro diario máximo en cajeros para tarjetas de débito
                es de 2000 soles por tarjeta.
                Los retiros en cajeros de otros bancos pueden tener una comisión adicional.
                """);

            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .embeddingModel(embeddingModel)
                    .embeddingStore(localEmbeddingStore)
                    .build();

            ingestor.ingest(politicasCuentaAhorro);
            ingestor.ingest(retirosCajero);
        };
    }
}
