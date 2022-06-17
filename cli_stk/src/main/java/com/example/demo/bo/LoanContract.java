package com.example.demo.bo;

import org.bson.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@org.springframework.data.mongodb.core.mapping.Document(collection="loanContracts")
public class LoanContract {
    @Id @NotNull
    String id;
    @Indexed @NotNull
    LocalDateTime dateTime;
    @Indexed @NotEmpty
    String customerId;
    @NotNull
    double loanAmount;
    @NotNull
    int loanPeriodDays;
    @NotBlank
    String loanType;
//    @Transient
    Document applicantDataRaw;
//    @Transient
    Document publicDataRaw;
    Document scoringData;
    ScoringStatus scoringStatus;


    public LoanContract(String id, LocalDateTime dateTime, String customerId, double loanAmount, int loanPeriodDays, String loanType, Document applicantDataRaw, Document publicDataRaw) {
        this.id = id;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.loanAmount = loanAmount;
        this.loanPeriodDays = loanPeriodDays;
        this.loanType = loanType;
        this.applicantDataRaw = applicantDataRaw;
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

    public double getLoanAmount() {
        return loanAmount;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public String getLoanType() {
        return loanType;
    }

    public Document getApplicantDataRaw() {
        return applicantDataRaw;
    }

    public Document getPublicDataRaw() {
        return publicDataRaw;
    }

    public ScoringStatus getScoringStatus() {
        return scoringStatus;
    }

    public Document getScoringData() {
        return scoringData;
    }

    public void setScoringData(Document scoringData) {
        this.scoringData = scoringData;
    }

    public void setScoringStatus(ScoringStatus scoringStatus) {
        this.scoringStatus = scoringStatus;
    }

    public enum ScoringStatus {

        COMPLETE,
        PROCESSING,
        TIMEOUT,
        ERROR
    }
}
