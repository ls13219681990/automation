package com.linln.admin.system.common.socket;

/**
 * SelectionKey 接口 实现类
 */
//public class HandlerHandlerSelectionKeyImplTest implements HandlerSelectionKey {
public class HandlerHandlerSelectionKeyImplTest{} /*{


    protected static final Logger logger = LoggerFactory.getLogger(HandlerHandlerSelectionKeyImplTest.class);





    @Override
    public void handler(SelectionKey key, Selector selector) {
        try {
            int keyState = selectionKeyState(key);

            switch (keyState) {
                case SelectionKey.OP_ACCEPT:

                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    accept(serverSocketChannel, selector);

                    break;
                case SelectionKey.OP_READ:
                    SocketChannel readSocketChannel = (SocketChannel) key.channel();
                    read(readSocketChannel, selector);
                 *//*   ByteBuffer bf = ByteBuffer.allocate(2048); //200M的Buffer
                    //注册写事件
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                    //绑定Buffer
                    key.attach(bf);*//*

                    break;
                case SelectionKey.OP_WRITE:
                   *//* Iterator<Map.Entry<String, SocketChannel>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<String, SocketChannel> next = iterator.next();
                        ByteBuffer buffer = ByteBuffer.allocate(2048);
                        try {
                            next.getValue().write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }*//*


                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (buffer.hasRemaining()) {
                        channel.write(buffer);
                    } else {
                        //发送完了就取消写事件，否则下次还会进入该分支
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                    break;
                case SelectionKey.OP_CONNECT:
                    System.out.println("可连接++++++++++++++++++++++++++++++++++++++++++");
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();
            key.cancel();
        }
    }




    *//**
     * 获取 SelectionKey 是什么事件
     *
     * @param key
     * @return
     *//*
    private int selectionKeyState(SelectionKey key) {
        if (key.isAcceptable()) {
            return SelectionKey.OP_ACCEPT;
        } else if (key.isReadable() && key.isValid()) {
            return SelectionKey.OP_READ;
        }
        return -1;
    }

    *//**
     * 接口客户端请求
     *
     * @param serverSocketChannel
     * @param selector
     * @throws IOException
     *//*
    private void accept(ServerSocketChannel serverSocketChannel, Selector selector) throws Exception {

        SocketChannel socketChannel = serverSocketChannel.accept();


        socketChannel.configureBlocking(false);
        channels.add(socketChannel);
        //将 channel 注册到  Selector
        socketChannel.register(selector, SelectionKey.OP_READ);

    }

    *//**
     * 读取客户端发送过来的信息
     *
     * @param socketChannel
     * @param selector
     * @throws IOException
     *//*
    private void read(SocketChannel socketChannel, Selector selector) throws Exception {

        ByteBuffer readBuffer = ByteBuffer.allocate(2048);
        int readBytes = socketChannel.read(readBuffer);

        *//*String msg = "";//客户端发送来的消息
        msg = new String(readBuffer.array(), 0, readBytes);*//*
        try {
            if (readBytes == -1) {
                logger.info("客户端已经关闭了连接");
                socketChannel.close();
            } else if (readBytes > 0) {

                boolean falg = true;
                String str = CommonMethod.byteArrayTurnHexString(readBuffer.array());
                str = str.substring(0, readBytes * 2);
                String parameter = "";
                for (Integer i = 0; i < str.length(); i += 2) {
                    String substring = str.substring(i, i + 2);
                    Integer integer = Integer.parseInt(substring, 16);
                    parameter += (char) Integer.parseInt(integer.toString());
                }


                //parameter = parameter.substring(0, readBytes * 2);
                logger.info("接收时间:" + CommonMethod.getDate() + "==>接受参数:" + str);

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
                    //切割数据：q切割为八个一组做数据计算
                    List<Map<String, List<String>>> maps = CommonMethod.dataSegmentationAndReplace(str);
                    for (Map<String, List<String>> dataMap : maps) {
                        stringListMap = dataMap;
                        //切割好后拿到所有的通道数据
                        List<String> passagewayData = CommonMethod.getPassagewayDataByMap(stringListMap);
                        String currentTime = CommonMethod.byParameterGetTime(passagewayData.get(0));
                        //时间超过当前时间一个小时就不要了
                        if (CommonMethod.getDatePoor(CommonMethod.getDate(), currentTime) > 60) {
                            logger.info("数据已超过一个小时该数据将舍弃==>数据时间:" + currentTime);
                            break;
                        }


                        String AcquisitionNo = CommonMethod.getKeyOrNull(stringListMap);
                        Integer No = Integer.parseInt(AcquisitionNo.substring(AcquisitionNo.length() - 2), 16) + 1;
                        List<AcquisitionSensor> acquisitionSensor = netWorkService.findByRegisterSSIDAndAc(str.substring(0, 9), No.toString());
                        //测点传感器数据
                        List<Object[]> ass = measuringSpotService.findByAcquisitionNoGetMeasuringSpotSensor(No.toString(), parameter.substring(0, 9));
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
                                            //处理小数点位
                                            String sensorData = "";
                                            if (sensor.getDecimalPoint() != null && !"".equals(sensor.getDecimalPoint())) {
                                                sensorData = String.format("%." + Integer.valueOf(sensor.getDecimalPoint()) + "f", value / Math.pow(10, Integer.valueOf(sensor.getDecimalPoint())));
                                                value = value / Math.pow(10, Integer.valueOf(sensor.getDecimalPoint()));
                                            }

                                            if (msd.getMeasuringSpotOriginalValue() == null) {
                                                msd.setMeasuringSpotOriginalValue(sensorData + ",");
                                            } else {
                                                msd.setMeasuringSpotOriginalValue(msd.getMeasuringSpotOriginalValue() + sensorData + ",");
                                            }
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


                                            if (calculationValue > 60000 || calculationValue < 400) {
                                                logger.info("传感器读数已超过正常读数该条数据将舍弃===>传感器读数为:" + value);
                                                break;
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
                                if (finalValue > 0) {
                                    finalValue = Double.valueOf(new DecimalFormat("#.00").format(finalValue / valueArr.size())) * ms.getMeasuringSpotSymbol();

                                }

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
                                        double dayRateValue = Double.valueOf(new DecimalFormat("#.00").format((msd.getMeasuringSpotValue() - spotData.getMeasuringSpotValue()) / (datePoor / 1440) > 0 ? msd.getMeasuringSpotValue() - spotData.getMeasuringSpotValue() / (datePoor / 1440) : 1));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/




