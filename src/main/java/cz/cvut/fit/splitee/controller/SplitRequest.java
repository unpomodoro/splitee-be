package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.helper.TYPE;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;

@AllArgsConstructor
final class SplitObject {
    Long memberId;
    BigDecimal value;
    BigDecimal amount;
}

@AllArgsConstructor
public class SplitRequest {
    Long billId;
    Long payerId;
    BigDecimal amount; //amount paid by payer. It is separate because payer might not be participant in a bill share
    TYPE type;
    ArrayList<SplitObject> splits;
}
