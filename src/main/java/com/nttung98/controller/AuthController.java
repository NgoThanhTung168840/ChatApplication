package com.nttung98.controller;

import com.google.gson.Gson;
import com.nttung98.dto.AuthUserDTO;
import com.nttung98.dto.GroupDTO;
import com.nttung98.dto.InitUserDTO;
import com.nttung98.dto.JwtDTO;
import com.nttung98.entity.Group;
import com.nttung98.entity.User;
import com.nttung98.mapper.GroupMapper;
import com.nttung98.mapper.UserMapper;
import com.nttung98.service.CustomUserDetailsService;
import com.nttung98.service.GroupService;
import com.nttung98.service.UserService;
import com.nttung98.utils.JwtUtil;
import com.nttung98.utils.StaticVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMapper groupMapper;
    

    @GetMapping(value = "/fetch")
    public InitUserDTO fetchInformation(HttpServletRequest request) {
        return userMapper.toUserDTO(getUserEntity(request));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping(value = "/create")
    public GroupDTO createGroupChat(HttpServletRequest request, @RequestBody String payload) {
        User user = getUserEntity(request);
        Gson gson = new Gson();
        GroupDTO groupDTO = gson.fromJson(payload, GroupDTO.class);
        Group group = groupService.createGroup(user.getId(), groupDTO.getName());
        return groupMapper.toGroupDTO(group, user.getId());
    }

    @PostMapping(value = "/auth")
    public AuthUserDTO createToken(@RequestBody JwtDTO authenticationRequest, HttpServletResponse response) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        User user = userService.findByNameOrEmail(authenticationRequest.getUsername(), authenticationRequest.getUsername());
        String token = jwtUtil.genToken(userDetails);
        Cookie jwtAuthToken = new Cookie(StaticVariable.SECURE_COOKIE, token);
        jwtAuthToken.setHttpOnly(true);
        jwtAuthToken.setSecure(false);
        jwtAuthToken.setPath("/");
        jwtAuthToken.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(jwtAuthToken);
        return userMapper.toLightUserDTO(user);
    }

    @GetMapping(value = "/logout")
    public ResponseEntity<?> fetchInformation(HttpServletResponse response) {
        Cookie cookie = new Cookie(StaticVariable.SECURE_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    private User getUserEntity(HttpServletRequest request) {
        String username;
        String jwtToken;
        User user = new User();
        Cookie cookie = WebUtils.getCookie(request, StaticVariable.SECURE_COOKIE);
        if (cookie != null) {
            jwtToken = cookie.getValue();
            username = jwtUtil.getUserNameFromJwtToken(jwtToken);
            user = userService.findByNameOrEmail(username, username);
        }
        return user;
    }
}