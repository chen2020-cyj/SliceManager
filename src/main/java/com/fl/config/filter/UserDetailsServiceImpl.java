package com.fl.config.filter;

import com.fl.config.bean.JwtUserDto;
import com.fl.entity.User;
import com.fl.service.MenuService;
import com.fl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String s){
        JwtUserDto jwtUserDto = null;
        User user;
        try {
            user = userService.selectByUser(s);
            System.out.println(user);
        }catch (Exception e){
            throw new UsernameNotFoundException("",e);
        }
        if (user == null){
            System.out.println("用户为空");
            throw new UsernameNotFoundException("");
        }else {
            List<GrantedAuthority> grantedAuthorities = menuService.mapToGrantedAuthorities(user);
            System.out.println(grantedAuthorities);
            jwtUserDto = new JwtUserDto(
                    user,
                    menuService.mapToGrantedAuthorities(user)
                     );
        }
        return jwtUserDto;
    }
}
