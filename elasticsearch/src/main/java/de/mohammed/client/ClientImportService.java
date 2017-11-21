package de.mohammed.client;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public interface ClientImportService {
    @PUT
    @Path("/")
    @Consumes("text/plain")
    public Response parseImport(@QueryParam("type") String type, String document);
}
