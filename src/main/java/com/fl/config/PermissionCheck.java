package com.fl.config;


import com.fl.utils.SecurityUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "zz")
public class PermissionCheck {

    public boolean check(String ...permissions){

        List<String> authorities = SecurityUtils.getCurrentUser().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return Arrays.stream(permissions).anyMatch(authorities::contains);
    }


}
