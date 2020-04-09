package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.modules.system.domain.AcquisitionSensor;
import com.linln.modules.system.domain.NetWork;
import com.linln.modules.system.repository.NetWorkRepository;
import com.linln.modules.system.service.NetWorkService;
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
public class NetWorkServiceImpl implements NetWorkService {


    @Autowired
    private NetWorkRepository netWorkRepository;


    @Override
    public Page<NetWork> getPageList(Example<NetWork> example) {
        PageRequest page = PageSort.pageSensorRequest();
        return  netWorkRepository.findAll(page);
    }

    @Override
    public NetWork getById(Long id) {
        return netWorkRepository.getById(id);
    }

    @Override
    public NetWork save(NetWork netWork) {
        return netWorkRepository.save(netWork);
    }

    @Override
    public NetWork getByRegisterSSID(String registerSSID) {
        return netWorkRepository.getByRegisterSSID(registerSSID);
    }

    @Override
    public List<AcquisitionSensor> findByRegisterSSIDAndAc(String registerSSID, String acquisitionNo) {
        return netWorkRepository.findByRegisterSSIDAndAc(registerSSID,acquisitionNo);
    }

    @Override
    public void saveAndFlush(NetWork netWork) {
        netWorkRepository.save(netWork);
    }

    @Override
    public Page<NetWork> findAll(Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return netWorkRepository.findAll(pageable);

    }

    @Override
    public List<NetWork> findAll() {
        List<NetWork> list = new ArrayList<>();
        Iterable<NetWork> all = netWorkRepository.findAll();
        Iterator<NetWork> iterator = all.iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());

        }
        return list;
    }

    @Override
    public Page<NetWork> finUserNetWork(Long userid,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return netWorkRepository.finUserNetWork(userid,pageable);
    }

    @Override
    public Page<NetWork> findAllByRegisterSSIDLike(String s,Integer page, Integer limit) {

        Pageable pageable = new PageRequest(page-1,limit);
        return netWorkRepository.findAllByRegisterSSIDLike(s,pageable);
    }

    @Override
    public NetWork findByRegisterSSID(String RegisterSSID) {
        return netWorkRepository.findByRegisterSSID(RegisterSSID);
    }

    @Override
    public void deleteById(Long id) {
        netWorkRepository.deleteById(id);
    }

    @Override
    public Page<NetWork> finUserNetWork(Long userid, String ssid,Integer page, Integer limit) {
        Pageable pageable = new PageRequest(page-1,limit);
        return netWorkRepository.finUserNetWork(userid,ssid,pageable);
    }
}
