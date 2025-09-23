package common.pojo.investment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class PaymentOrderRequest {

    private String successPath;
    private String failurePath;
}


