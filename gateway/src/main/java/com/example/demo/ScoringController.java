package com.example.demo;

import com.example.demo.bo.LoanContract;
import com.example.demo.bo.PublicDataOutbox;
import com.example.demo.dumpster.LoanContractRepository;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.messaging.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;

import static com.example.demo.bo.LoanContract.ScoringStatus.PROCESSING;


@RestController()
@RequestMapping("/v1.0/scoring")
public class ScoringController {

    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private LoanContractRepository lcRepo;
    @Autowired
    private MongoTemplate mongoTmpl;
//    @Autowired
//    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MessageListenerContainer mlContainer;
    @Autowired
    ApplicantDataDao adDao;

    @GetMapping("findById/{id}")
    public String index(@PathVariable String id) {
//        cityRepo.save(new CityBean(id, "2110"));
        LoanContract lc = lcRepo.findById(id);
        if (lc != null) {
            return "Ebago otkyde!: " + lc.getApplicantDataRaw();
        }
        return "Maduro Grandeur: " + id;
    }

    @PostMapping(path="/create", consumes = "application/json")
    @Validated
    public Document create(@Valid @RequestBody LoanContract lc) {

        try {
            adDao.storeApplicantData(lc);
        } catch (LoanInProgressException e) {
            return new Document().append("status", "Fail: Loan is being processed!");
        }



        final Object mutex = new Object();
        if (!mlContainer.isRunning())
            mlContainer.start();
        final Document resp = new Document();

        synchronized (mutex) {
            try {
                mutex.wait(10000l);
            } catch (InterruptedException e) {
                return new Document().append("status", "Fail: Please try again!");
            }
        }
        return resp;
    }

    public void setLcRepo(LoanContractRepository lcRepo) {
        this.lcRepo = lcRepo;
    }

    public void setMongoTmpl(MongoTemplate mongoTmpl) {
        this.mongoTmpl = mongoTmpl;
    }

//    public void setKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void setMlContainer(MessageListenerContainer mlContainer) {
        this.mlContainer = mlContainer;
    }

    public void setAdDao(ApplicantDataDao adDao) {
        this.adDao = adDao;
    }
}