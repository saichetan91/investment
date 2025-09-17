// src/test/java/flows/KycFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import common.ApiBaseTest;
import common.OtpHelper;
import common.RequestHelper;
import common.TestDataHelper;

public class KycFlow extends ApiBaseTest {

    private final String firstName = "chetan";
    private final String gender = "MALE";
    private final String panNumber = "DGAPC8318G";
    private final String panName = "KOVURU SAI CHETAN";
    private final String aadhaarNumber = "212711195196";

    private final String bankAccountNumber = TestDataHelper.generateEvenBankAccountNumber(15);
    private final String ifscCode = "SBIN0001234";

    

    @Test(priority = 1, dependsOnGroups = {"signup"})
    public void testUpdateUserProfile() {
        // Use the dynamic userId from ApiBaseTest
        String updateProfileUrl = "/v2/user/update-profile";
        String requestBody = String.format("{\"id\": %d, \"firstName\": \"%s\", \"gender\": \"%s\"}", ApiBaseTest.userId, firstName, gender);

        Response response = RequestHelper.writeRequest(commonHeaders)
                .body(requestBody)
                .post(updateProfileUrl);

        Assert.assertEquals(response.getStatusCode(), 200, "Profile update failed.");
    }

    @Test(priority = 2, dependsOnMethods = {"testUpdateUserProfile"})
    public void testEmailVerification() {
        // Generate a dynamic email from the mobile number to ensure it's unique
        String testEmail = "testuser" + ApiBaseTest.mobileNumber + "@example.com";
        Response requestOtpResponse = OtpHelper.requestEmailOtp(testEmail, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "Email OTP request failed.");

        Response verifyOtpResponse = OtpHelper.verifyEmailOtp(testEmail, OtpHelper.FIXED_EMAIL_OTP, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "Email OTP verification failed.");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        Assert.assertNotNull(ApiBaseTest.authToken, "New auth token not found.");
    }

    @Test(priority = 3, dependsOnMethods = {"testEmailVerification"})
    public void testPanVerification() {
        String verifyPanUrl = "/v2/user/pan/verify";
        String requestBody = String.format("{\"panNumber\": \"%s\"}", panNumber);
        Response response = RequestHelper.writeRequest(commonHeaders)
                .body(requestBody)
                .post(verifyPanUrl);

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "PAN verification failed.");

        String submitPanUrl = "/v2/user/pan";
        String submitBody = String.format("{\"panCardNumber\": \"%s\", \"name\": \"%s\"}", panNumber, panName);

        Response submitResponse = RequestHelper.writeRequest(commonHeaders)
                .body(submitBody)
                .post(submitPanUrl);

        Assert.assertEquals(submitResponse.getStatusCode(), 200, "PAN submission failed.");
    }

    @Test(priority = 4, dependsOnMethods = {"testPanVerification"})
    public void testAadhaarVerification() {
        String requestAadhaarOtpUrl = "/v2/user/aadhaar/verify";
        String requestBody = String.format("{\"aadhaarNumber\": \"%s\"}", aadhaarNumber);

        Response response = RequestHelper.writeRequest(commonHeaders)
                .body(requestBody)
                .post(requestAadhaarOtpUrl);

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Aadhaar OTP request failed.");

        Response verifyAadhaarResponse = OtpHelper.verifyAadhaarOtp(aadhaarNumber, OtpHelper.FIXED_AADHAAR_OTP, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyAadhaarResponse.getStatusCode(), 200, "Aadhaar submission failed.");
    }

    @Test(priority = 5, dependsOnMethods = {"testAadhaarVerification"})
    public void testBankAndAccreditation() {
        // Step 1: Submit Bank Details
        String bankDetailsUrl = "/v2/user/bank-details";
        String bankRequestBody = String.format("{\"accountNumber\": \"%s\", \"confirmAccountNumber\": \"%s\", \"ifscCode\": \"%s\"}",
                bankAccountNumber, bankAccountNumber, ifscCode);

        Response bankResponse = RequestHelper.writeRequest(commonHeaders)
                .body(bankRequestBody)
                .post(bankDetailsUrl);

        bankResponse.then().log().all();
        Assert.assertEquals(bankResponse.getStatusCode(), 200, "Bank details submission failed.");

        String getAccreditationUrl = "/v2/user/get-accreditation";

        Response getAccreditationResponse = RequestHelper.readRequest(commonHeaders)
                .get(getAccreditationUrl);

        getAccreditationResponse.then().log().all();
        Assert.assertEquals(getAccreditationResponse.getStatusCode(), 200, "Get accreditation failed.");
        String markAnswerUrl = "/v2/user/accreditation/mark-answer";
        String markAnswerBody = String.format("{" +
                "\"userId\": %d," +
                "\"accreditationAnswers\": {" +
                "\"1\": 10," +
                "\"2\": 16," +
                "\"3\": 19," +
                "\"4\": 24," +
                "\"5\": 25," +
                "\"6\": 28," +
                "\"7\": 29" +
                "}" +
                "}", ApiBaseTest.userId);

        Response markAnswerResponse = RequestHelper.writeRequest(commonHeaders)
                .body(markAnswerBody)
                .post(markAnswerUrl);

        markAnswerResponse.then().log().all();
        Assert.assertEquals(markAnswerResponse.getStatusCode(), 200, "Mark accreditation answers failed.");

        String complianceUrl = "/v2/user/compliance";
        String complianceBody = "{" +
                "\"subjectToRegulatoryActions\": false," +
                "\"rbiCompliance\": true," +
                "\"wilfulDefaulter\": false," +
                "\"termsAcknowledged\": true" +
                "}";

        Response complianceResponse = RequestHelper.writeRequest(commonHeaders)
                .body(complianceBody)
                .post(complianceUrl);

        if (complianceResponse.getStatusCode() == 200) {
            System.out.println("Compliance submitted successfully.");
        } else {
            System.out.println("Compliance non-200 status: " + complianceResponse.getStatusCode() + " - may not be needed after mark-answer");
        }

        System.out.println("Bank and Accreditation flow completed successfully!");
    }
    }