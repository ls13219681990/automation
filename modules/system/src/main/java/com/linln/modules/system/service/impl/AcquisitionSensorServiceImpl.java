package com.linln.modules.system.service.impl;


import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionSensor;
import com.linln.modules.system.repository.AcquisitionSensorRepository;
import com.linln.modules.system.service.AcquisitionSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class AcquisitionSensorServiceImpl implements AcquisitionSensorService {

    @PersistenceContext
    protected EntityManager em;


    @Autowired
    private AcquisitionSensorRepository acquisitionSensorRepository;


    @Override
    public AcquisitionSensor getByacquisitionIdAndPassagewayId(Long acquisitionId, String passagewayId) {
        return acquisitionSensorRepository.getByacquisitionIdAndPassagewayId(acquisitionId,passagewayId);
    }

    @Override
    public Page<AcquisitionSensor> getPageList(Example<AcquisitionSensor> example) {
        PageRequest page = PageSort.pageAcquisitionRequest();
        return  acquisitionSensorRepository.findAll(example, page);


    }

    @Override
    public void save(AcquisitionSensor AcquisitionSensor) {
        acquisitionSensorRepository.saveAndFlush(AcquisitionSensor);
    }

    @Override
    public void save(List<AcquisitionSensor> acquisitionList) {
        acquisitionSensorRepository.saveAll(acquisitionList);
    }



    @Override
    public AcquisitionSensor findByid(Long id) {
        /*return acquisitionSensorRepository.getById(id);*/
        return acquisitionSensorRepository.getOne(id);
    }

    @Override
    public void saveAndFlush(AcquisitionSensor AcquisitionSensor) {
        acquisitionSensorRepository.saveAndFlush(AcquisitionSensor);
    }

    @Override
    public void deleteById(Long id) {

        acquisitionSensorRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<AcquisitionSensor> acquisitionSensors) {
        for (AcquisitionSensor m : acquisitionSensors) {
            for (int i = 0; i < acquisitionSensors.size(); i++) {
                em.remove(em.merge(m));
                //em.remove();
                if (i % 30 == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }

        em.flush();
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList){
        return null;
    }

    @Override
    public  List<AcquisitionSensor> findAll() {
        return  acquisitionSensorRepository.findAll();
    }

    @Override
    public List<AcquisitionSensor> getByacquisitionId(Long id) {
        return acquisitionSensorRepository.getByacquisitionId(id);
    }

    @Override
    public void removeAcquisitionSensorById(Long id) {
        acquisitionSensorRepository.removeAcquisitionSensorById(id);
    }

    @Override
    public AcquisitionSensor findBySensorId(Long id) {
        return acquisitionSensorRepository.findBySensorId(id);
    }


}
