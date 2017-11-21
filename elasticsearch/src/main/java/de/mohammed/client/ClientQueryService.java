package de.mohammed.client;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

public interface ClientQueryService {


    @GET
    @Path("/")
    public Response parseQuery(@QueryParam("type") String type,
                               @QueryParam("utilsName") String utilsname,
                               @QueryParam("PMID") @DefaultValue("-1") int pMID,
                               @QueryParam("docTitle") String title);
}
