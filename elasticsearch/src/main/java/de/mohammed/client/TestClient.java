package de.mohammed.client;


import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;

public class TestClient {
    public static void main(String[] args) throws IOException{

        createType("first", "attempt");
        /*Client client = ClientBuilder.newClient();

        // client.register(new LoggingFilter());
        WebTarget target = client.target("http://localhost:8081/import");
        ResteasyWebTarget rtarget = (ResteasyWebTarget)target;
        ClientImportService importService=rtarget.proxy(ClientImportService.class);
        BufferedReader fileReader = new BufferedReader(new FileReader("C:\\\\Users\\user\\Desktop\\Das Studium\\Das 05. Semester\\Semesterprojekt\\git\\database\\Beispieleingaben\\1527573.json"));
        StringBuilder tBuilder=new StringBuilder();
        String line=null;
        while ((line=fileReader.readLine())!=null) {
            tBuilder.append(line).append("\n");
        }
        System.out.println(tBuilder.toString());
        Response document = importService.parseImport("document", tBuilder.toString());
        System.out.println(document.getEntity());


        // client.register(new LoggingFilter());
//        target = client.target("http://localhost:8081/query");
//        rtarget = (ResteasyWebTarget)target;
//        ClientQueryService queryService=rtarget.proxy(ClientQueryService.class);
//        Response response = queryService.parseQuery("document", null, 25620913, null);


       // System.out.println(response.readEntity(String.class));
    */}
    private static void createType(String index, String type) {

        try {
            TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).
                    addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            if (client.admin().cluster().prepareState().execute().actionGet().getState()
                    .getMetaData().hasIndex(index)) {
                client.admin().indices().preparePutMapping(index)
                        .setType(type)
                        .setSource("{\"properties\":{" +
                                "\"Abstract\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"AbstractContent\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Annotations\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Author\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Authors\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Date\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Identifier\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Journal\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Keywords\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Link\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"MeshHeadings\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"PMID\":{\"type\":\"long\"}," +
                                "\"PublicationType\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Suggest\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"TextminingVersion\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Title\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"suggest\":{\"properties\":{\"input\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}}}"
                        )
                        .get();
            } else {
                client.admin().indices().prepareCreate(index)
                        .addMapping(type, "{\"properties\":{" +
                                "\"Abstract\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"AbstractContent\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Annotations\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Author\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Authors\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Date\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Identifier\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Journal\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Keywords\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Link\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"MeshHeadings\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"PMID\":{\"type\":\"long\"}," +
                                "\"PublicationType\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Suggest\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"TextminingVersion\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"Title\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}," +
                                "\"suggest\":{\"properties\":{\"input\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\",\"ignore_above\":256}}}}}}}"
                        )
                        .get();
            }
        } catch (Exception e) {
            System.out.println("Etwas ist schief gelaufen, mein Freund :\\");
        }
    }
}
