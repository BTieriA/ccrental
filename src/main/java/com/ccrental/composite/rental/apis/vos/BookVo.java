package com.ccrental.composite.rental.apis.vos;

public class BookVo {
    private final String userName;
    private final String userContact;
    private final String userEmail;
    private final int userLevel;
    private final int userIndex;
    private final int rentalIndex;

    public BookVo(String userName, String userContact, String userEmail, int userLevel, int userIndex, int rentalIndex) {
        this.userName = userName;
        this.userContact = userContact;
        this.userEmail = userEmail;
        this.userLevel = userLevel;
        this.userIndex = userIndex;
        this.rentalIndex = rentalIndex;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public int getRentalIndex() {
        return rentalIndex;
    }
}
