package common.pojo.kyc;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class UpdateProfileRequest {

    private int id;
    private String firstName;
    private String gender;
}


