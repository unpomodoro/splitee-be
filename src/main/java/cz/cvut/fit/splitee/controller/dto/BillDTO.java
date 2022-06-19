package cz.cvut.fit.splitee.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {

    private Long id;
    private String description;
    private BigDecimal amount;
    private MembershipDTO payer;
    private Timestamp date;
    private String notes;

    public BillDTO(Long id,String description, BigDecimal amount, Timestamp date, String notes) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }
}
