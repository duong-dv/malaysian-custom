package com.source.malaysiancustom.service.dto;

/**
 * A DTO representing a password change required data - current and new password.
 */
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;

    private int questionOne;
    private int questionTwo;
    private int questionThree;

    private String questionAnswerOne;
    private String questionAnswerTwo;
    private String questionAnswerThree;

    public PasswordChangeDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordChangeDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public PasswordChangeDTO(
        String currentPassword,
        String newPassword,
        int questionOne,
        int questionTwo,
        int questionThree,
        String questionAnswerOne,
        String questionAnswerTwo,
        String questionAnswerThree
    ) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.questionOne = questionOne;
        this.questionTwo = questionTwo;
        this.questionThree = questionThree;
        this.questionAnswerOne = questionAnswerOne;
        this.questionAnswerTwo = questionAnswerTwo;
        this.questionAnswerThree = questionAnswerThree;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public int getQuestionOne() {
        return questionOne;
    }

    public int getQuestionTwo() {
        return questionTwo;
    }

    public int getQuestionThree() {
        return questionThree;
    }

    public String getQuestionAnswerOne() {
        return questionAnswerOne;
    }

    public String getQuestionAnswerTwo() {
        return questionAnswerTwo;
    }

    public String getQuestionAnswerThree() {
        return questionAnswerThree;
    }

    public void setQuestionOne(int questionOne) {
        this.questionOne = questionOne;
    }

    public void setQuestionTwo(int questionTwo) {
        this.questionTwo = questionTwo;
    }

    public void setQuestionThree(int questionThree) {
        this.questionThree = questionThree;
    }

    public void setQuestionAnswerOne(String questionAnswerOne) {
        this.questionAnswerOne = questionAnswerOne;
    }

    public void setQuestionAnswerTwo(String questionAnswerTwo) {
        this.questionAnswerTwo = questionAnswerTwo;
    }

    public void setQuestionAnswerThree(String questionAnswerThree) {
        this.questionAnswerThree = questionAnswerThree;
    }
}
