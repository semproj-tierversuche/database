package de.mohammed.service;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;


import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Path("/import")
public class ImportService {


    @PUT
    @Path("/")
    public Response parseImport(@QueryParam("type") String type, String document){
        if("document".equals(type)){

            return createDocument(document);

        }else if("utils".equals(type)){

            return createUtils(document);
        }

        return Response.status(400).entity("Fehlerhafte Anfrage").build();
    }



    public Response createDocument(String document) {

        BulkProcessor bulkProcessor = null;
        JSONParser parser = new JSONParser();


        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
               // @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                }
            }).setBulkActions(1).setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)).setFlushInterval(TimeValue.timeValueSeconds(5)).setConcurrentRequests(1).setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

            Object obj = parser.parse(document);
            JSONObject jsonDocument = (JSONObject) obj;

            //bevor ein Dokument indexiert wird, wird geprüft ob es bereits vorhanden ist. Falls ja, wird es erstmal gelöscht
            QueryBuilder qb = termQuery("PMID", jsonDocument.get("PMID"));
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();
            if(antwort.getHits() != null){
                DeleteResponse response = client.prepareDelete("semesterprojekt", "document", jsonDocument.get("PMID").toString()).get();
            }

            bulkProcessor.add(new IndexRequest("semesterprojekt", "document", jsonDocument.get("PMID").toString()).source(jsonDocument));

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(200).entity("ok").build();
    }


    public Response createUtils(String document) {

        BulkProcessor bulkProcessor = null;
        JSONParser parser = new JSONParser();

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
                //@Override
                public void beforeBulk(long executionId, BulkRequest request) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                }
            }).setBulkActions(1).setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)).setFlushInterval(TimeValue.timeValueSeconds(5)).setConcurrentRequests(1).setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

            Object obj = parser.parse(document);
            JSONObject jsonDocument = (JSONObject) obj;


            //bevor ein Dokument indexiert wird, wird geprüft ob es bereits vorhanden ist. Falls ja, wird es erstmal gelöscht
            QueryBuilder qb = termQuery("ResourceName", jsonDocument.get("ResourceName"));
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("utils").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();
            if(antwort.getHits() != null){
                DeleteResponse response = client.prepareDelete("semesterprojekt", "utils", jsonDocument.get("ResourceName").toString()).get();
            }

            bulkProcessor.add(new IndexRequest("semesterprojekt", "utils", jsonDocument.get("ResourceName").toString()).source(jsonDocument));

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(200).entity("ok").build();
    }
    @PUT
    @Path("/import")

    public Response importRelevanceList (@QueryParam("type") String type, String document){
        if("item_list".equals(type)){

            return createRelevanceList(document);
        }
        return Response.status(400).entity("Fehlerhafte Anfrage").build();
    }
    public Response createRelevanceList (String path){

        TransportClient client = null;
        BulkProcessor bulkProcessor = null;

        try {
            client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
                // @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                }

                //@Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                }
            }).setBulkActions(1).setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)).setFlushInterval(TimeValue.timeValueSeconds(5)).setConcurrentRequests(1).setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();

            try {
                JSONArray list = new JSONArray();
                BufferedReader br = new BufferedReader(new FileReader(path));
                String readLine = "";
                while ((readLine = br.readLine()) != null) {
                    list.add(readLine);
                }
                XContentBuilder builder = jsonBuilder()
                        .startObject()
                        .field("items", list)
                        .endObject();

                IndexResponse response = client.prepareIndex("relevance_list", "item_list", "1")
                        .setSource(builder)
                        .get();

            } catch (IOException e)
            {
                return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
            }

        } catch (Exception e) {
            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }
        return Response.status(200).entity("The Relevance List Created.").build();

    }
    public static Response deleteRelevanceList() {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            try {
                DeleteResponse response = client.prepareDelete("semesterprojekt", "item_list", "1").get();

            } catch (ElasticsearchException exception) {
                if (exception.status() == RestStatus.NOT_FOUND) {
                    return Response.status(404).entity("Index relevanceList does not found in Elasticsearch.").build();
                }
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(200).entity("The relevance List was deleted").build();
    }
}