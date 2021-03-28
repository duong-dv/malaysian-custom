package com.source.malaysiancustom.web.rest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.source.malaysiancustom.MalaysiancustomApp;
import com.source.malaysiancustom.domain.User;
import com.source.malaysiancustom.repository.UserRepository;
import com.source.malaysiancustom.web.rest.vm.LoginVM;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test login
 * Integration tests for the {@link UserJWTController} REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = MalaysiancustomApp.class)
public class UserJWTControllerIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Validator validator;

    /**
     * UserId and passWd are correct ->  login Success
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void testAuthorize() throws Exception {
        User user = new User();
        user.setLogin("123456789");
        user.setEmail("testAuthorize@example.com");
        user.setActivated(true);
        user.setLoginFalse(3L);
        user.setLoginFirst(true);
        user.setPassword(passwordEncoder.encode("A1234567"));

        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("123456789");
        login.setPassword("A1234567");

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(is(emptyString()))));
    }

    /**
     * UserId invalid -> Login Fail
     *
     * @throws Exception
     */
    @Test
    public void testInvalidUserIdLogin() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("A12345678");
        login.setPassword("A1234567");

        Set<ConstraintViolation<LoginVM>> errors = validator.validate(login);

        Assert.assertFalse(errors.isEmpty());

        errors.forEach(x -> Assert.assertEquals(x.getMessage(), "error.userid.invalid"));

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Length UserId > 16 -> Login Fail
     *
     * @throws Exception
     */
    @Test
    public void testLengthUserIdLogin() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("123456789123456789");
        login.setPassword("A1234567");

        Set<ConstraintViolation<LoginVM>> errors = validator.validate(login);

        Assert.assertFalse(errors.isEmpty());

        errors.forEach(x -> Assert.assertEquals(x.getMessage(), "error.userid.length"));

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Length passWd != 8 -> Login Fail
     *
     * @throws Exception
     */
    @Test
    public void testLengthPassWdLogin() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("123456789123456789");
        login.setPassword("A1234567");

        Set<ConstraintViolation<LoginVM>> errors = validator.validate(login);

        Assert.assertFalse(errors.isEmpty());

        errors.forEach(x -> Assert.assertEquals(x.getMessage(), "error.userid.length"));

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isBadRequest());
    }

    /**
     * UserId and PassWd wrong -> Login Fail
     *
     * @throws Exception
     */
    @Test
    public void testAuthorizeFails() throws Exception {
        User user = new User();
        user.setLogin("123456");
        user.setEmail("testAuthorizeFails@example.com");
        user.setActivated(true);
        user.setLoginFalse(3L);
        user.setLoginFirst(true);
        user.setPassword(passwordEncoder.encode("A1234567"));

        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("12345");
        login.setPassword("ABC12345");

        Set<ConstraintViolation<LoginVM>> errors = validator.validate(login);

        Assert.assertTrue(errors.isEmpty());

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }

    /**
     * UserId exists and passWs wrong -> Login fail, login fail -1
     *
     * @throws Exception
     */
    @Test
    public void testUserIdExistsPassWdWrong() throws Exception {
        User user = new User();
        user.setLogin("12345");
        user.setEmail("testUserIdExistsPassWdWrong@example.com");
        user.setActivated(true);
        user.setLoginFalse(3L);
        user.setLoginFirst(true);
        user.setPassword(passwordEncoder.encode("A1234567"));

        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("12345");
        login.setPassword("A123456B");

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized());

        User userResult = userRepository.findOneByLogin("12345").get();
        Assert.assertEquals(userResult.getLoginFalse().longValue(), 2);
    }

    /**
     * Login incorrectly 3 times -> lock accout
     * @throws Exception
     */
    @Test
    public void testAccountLockout() throws Exception {
        User user = new User();
        user.setLogin("888888");
        user.setEmail("testAccountLockout@example.com");
        user.setActivated(true);
        user.setLoginFalse(3L);
        user.setLoginFirst(true);
        user.setPassword(passwordEncoder.encode("A1234567"));

        userRepository.saveAndFlush(user);

        LoginVM login = new LoginVM();
        login.setUsername("888888");
        login.setPassword("A123456B");

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized());
        User userResult_1 = userRepository.findOneByLogin("888888").get();

        Assert.assertEquals(userResult_1.getLoginFalse().longValue(), 2);
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized());

        User userResult_2 = userRepository.findOneByLogin("888888").get();

        Assert.assertEquals(userResult_2.getLoginFalse().longValue(), 1);
        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized());

        User userResult_3 = userRepository.findOneByLogin("888888").get();
        Assert.assertEquals(userResult_3.getLoginFalse().longValue(), 0);

        Assert.assertFalse(userResult_3.getActivated());
    }

    @Test
    public void testLoginAccoutLock() throws Exception {
        User user = new User();
        user.setLogin("888888");
        user.setEmail("testAccountLockout@example.com");
        user.setActivated(false);
        user.setLoginFalse(3L);
        user.setLoginFirst(true);
        user.setPassword(passwordEncoder.encode("A1234567"));

        LoginVM login = new LoginVM();
        login.setUsername("888888");
        login.setPassword("A1234567");

        mockMvc
            .perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(login)))
            .andExpect(status().isUnauthorized());
    }
}
