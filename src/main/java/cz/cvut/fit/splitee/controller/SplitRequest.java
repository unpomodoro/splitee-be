package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.helper.TYPE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
final class SplitObject {
    Long memberId;
    BigDecimal value;
    BigDecimal amount;

    @Override
    public String toString() {
        return "SplitObject{" +
                "memberId=" + memberId +
                ", value=" + value +
                ", amount=" + amount +
                '}';
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SplitRequest {
    Long billId;
    Long payerId;
    BigDecimal amount; //amount paid by payer. It is separate because payer might not be participant in a bill share
    TYPE type;
    ArrayList<SplitObject> splits;

    @Override
    public String toString() {
        return "SplitRequest{" +
                "billId=" + billId +
                ", payerId=" + payerId +
                ", amount=" + amount +
                ", type=" + type +
                ", splits=" + splits +
                '}';
    }

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
