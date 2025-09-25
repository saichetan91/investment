package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BankDetailsRequest {

    private String accountNumber;
    private String confirmAccountNumber;
    private String ifscCode;
}


