package de.mohammed.service;

import com.google.gson.Gson;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Path("/query")
public class QueryService {

    @GET
    @Path("/document/{id}")
    public Response getDocumentByID(@PathParam("id") int pMID){

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

            QueryBuilder qb = termQuery("PMID", pMID);
            SearchResponse antwort = client.prepareSearch("semesterprojekt").setTypes("pubmed").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb)                 // Query
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
}
