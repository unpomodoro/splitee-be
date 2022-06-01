package cz.cvut.fit.splitee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MembershipDTO {

    private Long id;
    private String name;
    private String photo;
    private String bankAccount;
}
