package com.example.demo;

import com.example.demo.bo.LoanContract;
import com.example.demo.bo.PublicDataOutbox;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.messaging.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.time.Duration;
import java.util.concurrent.Executor;

import static com.example.demo.bo.LoanContract.ScoringStatus.PROCESSING;

@Component
public class MongoTransactionOutbox extends DefaultMessageListenerContainer implements InitializingBean {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private KafkaTemplate<String,PublicDataOutbox> kafkaTmpl;
    @Autowired
    private MessageListenerContainer mlContainer;
    @Autowired
    private MongoTemplate mongoTmpl;

    public MongoTransactionOutbox(MongoTemplate mongoTmpl, KafkaTemplate<String,PublicDataOutbox> kafkaTmpl, MessageListenerContainer mlContainer) {
        super(mongoTmpl);

        this.mongoTmpl = mongoTmpl;
        this.kafkaTmpl = kafkaTmpl;
        this.mlContainer = mlContainer;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
//        if (!isRunning())
//            start();
        MessageListener<ChangeStreamDocument<Document>, PublicDataOutbox> listener = (Message<ChangeStreamDocument<Document>, PublicDataOutbox> msg) -> {
            log.info("EVENT TRIGGERED MON!!");
            mongoTmpl.remove(msg.getBody());
            kafkaTmpl.send("pdRequest", msg.getBody().getId(), msg.getBody());
            log.info("ROW REMOVED MON!!" + msg.getBody());
            // KAFKA CALL

//            resp.append("scoringData", msg.getBody().getScoringData());
//            log.info("appending");
//            System.out.println(msg.getRaw());
//            synchronized(mutex) {
//                log.info("notifying");
//                mutex.notify();
//                log.info("notified lol bye");
//            }
        };
//        Aggregation listenReadiness = Aggregation.newAggregation(Aggregation.match(Criteria.where("operationType").is("update").and("documentKey._id").is(lc.getId()).and("fullDocument.scoringStatus").ne(PROCESSING)));
        Aggregation listenReadiness = Aggregation.newAggregation(Aggregation.match(Criteria.where("operationType").is("insert")));
        // TODO: resumeAt - store token on disk, then use it
        ChangeStreamOptions csOpts = ChangeStreamOptions.builder().filter(listenReadiness).build();

//                Aggregates.match(Filters.in("operationType","insert", "update")));
        ChangeStreamRequest.ChangeStreamRequestOptions options = new ChangeStreamRequest.ChangeStreamRequestOptions(
                "db1",
                "orchestrator_pdOutbox",
                csOpts
        );

        Subscription subscription = mlContainer.register(new ChangeStreamRequest<>(listener, options), PublicDataOutbox.class);

    }
}
