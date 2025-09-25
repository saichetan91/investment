package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class PanVerifyRequest {

    private String panNumber;
}


