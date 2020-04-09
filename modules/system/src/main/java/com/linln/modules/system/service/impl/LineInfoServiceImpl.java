package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.Dept;
import com.linln.modules.system.domain.Dict;
import com.linln.modules.system.domain.LineInfo;
import com.linln.modules.system.page.LineMeasuringSpotDataPage;
import com.linln.modules.system.repository.DictRepository;
import com.linln.modules.system.repository.LineInfoRepository;
import com.linln.modules.system.service.DictService;
import com.linln.modules.system.service.LineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * @author SWICS
 * @date 2018/8/14
 */
@Service
public class LineInfoServiceImpl implements LineInfoService {

    @Autowired
    private LineInfoRepository lineInfoRepository;


    @Autowired
    private EntityManager entityManager;


    @Override
    public Page<LineInfo> getPageList(Example<LineInfo> example) {

        PageRequest page = PageSort.pageNoRequest();
        return lineInfoRepository.findAll(example, page);
    }

    @Override
    public List<LineInfo> findAll() {
        return   lineInfoRepository.findAll();
    }

    @Override
    public LineInfo getById(Long id) {
        return lineInfoRepository.getOne(id);
    }

    @Override
    public LineInfo save(LineInfo lineInfo) {
        return lineInfoRepository.save(lineInfo);
    }

    @Override
    public void saveAndFlush(LineInfo lineInfo) {
        lineInfoRepository.saveAndFlush(lineInfo);
    }

    @Override
    public LineInfo findByName(String name) {
        return lineInfoRepository.findByName(name);
    }

    @Override
    public void removeById(Long id) {
        lineInfoRepository.removeById(id);
    }

    @Override
    public List<Object[]> findByNameGetMeasuringSpotData(String name) {
        return lineInfoRepository.findByNameGetMeasuringSpotData(name);
    }

    @Override
    public void update(LineInfo lineInfo) {


        entityManager.merge(lineInfo);
        entityManager.flush();

    }
}
