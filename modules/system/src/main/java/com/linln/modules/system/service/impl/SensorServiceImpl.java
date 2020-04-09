package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.Sensor;
import com.linln.modules.system.repository.AcquisitionInstrumentRepository;
import com.linln.modules.system.repository.SensorRepository;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import com.linln.modules.system.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {

    @Autowired
    private SensorRepository sensorRepository;


    @Override
    public Page<Sensor> getPageList(Example<Sensor> example) {
            // 创建分页对象
        PageRequest page = PageSort.pageSensorRequest();

        //return sensorRepository.findAll(example, page);
        return null;
    }

    @Override
    public void save(Sensor sensor) {
        sensorRepository.save(sensor);
    }

    @Override
    public void deleteById(Long id) {
        sensorRepository.deleteById(id);
    }

    @Override
    public void save(List<Sensor> sensorsList) {
        sensorRepository.saveAll(sensorsList);
    }

    @Override
    public Sensor getByNo(String no) {
        //sensorRepository();
        return sensorRepository.getByNo(no);
    }

    @Override
    public List<Sensor> findByAuxiliarySensor() {
        return sensorRepository.findByAuxiliarySensor();
    }

    @Override
    public Sensor findByName(String name) {
        return sensorRepository.findByName(name);
    }

    @Override
    public List<Sensor> findUnboundSensor() {
        return sensorRepository.findUnboundSensor();
    }

    @Override
    public Page<Sensor> findUserSensor(Long userId,Integer page, Integer limit) {


        Pageable pageable = new PageRequest(page-1,limit);


        return  sensorRepository.findUserSensor(userId, pageable);

    }

    @Override
    public Page<Sensor> findUserSensor(Long userId, String no,Integer page, Integer limit) {


        Pageable pageable = new PageRequest(page-1,limit);


        return sensorRepository.findUserSensor(userId,no, pageable);

    }

    @Override
    public Page<Sensor> findAllByNoContaining(String no,Integer page, Integer limit) {


        Pageable pageable = new PageRequest(page-1,limit);

        return sensorRepository.findAllByNoLike(no, pageable);
    }


    @Override
    public Sensor findByid(Long id) {
        return sensorRepository.getById(id);
    }

    @Override
    public void saveAndFlush(Sensor sensor) {
        sensorRepository.save(sensor);
    }

    @Override
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        //return sensorRepository.updateStatus(statusEnum.getCode(),idList) > 0;
        return null;
    }

    @Override
    public List<Sensor> findAll() {
        List<Sensor> list = new ArrayList<>();
        Iterable<Sensor> all = sensorRepository.findAll();
        Iterator<Sensor> iterator = all.iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());

        }
        return list;
    }

    @Override
    public Page<Sensor> findAll(Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return  sensorRepository.findAll(pageable);
    }
}
