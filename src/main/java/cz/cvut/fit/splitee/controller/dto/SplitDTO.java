package cz.cvut.fit.splitee.controller.dto;

import cz.cvut.fit.splitee.helper.TYPE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SplitDTO {

    private Long billId;
    private Long memberId;
    private BigDecimal amount;
    private TYPE type;
    private BigDecimal value;
    private boolean isPayer;
    private BigDecimal amountPaid;
}
