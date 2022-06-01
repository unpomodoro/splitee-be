package cz.cvut.fit.splitee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter @Setter
@AllArgsConstructor
public class DebtDTO {

    private Long owes;
    private Long getsBack;
    private BigDecimal amount;
}
