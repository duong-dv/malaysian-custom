package com.source.malaysiancustom.service;

import com.source.malaysiancustom.config.Constants;
import com.source.malaysiancustom.domain.Authority;
import com.source.malaysiancustom.domain.User;
import com.source.malaysiancustom.repository.AuthorityRepository;
import com.source.malaysiancustom.repository.UserRepository;
import com.source.malaysiancustom.security.AuthoritiesConstants;
import com.source.malaysiancustom.security.SecurityUtils;
import com.source.malaysiancustom.service.dto.UserDTO;
import io.github.jhipster.security.RandomUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public void changePassword(
        String currentClearTextPassword,
        String newPassword,
        int questionOne,
        int questionTwo,
        int questionThree,
        String questionAnswerOne,
        String questionAnswerTwo,
        String questionAnswerThree
    ) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(
                user -> {
                    if (questionOne == 0 && questionTwo == 0 && questionThree == 0) {
                        throw new QuestionEmptyException();
                    } else {
                        if (questionOne != 0 && questionAnswerOne.isEmpty()) {
                            throw new QuestionEmptyException();
                        }
                        if (questionTwo != 0 && questionAnswerTwo.isEmpty()) {
                            throw new QuestionEmptyException();
                        }
                        if (questionThree != 0 && questionAnswerThree.isEmpty()) {
                            throw new QuestionEmptyException();
                        }
                    }
                    if (user.isLoginFirst()) {
                        user.setQuestionOne(questionOne);
                        user.setQuestionTwo(questionTwo);
                        user.setQuestionThree(questionThree);
                        user.setQuestionOneAnswer(questionOne != 0 ? questionAnswerOne : "");
                        user.setQuestionTwoAnswer(questionTwo != 0 ? questionAnswerTwo : "");
                        user.setQuestionThreeAnswer(questionThree != 0 ? questionAnswerThree : "");
                    } else {
                        if (
                            !(
                                questionOne == user.getQuestionOne() &&
                                questionTwo == user.getQuestionTwo() &&
                                questionThree == user.getQuestionThree() &&
                                questionAnswerOne.equals(user.getQuestionOneAnswer()) &&
                                questionAnswerTwo.equals(user.getQuestionTwoAnswer()) &&
                                questionAnswerThree.equals(user.getQuestionThreeAnswer())
                            )
                        ) {
                            throw new NotCorrectException();
                        }
                    }
                    String currentEncryptedPassword = user.getPassword();
                    user.setLoginFirst(false);

                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw new InvalidPasswordException();
                    }
                    String encryptedPassword = passwordEncoder.encode(newPassword);
                    user.setPassword(encryptedPassword);
                    log.debug("Changed password for User: {}", user);
                }
            );
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }
}
