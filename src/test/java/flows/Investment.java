package flows;

import common.ApiBaseTest;
import common.RequestHelper;
import manager.RequestManager;
import common.Endpoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import common.util.JsonUtil;
import common.pojo.investment.DealDetail;
import common.pojo.investment.InvestmentStartRequest;
import common.pojo.investment.PaymentOrderRequest;

public class Investment extends ApiBaseTest {
    private static final String TAP_AUTH_OVERRIDE = "6XpB92sJ9H12IOkvqqZiL";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        ApiBaseTest.initialize();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
    }

    @Test(priority = 1)
    public void runInvestmentFlow() {

        try {
            String overrideTapAuth = TAP_AUTH_OVERRIDE;

            // Step 1: Fetch available deals
            System.out.println("=== Fetching Deals ===");
            RequestManager readMgr = new RequestManager(readAuth(overrideTapAuth));

            Response dealsResponse = readMgr.getRequest(Endpoints.DEALS);

            if (dealsResponse.getStatusCode() != 200) {
                System.out.println("Failed to fetch deals. Status: " + dealsResponse.getStatusCode());
                return;
            }

            System.out.println("Deals Response: " + dealsResponse.prettyPrint());

            int dealId = 4487;
            double investmentAmount = 13;

            // Step 2: Fetch specific deal details
            System.out.println("\n=== Fetching Deal Details for Deal ID: " + dealId + " ===");
            Response dealDetails = readMgr.getRequest(Endpoints.dealById(dealId));

            if (dealDetails.getStatusCode() != 200) {
                System.out.println("Failed to fetch deal details. Status: " + dealDetails.getStatusCode());
                return;
            }

            System.out.println("Deal Details: " + dealDetails.prettyPrint());

            // Step 2.1: Validate deal availability and investment amount
            JSONObject dealResult = new JSONObject(dealDetails.getBody().asString()).getJSONObject("result");

            boolean isSoldOut = dealResult.getBoolean("isSoldOut");
            if (isSoldOut) {
                System.err.println("ERROR: Deal ID " + dealId + " is SOLD OUT. Cannot proceed with investment.");
                return;
            }

            JSONObject minInvestment = dealResult.getJSONObject("minInvestment");
            double minimumAmount = minInvestment.getDouble("amount");

            if (investmentAmount < minimumAmount) {
                System.err.println("ERROR: Investment amount " + investmentAmount + " is below minimum required amount of " + minimumAmount);
                return;
            }

            if (dealResult.has("maxInvestment")) {
                JSONObject maxInvestment = dealResult.getJSONObject("maxInvestment");
                double maximumAmount = maxInvestment.getDouble("amount");

                if (investmentAmount > maximumAmount) {
                    System.err.println("ERROR: Investment amount " + investmentAmount + " exceeds maximum allowed amount of " + maximumAmount);
                    return;
                }
            }

            if (dealResult.has("allowInvestment")) {
                boolean allowInvestment = dealResult.getBoolean("allowInvestment");
                if (!allowInvestment) {
                    System.err.println("ERROR: Investment is not allowed for this deal at the moment.");
                    return;
                }
            }

            System.out.println("✓ Deal validation passed. Proceeding with investment...");
            System.out.println("✓ Deal is available (not sold out)");
            System.out.println("✓ Investment amount " + investmentAmount + " is within valid range [" + minimumAmount + " - " + (dealResult.has("maxInvestment") ? dealResult.getJSONObject("maxInvestment").getDouble("amount") : "unlimited") + "]");

            // Step 3: Get investment terms
            System.out.println("\n=== Fetching Investment Terms ===");
            Response investmentTerms = readAuth(overrideTapAuth)
                    .queryParam("amount", investmentAmount)
                    .queryParam("financeType", "INVOICE_DISCOUNTING")
                    .get(Endpoints.investmentTerms(dealId));

            if (investmentTerms.getStatusCode() != 200) {
                System.out.println("Failed to fetch investment terms. Status: " + investmentTerms.getStatusCode());
                return;
            }

            System.out.println("Investment Terms: " + investmentTerms.prettyPrint());

            // Step 4: Check buffer availability
            System.out.println("\n=== Checking Buffer Availability ===");
            Response bufferCheck = readAuth(overrideTapAuth)
                    .queryParam("amount", investmentAmount)
                    .get(Endpoints.INVESTMENTS_BUFFER_AVAILABLE);

            System.out.println("Buffer Check Status: " + bufferCheck.getStatusCode());
            if (bufferCheck.getStatusCode() == 200) {
                System.out.println("Buffer Response: " + bufferCheck.prettyPrint());
            }

            // Step 5: Start investment flow
            System.out.println("\n=== Starting Investment Flow ===");
            InvestmentStartRequest payload = buildInvestmentStartRequest(dealId, investmentAmount);

            Response investmentResponse = writeAuth(overrideTapAuth)
                    .contentType(ContentType.JSON)
                    .body(JsonUtil.toJson(payload))
                    .post(Endpoints.INVESTMENTS_START_ID_FLOW);

            System.out.println("Investment Flow Response Status: " + investmentResponse.getStatusCode());
            System.out.println("Investment Response: " + investmentResponse.prettyPrint());

            // Step 6: Post-investment checks
            if (investmentResponse.getStatusCode() == 200) {
                System.out.println("\n=== Fetching Transaction View ===");
                JSONObject transactionFilter = buildTransactionFilter();

                Response transactionView = writeAuth(overrideTapAuth)
                        .contentType(ContentType.JSON)
                        .body(transactionFilter.toString())
                        .post(Endpoints.INVESTMENT_DASHBOARD_TRANSACTION_VIEW);

                System.out.println("Transaction View Status: " + transactionView.getStatusCode());
                if (transactionView.getStatusCode() == 200) {
                    System.out.println("Transaction View: " + transactionView.prettyPrint());
                }

                System.out.println("\n=== Fetching Investment Metrics ===");
                Response metricsResponse = readMgr.getRequest(Endpoints.INVESTMENT_DASHBOARD_METRICS_ALL);

                System.out.println("Metrics Status: " + metricsResponse.getStatusCode());
                if (metricsResponse.getStatusCode() == 200) {
                    System.out.println("Investment Metrics: " + metricsResponse.prettyPrint());
                }

                System.out.println("\n=== Checking User Investment Status ===");
                Response userInvestmentStatus = readMgr.getRequest(Endpoints.USER_IS_INVESTED);

                System.out.println("User Investment Status: " + userInvestmentStatus.getStatusCode());
                if (userInvestmentStatus.getStatusCode() == 200) {
                    System.out.println("User Investment Response: " + userInvestmentStatus.prettyPrint());
                }
            }

        } catch (Exception e) {
            System.err.println("Error occurred during investment flow: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static RequestSpecification readAuth(String tapAuth) {
        return RequestHelper.readRequestWithAuth(commonHeaders, tapAuth);
    }

    private static RequestSpecification writeAuth(String tapAuth) {
        return RequestHelper.writeRequestWithAuth(commonHeaders, tapAuth);
    }

    private static JSONObject buildTransactionFilter() {
        return new JSONObject()
                .put("pageNumber", 0)
                .put("pageSize", 3)
                .put("investmentStatus", new JSONObject()
                        .put("values", new String[]{"SUCCESS"})
                        .put("filterType", "IN"))
                .put("transactionType", new JSONObject()
                        .put("values", new String[]{"INVESTMENT"})
                        .put("filterType", "IN"))
                .put("transactionDate", new JSONObject()
                        .put("sortOrder", "DESC"));
    }

    private static InvestmentStartRequest buildInvestmentStartRequest(int dealId, double investmentAmount) {
        DealDetail detail = DealDetail.builder()
                .dealId(dealId)
                .investmentAmount(investmentAmount)
                .reinvestment(false)
                .build();

        PaymentOrderRequest por = PaymentOrderRequest.builder()
                .successPath("https://stage.getultra.club/dashboard?status=success&rechargeStatus=PAID")
                .failurePath("https://stage.getultra.club/dashboard?status=failure")
                .build();

        return InvestmentStartRequest.builder()
                .dealDetail(detail)
                .useWalletBalance(true)
                .allowPartialPayment(true)
                .paymentOrderRequest(por)
                .build();
    }
}