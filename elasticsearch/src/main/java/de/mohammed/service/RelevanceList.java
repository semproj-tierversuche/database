package de.mohammed.service;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class RelevanceList {

    //Ich denke, es würde einfacher für uns sein, den text file vom Middleware zu kriegen.
    // JSONArray würde auch gehen.
    public Response updateRelevanceList(File modifiedRelevanceList) {

        String currentDate = new Date().toString();     //format example: Tue Dec 17 07:56:32 UTC 2015.

        BulkProcessor bulkProcessor = null;
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            GetResponse response = client.prepareGet("relevanceList", "listItem", "0")
                    .execute()
                    .actionGet();

            if(response != null) {
                if(!(response.getField("postDate").toString().equals(currentDate))){
                    Response respFromDeleteMethod = deleteRelevanceList();
                    Response respFromCreateMethod = null;
                    if(respFromDeleteMethod.getStatus() == 200) {  
                        respFromCreateMethod = createRelevanceList(modifiedRelevanceList);
                    } else{
                        return respFromCreateMethod;
                    }
                    if(respFromCreateMethod.getStatus() == 200) {
                        return Response.status(200).entity("The relevance list is modified on " + currentDate + ".").build();
                    } else{
                        return respFromCreateMethod;
                    }
                } else {
                    return Response.status(200).entity("The relevance list has already been modified on "+ currentDate+".").build();
                }

            } else{
                 return Response.status(404).entity("Data Not Found").build();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

    }


    public Response deleteRelevanceList() {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            try {
                DeleteIndexRequest request = new DeleteIndexRequest("relevanceList");
                client.admin().indices().delete(request);
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

    // The fucktion will be always called, when we've gotten an updated file, and now we what to index the new list.
    public Response createRelevanceList( File relevanceList) {

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

            client.admin().indices().prepareCreate("relevanceList").get();       // A new Index is created.
            client.admin().indices().preparePutMapping("relevanceList").setType("listItem").get();
           // File file = new File("pathToRelevanceList");    //But we need path to the file. Do we have a path??
            BufferedReader br = new BufferedReader(new FileReader(relevanceList));
            String readLine = "";
            int id = 0;
            while ((readLine = br.readLine()) != null) {    //read File line by line and store it as an JsonObject.
                XContentBuilder builder = null;             // every item has two fields: content and Date.
                try {
                    builder = jsonBuilder()
                            .startObject()
                            .field("LineContent", readLine)
                            .field("postDate", new Date().toString())
                            .endObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                IndexResponse response = client.prepareIndex("relevanceList", "listItem")
                        .setSource(builder)
                        .setId(Integer.toString(id))
                        .execute()
                        .actionGet();
                id++;

                // idea #1: read and index every line as a jsonDoc without any fields.
                /* Object obj = parser.parse(readLine);
                JSONObject jsonListLine = (JSONObject) obj;

                bulkProcessor.add(new IndexRequest("relevanceList", typeName, Integer.toString(id)).source(jsonListLine));
                */
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }
        return Response.status(200).entity("The Relevance List Created.").build();
    }

}
