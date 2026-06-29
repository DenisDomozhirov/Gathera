package com.gathera.gathera.Locations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface LocationRepository extends JpaRepository<LocationsEntity, Long> {

    @Query("""
            SELECT l from LocationsEntity l
            WHERE (:id IS NULL OR l.id = :id)
            AND (:capacity IS NULL OR l.capacity < :capacity)
""")
    List<LocationsEntity> searchLocations(
            Long id,
            Integer capacity,
            Pageable pageable
    );

    @Transactional
    @Modifying
    @Query("""
            UPDATE LocationsEntity l
            SET l.name = :name,
                l.address = :address,
                l.capacity = :capacity
                where l.id = :id
""")
    void updateLocation(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("address") String address,
            @Param("capacity") Integer capacity
    );

}
