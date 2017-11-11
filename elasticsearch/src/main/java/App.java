import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.index.IndexRequest;
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

    public static HashMap<Integer, Record> checkBulkMap = new <Integer, Record>HashMap();
    public static HashMap<Integer, Integer> result = new <Integer, Integer>HashMap();

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
            //String name = (String) jsonObject.get("Title");

            //createDataWithBulk(bulkProcessor, jsonObject);


            JsonObject jsonObjectresult = searchDocumentByPMID(client, 25620913);
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


    //searchible Feld definieren
    private static void createDataWithBulk(BulkProcessor bulkRequest, JSONObject jsonObject) throws IOException {
        try {
            bulkRequest.add(new IndexRequest("semesterprojekt", "pubmed").source(jsonObject));
        } catch (Exception e) {

        }
    }



    //parallel query durchführen
    private static void searchCandidatesInES(TransportClient client, Record record) {

        try {

            List<Integer> prefixTokenList = new ArrayList<Integer>(Arrays.asList(record.prefixTokens));


            QueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("PrefixTokens", prefixTokenList))).must(QueryBuilders.rangeQuery("R_ID").gt(record.getRid()));

            SearchResponse antwort = client.prepareSearch("db").setTypes("test").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            Map map = null;
            //int iii = 0;
            for (SearchHit hit : antwort.getHits()) {
                map = hit.getSource();

                Gson converter = new Gson();
                Type type = new TypeToken<List<Integer>>() {
                }.getType();
                List<Integer> iList = converter.fromJson(map.get("Tokens").toString(), type);
                Integer[] iArray = iList.toArray(new Integer[0]);

            }
        } catch (Exception e) {

        }
    }
}
