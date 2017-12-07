import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
import static org.elasticsearch.index.query.QueryBuilders.*;


public class App implements Serializable {


    public static void main(String[] args) throws IOException, InterruptedException {

        TransportClient client = null;
        BulkProcessor bulkProcessor = null;
        JSONParser parser = new JSONParser();

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


           /* JsonObject jsonObjectPMIDresult = searchDocumentByPMID(client, 11489449);
            Gson pMIDgson = new GsonBuilder().setPrettyPrinting().create();
            String pMIDGsonString = pMIDgson.toJson(jsonObjectPMIDresult);
            System.out.println("By PMID " + pMIDGsonString);*/

            String title = "Subthreshold membrane currents confer distinct tuning properties that enable neurons to encode the integral or derivative of their input.";
           // deleteDocumentByTitle(client, title);


            ArrayList<JsonObject> jsonObjectresult = searchDocumentMLT(client, 25620913);
            System.out.println("More_Like_This: ");
            for(JsonElement result : jsonObjectresult) {
                Gson titlegson = new GsonBuilder().setPrettyPrinting().create();
                String titleGsonString = titlegson.toJson(result);
                System.out.println(titleGsonString);
            }

           /* JsonObject jsonObjectresult = searchDocumentByTitle(client, title);
            Gson titlegson = new GsonBuilder().setPrettyPrinting().create();
            String titleGsonString = titlegson.toJson(jsonObjectresult);
            System.out.println("By Title " + titleGsonString);*/


            System.out.println("Finish!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static JsonObject searchDocumentByTitle(TransportClient client, String title) {

        try {

            QueryBuilder qb = matchQuery("Title", title);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();
            Map map = null;

            // String output = antwort.toString();
            // System.out.println("OUPUT: " + output);
            SearchHits hits = antwort.getHits();
            for (SearchHit hit : hits) {
                map = hit.getSource();

                Gson gson = new Gson();
                return gson.toJsonTree(map).getAsJsonObject();
            }

        } catch (Exception e) {

        }

        return null;
    }

    private static JsonObject searchDocumentByPMID(TransportClient client, int PMID) {

        try {
            QueryBuilder qb = termQuery("PMID", PMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
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

    private static void deleteDocumentByPMID(TransportClient client, int pMID) {
        try {
            QueryBuilder qb = termQuery("PMID", pMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            if (antwort.getHits() != null) {
                for (SearchHit hit : antwort.getHits()) {
                    String yourId = hit.getId();
                    DeleteResponse response = client.prepareDelete("semesterprojekt", "document", yourId).get();
                }

                System.out.println("Deleted!!" + pMID);
                System.out.println();
            } else {
                System.out.println("Document not found " + pMID);
            }

        } catch (Exception e) {

        }
    }

    private static void deleteDocumentByTitle(TransportClient client, String title) {
        try {
            QueryBuilder qb = matchQuery("Title", title);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            if (antwort.getHits() != null) {
                for (SearchHit hit : antwort.getHits()) {
                    String yourId = hit.getId();
                    DeleteResponse response = client.prepareDelete("semesterprojekt", "document", yourId).get();
                }

                System.out.println("Deleted!!" + title);
                System.out.println();
            }
            else{
                System.out.println("Document not found " + title);
            }

        } catch (Exception e) {

        }

    }

    private static ArrayList<JsonObject> searchDocumentMLT(TransportClient client, int pMID){

        try {

            JsonObject jObject = searchDocumentByPMID(client, pMID);
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jObject.toString());
            String[] field = {"AbstractContent"};
            MoreLikeThisQueryBuilder.Item[] items = null;
            String pMID_string = Integer.toString(pMID);
            String[] text = {obj.get("AbstractContent").toString()};
           // String[] text = {"memantine", "NMDA", "brain injury", "rats", "LRRK2"};

            QueryBuilder qb = moreLikeThisQuery(field, text, items).minTermFreq(2).minDocFreq(1).maxQueryTerms(10).minimumShouldMatch("90%").include(false);
            //include(flase by default): specifies whether the documents should be included from the search.
            // I haven't mentioned the difference. the Source-Doc is still in the list of results.
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                   .get();
            ArrayList<JsonObject> result_list = new ArrayList<JsonObject>();
            //Falls wir als Output die Liste von ids bekommen wollen werden;
            ArrayList<String> resultListOfId = new ArrayList<String>();
            Map map = null;

          for(SearchHit hit : antwort.getHits()){
              // Das Ziel: die Quelldatei rauszunehmen. Mit dem Feld 'unlike' hat es leider bis jetzt nicht geklappt.
              String id = hit.getId();

             if( !pMID_string.equals(id)) {
                  resultListOfId.add(id);
                  System.out.println("ID: "+ id);
                  map = hit.getSource();
                  Gson gson = new Gson();
                  result_list.add(gson.toJsonTree(map).getAsJsonObject());
              }
          }
            return result_list;
        }catch (Exception e){

        }

        return null;
    }

    // we compare the Abstract of the given document with abstract-fields of other documents.
    private static JsonObject getCommonTermSearch(TransportClient client, int pMID){
        try {
            String text = null;

            CommonTermsQueryBuilder qb = new CommonTermsQueryBuilder("AbstractContent", text);
            qb.cutoffFrequency(0.002f).highFreqMinimumShouldMatch("3");
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

        } catch (Exception e){

        }

        return null;
    }

    //searchible Feld definieren
    private static void createDataWithBulk(BulkProcessor bulkRequest, JSONObject jsonObject) throws IOException {
        try {
            bulkRequest.add(new IndexRequest("semesterprojekt", "document").source(jsonObject));
        } catch (Exception e) {

        }

    }

}
