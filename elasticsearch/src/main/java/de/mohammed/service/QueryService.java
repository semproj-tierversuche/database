package de.mohammed.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.moreLikeThisQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Path("/query")
public class QueryService {


    @GET
    @Path("/")
    public Response parseQuery(@QueryParam("type") String type,
                               @QueryParam("utilsName") String utilsname,
                               @QueryParam("PMID") @DefaultValue("-1") int pMID,
                               @QueryParam("docTitle") String title) {

        if ("document".equals(type)) {
            if (pMID != -1) {
                return getDocumentByID(pMID);
            } else {
                return getDocumentByTitle(title);
            }

        } else if ("utils".equals(type)) {

            return getUtils(utilsname);
        }

        return Response.status(400).entity("Fehlerhafte Anfrage").build();
    }

    @GET
    @Path("/version")
    public Response getVersion(@QueryParam("utilsname") String utilsname) {

        if (utilsname == null) {
            try {
                TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

                QueryBuilder qb = termQuery("ResourceName", utilsname);
                SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("utils").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                        .get();

                Map map = null;
                for (SearchHit hit : antwort.getHits()) {

                    map = hit.getSource();
                    Gson gson = new Gson();
                    return Response.status(200).entity(gson.toJsonTree(map).getAsJsonObject().get("tmVersion").toString()).build();
                }

            } catch (Exception e) {

                return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
            }
            return Response.status(404).entity("Not Found " + utilsname).build();

        } else {

        }

        return Response.status(404).entity("No DB version ").build();
    }


    public Response getDocumentByID(int pMID) {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            QueryBuilder qb = termQuery("PMID", pMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            Map map = null;

            for (SearchHit hit : antwort.getHits()) {

                map = hit.getSource();
                Gson gson = new Gson();
                return Response.status(200).entity(gson.toJsonTree(map).getAsJsonObject().toString()).build();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(404).entity("Not Found " + pMID).build();
    }


    public Response getUtils(String utilsname) {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            QueryBuilder qb = termQuery("ResourceName", utilsname);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("utils").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            Map map = null;
            for (SearchHit hit : antwort.getHits()) {

                map = hit.getSource();
                Gson gson = new Gson();
                return Response.status(200).entity(gson.toJsonTree(map).getAsJsonObject().toString()).build();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();

        }

        return Response.status(404).entity("Not Found " + utilsname).build();
    }

    public Response getDocumentByTitle(String title) {
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            QueryBuilder qb = matchQuery("docTitle", title);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            Map map = null;
            for (SearchHit hit : antwort.getHits()) {

                map = hit.getSource();
                Gson gson = new Gson();
                return Response.status(200).entity(gson.toJsonTree(map).getAsJsonObject().toString()).build();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();

        }

        return Response.status(404).entity("Not Found " + title).build();
    }

    public Response deleteDocumentByPMID(int pMID) {
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            JSONObject jsonDocument = null;
            QueryBuilder qb = termQuery("PMID", pMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            if (antwort.getHits() != null) {
                DeleteResponse response = client.prepareDelete("semesterprojekt", "document", jsonDocument.get("PMID").toString()).get();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(404).entity("Not Found " + pMID).build();
    }

    public Response deleteDocumentByTitle(String title) {
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            JSONObject jsonDocument = null;
            QueryBuilder qb = termQuery("docTitle", title);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            if (antwort.getHits() != null) {
              DeleteResponse response = client.prepareDelete("semesterprojekt", "document", jsonDocument.get("PMID").toString()).get();
            }

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(404).entity("Not Found " + title).build();
    }
    @GET
    @Path("/search")
    public Response searchSimilarQuery(int pMID){
        ArrayList<JsonObject> resultOfSearch = new ArrayList<JsonObject>();
        ArrayList<String> idListOfMoreLikeThis = new ArrayList<String>();
        try {
            JsonObject jObject = (JsonObject)getDocumentByID(pMID).getEntity();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jObject.toString());
            ArrayList<String> meshTermsList = searchByMeshTerms(obj);

            if(meshTermsList.isEmpty()){
                return null;
            }
            idListOfMoreLikeThis = searchByMoreLikeThis(obj, meshTermsList);
            resultOfSearch = searchByRelevanceList(idListOfMoreLikeThis);
            return Response.status(200).entity(resultOfSearch).build();
        } catch (Exception e){

        }
        return Response.status(404).entity("Not Found ").build();
    }

    public ArrayList<String> searchByMeshTerms(JSONObject obj){
        if(obj.get("MeshHeadings") == null)
        {
            return null;
        }
        ArrayList<String> meshTerms = new ArrayList<String>(Arrays.asList(obj.get("MeshHeadings").toString().split(" , ")));
        // Wenn das Feld "MeshHeading" in dem InputDocument leer ist, dann geben null zurück. Die Suche ist beendet.
        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            //The Query funktioniert auch mit obj.get("MeshHeadings").
            QueryBuilder qb = matchQuery("MeshHeadings", meshTerms).minimumShouldMatch("40%");
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();
            ArrayList<String> resultIds = new ArrayList<String>();
            for (SearchHit hit : antwort.getHits()) {
                resultIds.add(hit.getId().toString());
            }
            return resultIds;
        } catch (Exception e){

        }
        return null;
    }
    private static ArrayList<String> searchByMoreLikeThis( JSONObject obj, ArrayList<String> ids){
        ArrayList<String> result = new ArrayList<String>();
        try{
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            String[] field = {"Abstract"};
            MoreLikeThisQueryBuilder.Item[] items = null;
            String[] text = {obj.get("Abstract").toString()};
            QueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("PMID",ids)).minimumShouldMatch(1)
                    .must(moreLikeThisQuery(field, text, items).minTermFreq(2).minDocFreq(1).maxQueryTerms(10).minimumShouldMatch("50%"));
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();

            for(SearchHit hit : antwort.getHits()){
                result.add(hit.getId());
            }
            return result;

        } catch (Exception e){

        }
        return null;
    }

    private static ArrayList<JsonObject> searchByRelevanceList(ArrayList<String> ids){
        System.out.println("Ids:" + ids.size());
        ArrayList<JsonObject> result = new ArrayList<JsonObject>();

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            GetResponse relevanceList  = client.prepareGet("semesterprojekt", "item_list", "1").get();
            QueryBuilder qb = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("PMID",ids)).minimumShouldMatch(1)
                    .must(matchQuery("Abstract",relevanceList.getSource()).minimumShouldMatch("10%"));
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("document").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
                    .get();
            Map map = null;

            String output = antwort.toString();
            SearchHits hits = antwort.getHits();
            for (SearchHit hit : hits) {
                map = hit.getSource();

                Gson gson = new Gson();
                result.add(gson.toJsonTree(map).getAsJsonObject());
                return result;
            }

        } catch (Exception e){

        }
        return null;
    }

}
