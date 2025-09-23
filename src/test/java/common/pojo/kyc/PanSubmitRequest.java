package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class PanSubmitRequest {

    private String panCardNumber;
    private String name;
}


