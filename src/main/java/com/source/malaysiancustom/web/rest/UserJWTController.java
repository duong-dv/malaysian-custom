package com.source.malaysiancustom.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.source.malaysiancustom.domain.User;
import com.source.malaysiancustom.repository.UserRepository;
import com.source.malaysiancustom.security.UserNotActivatedException;
import com.source.malaysiancustom.security.jwt.JWTFilter;
import com.source.malaysiancustom.security.jwt.TokenProvider;
import com.source.malaysiancustom.service.UserService;
import com.source.malaysiancustom.web.rest.vm.LoginVM;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {
    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        Optional<User> userOpt = userService.getUserWithAuthoritiesByLogin(loginVM.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );
        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            userOpt = userService.getUserWithAuthoritiesByLogin(loginVM.getUsername());
            if (userOpt.isPresent()) {
                User data = userOpt.get();
                if (data.getLoginFalse() <= 1) {
                    data.setActivated(false);
                    data.setLoginFalse(0L);
                    userRepository.save(data);
                    throw new BadCredentialsException("Account has been locked. Please contact the administrator");
                } else {
                    data.setLoginFalse(data.getLoginFalse() - 1);
                    userRepository.save(data);
                    throw new BadCredentialsException(
                        String.format("Invalid User ID/Password. Number of attempts left %d", data.getLoginFalse())
                    );
                }
            }

            throw new BadCredentialsException("Invalid User ID/Password");
        }
        User user = userOpt.get();
        user.setLoginFalse(3L);
        userRepository.save(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
        String jwt = tokenProvider.createToken(authentication, rememberMe);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {
        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
