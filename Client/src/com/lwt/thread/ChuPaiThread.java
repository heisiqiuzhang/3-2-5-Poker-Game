package com.lwt.thread;

import com.alibaba.fastjson.JSON;
import com.lwt.model.Message;
import com.lwt.model.Poker;
import com.lwt.model.PokerLabel;
import com.lwt.view.MainFrame;


import javax.annotation.processing.Messager;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChuPaiThread extends Thread{

    private int i;
    private MainFrame mainFrame;
    private boolean isRun;


    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }

    public ChuPaiThread(int i, MainFrame mainFrame)
    {
        //调试点1
        isRun=true;
        this.i=i;
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

        if(i==-1 || isRun==false)
        {
            //不出
            if(mainFrame.isOut==false){
                msg = new Message(4, mainFrame.currentPlayer.getId(), "不出", null);
            }
            //出牌
            else if(mainFrame.isOut==true) {

                if (mainFrame.isLastOut==true){
                    msg = new Message(6,mainFrame.currentPlayer.getId(),mainFrame.currentPlayer.getRole(),"最后一张牌",changePokerLabelToPoer(mainFrame.selectPokerLabels));

                    mainFrame.removeOutPokerFromPokerList();
                }else {
                    msg = new Message(5, mainFrame.currentPlayer.getId(), "出牌", changePokerLabelToPoer(mainFrame.selectPokerLabels));

                    //将当前发送出去的扑克牌 从扑克牌列表中移除
                    mainFrame.removeOutPokerFromPokerList();
                }
                }

            //mainFrame.sendThread.setMsg(JSON.toJSONString(msg));
            }

        //转换为json 交给 sendThread发送到服务器去
        mainFrame.sendThread.setMsg(JSON.toJSONString(msg));


    }
    public List<Poker> changePokerLabelToPoer(List<PokerLabel> selectedPokerLabels)
    {
        List<Poker> list = new ArrayList<Poker>();
        for (int i = 0; i < selectedPokerLabels.size(); i++) {
            PokerLabel pokerLabel =selectedPokerLabels.get(i);
            Poker poker = new Poker(pokerLabel.getId(),pokerLabel.getName(),pokerLabel.getNum(),pokerLabel.isOut());
            list.add(poker);
        }
        return list;
    }
}
