package com.cruise.parkinglotto.service;

import com.cruise.parkinglotto.domain.ParkingSpace;
import com.cruise.parkinglotto.repository.ApplicantRepository;
import com.cruise.parkinglotto.repository.ParkingSpaceRepository;
import com.cruise.parkinglotto.web.dto.ParkingSpaceDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ApplicantRepository applicantRepository;

    public ParkingSpaceImgResponseDTO findParkingSpaceImg(Long memberId) {

        Long applicantId = applicantRepository.findByMember(memberId);
        Long parkingSpaceId = applicantRepository.findParkingSpaceId(applicantId);
        log.info(parkingSpaceId+"");
        Optional<ParkingSpace> findParkingSpace = parkingSpaceRepository.findById(parkingSpaceId);
        return new ParkingSpaceImgResponseDTO(findParkingSpace.get().getFloorPlanImageUrl());
    }

}
