package cz.cvut.fit.splitee.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SignupRequest {

    private String name;
    private String email;
    private String password;
}
