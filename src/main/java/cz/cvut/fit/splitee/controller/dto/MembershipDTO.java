package cz.cvut.fit.splitee.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor
public class MembershipDTO {

    private Long id;
    private String name;
    private String photo;
    private String bankAccount;
    private BigDecimal debt;
    private Long accountId; // to determinate photo change possibility
}
