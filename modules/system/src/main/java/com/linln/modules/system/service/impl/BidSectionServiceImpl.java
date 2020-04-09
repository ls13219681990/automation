package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.BidSection;
import com.linln.modules.system.repository.BidSectionRepository;
import com.linln.modules.system.service.BidSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class BidSectionServiceImpl implements BidSectionService {

    @Autowired
    private BidSectionRepository bidSectionRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public Page<BidSection> getPageList(Example<BidSection> example) {
        PageRequest page = PageSort.pageNoRequest();
        return bidSectionRepository.findAll(page);

    }

    @Override
    public BidSection getById(Long id) {
        BidSection bidSection = new BidSection();
        Iterable<BidSection> bidSections = bidSectionRepository.findAllById(Collections.singleton(id));
        Iterator<BidSection> iterator = bidSections.iterator();
        while (iterator.hasNext()) {
            bidSection = iterator.next();
        }
        return bidSection;
    }

    @Override
    public BidSection save(BidSection bidSection) {
        return bidSectionRepository.save(bidSection);
    }

    @Override
    public void saveAndFlush(BidSection bidSection) {
        bidSectionRepository.save(bidSection);
    }

    @Override
    public BidSection findByName(String name) {
        return bidSectionRepository.findByName(name);
    }

    @Override
    public void removeById(Long id) {
        bidSectionRepository.removeById(id);
    }

    @Override
    public List<Object[]> findByNameGetMeasuringSpotData(String bidSectionName) {
        return bidSectionRepository.findByNameGetMeasuringSpotData(bidSectionName);
    }

    @Override
    public List<Object[]> findByNameGetMeasuringSpotData(String bidSectionName, String startTime, String endTime) {

        return bidSectionRepository.findByNameGetMeasuringSpotData(bidSectionName, startTime, endTime);
    }

    @Override
    public void update(BidSection bidSection) {
        entityManager.merge(bidSection);
        entityManager.flush();
    }

    @Override
    public Page<BidSection> findByLineInfoId(Long id, Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page - 1, limit);
        return bidSectionRepository.findByLineInfoId(id, pageable);
    }

    @Override
    public List<BidSection> findByLineInfoId(Long id) {
        return bidSectionRepository.findByLineInfoId(id);
    }

    @Override
    public List<BidSection> findAll() {
        List<BidSection> bidSections = new ArrayList<>();
        Iterator<BidSection> iterator = bidSectionRepository.findAll().iterator();
        while (iterator.hasNext()) {
            bidSections.add(iterator.next());
        }

        return bidSections;
    }


}
