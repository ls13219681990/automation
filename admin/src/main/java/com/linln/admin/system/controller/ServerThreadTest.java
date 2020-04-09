package com.linln.admin.system.controller;


import com.linln.admin.system.common.ApplicationContextProvider;
import com.linln.modules.system.Common.EntityUtils;
import com.linln.modules.system.domain.*;
import com.linln.modules.system.page.AcquisitionSensorPage;
import com.linln.modules.system.service.*;
import com.linln.modules.system.service.impl.*;
import org.springframework.util.CollectionUtils;
import com.linln.admin.system.common.CommonMethod;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.*;

public class ServerThreadTest extends Thread {


    Map<String, String> uploadData = new HashMap<>();


    private Socket socket;
    InputStream inputStream;

    //存储上传数据 注册码:
    private static Map<String, String> UPLOAD_DATA = new HashMap<>();

    public ServerThreadTest(Socket socket) {
        this.socket = socket;
    }

    public void run() {

        try {
            while (true) {


                inputStream = socket.getInputStream();

                SocketChannel channel = socket.getChannel();

                int trafficClass = socket.getTrafficClass();

                SocketAddress remoteSocketAddress = socket.getRemoteSocketAddress();

                int port = socket.getPort();

                /*System.out.println("当前线程名称：" + Thread.currentThread().getName() + "+IP地址" + remoteSocketAddress + "+端口" + port);*/
                InetAddress inetAddress = socket.getInetAddress();

                int available = inputStream.available();
                /*    NetWork work = netWorkService.getByRegisterSSID("www.usr.cn");*/
                //获取流中的数据放入byt数组
                int count = 0;
                while (count == 0) {
                    count = inputStream.available();
                }
                byte[] bytes = new byte[count];
                inputStream.read(bytes);

                System.out.println("传过来的数据==>" + bytes);
                //转十六进制字符串
                String str = CommonMethod.byteArrayTurnHexString(bytes);

                String parameter = "";
                for (Integer i = 0; i < str.length(); i += 2) {
                    String substring = str.substring(i, i + 2);
                    Integer integer = Integer.parseInt(substring, 16);
                    parameter += (char) Integer.parseInt(integer.toString());
                }

                boolean falg = true;
                System.out.println("当前线程" + Thread.currentThread().getId());
                ThreadGroup currentGroup =
                        Thread.currentThread().getThreadGroup();
                int noThreads = currentGroup.activeCount();
                Thread[] lstThreads = new Thread[noThreads];
                currentGroup.enumerate(lstThreads);
                for (int i = 0; i < noThreads; i++) {
                    System.out.println("正在运行的所有线程号：" + i + " = " + lstThreads[i].getName());
                }
                if (parameter.indexOf("SWI") != -1) {


                    //判断是否有当前线程
                    if (!uploadData.containsKey(String.valueOf(Thread.currentThread().getId()))) {

                        if (uploadData.containsValue(parameter)) {
                            //如果有心跳包 那么说明是断开了链接再连上了

                            //找到以前的
                            String key = CommonMethod.getKey(uploadData, parameter);

                            uploadData.remove(key);


                            uploadData.put(String.valueOf(Thread.currentThread().getId()), parameter);
                        } else {
                            //如果两个都没有 那么说明是第一次注册直接保存
                            uploadData.put(String.valueOf(Thread.currentThread().getId()), parameter);

                        }
                    }
                } else {
                    if (!uploadData.containsKey(String.valueOf(Thread.currentThread().getId()))) {
                        falg = false;
                    }
                }

                if (falg) {
                    System.out.println("接收时间:" + CommonMethod.getDate() + "==>接受参数:" + str);

                    //第一步  判断注册码是否在4G模块表中存在
                    //第二步  存储心跳包
                    //第三步 保存传感器原始数据
                    //四步  计算值 保存到测点

                    Map<String, List<String>> stringListMap = new LinkedHashMap<>();
                    if (str.contains("BB")) {


                        NetWorkService netWorkService = ApplicationContextProvider.getBean("netWorkServiceImpl", NetWorkServiceImpl.class);
                        SensorDataServiceImpl sensorDataService = ApplicationContextProvider.getBean("sensorDataServiceImpl", SensorDataServiceImpl.class);
                        MeasuringSpotServiceImpl measuringSpotService = ApplicationContextProvider.getBean("measuringSpotServiceImpl", MeasuringSpotServiceImpl.class);
                        TemperatureResistanceService temperatureResistanceService = ApplicationContextProvider.getBean("temperatureResistanceServiceImpl", TemperatureResistanceServiceImpl.class);
                        TemperatureResistanceDataService temperatureResistanceDataService = ApplicationContextProvider.getBean("temperatureResistanceDataServiceImpl", TemperatureResistanceDataServiceImpl.class);
                        SensorParameterServiceImpl sensorParameterService = ApplicationContextProvider.getBean("sensorParameterServiceImpl", SensorParameterServiceImpl.class);
                        SensorService sensorService = ApplicationContextProvider.getBean("sensorServiceImpl", SensorServiceImpl.class);
                        MeasuringSpotDataService measuringSpotDataService = ApplicationContextProvider.getBean("measuringSpotDataServiceImpl", MeasuringSpotDataServiceImpl.class);
                        MeasuringSpotSensorService measuringSpotSensorService = ApplicationContextProvider.getBean("measuringSpotSensorServiceImpl", MeasuringSpotSensorServiceImpl.class);


                        //接受数据 传感器存储原始数据
                        //NetWork byRegisterSSID = netWorkService.getByRegisterSSID(String.valueOf(Thread.currentThread().getId()));

                        //切割数据：q切割为八个一组做数据计算
                        List<Map<String, List<String>>> maps = CommonMethod.dataSegmentationAndReplace(str);
                        for (Map<String, List<String>> dataMap : maps) {

                            stringListMap = dataMap;
                            //切割好后拿到所有的通道数据

                            List<String> passagewayData = CommonMethod.getPassagewayDataByMap(stringListMap);

                            String currentTime = CommonMethod.byParameterGetTime(passagewayData.get(0));


                            String AcquisitionNo = CommonMethod.getKeyOrNull(stringListMap);
                            Integer No = Integer.parseInt(AcquisitionNo.substring(AcquisitionNo.length() - 2), 16) + 1;

                            List<AcquisitionSensor> acquisitionSensor = netWorkService.findByRegisterSSIDAndAc(uploadData.get(String.valueOf(Thread.currentThread().getId())), No.toString());


                            /*for (int i = 0; i < acquisitionSensor.size(); i++) {

                                if (!"00000000".equals(passagewayData.get(Integer.valueOf(acquisitionSensor.get(i).getPassagewayId())))) {

                                    //根据传感器ID和通道ID查询最后一次接受的

                                    SensorData sd = new SensorData();
                                    sd.setSensorId(acquisitionSensor.get(i).getSensorId());
                                    sd.setData(passagewayData.get(Integer.valueOf(acquisitionSensor.get(i).getPassagewayId())));
                                    sd.setReceiveTime(currentTime);
                                    sensorDataService.save(sd);
                                }
                            }*/
                            //测点传感器数据
                            List<Object[]> ass = measuringSpotService.findByAcquisitionNoGetMeasuringSpotSensor(No.toString(), uploadData.get(String.valueOf(Thread.currentThread().getId())));
                            List<AcquisitionSensorPage> acquisitionSensors = EntityUtils.castEntity(ass, AcquisitionSensorPage.class, new AcquisitionSensorPage());
                            if (acquisitionSensors.size() > 0 && !CollectionUtils.isEmpty(acquisitionSensors)) {
                                //测点信息
                                List<MeasuringSpot> measuringSpotList = measuringSpotService.findByAcquisitionNo(No.toString());
                                for (MeasuringSpot ms : measuringSpotList) {
                                    List<Double> valueArr = new ArrayList<>();
                                    SensorParameter sparameter = new SensorParameter();
                                    MeasuringSpotData msd = new MeasuringSpotData();
                                    List<MeasuringSpotSensor> measuringSpotSensorList = measuringSpotSensorService.getByMeasuringSpotId(ms.getId());
                                    for (int j = 0; j < acquisitionSensors.size(); j++) {
                                        for (MeasuringSpotSensor measuringSpotSensor : measuringSpotSensorList) {
                                            if (measuringSpotSensor.getAcquisitionSensorId() == Long.valueOf(acquisitionSensors.get(j).getId().toString())) {

                                                Double calculationValue = 0d;
                                                String parameterData = passagewayData.get(Integer.valueOf(acquisitionSensors.get(j).getPassagewayId()));
                                                Double value = CommonMethod.calculationParameter(parameterData);
                                                sparameter = sensorParameterService.findBySensourId(Long.valueOf(acquisitionSensors.get(j).getSensorId().toString()));
                                                Sensor sensor = sensorService.findByid(sparameter.getSensourId());
                                                //计算值
                                                if (parameter != null) {


                                                    if (!"1".equals(sensor.getIsAuxiliarySensor())) {


                                                        Sensor auxiliarySensor = measuringSpotService.findMeasuringSpotSensorAuxiliarySensorByName(ms.getName());


                                                        //没有辅助传感器就不加T0的公式
                                                        if (auxiliarySensor == null) {
                                                            calculationValue = sparameter.getK() * ((value * value - sparameter.getF0() * sparameter.getF0()) / 1000) + sparameter.getB();
                                                        } else {
                                                            //SensorData auxiliarySensorData = sensorDataService.findBySensorIdAndReceiveTime(auxiliarySensor.getId(), acquisitionSensors.get(j).getReceiveTime());

                                                            SensorData auxiliarySensorData = sensorDataService.findBySensorId(auxiliarySensor.getId());

                                                            //计算原始值
                                                            String sensorDatas = auxiliarySensorData.getData();
                                                            Double auxiliarySensorvalue = CommonMethod.calculationParameter(sensorDatas);

                                                            TemperatureResistance tr = temperatureResistanceService.findByName(auxiliarySensor.getTemperatureResistanceName());
                                                            //温阻表的电阻排序
                                                            List<TemperatureResistanceData> data = temperatureResistanceDataService.findByTemperatureResistanceId(tr.getId());
                                                            Collections.sort(data, new Comparator<TemperatureResistanceData>() {
                                                                public int compare(TemperatureResistanceData arg0, TemperatureResistanceData arg1) {
                                                                    Double hits0 = Double.valueOf(arg0.getTempResist());
                                                                    Double hits1 = Double.valueOf(arg1.getTempResist());
                                                                    if (hits1 < hits0) {
                                                                        return 1;
                                                                    } else if (hits1 == hits0) {
                                                                        return 0;
                                                                    } else {
                                                                        return -1;
                                                                    }
                                                                }
                                                            });


                                                            //温度查表法
                                                            Double T = 0.0d;
                                                            for (int i = 0; i < data.size(); i++) {
                                                                //查找到第一个大于他的数求差值
                                                                if (auxiliarySensorvalue / 1000 < Double.valueOf(data.get(i).getTempResist())) {
                                                                    Double t = auxiliarySensorvalue / 1000;
                                                                    Double t2 = Double.valueOf(data.get(i).getTempResist());
                                                                    Double t1 = Double.valueOf(data.get(i + 1).getTempResist());
                                                                    Double a2 = Double.valueOf(data.get(i).getTemp());
                                                                    Double a1 = Double.valueOf(data.get(i + 1).getTemp());
                                                                    T = a1 - ((t1 - t) / (t1 - t2)) * (a1 - a2);
                                                                    break;
                                                                }
                                                            }

                                                            calculationValue = sparameter.getK() * ((value * value - sparameter.getF0() * sparameter.getF0()) / 1000) + sparameter.getKt() * (T - sparameter.getT0()) + sparameter.getB();
                                                        }
                                                    }
                                                }
                                                msd.setMeasuringSpotId(ms.getId());
                                                msd.setReceiveTime(currentTime);
                                                if (msd.getMeasuringSpotSensorNo() == null || "".equals(msd.getMeasuringSpotSensorNo())) {
                                                    msd.setMeasuringSpotSensorNo(sensor.getNo() + ",");
                                                } else {
                                                    msd.setMeasuringSpotSensorNo(msd.getMeasuringSpotSensorNo() + sensor.getNo() + ",");
                                                }

                                                if (msd.getMeasuringSpotOriginalValue() == null) {
                                                    msd.setMeasuringSpotOriginalValue(value + ",");
                                                } else {
                                                    msd.setMeasuringSpotOriginalValue(msd.getMeasuringSpotOriginalValue() + value + ",");
                                                }

                                                valueArr.add(calculationValue);
                                            }
                                        }


                                    }
//分组计算值   //C*（A1+A2+...+An)/n
                                    Double finalValue = 0d;

                                    for (int i = 0; i <= valueArr.size() - 1; i++) {
                                        finalValue += (sparameter.getC() * (valueArr.get(i))) / 1;
                                    }
                                    MeasuringSpotData measuringSpotData = measuringSpotDataService.findFirstByMeasuringSpotIdByOrderByReceiveTimeDesc(ms.getId());
                                    finalValue = Double.valueOf(new DecimalFormat("#.00").format(finalValue / valueArr.size())) * ms.getMeasuringSpotSymbol();

                                    msd.setMeasuringSpotValue(finalValue);
                                    if (ms.getCalculatedValue() != null) {
                                        msd.setMeasuringSpotValue(msd.getMeasuringSpotValue() + ms.getCalculatedValue());
                                    }


                                    if (measuringSpotData == null) {
                                        //第一次接收到数据如果是设置了初值就用初值
                                        if (ms.getInitialValue() != null && !"".equals(ms.getInitialValue())) {
                                            msd.setMeasuringSpotValue(Double.valueOf(ms.getInitialValue()) * ms.getMeasuringSpotSymbol());
                                        }
                                        msd.setDayRateValue(0d);
                                        msd.setMeasuringSpotDifference(0d);
                                        msd.setMeasuringSpotAccumulationValue(0d);

                                    } else {
                                        //计算日速率
                                        MeasuringSpotData spotData = measuringSpotDataService.findByReceiveTime(CommonMethod.getTimeMinuteAdditionAndSubtraction(currentTime, 1438L));
                                        if (spotData != null) {
                                            Long datePoor = CommonMethod.getDatePoor(spotData.getReceiveTime(), currentTime);
                                            double dayRateValue = Double.valueOf(new DecimalFormat("#.00").format((msd.getMeasuringSpotValue() - spotData.getMeasuringSpotValue()) / (datePoor / 1440)));
                                            msd.setDayRateValue(dayRateValue);
                                        } else {
                                            msd.setDayRateValue(0d);
                                        }

                                        msd.setMeasuringSpotDifference(Double.valueOf(new DecimalFormat("#.00").format((msd.getMeasuringSpotValue() - measuringSpotData.getMeasuringSpotValue()) * ms.getMeasuringSpotSymbol())
                                        ));
                                        msd.setMeasuringSpotAccumulationValue(Double.valueOf(new DecimalFormat("#.00").format((measuringSpotData.getMeasuringSpotAccumulationValue() + msd.getMeasuringSpotDifference()) * ms.getMeasuringSpotSymbol())));
                                    }
                                    msd.setWhetherAlignment("1");
                                    measuringSpotDataService.save(msd);
                                }
                            }
                        }
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            run();
        }/* finally {

        }*/
    }

}


//传入采集仪编号

/*
                        System.out.println();
                        Iterator<Map.Entry<String, List<String>>> iterator = stringListMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            int i = 0;
                            Map.Entry<String, List<String>> next = iterator.next();

                            List<String> value = next.getValue();
                            if (i == 0) {
                                for (int j = 1; j < value.size() - 1; j++) {
                                    //获取时间
                                    String data = value.get(0);
                                    double integer = CommonMethod.calculationParameter(data);
                                    System.out.println(integer);
                                }
                            }
                            i++;
                        }
                        */
                    /*OutputStream os = socket.getOutputStream();
                    byte[] byteValue = new byte[256];
                    StringBuilder value = new StringBuilder();
                    for (int i = 0; i < 256; i++) {
                        byteValue[i] = (byte) (i & 0xff);
                    }
                    String hexString = value.toString();
                    os.write(byteValue);*/