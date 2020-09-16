package com.lwt.thread;

import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.view.MainFrame;

import java.util.concurrent.TransferQueue;

public class FeisiThread extends Thread {

    private int i;

    private MainFrame mainFrame;

    private boolean isRun;

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public FeisiThread(int i, MainFrame mainFrame)
    {
        isRun = true;
        this.i = i;
        this.mainFrame=mainFrame;
    }

    public void run() {
        while (i >= 0) {
            mainFrame.timeLabel.setText(i + "");

            i--;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message msg = null;

        if(i==-1||isRun==false){
            if(mainFrame.isFeisi){
                msg = new Message(8,mainFrame.currentPlayer.getId(),"废除4",null);
            }
            else{
                msg = new Message(8,mainFrame.currentPlayer.getId(),"不废除4",null);
            }
        }

        //将消息传到服务器端
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));

    }
}
