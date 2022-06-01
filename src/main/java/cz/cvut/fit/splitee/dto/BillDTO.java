package cz.cvut.fit.splitee.dto;

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
    private Timestamp date;
    private String notes;
}
