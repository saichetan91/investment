package common;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class RequestHelper {

    private static RequestSpecification baseWithHeaders(Map<String, String> headers) {
        return RestAssured.given()
                .headers(headers)
                .log().all();
    }

    private static RequestSpecification withSession(RequestSpecification specification) {
        RequestSpecification spec = specification;
        if (ApiBaseTest.authToken != null) spec = spec.header("X-Tap-Auth", ApiBaseTest.authToken);
        if (ApiBaseTest.tapSessionId != null) spec = spec.header("X-Tap-Session", ApiBaseTest.tapSessionId);
        if (ApiBaseTest.deviceId != null) spec = spec.header("X-Device-Id", ApiBaseTest.deviceId);
        if (ApiBaseTest.tapDshan != null) spec = spec.header("X-Tap-Dshan", ApiBaseTest.tapDshan);
        if (ApiBaseTest.sessionCookie != null) spec = spec.cookie("_session", ApiBaseTest.sessionCookie);
        return spec;
    }

    public static RequestSpecification writeRequest(Map<String, String> headers) {
        return withSession(baseWithHeaders(headers)
                .header("X-Platform-Request", "WEB_WRITE"));
    }

    public static RequestSpecification readRequest(Map<String, String> headers) {
        return withSession(baseWithHeaders(headers)
                .header("X-Platform-Request", "WEB_READ"));
    }

    public static RequestSpecification writeRequestWithAuth(Map<String, String> headers, String authTokenOverride) {
        return writeRequest(headers)
                .header("X-Tap-Auth", authTokenOverride);
    }

    public static RequestSpecification readRequestWithAuth(Map<String, String> headers, String authTokenOverride) {
        return readRequest(headers)
                .header("X-Tap-Auth", authTokenOverride);
    }

    public static RequestSpecification anonymousWrite(Map<String, String> headers) {
        return baseWithHeaders(headers)
                .header("X-Platform-Request", "WEB_WRITE");
    }
}