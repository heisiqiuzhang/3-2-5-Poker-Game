package com.lwt.thread;

import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.model.Poker;
import com.lwt.model.PokerLabel;
import com.lwt.view.MainFrame;

import java.util.ArrayList;
import java.util.List;

public class JieShuThread extends Thread {

    private MainFrame mainFrame;


    public JieShuThread( MainFrame mainFrame)
    {

        this.mainFrame=mainFrame;
    }

    public void run()
    {
        Message msg = null;

        msg = new Message(7,mainFrame.currentPlayer.getId(),mainFrame.currentPlayer.getRole(),"跳过",null);

        //转换为json 交给 sendThread发送到服务器去
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));

    }
}

