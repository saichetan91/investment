package common;

import java.util.Random;
import java.util.UUID;

public class TestDataHelper {

    public static String generateRandomMobileNumber() {
        Random random = new Random();
        int firstDigit = random.nextInt(4) + 6; // 6-9
        String remainingDigits = String.format("%09d", random.nextInt(1_000_000_000));
        return firstDigit + remainingDigits;
    }

    public static String generateEvenBankAccountNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i < length - 1; i++) {
            sb.append(random.nextInt(10));
        }
        int[] evenDigits = {0, 2, 4, 6, 8};
        sb.append(evenDigits[random.nextInt(evenDigits.length)]);
        return sb.toString();
    }

    public static String newDeviceId() {
        return UUID.randomUUID().toString();
    }
}





