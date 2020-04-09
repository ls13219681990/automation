package com.linln.modules.system.repository;

import com.linln.modules.system.domain.TemperatureResistance;
import com.linln.modules.system.domain.TemperatureResistanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
public interface TemperatureResistanceDataRepository extends JpaRepository<TemperatureResistanceData, Long> {


    /**
     * 根据温阻ID查询温阻数据
     *
     */
    @Query(" select trd from TemperatureResistanceData  trd where trd.temperatureResistanceId = :temperatureResistanceId order by trd.tempResist asc")
    List<TemperatureResistanceData>  findByTemperatureResistanceId(@Param("temperatureResistanceId") Long temperatureResistanceId);

}
