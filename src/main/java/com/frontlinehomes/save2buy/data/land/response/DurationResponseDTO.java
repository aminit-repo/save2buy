package com.frontlinehomes.save2buy.data.land.response;

import com.frontlinehomes.save2buy.data.land.data.DurationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DurationResponseDTO {
    private Long id;
    private Integer length;
    @Enumerated(EnumType.STRING)
    private DurationType frequency;
}
