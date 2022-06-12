package com.a4e.service.publicdata;

import com.a4e.service.publicdata.bo.PublicDataOutbox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.io.IOException;

@Service
public class PublicDataController {
//    @KafkaListener(topics = "#{'${avro.topic.name}'}")
    private final Log log = LogFactory.getLog(getClass());

    @KafkaListener(topics = "pdRequest")
//    @Validated
    public void subscribe(PublicDataOutbox pdMessage) throws IOException {
        log.info(String.format("Consumed Message -> %s", pdMessage.getPublicDataRaw()));
    }
}
