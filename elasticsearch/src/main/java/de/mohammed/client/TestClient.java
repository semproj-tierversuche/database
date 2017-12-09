package de.mohammed.client;


import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException{
        Client client = ClientBuilder.newClient();

        // client.register(new LoggingFilter());
        WebTarget target = client.target("http://localhost:8081/import");
        ResteasyWebTarget rtarget = (ResteasyWebTarget)target;
        ClientImportService importService=rtarget.proxy(ClientImportService.class);
        BufferedReader fileReader = new BufferedReader(new FileReader("/home/yogamapple/Dropbox/Die Höhle des Löwen/Uni/5. Semester/Semesterprojekt/Beispieleingaben/beispiel1.json"));
        StringBuilder tBuilder=new StringBuilder();
        String line=null;
        while ((line=fileReader.readLine())!=null) {
            tBuilder.append(line).append(System.lineSeparator());
        }
        Response document = importService.parseImport("document", tBuilder.toString());
        //System.out.println(document);


         //client.register(new LoggingFilter());
        target = client.target("http://localhost:8081/query");
        rtarget = (ResteasyWebTarget)target;
        ClientQueryService queryService=rtarget.proxy(ClientQueryService.class);
        Response response = queryService.parseQuery("document", null, 25620913, null);


       // System.out.println(response.readEntity(String.class));
    }

}
