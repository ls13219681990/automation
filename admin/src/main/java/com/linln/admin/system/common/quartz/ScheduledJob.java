package com.linln.admin.system.common.quartz;

import com.linln.admin.system.common.socket.ChannelInfo;
import com.linln.admin.system.common.socket.HandlerHandlerSelectionKeyImpl;
import com.linln.modules.system.domain.AcquisitionInstrument;
import com.linln.modules.system.domain.NetWork;
import com.linln.modules.system.service.AcquisitionInstrumentService;
import com.linln.modules.system.service.NetWorkService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.linln.admin.system.common.CommonMethod;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.linln.admin.system.common.CommonMethod.getCode;
import static com.linln.admin.system.common.socket.HandlerHandlerSelectionKeyImpl.channelList;


public class ScheduledJob implements Job {


    protected static final Logger logger = LoggerFactory.getLogger(Job.class);


    @Autowired
    AcquisitionInstrumentService acquisitionInstrumentService;

    @Autowired
    NetWorkService netWorkService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {

        /**
         * 业务:采集仪关闭了间隔采集就按整点主动采集一次数据的方式进行采集
         * 思路:获取所有的4G模块 拿到所有模块下面的采集仪 关闭了采集间隔的就进行发送命令进行采集
         */

        String currentDate = CommonMethod.getCurrentDate() + " 00:00:00";

        Long datePoor = CommonMethod.getDatePoor(currentDate, CommonMethod.getDate());


        try {
            logger.info("开始检测关闭采集间隔的采集仪");

            Map<String, String> map = new HashMap<>();
            for (ChannelInfo channelInfo : channelList) {
                //去重
                map.put(channelInfo.getNetWork(), channelInfo.getNetWork());
            }

            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                NetWork netWork = netWorkService.findByRegisterSSID(next.getKey());
                if (netWork != null) {
                    List<AcquisitionInstrument> acquisitionInstrumentList = acquisitionInstrumentService.findByNetWorkId(String.valueOf(netWork.getId()));
                    for (AcquisitionInstrument acquisitionInstrument : acquisitionInstrumentList) {
                        if (acquisitionInstrument.getIntervalstate() == 0 && datePoor % acquisitionInstrument.getIntervalTime() == 0) {
                            logger.info("对关闭了采集间隔的采集仪进行单次采集======>当前时间" + CommonMethod.getDate());
                            HandlerHandlerSelectionKeyImpl.sendData(netWork.getRegisterSSID(), getCode(3, CommonMethod.intToHex(Integer.valueOf(acquisitionInstrument.getNo()) - 1), acquisitionInstrument.getIntervalTime(), 0));
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}