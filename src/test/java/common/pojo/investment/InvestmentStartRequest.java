package common.pojo.investment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InvestmentStartRequest {

    private DealDetail dealDetail;
    private boolean useWalletBalance;
    private boolean allowPartialPayment;
    private PaymentOrderRequest paymentOrderRequest;
}


