package com.lwt.thread;

import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.view.MainFrame;

public class SendIdOfPartnerThread extends Thread{
    private MainFrame mainFrame;

    private int partnerPlayerId;

    public SendIdOfPartnerThread(MainFrame mainFrame, int partnerPlayerId)
    {

        this.partnerPlayerId=partnerPlayerId;
        this.mainFrame=mainFrame;
    }

    public void run() {

        Message msg = null;

        msg = new Message(10,mainFrame.currentPlayer.getId(),partnerPlayerId+"",null);

        //将消息传到服务器端
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));

    }
}
