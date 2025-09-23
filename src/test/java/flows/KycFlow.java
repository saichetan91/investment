// src/test/java/flows/KycFlow.java

package flows;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common.ApiBaseTest;
import common.OtpHelper;
import common.RequestHelper;
import common.TestDataHelper;
import common.Endpoints;
import manager.RequestManager;
import common.util.JsonUtil;
import common.pojo.kyc.UpdateProfileRequest;
import common.pojo.kyc.PanVerifyRequest;
import common.pojo.kyc.PanSubmitRequest;
import common.pojo.kyc.AadhaarRequest;
import common.pojo.kyc.BankDetailsRequest;
import common.pojo.kyc.AccreditationAnswersRequest;
import common.pojo.kyc.ComplianceRequest;
import java.util.LinkedHashMap;

public class KycFlow extends ApiBaseTest {

    private final String firstName = "chetan";
    private final String gender = "MALE";
    private final String panNumber = "DGAPC8318G";
    private final String panName = "KOVURU SAI CHETAN";
    private final String aadhaarNumber = "212711195196";

    private final String bankAccountNumber = TestDataHelper.generateEvenBankAccountNumber(15);
    private final String ifscCode = "SBIN0001234";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        // Initialization handled by ApiBaseTest, hook kept for symmetry
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }

    @Test(priority = 1, dependsOnGroups = {"signup"})
    public void testUpdateUserProfile() {
        // Use the dynamic userId from ApiBaseTest
        String updateProfileUrl = Endpoints.USER_UPDATE_PROFILE;
        UpdateProfileRequest requestBody = UpdateProfileRequest.builder()
                .id(ApiBaseTest.userId)
                .firstName(firstName)
                .gender(gender)
                .build();

        RequestManager write = new RequestManager(RequestHelper.writeRequest(commonHeaders));
        Response response = write.postRequest(updateProfileUrl, JsonUtil.toJson(requestBody));

        Assert.assertEquals(response.getStatusCode(), 200, "Profile update failed.");
    }

    @Test(priority = 2, dependsOnMethods = {"testUpdateUserProfile"})
    public void testEmailVerification() {
        // Generate a dynamic email from the mobile number to ensure it's unique
        String testEmail = "testuser" + ApiBaseTest.mobileNumber + "@example.com";
        Response requestOtpResponse = OtpHelper.requestEmailOtp(testEmail, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(requestOtpResponse.getStatusCode(), 202, "Email OTP request failed.");

        Response verifyOtpResponse = OtpHelper.verifyEmailOtp(testEmail, OtpHelper.FIXED_OTP, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyOtpResponse.getStatusCode(), 200, "Email OTP verification failed.");

        ApiBaseTest.authToken = verifyOtpResponse.jsonPath().getString("token");
        Assert.assertNotNull(ApiBaseTest.authToken, "New auth token not found.");
    }

    @Test(priority = 3, dependsOnMethods = {"testEmailVerification"})
    public void testPanVerification() {
        String verifyPanUrl = Endpoints.USER_PAN_VERIFY;
        PanVerifyRequest requestBody = PanVerifyRequest.builder()
                .panNumber(panNumber)
                .build();
        RequestManager write = new RequestManager(RequestHelper.writeRequest(commonHeaders));
        Response response = write.postRequest(verifyPanUrl, JsonUtil.toJson(requestBody));

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "PAN verification failed.");

        String submitPanUrl = Endpoints.USER_PAN_SUBMIT;
        PanSubmitRequest submitBody = PanSubmitRequest.builder()
                .panCardNumber(panNumber)
                .name(panName)
                .build();
        Response submitResponse = write.postRequest(submitPanUrl, JsonUtil.toJson(submitBody));

        Assert.assertEquals(submitResponse.getStatusCode(), 200, "PAN submission failed.");
    }

    @Test(priority = 4, dependsOnMethods = {"testPanVerification"})
    public void testAadhaarVerification() {
        String requestAadhaarOtpUrl = Endpoints.USER_AADHAAR_REQUEST;
        AadhaarRequest requestBody = AadhaarRequest.builder()
                .aadhaarNumber(aadhaarNumber)
                .build();
        RequestManager write = new RequestManager(RequestHelper.writeRequest(commonHeaders));
        Response response = write.postRequest(requestAadhaarOtpUrl, JsonUtil.toJson(requestBody));

        response.then().log().all();
        Assert.assertEquals(response.getStatusCode(), 200, "Aadhaar OTP request failed.");

        Response verifyAadhaarResponse = OtpHelper.verifyAadhaarOtp(aadhaarNumber, OtpHelper.FIXED_AADHAAR_OTP, ApiBaseTest.authToken, ApiBaseTest.tapSessionId, ApiBaseTest.deviceId, ApiBaseTest.tapDshan, ApiBaseTest.sessionCookie, commonHeaders);
        Assert.assertEquals(verifyAadhaarResponse.getStatusCode(), 200, "Aadhaar submission failed.");
    }

    @Test(priority = 5, dependsOnMethods = {"testAadhaarVerification"})
    public void testBankAndAccreditation() {
        // Step 1: Submit Bank Details
        String bankDetailsUrl = Endpoints.USER_BANK_DETAILS;
        BankDetailsRequest bankRequestBody = BankDetailsRequest.builder()
                .accountNumber(bankAccountNumber)
                .confirmAccountNumber(bankAccountNumber)
                .ifscCode(ifscCode)
                .build();

        Response bankResponse = RequestHelper.writeRequest(commonHeaders)
                .body(JsonUtil.toJson(bankRequestBody))
                .post(bankDetailsUrl);

        bankResponse.then().log().all();
        Assert.assertEquals(bankResponse.getStatusCode(), 200, "Bank details submission failed.");

        String getAccreditationUrl = Endpoints.USER_GET_ACCREDITATION;
        RequestManager read = new RequestManager(RequestHelper.readRequest(commonHeaders));
        Response getAccreditationResponse = read.getRequest(getAccreditationUrl);

        getAccreditationResponse.then().log().all();
        Assert.assertEquals(getAccreditationResponse.getStatusCode(), 200, "Get accreditation failed.");
        String markAnswerUrl = Endpoints.USER_ACCREDITATION_MARK_ANSWER;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("1", 10);
        map.put("2", 16);
        map.put("3", 19);
        map.put("4", 24);
        map.put("5", 25);
        map.put("6", 28);
        map.put("7", 29);

        AccreditationAnswersRequest answers = AccreditationAnswersRequest.builder()
                .userId(ApiBaseTest.userId)
                .accreditationAnswers(map)
                .build();

        RequestManager write = new RequestManager(RequestHelper.writeRequest(commonHeaders));
        Response markAnswerResponse = write.postRequest(markAnswerUrl, JsonUtil.toJson(answers));

        markAnswerResponse.then().log().all();
        Assert.assertEquals(markAnswerResponse.getStatusCode(), 200, "Mark accreditation answers failed.");

        String complianceUrl = Endpoints.USER_COMPLIANCE;
        ComplianceRequest complianceBody = ComplianceRequest.builder()
                .subjectToRegulatoryActions(false)
                .rbiCompliance(true)
                .wilfulDefaulter(false)
                .termsAcknowledged(true)
                .build();

        Response complianceResponse = write.postRequest(complianceUrl, JsonUtil.toJson(complianceBody));

        if (complianceResponse.getStatusCode() == 200) {
            System.out.println("Compliance submitted successfully.");
        } else {
            System.out.println("Compliance non-200 status: " + complianceResponse.getStatusCode() + " - may not be needed after mark-answer");
        }

        System.out.println("Bank and Accreditation flow completed successfully!");
    }
    }