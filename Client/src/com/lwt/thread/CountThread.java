package com.lwt.thread;


import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.view.MainFrame;

import javax.swing.*;

//定时器的线程
public class CountThread extends Thread {

    private int i;

    private MainFrame mainFrame;

    private boolean isRun;

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public CountThread(int i, MainFrame mainFrame)
    {
        isRun=true;
        this.i = i;
        this.mainFrame=mainFrame;
    }

    public void run()
    {
        while(i>=0 && isRun)
        {
            mainFrame.timeLabel.setText(i+"");

            i--;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Message msg = null;
        //时间到了 或者  进行过花色的选取(并且选的为黑桃)
        if(i==-1 || isRun == false)
        {
            if(mainFrame.isHeitao) {
                msg = new Message(1,mainFrame.currentPlayer.getId(), "黑桃", null);
            }
            else if(mainFrame.isMeihua){
                msg = new Message(2,mainFrame.currentPlayer.getId(), "梅花", null);
            }
            else if(mainFrame.isFangkuai)
            {
                msg = new Message(3,mainFrame.currentPlayer.getId(), "方块", null);
            }else
            {
                msg = new Message(3,mainFrame.currentPlayer.getId(), "方块", null);
            }

        }

        //将消息传到服务器端
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));


    }
}
