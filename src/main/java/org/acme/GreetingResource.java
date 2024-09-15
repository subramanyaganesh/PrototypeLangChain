package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.io.FileInputStream;
import java.io.InputStream;

@Path("/")
public class GreetingResource {

    @Inject
    MyAIService myAIService;

    @Inject
    ClaudeService openAIService;

    @GET
    @Path("/upload")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile() {
        try {
            InputStream fileInputStream = new FileInputStream("/home/subramanyaganesh/Desktop/adhadshjk.json");
            String response = openAIService.uploadFileToOpenAI(fileInputStream, "file.txt");

            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    @GET
    @Path("/upload1")
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFileClaude() {
        try {
            String response = openAIService.askClaude("For each of the physical component provide the corresponding aws implementation",
                    "/home/subramanyaganesh/Desktop/adhadshjk.json");

            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/get")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "I am here";
    }

    @POST
    @Path("/chat")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String hello(String body) {

        return myAIService.chat(body);

    }
}
