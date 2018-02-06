package de.mohammed.service;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


public class RelevanceList {
    public static void main(String[] args){

        System.out.println(createRelevanceList("/Users/Desktop/projekt/list.txt"));
        //       System.out.println(deleteRelevanceList());

    }

    public static String deleteRelevanceList() {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            try {
                DeleteResponse response = client.prepareDelete("relevance_list", "item_list", "1").get();
            } catch (ElasticsearchException exception) {
                return "Not Found in Elasticsearch";
            }

        } catch (Exception e) {

            return "Something wrong";
        }

        return "The index was successfully deleted.";
    }

    // The funktion will be always called, when we've gotten an updated file, and now we what to index the new list.

    public static int createRelevanceList( String path) {

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
                   // System.out.println(builder.prettyPrint().string());

                    IndexResponse response = client.prepareIndex("relevance_list", "item_list", "1")
                        .setSource(builder)
                        .get();

            } catch (IOException e)
            {
                e.printStackTrace();
            }

        } catch (Exception e) {
            return 0;
        }
        return 1;
    }
}

