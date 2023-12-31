package org.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.test.entity.user_elements.Bag;
import org.test.entity.user_elements.BagElement;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MineDTO {
    private boolean userDead;
    private boolean cooldownActive;
    private LocalDateTime endTime;
    
    private List<OreDTO> oreDTOList;
    private String report;
}
