package com.linln.admin.system.common.socket;

import lombok.Data;

import java.nio.channels.SocketChannel;


/**
 * 用于存放采集仪socket数据
 */
@Data
public class ChannelInfo {

    String ip;
    SocketChannel channelData; //通道信息
    String netWork; //模块标识
}
