package com.lwt.thread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwt.model.Player;
import com.lwt.model.Poker;
import com.lwt.view.MainFrame;


import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

//创建一个接收消息的线程
public class ReceiveThread extends Thread {

    private Socket socket;
    private MainFrame mainFrame;
    private int step = 0;
    private boolean isRun;

    private boolean isDuDa;

    private int shangShangJiaIsOver;
    private int shangShangShangJiaIsOver;
    private int hongSanPlayerId;
    private int partnerPlayerId;

    private int hongsanPlayerCount_Over=0;
    private int feiHongsanPlayerCount_Over=0;

    private List<Player> quanJuPlayers = new ArrayList<Player>();

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean isRun) {
        this.isRun = isRun;
    }


    public ReceiveThread(Socket socket, MainFrame mainFrame)
    {
        this.socket = socket;
        this.mainFrame = mainFrame;
    }

    public void run()
    {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            while(true)
            {
                //if(isRun==false)
                    //break;

                //接收从服务器端传递过来的消息  json字符串
                String jsonString = dataInputStream.readUTF();

                if (step == 0)
                {
                    //System.out.println(jsonString);
                    List<Player> players = new ArrayList<Player>();
                    //解析json字符串    从"[{},{}]"解析为 [{},{}]
                    //将json字符串转换为json数组
                    JSONArray playerJsonArray = JSONArray.parseArray(jsonString);
                    for (int i = 0; i < playerJsonArray.size(); i++) {
                        //获得单个json对象  ————> 玩家对象
                        JSONObject playerJson = (JSONObject) playerJsonArray.get(i);
                        int id = playerJson.getInteger("id");
                        String name = playerJson.getString("name");

                        //存放扑克列表
                        List<Poker> pokers = new ArrayList<Poker>();
                        JSONArray pokerJsonArray = playerJson.getJSONArray("pokers");
                        for (int j = 0; j < pokerJsonArray.size(); j++) {
                            //每循环一次，获得一个扑克对象
                            JSONObject pokerJson = (JSONObject) pokerJsonArray.get(j);
                            int pid = pokerJson.getInteger("id");
                            String pname = pokerJson.getString("name");
                            int num = pokerJson.getInteger("num");

                            Poker poker = new Poker(pid, pname, num);
                            pokers.add(poker);
                        }
                        Player player = new Player(id, name, pokers);
                        players.add(player);
                        quanJuPlayers.add(player);
                    }
                    //获得5个玩家的信息了
                    if (players.size() == 5) {
                        mainFrame.showAllPlayersInfo(players);
                        step = 1;//玩家到齐，进展到第2步
                    }
                }

                else if(step == 1)
                {
                    //接收选择队友花色的信息
                    JSONObject msgJsonObject = JSONObject.parseObject(jsonString);

                    //解析消息对象
                    int typeid = msgJsonObject.getInteger("typeid");
                    int playerid = msgJsonObject.getInteger("playerid");
                    String contentString = msgJsonObject.getString("content");

                    //红3以及队友的id
                    if(typeid==10){
                        partnerPlayerId = playerid;
                    }

                    //独打与否的消息
                    if(typeid==9){
                        if(contentString.equals("独打")){
                            isDuDa=true;
                        }else{
                            isDuDa=false;
                        }
                    }

                    //废除4与否的消息
                    if(typeid==8){

                        //将所有玩家拥有的4，根据废除或不废除的content信息，将其移除或不移除
                        mainFrame.showFeisiResultIcon(contentString);

                        //如果废4，则将所有玩家手中所有的4从扑克列表中移除
                        if("废除4".equals(contentString)){
                            //从扑克列表和面板中移除
                            mainFrame.remove4fromPokerLabels();
                        }

                        if(mainFrame.currentPlayer.getId() == playerid)
                        {
                            //第一家出牌，显示出牌的按钮
                            mainFrame.showChuPaiAndBuchuPaiLabel();
                        }
                    }

                    //显示红三图标
                    if (typeid == 1 || typeid == 2 || typeid == 3)
                    {
                        //将红3的id储存为一个全局变量
                        hongSanPlayerId=playerid;

                        if(mainFrame.currentPlayer.getId() == playerid)
                        {
                            //给所有玩家发送是否独打的消息
                            mainFrame.isDuDa(contentString);

                            //显示废除4与否的按钮
                            mainFrame.FeiSi();
                        }

                        if(!mainFrame.isDuda){
                            //如果当前玩家为红3搭档
                            if(mainFrame.makeSurePartnerPlayer(contentString)){
                                mainFrame.sendId(mainFrame.currentPlayer.getId());
                            }
                        }

                        //显示红3图标
                        mainFrame.showHongsanIcon(playerid);

                        //显示红3所选队友花色
                        mainFrame.showPartnerIcon(contentString);

                        //之前的消息框隐藏
                        //mainFrame.buchuIconLabel.setVisible(false);
                        //所有玩家，都可以选择出牌列表
                        mainFrame.addClickEventToPoker();

                    }

                    //接收到不出牌的消息
                    if(typeid==4)
                    {
                            //显示不出牌的消息
                            mainFrame.showBuchuIcon(playerid);

                            //判断自己是不是下家
                            if (playerid - 4 == mainFrame.currentPlayer.getId() || playerid + 1 == mainFrame.currentPlayer.getId()) {
                                //if(mainFrame.currentPlayer.getPokers()==null)

                                if(mainFrame.pokerLabels.size()==0){
                                    mainFrame.dengDaiThread();

                                }
                                else{
                                mainFrame.showChuPaiAndBuchuPaiLabel();
                                }
                            }

                    }

                    //接收到出牌的消息
                    if (typeid == 5)
                    {
                            //System.out.println(jsonString);
                            //获得出牌列表
                            JSONArray pokersJsonArray = msgJsonObject.getJSONArray("pokers");

                            List<Poker> outPokers = new ArrayList<Poker>();

                            for (int i = 0; i < pokersJsonArray.size(); i++) {
                                JSONObject pokerJsonObject = (JSONObject) pokersJsonArray.get(i);
                                int id = pokerJsonObject.getInteger("id");
                                String name = pokerJsonObject.getString("name");
                                int num = pokerJsonObject.getInteger("num");
                                boolean isOut = pokerJsonObject.getBoolean("isOut");
                                Poker p = new Poker(id, name, num, isOut);
                                System.out.println(p);
                                outPokers.add(p);

                            }

                            //显示出牌列表
                            mainFrame.showOutPokerList(playerid, outPokers);

                            //判断自己是不是下家
                            if (playerid + 1 == mainFrame.currentPlayer.getId() || playerid - 4 == mainFrame.currentPlayer.getId()) {
                                if(mainFrame.pokerLabels.size()==0){
                                    mainFrame.prevPlayerid = playerid;//记录上一家出牌的玩家id
                                    mainFrame.dengDaiThread();
                                }
                                else{
                                    mainFrame.showChuPaiAndBuchuPaiLabel();
                                }

                            }

                        mainFrame.prevPlayerid = playerid;//记录上一家出牌的玩家id


                    }

                    //接收到  所出牌为最后手牌  的消息
                    if(typeid==6){

                        if(playerid==hongSanPlayerId||playerid==partnerPlayerId){
                            hongsanPlayerCount_Over += 1;
                        }else{
                            feiHongsanPlayerCount_Over += 1;
                        }

                        mainFrame.OverPlayerid.add(playerid);

                        //System.out.println(isDuDa);
                        if(isDuDa){   //红3独打
                            if(playerid==hongSanPlayerId){  //红3玩家先行出完牌
                                if(mainFrame.currentPlayer.getId()==playerid){
                                    JOptionPane.showMessageDialog(mainFrame,"\t\t\t\t赢\n------------\n|--独打--|\n------------");
                                }else{
                                    JOptionPane.showMessageDialog(mainFrame,"\t\t\t\t输\n------------\n|--独打--|\n------------");
                                }
                            }
                            if(playerid!=hongSanPlayerId){
                                if(mainFrame.currentPlayer.getId()!=hongSanPlayerId){
                                    JOptionPane.showMessageDialog(mainFrame,"\t\t\t\t赢\n------------\n|--独打--|\n------------");
                                }else{
                                    JOptionPane.showMessageDialog(mainFrame,"\t\t\t\t输\n------------\n|--独打--|\n------------");
                                }
                            }
                        }
                        /*
                        * 2020.8.26
                        * 到今天为止，除了红3有队友的情况下：无法进行输赢的判断
                        * 其它功能都已经实现
                        *
                        * */

                        //红3有队友
                        else{
                            if(hongsanPlayerCount_Over>=2){
                                if(mainFrame.currentPlayer.getId()==hongSanPlayerId||mainFrame.currentPlayer.getId()==partnerPlayerId){
                                    JOptionPane.showMessageDialog(mainFrame,"恭喜您，携手搭档\n斩获三枚“傻蛋”");
                                }else{
                                    JOptionPane.showMessageDialog(mainFrame,"笨蛋，三人打不过俩！");
                                }
                            }

                            else if(feiHongsanPlayerCount_Over>=3){
                                if(mainFrame.currentPlayer.getId()==hongSanPlayerId||mainFrame.currentPlayer.getId()==partnerPlayerId){
                                    JOptionPane.showMessageDialog(mainFrame,"再接再厉，人多打人少，下局干他丫");
                                }else{
                                    JOptionPane.showMessageDialog(mainFrame,"-------------------\n|--人多打人少--|\n|--个个都敢搞--|\n-------------------");
                                }
                            }

                            else{
                                mainFrame.showDengDaiLabel(mainFrame.OverPlayerid);

                                JSONArray pokersJsonArray = msgJsonObject.getJSONArray("pokers");

                                List<Poker> outPokers = new ArrayList<Poker>();

                                for (int i = 0; i < pokersJsonArray.size(); i++) {
                                    JSONObject pokerJsonObject = (JSONObject) pokersJsonArray.get(i);
                                    int id = pokerJsonObject.getInteger("id");
                                    String name = pokerJsonObject.getString("name");
                                    int num = pokerJsonObject.getInteger("num");
                                    boolean isOut = pokerJsonObject.getBoolean("isOut");
                                    Poker p = new Poker(id, name, num, isOut);
                                    System.out.println(p);
                                    outPokers.add(p);

                                }

                                //显示出牌列表
                                mainFrame.showOutPokerList(playerid, outPokers);

                                //判断自己是不是下家
                                if (playerid + 1 == mainFrame.currentPlayer.getId() || playerid - 4 == mainFrame.currentPlayer.getId())
                                {
                                    if(mainFrame.pokerLabels.size()==0){
                                        mainFrame.prevPlayerid = playerid;
                                        mainFrame.dengDaiThread();
                                    }
                                    else{
                                        mainFrame.prevPlayerid = playerid;
                                        mainFrame.showChuPaiAndBuchuPaiLabel();
                                    }
                                }

                                //判断自己是不是下下家，然后将传送过来的消息存储在当前玩家下
                                if(playerid + 2 == mainFrame.currentPlayer.getId()||playerid - 3 == mainFrame.currentPlayer.getId())
                                {
                                    shangShangJiaIsOver--;
                                    System.out.println("上上家的判定是否为空的值"+shangShangJiaIsOver);
                                }

                                //判断自己是不是下下下家，然后将传送过来的消息存储在当前玩家下
                                if(playerid + 3 == mainFrame.currentPlayer.getId()||playerid - 2 == mainFrame.currentPlayer.getId())
                                {
                                    shangShangShangJiaIsOver--;
                                }
                                //记录已经出完牌的玩家id
                                //用一个集合，将已经出完牌的玩家的id依次添加进去
                                mainFrame.OverPlayerid.add(playerid);
                            }
                            }

                    }
                    //上家传过来的消息为“跳过”
                    if(typeid==7){

                        /*for (int i = 0; i < mainFrame.OverPlayerid.size(); i++) {
                            //所有玩家显示：谁已经出完牌了的标签
                            mainFrame.showDengDaiLabel((Integer) mainFrame.OverPlayerid.get(i));
                        }*/

                        mainFrame.showDengDaiLabel(mainFrame.OverPlayerid);

                        if(playerid + 3 == mainFrame.currentPlayer.getId()||playerid - 2 == mainFrame.currentPlayer.getId())
                        {
                            shangShangShangJiaIsOver--;
                        }

                        if(playerid + 2 == mainFrame.currentPlayer.getId()||playerid - 3 == mainFrame.currentPlayer.getId())
                        {
                            shangShangJiaIsOver--;
                        }


                        //判断自己是不是下家
                        if (playerid - 4 == mainFrame.currentPlayer.getId() || playerid + 1 == mainFrame.currentPlayer.getId())
                        {

                            //上家跳过，自己也已经结束出牌
                            if(mainFrame.pokerLabels.size()==0)
                            {

                                if(playerid==0) {
                                    mainFrame.prevPlayerid = playerid + 4;
                                }else{
                                    mainFrame.prevPlayerid = playerid -1;
                                }
                                mainFrame.dengDaiThread();


                            }
                            //上家跳过，自己还未出完牌
                            else if(mainFrame.pokerLabels.size()!=0){
                                    //上家结束之后，并且上家出的牌，谁都大不住，则自己借东风
                                    System.out.println(mainFrame.prevPlayerid);

                                    if (mainFrame.prevPlayerid == playerid)
                                    {
                                        mainFrame.jieDongFengLabel();
                                    }
                                    //上上家出牌
                                    else if(mainFrame.prevPlayerid==playerid-1||mainFrame.prevPlayerid==playerid+4)
                                    {

                                        System.out.println("上上家出完牌，进入时的判定值"+shangShangJiaIsOver);
                                        if(shangShangJiaIsOver==-2)
                                        {
                                            mainFrame.jieDongFengLabel();
                                        }else{
                                            mainFrame.showChuPaiAndBuchuPaiLabel();
                                        }
                                    }

                                    else if(mainFrame.prevPlayerid==playerid-2||mainFrame.prevPlayerid==playerid+3){
                                        System.out.println("上上上家出完牌，进入时的判定值"+shangShangJiaIsOver);
                                        if(shangShangShangJiaIsOver==-2){
                                            mainFrame.jieDongFengLabel();
                                        }else{
                                            mainFrame.showChuPaiAndBuchuPaiLabel();
                                        }
                                    }
                                    else{
                                        mainFrame.showChuPaiAndBuchuPaiLabel();
                                    }

                            }
                            /*else{
                                mainFrame.showChuPaiAndBuchuPaiLabel();
                            }*/
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
