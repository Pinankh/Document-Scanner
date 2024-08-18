package com.camscanner.paperscanner.pdfcreator.features.subscription;

public class UserRepository {
    private static volatile UserRepository INSTANCE;

    public static UserRepository get() {
        if (INSTANCE == null) {
            synchronized (UserRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserRepository();
                }
            }
        }
        return INSTANCE;
    }

    private UserRepository() {
    }




}
