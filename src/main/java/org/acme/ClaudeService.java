package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataOutput;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class ClaudeService {

    @ConfigProperty(name = "quarkus.langchain4j.openai.api-key")
    String OPENAI_API_KEY;


    @ConfigProperty(name = "quarkus.langchain4j.claude.api-key")
    String ANTHROPIC_API_KEY ;

    public String askClaude(String prompt, String filePath) {
        HttpClient client = HttpClient.newHttpClient();

        try {
            // Read file content
            String fileContent = Files.readString(Path.of(filePath));

            // Create the prompt by appending the file content to it
            prompt = prompt.trim() + "\n" + fileContent;

            // Create a JSON object for the messages
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");

            // Add text prompt to the message content
            JSONArray contentArray = new JSONArray();
            contentArray.put(new JSONObject().put("type", "text").put("text", prompt));

            messageObject.put("content", contentArray);

            // Create the full request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "claude-3-sonnet-20240229");
            requestBody.put("max_tokens", 4096);
            requestBody.put("messages", new JSONArray().put(messageObject));

            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("X-API-Key", ANTHROPIC_API_KEY)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check the response
            if (response.statusCode() == 200) {
                // Extract and return the response content
                JSONObject responseBody = new JSONObject(response.body());
                return responseBody.getJSONArray("content").getJSONObject(0).getString("text");
            } else {
                System.out.println("API Error: " + response.body());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error processing request: " + e.getMessage());
            return null;
        }
    }

    public String uploadFileToOpenAI(InputStream fileInputStream, String filename) {
        // Create a multipart form data request
        System.out.println("===================================");
        System.out.println(OPENAI_API_KEY);
        System.out.println("===================================");
        MultipartFormDataOutput formData = new MultipartFormDataOutput();
        formData.addFormData("file", fileInputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE, filename);
        formData.addFormData("purpose", "fine-tune", MediaType.TEXT_PLAIN_TYPE);

        Client client = ClientBuilder.newClient();
        Response response = client.target("https://api.openai.com/v1/files").request(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + OPENAI_API_KEY).post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA));

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to upload file to OpenAI: " + response.getStatus());
        }

        return response.readEntity(String.class);  // Get response body
    }
}
