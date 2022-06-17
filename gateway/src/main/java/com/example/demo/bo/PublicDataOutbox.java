package com.example.demo.bo;

import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@org.springframework.data.mongodb.core.mapping.Document(collection="orchestrator_pdOutbox")
public class PublicDataOutbox {
    @Id @NotNull
    String id;
    @Indexed @NotNull
    LocalDateTime dateTime;
    @Indexed @NotEmpty
    String customerId;
    Document publicDataRaw;


    public PublicDataOutbox(String id, LocalDateTime dateTime, String customerId, Document publicDataRaw) {
        this.id = id;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.publicDataRaw = publicDataRaw;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Document getPublicDataRaw() {
        return publicDataRaw;
    }
}
