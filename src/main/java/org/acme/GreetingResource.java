package org.acme;

import dev.ai4j.openai4j.OpenAiClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/")
public class GreetingResource {

    @Inject
    MyAIService myAIService;

    @GET
    @Path("get")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "I am here";
    }

    @POST
    @Path("chat")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String hello(String body) {
        return myAIService.chat(body);
    }
}
