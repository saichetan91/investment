package common;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class RequestHelper {

    public static RequestSpecification writeRequest(Map<String, String> headers) {
        return RestAssured.given()
                .headers(headers)
                .header("X-Platform-Request", "WEB_WRITE")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .log().all();
    }

    public static RequestSpecification readRequest(Map<String, String> headers) {
        return RestAssured.given()
                .headers(headers)
                .header("X-Platform-Request", "WEB_READ")
                .header("X-Tap-Auth", ApiBaseTest.authToken)
                .header("X-Tap-Session", ApiBaseTest.tapSessionId)
                .header("X-Device-Id", ApiBaseTest.deviceId)
                .header("X-Tap-Dshan", ApiBaseTest.tapDshan)
                .cookie("_session", ApiBaseTest.sessionCookie)
                .log().all();
    }
}





