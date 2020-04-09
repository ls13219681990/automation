package com.linln.admin.system.common.socket;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
     * SelectionKey 处理接口
     *
     */
    public  interface HandlerSelectionKey {
        public void handler(SelectionKey key, Selector selector) throws IOException;
    }