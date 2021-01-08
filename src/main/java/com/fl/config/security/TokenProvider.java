package com.fl.config.security;


import com.fl.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider{
    public static final String AUTHORITIES_KEY = "auth";


    public Authentication getAuthentication(String token){
        String authoritiesStr = JwtUtils.tokenInfo(token, AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = !StringUtils.isEmpty(authoritiesStr)?
                Arrays.stream(authoritiesStr.toString().replace("[","").replace("]","").split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()): Collections.emptyList();
        User principal = new User(JwtUtils.tokenInfo(token,"name"),"******",authorities);

        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
    }
}
