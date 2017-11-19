package de.mohammed.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path("/test")
public class TestService {

    @Path("/bla/{name}")
    @GET
    public String print(@PathParam("name") String name, @QueryParam("greet") String greet, @QueryParam("number") int number){

        return greet+" "+name+"(number:"+number+")";
    }
}
