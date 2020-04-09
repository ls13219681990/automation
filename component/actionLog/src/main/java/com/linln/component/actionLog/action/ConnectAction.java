package com.linln.component.actionLog.action;

import com.linln.component.actionLog.action.base.ActionMap;
import com.linln.component.actionLog.action.base.ResetLog;
import com.linln.component.actionLog.action.model.BusinessMethod;
import com.linln.modules.system.domain.Role;

import javax.persistence.Table;

/**
 * 角色日志行为
 * @author SWICS
 * @date 2018/10/14
 */
public class ConnectAction extends ActionMap {

    public static final String DISCONNECT = "disconnect";

    @Override
    public void init() {
        //
        putMethod(DISCONNECT, new BusinessMethod("断开连接","disconnect"));
    }

    // 角色授权行为方法
    public void disconnect(ResetLog resetLog){
            resetLog.getActionLog().setMessage("已断开连接");


    }
}
