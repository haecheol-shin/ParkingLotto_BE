package com.cruise.parkinglotto.web.dto.drawDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.cruise.parkinglotto.web.dto.parkingSpaceDTO.ParkingSpaceResponseDTO.GetNameAndUrlParkingSpaceResultDTO;

import java.time.LocalDateTime;
import java.util.List;

public class DrawResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCurrentDrawInfoDTO {
        private LocalDateTime drawStartAt;
        private LocalDateTime drawEndAt;
        private LocalDateTime usageStartAt;
        private LocalDateTime usageEndAt;
        private String mapImageUrl;
        private List<GetNameAndUrlParkingSpaceResultDTO> getNameAndUrlParkingSpaceResultDTO;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWinnerMemberDto {
        private Long drawId;
        private String name_ko;
        private String account_id;
        private String depth_path_name;
        private Long reserve_num;
        private Double weighted_total_score;
    }

}

