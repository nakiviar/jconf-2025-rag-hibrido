package com.jconf.demo.ai.retrievers;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Context;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.models.SemanticSearchOptions;
import com.azure.search.documents.models.QueryType;
import com.azure.search.documents.models.SearchMode;

import com.azure.search.documents.util.SearchPagedIterable;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AzureSearchTextRetriever implements ContentRetriever {

    public enum Mode {
        TEXT,
        SEMANTIC
    }

    private final SearchClient client;
    private final String textField;
    private final int k;
    private final Mode mode;
    private final String semanticConfigName;

    public AzureSearchTextRetriever(String endpoint,
                                    String apiKey,
                                    String index,
                                    String textField,
                                    int k,
                                    Mode mode,
                                    String semanticConfigName) {

        // El cliente del SDK, fuertemente tipado a Map<String, Object>
        this.client = new SearchClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .indexName(index)
                .buildClient();

        this.textField = textField;
        this.k = k;
        this.mode = mode;
        this.semanticConfigName = (semanticConfigName == null || semanticConfigName.isBlank())
                ? null
                : semanticConfigName;
    }

    @Override
    public List<Content> retrieve(Query query) {

        // Opciones básicas
        SearchOptions options = new SearchOptions()
                .setIncludeTotalCount(false)
                .setTop(k)
                // Sólo buscamos y devolvemos el campo de texto que nos interesa
                .setSearchFields(textField)
                .setSelect(textField)
                .setQueryType(QueryType.SIMPLE)
                .setSearchMode(SearchMode.ANY);

        // Si estás en modo "semantic" y tienes configuración declarada en el índice
        // ver docs de SemanticSearchOptions. :contentReference[oaicite:3]{index=3}
        if (mode == Mode.SEMANTIC && semanticConfigName != null) {
            SemanticSearchOptions semantic = new SemanticSearchOptions()
                    .setSemanticConfigurationName(semanticConfigName)
                    .setSemanticQuery(query.text());

            options.setSemanticSearchOptions(semantic);
        }

        SearchPagedIterable results = client.search(query.text(), options, Context.NONE);

        List<Content> out = new ArrayList<>();

        for (SearchResult result : results) {
            Map<String, Object> doc = result.getDocument(Map.class);
            Object rawText = doc.get(textField);

            if (!(rawText instanceof String text) || text.isBlank()) continue;

            Metadata metadata = new Metadata();
            metadata.put("source", "azure-search-sdk");
            metadata.put("score", result.getScore());
            metadata.put("index", client.getIndexName());

            TextSegment segment = TextSegment.from(text, metadata);
            out.add(Content.from(segment));
        }

        return out;
    }
}
