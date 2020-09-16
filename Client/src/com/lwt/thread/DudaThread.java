package com.lwt.thread;

import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.view.MainFrame;

public class DudaThread extends Thread {

    private MainFrame mainFrame;

    private boolean isDuda;


    public DudaThread(MainFrame mainFrame, boolean isDuda)
    {
        this.isDuda = isDuda;

        this.mainFrame=mainFrame;
    }

    public void run() {

        Message msg = null;

        if(isDuda){
            msg = new Message(9,mainFrame.currentPlayer.getId(),"独打",null);
        }
        else{
            msg = new Message(9,mainFrame.currentPlayer.getId(),"不独打",null);
        }

        //将消息传到服务器端
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));

    }
}
