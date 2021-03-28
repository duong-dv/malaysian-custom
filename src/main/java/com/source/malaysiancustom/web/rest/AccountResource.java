package com.source.malaysiancustom.web.rest;

import com.source.malaysiancustom.service.UserService;
import com.source.malaysiancustom.service.dto.PasswordChangeDTO;
import com.source.malaysiancustom.service.dto.UserDTO;
import com.source.malaysiancustom.web.rest.errors.InvalidPasswordException;
import com.source.malaysiancustom.web.rest.vm.ManagedUserVM;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserService userService;

    public AccountResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        userService.changePassword(
            passwordChangeDto.getCurrentPassword(),
            passwordChangeDto.getNewPassword(),
            passwordChangeDto.getQuestionOne(),
            passwordChangeDto.getQuestionTwo(),
            passwordChangeDto.getQuestionThree(),
            passwordChangeDto.getQuestionAnswerOne() != null ? passwordChangeDto.getQuestionAnswerOne() : "",
            passwordChangeDto.getQuestionAnswerTwo() != null ? passwordChangeDto.getQuestionAnswerTwo() : "",
            passwordChangeDto.getQuestionAnswerThree() != null ? passwordChangeDto.getQuestionAnswerThree() : ""
        );
    }

    private static boolean checkPasswordLength(String password) {
        return (
            !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH
        );
    }
}
