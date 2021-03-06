import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.*;


import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;


public class App implements Serializable {



    public static void main(String[] args) throws IOException, InterruptedException {

        TransportClient client = null;
        BulkProcessor bulkProcessor = null;
        JSONParser parser = new JSONParser();

        try {
            client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                }
            }).setBulkActions(1).setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB)).setFlushInterval(TimeValue.timeValueSeconds(5)).setConcurrentRequests(1).setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)).build();



            Object obj = parser.parse(new FileReader("/home/yogamapple/Dropbox/Die Höhle des Löwen/Uni/5. Semester/Semesterprojekt/Beispieleingaben/beispiel1.json"));
            JSONObject jsonObject = (JSONObject) obj;
            String name = (String) jsonObject.get("Title");

            DeleteResponse response = client.prepareDelete("semesterprojekt", "pubmed", "25620913").get();
            bulkProcessor.add(new IndexRequest("semesterprojekt", "pubmed", "25620913").source(jsonObject));

            JsonObject jsonObjectPMIDresult = searchDocumentByPMID(client, 25620913);
            Gson pMIDgson = new GsonBuilder().setPrettyPrinting().create();
            String pMIDGsonString = pMIDgson.toJson(jsonObjectPMIDresult);
            System.out.println("By PMID " + pMIDGsonString);

//
//            String title =
//                    "Subthreshold membrane currents confer distinct tuning properties that enable neurons to encode the integral or derivative of their input.";
//            JsonObject jsonObjectresult = searchDocumentByTitle(client, title);
//            Gson titlegson = new GsonBuilder().setPrettyPrinting().create();
//            String titleGsonString = titlegson.toJson(jsonObjectresult);
//            System.out.println("By Title " + titleGsonString);


            System.out.println("Finish!");

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static JsonObject searchDocumentByPMID(TransportClient client, int PMID) {

        try {
            QueryBuilder qb = termQuery("PMID", PMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("pubmed").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            Map map = null;

            for (SearchHit hit : antwort.getHits()) {

                map = hit.getSource();
                Gson gson = new Gson();
                return gson.toJsonTree(map).getAsJsonObject();
            }

        } catch (Exception e) {

        }
        return null;
    }

}
