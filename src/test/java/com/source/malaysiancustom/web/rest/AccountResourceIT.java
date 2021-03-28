package com.source.malaysiancustom.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.source.malaysiancustom.MalaysiancustomApp;
import com.source.malaysiancustom.domain.User;
import com.source.malaysiancustom.repository.AuthorityRepository;
import com.source.malaysiancustom.repository.UserRepository;
import com.source.malaysiancustom.service.QuestionEmptyException;
import com.source.malaysiancustom.service.UserService;
import com.source.malaysiancustom.service.dto.PasswordChangeDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountResource} REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = MalaysiancustomApp.class)
public class AccountResourceIT {
    static final String RANDOM = "1234567ABCDEGT";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc restAccountMockMvc;

    @Test
    @Transactional
    @WithMockUser("123")
    public void testChangePasswordNoQuestion() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(8, RANDOM);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("123");
        user.setQuestionOne(0);
        user.setQuestionTwo(0);
        user.setQuestionThree(0);
        user.setQuestionOneAnswer("");
        user.setQuestionTwoAnswer("");
        user.setQuestionThreeAnswer("");
        user.setEmail("testChangePasswordNoQuestion@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
            .perform(
                post("/api/account/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "A2222222", 0, 0, 0, "", "", "")))
            )
            .andExpect(status().isInternalServerError())
            .andExpect(x -> assertTrue(x.getResolvedException() instanceof QuestionEmptyException));

        User updatedUser = userRepository.findOneByLogin("123").orElse(null);
        assertThat(passwordEncoder.matches("A2222222", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("1234")
    public void testChangePasswordWrongExistingPassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(8, RANDOM);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("1234");
        user.setQuestionOne(1);
        user.setQuestionTwo(0);
        user.setQuestionThree(0);
        user.setQuestionOneAnswer("test");
        user.setQuestionTwoAnswer("");
        user.setQuestionThreeAnswer("");
        user.setEmail("testChangePasswordNoQuestion@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
            .perform(
                post("/api/account/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO("A123456B", "A2222222", 1, 0, 0, "test", "", "")))
            )
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("1234").orElse(null);
        assertThat(passwordEncoder.matches("A2222222", updatedUser.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @Transactional
    @WithMockUser("33333")
    public void testChangePassword() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(8, RANDOM);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("33333");
        user.setEmail("change-password@example.com");
        user.setLoginFirst(true);
        user.setQuestionOne(1);
        user.setQuestionTwo(0);
        user.setQuestionThree(0);
        user.setQuestionOneAnswer("test");
        user.setQuestionTwoAnswer("");
        user.setQuestionThreeAnswer("");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
            .perform(
                post("/api/account/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "New12345", 1, 0, 0, "test", "", "")))
            )
            .andExpect(status().isOk());

        User updatedUser = userRepository.findOneByLogin("33333").orElse(null);
        assertThat(passwordEncoder.matches("New12345", updatedUser.getPassword())).isTrue();
        assertThat(updatedUser.isLoginFirst()).isFalse();
    }

    @Test
    @Transactional
    @WithMockUser("555555")
    public void testChangePasswordEmpty() throws Exception {
        User user = new User();
        String currentPassword = RandomStringUtils.random(8, RANDOM);
        user.setPassword(passwordEncoder.encode(currentPassword));
        user.setLogin("555555");
        user.setEmail("change-password-empty@example.com");
        userRepository.saveAndFlush(user);

        restAccountMockMvc
            .perform(
                post("/api/account/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "")))
            )
            .andExpect(status().isBadRequest());

        User updatedUser = userRepository.findOneByLogin("555555").orElse(null);
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
    }
}
