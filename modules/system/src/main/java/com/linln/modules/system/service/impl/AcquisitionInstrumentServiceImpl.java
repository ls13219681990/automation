package com.linln.modules.system.service.impl;


import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.MeasuringSpot;
import com.linln.modules.system.repository.AcquisitionInstrumentRepository;
import com.linln.modules.system.repository.LineInfoRepository;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Service
public class AcquisitionInstrumentServiceImpl implements AcquisitionInstrumentService {

    @Autowired
    private AcquisitionInstrumentRepository acquisitionRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public Page<AcquisitionInstrument> getPageList() {
        PageRequest page = PageSort.pageAcquisitionRequest();
        Page<AcquisitionInstrument> all = acquisitionRepository.findAll( page);
        entityManager.clear();
        return all;

    }


    @Override
    public void save(AcquisitionInstrument acquisitionInstrument) {
        acquisitionRepository.save(acquisitionInstrument);
    }

    @Override
    public void save(List<AcquisitionInstrument> acquisitionList) {
        acquisitionRepository.saveAll(acquisitionList);
    }

    @Override
    public AcquisitionInstrument getByAcquisitionNo(String acquisitionNo) {
        /*return acquisitionRepository.findByNo(acquisitionNo);*/
        return null;
    }

    @Override
    public AcquisitionInstrument findByid(Long id) {
        AcquisitionInstrument acquisitionInstrument = new AcquisitionInstrument();
        /*return acquisitionRepository.getById(id);*/
        Iterable<AcquisitionInstrument> acquisitionInstruments = acquisitionRepository.findAllById(Collections.singleton(id));
        Iterator<AcquisitionInstrument> iterator = acquisitionInstruments.iterator();
        while (iterator.hasNext()){
            acquisitionInstrument = iterator.next();
        }
        return acquisitionInstrument;
    }

    @Override
    public void saveAndFlush(AcquisitionInstrument acquisitionInstrument) {
        acquisitionRepository.save(acquisitionInstrument);
    }

    @Override
    public void deleteById(Long id) {
        acquisitionRepository.deleteById(id);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return null;
    }

    @Override
    public Page<AcquisitionInstrument> findAll(Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        Page<AcquisitionInstrument> all = acquisitionRepository.findAll( pageable);
        return all;
    }

    @Override
    public List<AcquisitionInstrument> findAll() {
        List<AcquisitionInstrument> list = new ArrayList<>();
        Iterable<AcquisitionInstrument> all = acquisitionRepository.findAll();
        Iterator<AcquisitionInstrument> iterator = all.iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());

        }
        return list;
    }



    @Override
    public AcquisitionInstrument findByNo(String no) {
        return acquisitionRepository.getByNo(no);
    }

    @Override
    public void update(AcquisitionInstrument acquisitionInstrument) {
        acquisitionRepository.updateAcquisitionInstrument(acquisitionInstrument.getId());
    }

    @Override
    public List<AcquisitionInstrument> findByWorkSpotId(Long id) {
        return acquisitionRepository.findByWorkSpotId(id);
    }

    @Override
    public List<AcquisitionInstrument> getByNetWorkId(Long id) {
        return acquisitionRepository.getByNetWorkId(id);
    }

    @Override
    public void removeById(Long id) {
        acquisitionRepository.removeById(id);
    }

    @Override
    public void deleteBatch(List<AcquisitionInstrument> menus) {
        List<Long> datas = new ArrayList<>();
        for (AcquisitionInstrument ai : menus){
            datas.add(ai.getId());
        }
        acquisitionRepository.deleteBatch(datas);
    }

    @Override
    public List<AcquisitionInstrument> findByWorkSpotIdIsNull() {
        return acquisitionRepository.findByWorkSpotIdIsNull();
    }

    @Override
    public List<AcquisitionInstrument> findByWorkSpotIdAll(Long id,Long measuringSpotId) {
        return acquisitionRepository.findByWorkSpotIdAll(id,measuringSpotId);
    }

    @Override
    public MeasuringSpot findByAcquisitionIDGetMeasuringSpot(Long id) {
        return acquisitionRepository.findByAcquisitionIDGetMeasuringSpot(id);
    }

    @Override
    public Page<AcquisitionInstrument> findUserAcquisition(Long userId,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return acquisitionRepository.findUserAcquisition(userId,pageable);
    }

    @Override
    public Page<AcquisitionInstrument> findUserAcquisitionByNo(Long userId, String no,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return  acquisitionRepository.findUserAcquisitionByNo(userId,no,pageable);
    }

    @Override
    public Page<AcquisitionInstrument> findByNoLike(String no,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return acquisitionRepository.findAllByNoContaining(no,pageable);
    }

    @Override
    public void updateInterval(Long id, Integer interval) {
        acquisitionRepository.updateInterval(id, interval);
    }

    @Override
    public List<AcquisitionInstrument> findByNetWorkId(String id) {
        return acquisitionRepository.findByNetWorkId(id);
    }


}
