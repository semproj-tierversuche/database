package de.mohammed.service;

import com.google.gson.Gson;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.reindex.DeleteByQueryAction.*;

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
                System.out.println("test");
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
    public Response getVersion(@QueryParam("utilsname") String utilsname){

        if(utilsname == null){
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
            return Response.status(404).entity("Not Found "+ utilsname).build();

        }else{
            return Response.status(404).entity("No DB version ").build();
        }
    }


    public Response getDocumentByID(int pMID){

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

        return Response.status(404).entity("Not Found "+ pMID).build();
    }


    public Response getUtils(String utilsname){

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            QueryBuilder qb = termQuery("ResourceName", utilsname);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("utils")
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
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

        return Response.status(404).entity("Not Found "+ utilsname).build();
    }

    public Response getDocumentByTitle(String title) {
        return null;
    }

    public Response deleteDocumentByID(int pMID) {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).
                    addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            //QueryBuilder qb = termQuery("PMID", pMID);
            client.prepareDelete("semesterprojekt", "document", Integer.toString(pMID)).get();
        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(200).entity("ok").build();
    }

    public Response deleteDocumentByTitle(String title) {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).
                    addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            INSTANCE.newRequestBuilder(client)
                    .filter(QueryBuilders.matchQuery("title", title))
                    .source("semesterprojekt")
                    .get();

        } catch (Exception e) {

            return Response.status(500).entity("Fehler in Elasticsearch: " + e).build();
        }

        return Response.status(200).entity("ok").build();
    }

}
