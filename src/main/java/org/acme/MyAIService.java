package org.acme;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface MyAIService {

    //@SystemMessage("based on the current cost rates estimate a cost for this. Also provide a field for total costPerHour,costPerWeek and costPerMonth")
    public String chat (@UserMessage String message);
}
