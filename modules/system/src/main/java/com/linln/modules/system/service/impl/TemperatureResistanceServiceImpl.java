package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.ActionLog;
import com.linln.modules.system.domain.TemperatureResistance;
import com.linln.modules.system.repository.ActionLogRepository;
import com.linln.modules.system.repository.TemperatureResistanceRepository;
import com.linln.modules.system.service.ActionLogService;
import com.linln.modules.system.service.TemperatureResistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class TemperatureResistanceServiceImpl implements TemperatureResistanceService {

    @Autowired
    private TemperatureResistanceRepository temperatureResistanceRepository;

    @Autowired
    EntityManager entityManager;

    @Override
    public Page<TemperatureResistance> getPageList(Example<TemperatureResistance> example) {
        PageRequest page = PageSort.pageSensorRequest();
        return  temperatureResistanceRepository.findAll(example, page);
    }

    @Override
    public void save(TemperatureResistance temperatureResistance) {
        temperatureResistanceRepository.save(temperatureResistance);
    }

    @Override
    public void saveAll(List<TemperatureResistance> temperatureResistance) {
        temperatureResistanceRepository.saveAll(temperatureResistance);
    }

    @Override
    public void update(TemperatureResistance temperatureResistance) {
        entityManager.merge(temperatureResistance);
        entityManager.flush();
    }

    @Override
    public List<TemperatureResistance> findAll() {
        return temperatureResistanceRepository.findAll();
    }

    @Override
    public TemperatureResistance findByName(String name) {
        return temperatureResistanceRepository.findByName(name);
    }

    @Override
    public TemperatureResistance findById(Long id) {
        return temperatureResistanceRepository.getOne(id);
    }
}
