package cz.cvut.fit.splitee.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequest {
    String email;
    String password;
}
