package com.cruise.parkinglotto.repository;

import com.cruise.parkinglotto.domain.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByDrawId(Long drawId);
  
    @Modifying
    @Query("UPDATE ParkingSpace p SET p.remainSlots = p.remainSlots - 1 WHERE p.id = :id AND p.remainSlots > 0")
    void decrementRemainSlots(@Param("id") Long id);

    List<ParkingSpace> findByConfirmed(boolean confirmed);

    Optional<ParkingSpace> findParkingSpaceByDrawId(Long drawId);
}
