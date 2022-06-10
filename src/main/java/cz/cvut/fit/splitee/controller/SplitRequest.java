package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.helper.TYPE;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
final class SplitObject {
    Long memberId;
    BigDecimal value;
    BigDecimal amount;
}

@AllArgsConstructor
@NoArgsConstructor
public class SplitRequest {
    Long billId;
    Long payerId;
    BigDecimal amount; //amount paid by payer. It is separate because payer might not be participant in a bill share
    TYPE type;
    ArrayList<SplitObject> splits;

    public SplitRequest(Long billId, Long payerId, BigDecimal amount, String type, ArrayList<SplitObject> splits) {
        this.billId = billId;
        this.payerId = payerId;
        this.amount = amount;
        this.splits = splits;
        switch (type) {
            case "EQUAL" -> this.type = TYPE.EQUAL;
            case "EXACT" -> this.type = TYPE.EXACT;
            case "PERCENTAGE" -> this.type = TYPE.PERCENTAGE;
            case "SHARE" -> this.type = TYPE.SHARE;
        }
    }
}
