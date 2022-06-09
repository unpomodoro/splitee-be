package cz.cvut.fit.splitee.dto;

import cz.cvut.fit.splitee.helper.TYPE;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
public class BillDTO {

    private String description;
    private BigDecimal amount;
    private MembershipDTO payer;
    private Timestamp date;
    private String notes;
    private TYPE type;
}
