package cz.cvut.fit.splitee.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AccountDTO {

    private String email;
    private String password;
    private String name;
    private String photo;
    private String bankAccount;
}
