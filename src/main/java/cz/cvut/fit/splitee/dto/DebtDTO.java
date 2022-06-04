package cz.cvut.fit.splitee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter @Setter
@AllArgsConstructor
public class DebtDTO {

    private MembershipDTO owes;
    private MembershipDTO getsBack;
    private BigDecimal amount;
}
