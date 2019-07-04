package com.comma.fit.authentication.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtToken implements AuthenticationToken {

    private String token;

    @Override
    public Object getPrincipal() {

        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
