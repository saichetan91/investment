// src/test/java/common/ApiBaseTest.java
package common;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class ApiBaseTest {

    protected static String authToken;
    protected static int userId;
    protected static String tapSessionId;
    protected static String deviceId;
    protected static String tapDshan;
    protected static String sessionCookie;
    protected static String mobileNumber;
    

    protected static Map<String, String> commonHeaders = new HashMap<>();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        initialize();
    }

    public static void initialize() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestAssured.baseURI = properties.getProperty("base.url");

        commonHeaders.put("Content-Type", "application/json");
        commonHeaders.put("Origin", "https://stage.getultra.club");
        commonHeaders.put("Accept", "*/*");
        commonHeaders.put("X-Client-Type", "Web Investor App");
        commonHeaders.put("X-Device-Type", "Desktop");
        commonHeaders.put("X-OS-Type", "Mac OS");
        commonHeaders.put("X-OS-Version", "10.15.7");
        commonHeaders.put("X-Device-Model", "Macintosh");
        commonHeaders.put("X-Platform-Request", "WEB_READ");
        commonHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36");
        commonHeaders.put("X-App-Id", "ULTRA");

        // requestSpec removed; consumers create their own specs via RequestHelper
    }
}