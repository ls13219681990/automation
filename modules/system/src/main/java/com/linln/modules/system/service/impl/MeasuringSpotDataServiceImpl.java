package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.MeasuringSpotData;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.domain.SensorData;
import com.linln.modules.system.repository.MeasuringSpotDataRepository;
import com.linln.modules.system.repository.MeasuringSpotRepository;
import com.linln.modules.system.service.MeasuringSpotDataService;
import com.linln.modules.system.service.MeasuringSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class MeasuringSpotDataServiceImpl implements MeasuringSpotDataService {


    @PersistenceContext
    private EntityManager em;



    @Autowired
    private MeasuringSpotDataRepository measuringSpotDataRepository;



    @Override
    public Page<MeasuringSpotData> getPageList(Example<MeasuringSpotData> example) {
        return null;
    }

    @Override
    public void saveAll(List<MeasuringSpotData> measuringSpotDataList) {
        measuringSpotDataRepository.saveAll(measuringSpotDataList);
    }

    @Override
    public MeasuringSpotData getById(Long id) {
        return measuringSpotDataRepository.getOne(id);
    }

    @Override
    public void save(MeasuringSpotData measuringSpotData) {
         measuringSpotDataRepository.save(measuringSpotData);
    }

    @Override
    public void saveAndFlush(MeasuringSpotData measuringSpotData) {
        measuringSpotDataRepository.saveAndFlush(measuringSpotData);
    }

    @Override
    public MeasuringSpotData findFirstByOrderByReceiveTimeDesc() {
        return measuringSpotDataRepository.findFirstByOrderByReceiveTimeDesc();
    }

    @Override
    public MeasuringSpotData findFirstByMeasuringSpotIdByOrderByReceiveTimeDesc(Long id) {
        return measuringSpotDataRepository.findFirstByMeasuringSpotIdOrderByReceiveTimeDesc(id);
    }

    @Override
    public List<MeasuringSpotData> findByMeasuringSpotIdOrderByReceiveTime(Long id,Integer page, Integer limit) {

        PageRequest pageable = PageRequest.of(page - 1, limit);

        return measuringSpotDataRepository.findByMeasuringSpotIdOrderByReceiveTime(id,pageable);
    }

    @Override
    public List<MeasuringSpotData> findByMeasuringSpotId(Long id) {
        return measuringSpotDataRepository.findByMeasuringSpotIdOrderByIdAsc(id);
    }

    @Override
    public MeasuringSpotData findByReceiveTime(String time) {
        return measuringSpotDataRepository.findByReceiveTime(time);
    }

    @Override
    public void updateMeasuringSpotDataList(Byte status,List<MeasuringSpotData> measuringSpotDataList) {
        List<Long> ids = new ArrayList<>();
        for (MeasuringSpotData measuringSpotData: measuringSpotDataList) {
            ids.add(measuringSpotData.getId());
        }

        measuringSpotDataRepository.updateMeasuringSpotDataList(status,ids);
    }

    @Override
    public List<MeasuringSpotData> findByReceiveTimeGreaterThan(String time) {
        return measuringSpotDataRepository.findByReceiveTimeGreaterThan(time);
    }

    @Override
    public MeasuringSpotData findFirstByReceiveTimeLessThanOrderByIdDesc(String time) {
        return measuringSpotDataRepository.findFirstByReceiveTimeLessThanOrderByIdDesc(time);
    }

    @Override
    public void updateAll(List<MeasuringSpotData> measuringSpotDataList) {
        for (int i = 0; i < measuringSpotDataList.size(); i++) {
            em.merge(measuringSpotDataList.get(i));

            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }

        }
    }

    @Override
    public void update(MeasuringSpotData measuringSpotDataList) {
        em.merge(measuringSpotDataList);
        em.flush();
    }

    @Override
    public List<MeasuringSpotData> findByIdGreaterThanAndIdLessThan(Long startId, Long EndId) {
        return measuringSpotDataRepository.findByIdGreaterThanAndIdLessThan(startId, EndId);
    }

    @Override
    public void deleteAll(List<MeasuringSpotData> measuringSpotDataList) {
        for (int i = 0; i < measuringSpotDataList.size(); i++) {
            em.remove(measuringSpotDataList.get(i));
            if (i == 100) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
    }

    @Override
    public MeasuringSpotData findFirstByWhetherAlignmentAndReceiveTimeGreaterThan(String time, String whetherAlignment) {
        return measuringSpotDataRepository.findFirstByWhetherAlignmentAndReceiveTimeGreaterThan(time,whetherAlignment);
    }

    @Override
    public List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(Long id, String startDate, String EndDate) {

        return measuringSpotDataRepository.findByMeasuringSpotIdAndReceiveTimeGreaterThanAndReceiveTimeLessThan(id, startDate, EndDate);
    }

    @Override
    public MeasuringSpotData getDateMin(Long id) {
        return measuringSpotDataRepository.findByMeasuringSpotIdOrderByReceiveTimeAsc(id);
    }

    @Override
    public MeasuringSpotData getDateMax(Long id) {
        return measuringSpotDataRepository.findByMeasuringSpotIdOrderByReceiveTimeDesc(id);
    }

    @Override
    public List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeGreaterThan(Long id, String startDate) {
        return  measuringSpotDataRepository.findByMeasuringSpotIdAndReceiveTimeGreaterThan(id,startDate);
    }

    @Override
    public List<MeasuringSpotData> findByMeasuringSpotIdAndReceiveTimeLessThan(Long id, String endDate) {
        return measuringSpotDataRepository.findByMeasuringSpotIdAndReceiveTimeLessThan(id, endDate);
    }


    @Override
    public MeasuringSpotData findByworkSpotIdMeasuringDataDateMin(Long workSpotId) {
        return measuringSpotDataRepository.findByworkSpotIdMeasuringDataDateMin(workSpotId);
    }

    @Override
    public MeasuringSpotData findByworkSpotIdMeasuringDataDateMax(Long workSpotId) {
        return measuringSpotDataRepository.findByworkSpotIdMeasuringDataDateMax(workSpotId);
    }

    @Override
    public List<MeasuringSpotData> findByWorkSpotIAndStartDateAndEndDate(Long workSpotId, String startDate, String endDate) {
        return measuringSpotDataRepository.findByWorkSpotIAndStartDateAndEndDate(workSpotId, startDate, endDate);
    }


}
