package team3.cashvault.util;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.util.StringUtils;

import team3.cashvault.domain.dto.NewUserDto;
import team3.cashvault.domain.dto.UpdateUserDto;


public class ValidationUtils {
    public static String validateUser(NewUserDto newUser) {
        // Check if email is provided and in valid format
        if (!StringUtils.hasText(newUser.getEmail()) || !isValidEmail(newUser.getEmail())) {
            return "Invalid email address.";
        }

        // Check if first name and last name are provided
        if (!StringUtils.hasText(newUser.getFirstName()) || !StringUtils.hasText(newUser.getLastName())) {
            return "First name and last name are required.";
        }
        // Check if mobile number is provided and is valid
        if (!StringUtils.hasText(newUser.getMobile()) || !isValidMobile(newUser.getMobile())) {
            return "Invalid or missing mobile number.";
        }

        // Check if username is provided
        if (!StringUtils.hasText(newUser.getUsername())) {
            return "Username is required.";
        }

        // Check if hashed password is provided
        if (!StringUtils.hasText(newUser.getHashedPassword())) {
            return "Password is required.";
        }
        // Check if credit card number is provided and has 16 digits (you can add
        // additional validation here)
        if (!StringUtils.hasText(newUser.getCreditCardNumber())
                || !isValidCreditCardNumber(newUser.getCreditCardNumber())) {
            return "Invalid credit card number. It must have 16 digits.";
        }

        // Check if DOB is provided and user is at least 18 years old
        if (!StringUtils.hasText(newUser.getDob()) || !isValidDOB(newUser.getDob())) {
            return "Invalid or missing date of birth. User must be at least 18 years old.";
        }

        return null;
    }

    public static String validateUserChange(UpdateUserDto userData) {
      
        // Check if first name and last name are provided
        if (!StringUtils.hasText(userData.getFirstName()) || !StringUtils.hasText(userData.getLastName())) {
            return "First name and last name are required.";
        }
        // Check if mobile number is provided and is valid
        if (!StringUtils.hasText(userData.getMobile()) || !isValidMobile(userData.getMobile())) {
            return "Invalid or missing mobile number.";
        }

        // Check if username is provided
        if (!StringUtils.hasText(userData.getUsername())) {
            return "Username is required.";
        }

        return null;
    }

     // Validate email format
     public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidMobile(String mobile) {
        // Regular expression for Australian mobile numbers
        String regex = "^(\\+?61|0)4\\d{8}$|^\\+?61\\d{9}$";
        return mobile.matches(regex);
    }

    public static boolean isValidCreditCardNumber(String creditCardNumber) {
        // Check if the credit card number contains only numeric digits and is 16
        // characters long
        return creditCardNumber.matches("[0-9]+") && creditCardNumber.length() == 16;
    }

    public static boolean isValidDOB(String dob) {
        // Parse the date string into a LocalDate object
        LocalDate dateOfBirth = LocalDate.parse(dob);

        // Calculate the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate age by subtracting the birth year from the current year
        int age = Period.between(dateOfBirth, currentDate).getYears();

        // Check if the age is at least 18
        return age >= 18;
    }
}
