package cz.cvut.fit.splitee.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private Collection<String> groups;
    private String type = "Bearer";

    public JwtResponse(String token, Long id, String email, Collection<String> groupCodes) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.groups = groupCodes;
    }
}
