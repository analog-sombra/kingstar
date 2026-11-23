package com.librarymanagement.util;

import java.util.regex.Pattern;

public final class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,16}$");

    private PasswordValidator() {
    }

    public static boolean isValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}
