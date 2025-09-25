package common;

public final class Endpoints {

    private Endpoints() { }

    // Auth - Phone
    public static final String AUTH_PHONE_REQUEST = "/v2/auth/phone/otp/request";
    public static final String AUTH_PHONE_VERIFY = "/v2/auth/phone/otp/verify";

    // Auth - Email
    public static final String AUTH_EMAIL_REQUEST = "/v2/auth/email/otp/request";
    public static final String AUTH_EMAIL_VERIFY = "/v2/auth/email/otp/verify";

    // User
    public static final String USER_UPDATE_PROFILE = "/v2/user/update-profile";
    public static final String USER_PAN_VERIFY = "/v2/user/pan/verify";
    public static final String USER_PAN_SUBMIT = "/v2/user/pan";
    public static final String USER_AADHAAR_OTP_VERIFY = "/v2/user/aadhaar";
    public static final String USER_AADHAAR_REQUEST = "/v2/user/aadhaar/verify";
    public static final String USER_BANK_DETAILS = "/v2/user/bank-details";
    public static final String USER_GET_ACCREDITATION = "/v2/user/get-accreditation";
    public static final String USER_ACCREDITATION_MARK_ANSWER = "/v2/user/accreditation/mark-answer";
    public static final String USER_COMPLIANCE = "/v2/user/compliance";
    public static final String USER_IS_INVESTED = "/v2/user/is-invested";

    // Deals / Investments
    public static final String DEALS = "/v2/deals";
    public static String dealById(int dealId) { return "/v2/deals/" + dealId; }
    public static String investmentTerms(int dealId) { return "/v2/deals/" + dealId + "/investment-terms"; }
    public static final String INVESTMENTS_START_ID_FLOW = "/v2/investments/start-invoice-discounting-investment-flow";
    public static final String INVESTMENTS_BUFFER_AVAILABLE = "/v2/investments/buffer-available";
    public static final String INVESTMENT_DASHBOARD_TRANSACTION_VIEW = "/v2/investment-dashboard/get-transaction-view";
    public static final String INVESTMENT_DASHBOARD_METRICS_ALL = "/v2/investment-dashboard/get-metrics/ALL";
}


