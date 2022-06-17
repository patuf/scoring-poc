package com.example.demo;

import com.example.demo.bo.LoanContract;
import com.example.demo.bo.PublicDataOutbox;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.bo.LoanContract.ScoringStatus.PROCESSING;

@Component
public class ApplicantDataDao {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private MongoTemplate mongoTmpl;

    @Transactional(rollbackFor = { LoanInProgressException.class })
    public void storeApplicantData(LoanContract lc) throws LoanInProgressException {
        lc.setScoringStatus(PROCESSING);
        Query qry = new Query(Criteria.where("_id").is(lc.getId()).and("scoringStatus").ne(PROCESSING));

        try {
            mongoTmpl.update(LoanContract.class)
                    .matching(qry)
                    .replaceWith(lc)
                    .withOptions(FindAndReplaceOptions.options().upsert())
                    .findAndReplace();
        } catch (DuplicateKeyException ex) {
            throw new LoanInProgressException();
        }
        if (lc.getPublicDataRaw() != null) {
            PublicDataOutbox pbo = new PublicDataOutbox(lc.getId(), lc.getDateTime(), lc.getCustomerId(), lc.getPublicDataRaw());
            mongoTmpl.insert(pbo);
        } else {
            log.info("Not Sending in PublicData outbox");
        }
    }
}
