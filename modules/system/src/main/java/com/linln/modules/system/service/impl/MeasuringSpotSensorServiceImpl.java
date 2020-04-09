package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.domain.MeasuringSpotSensor;
import com.linln.modules.system.repository.MeasuringSpotRepository;
import com.linln.modules.system.repository.MeasuringSpotSensorRepository;
import com.linln.modules.system.service.MeasuringSpotSensorService;
import com.linln.modules.system.service.MeasuringSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class MeasuringSpotSensorServiceImpl implements MeasuringSpotSensorService {

    @Autowired
    private MeasuringSpotSensorRepository mssr;


    @Override
    public List<MeasuringSpotSensor> getByMeasuringSpotId(Long id) {
        return mssr.getByMeasuringSpotId(id);
    }

    @Override
    public MeasuringSpotSensor getById(Long id) {
        return mssr.getOne(id);
    }

    @Override
    public void save(MeasuringSpotSensor measuringSpot) {
        mssr.save(measuringSpot);
    }

    @Override
    public void saveAndFlush(MeasuringSpotSensor measuringSpot) {
        mssr.saveAndFlush(measuringSpot);
    }

    @Override
    @Transactional
    public void removeMeasuringSpotSensorByAcquisitionSensorId(Long acquisitionSensorId) {
        mssr.removeMeasuringSpotSensorByAcquisitionSensorId(acquisitionSensorId);
    }

    @Override
    public MeasuringSpotSensor findByAcquisitionSensorId(Long id) {
        return mssr.findDistinctByAcquisitionSensorId(id);
    }

    @Override
    public void deleteBatch(List<MeasuringSpotSensor> mss) {
        List<Long> datas = new ArrayList<>();
        for (MeasuringSpotSensor measuringSpotSensor: mss) {
            datas.add(measuringSpotSensor.getMeasuringSpotSensorId());
        }
        mssr.deleteBatch(datas);
    }

    @Override
    public List<MeasuringSpotSensor> findByAcquisitionId(Long id) {
        return mssr.findByAcquisitionId(id);
    }
}
