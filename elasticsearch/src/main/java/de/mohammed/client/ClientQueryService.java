package de.mohammed.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface ClientQueryService {

    @GET
    @Path("/document/{id}")
    public Response getDocumentByID(@PathParam("id") int pMID);
}
