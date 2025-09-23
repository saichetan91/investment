// src/test/java/common/OtpHelper.java

package common;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class OtpHelper {

    public static final String FIXED_OTP = "1001";
    public static final String FIXED_AADHAAR_OTP = "123456";

    public static Response requestPhoneOtp(String mobileNumber, String countryCode, String deviceId, Map<String, String> headers) {
        String requestBody = String.format("{\"phone\": \"%s\", \"countryCode\": \"%s\"}", mobileNumber, countryCode);
        return withDevice(anonWrite(headers), deviceId)
                .body(requestBody)
                .post(Endpoints.AUTH_PHONE_REQUEST);
    }

    public static Response verifyPhoneOtp(String mobileNumber, String countryCode, String fixedOtp, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"phone\": \"%s\", \"otp\": \"%s\", \"countryCode\": \"%s\"}", mobileNumber, fixedOtp, countryCode);
        return withTempSession(anonWrite(headers), tapSessionId, deviceId, tapDshan, sessionCookie)
                .body(requestBody)
                .post(Endpoints.AUTH_PHONE_VERIFY);
    }

    public static Response requestEmailOtp(String email, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"email\": \"%s\"}", email);
        return withAuth(withTempSession(anonWrite(headers), tapSessionId, deviceId, tapDshan, sessionCookie), authToken)
                .body(requestBody)
                .post(Endpoints.AUTH_EMAIL_REQUEST);
    }

    public static Response verifyEmailOtp(String email, String fixedOtp, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"email\": \"%s\", \"otp\": \"%s\"}", email, fixedOtp);
        return withAuth(withTempSession(anonWrite(headers), tapSessionId, deviceId, tapDshan, sessionCookie), authToken)
                .body(requestBody)
                .post(Endpoints.AUTH_EMAIL_VERIFY);
    }

    public static Response verifyAadhaarOtp(String aadhaarNumber, String fixedAadhaarOtp, String authToken, String tapSessionId, String deviceId, String tapDshan, String sessionCookie, Map<String, String> headers) {
        String requestBody = String.format("{\"aadhaarNumber\": \"%s\", \"otp\": \"%s\"}", aadhaarNumber, fixedAadhaarOtp);
        return withAuth(withTempSession(anonWrite(headers), tapSessionId, deviceId, tapDshan, sessionCookie), authToken)
                .body(requestBody)
                .post(Endpoints.USER_AADHAAR_OTP_VERIFY);
    }

    private static RequestSpecification anonWrite(Map<String, String> headers) {
        return RequestHelper.anonymousWrite(headers);
    }

    private static RequestSpecification withDevice(RequestSpecification spec, String deviceId) {
        return spec.header("X-Device-Id", deviceId);
    }

    private static RequestSpecification withTempSession(RequestSpecification spec, String tapSessionId, String deviceId, String tapDshan, String sessionCookie) {
        RequestSpecification s = spec;
        if (tapSessionId != null) s = s.header("X-Tap-Session", tapSessionId);
        if (deviceId != null) s = s.header("X-Device-Id", deviceId);
        if (tapDshan != null) s = s.header("X-Tap-Dshan", tapDshan);
        if (sessionCookie != null) s = s.cookie("_session", sessionCookie);
        return s;
    }

    private static RequestSpecification withAuth(RequestSpecification spec, String authToken) {
        return spec.header("X-Tap-Auth", authToken);
    }
}