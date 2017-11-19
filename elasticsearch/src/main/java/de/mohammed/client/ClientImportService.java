package de.mohammed.client;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface ClientImportService {
    @Path("/insert")
    @PUT
    @Consumes("text/plain")
    public Response createDocument(String document);
}
