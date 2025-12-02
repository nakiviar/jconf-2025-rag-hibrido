package com.jconf.demo.ai.retrievers;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.search.documents.SearchClient;
import com.azure.search.documents.SearchClientBuilder;
import com.azure.search.documents.models.SearchOptions;
import com.azure.search.documents.models.SearchResult;
import com.azure.search.documents.util.SearchPagedIterable;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.data.document.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AzureSearchTextRetriever implements ContentRetriever {

    private final SearchClient client;
    private final String textField;
    private final int k;

    public AzureSearchTextRetriever(String endpoint,
                                    String apiKey,
                                    String index,
                                    String textField,
                                    int k) {

        this.client = new SearchClientBuilder()
                .endpoint(endpoint)
                .credential(new AzureKeyCredential(apiKey))
                .indexName(index)
                .buildClient();

        this.textField = textField;
        this.k = k;
    }


    @Override
    public List<Content> retrieve(Query query) {

        var options = new SearchOptions()
                .setIncludeTotalCount(false)
                .setTop(k);

        // Uso de SearchPagedIterable para los resultados
        SearchPagedIterable results = client.search(query.text(), options, null);

        List<Content> out = new ArrayList<>();

        for (SearchResult r : results) {

            var doc = r.getDocument(Map.class);
            var text = (String) doc.get(textField);

            if (text == null) continue;

            // Construir Metadata en vez de Map
            Metadata metadata = new Metadata();
            metadata.put("source", "azure-search-sdk");
            metadata.put("score", r.getScore()); // el valor se guarda como Object internamente

            var segment = TextSegment.from(text, metadata);

            out.add(Content.from(segment));
        }

        return out;
    }
}