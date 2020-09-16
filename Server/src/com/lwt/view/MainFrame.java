package com.lwt.view;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwt.model.Player;
import com.lwt.model.Poker;
import com.sun.source.tree.Scope;
import jdk.jshell.execution.LoaderDelegate;
import jdk.nashorn.api.tree.ForInLoopTree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainFrame {

    //创建玩家列表
    public List<Player> players = new ArrayList<Player>();

    public int index=0;
    //存放扑克列表
    public List<Poker> allpokers= new ArrayList<Poker>();
    //存放剩余的4张牌
    public List<Poker> lordpokers = new ArrayList<Poker>();

    public int step = 0;//牌局的进展步骤

    public MainFrame() {
        //玩家到齐之前，创建扑克列表
        createPokers();


        try {
            //1.创建服务器端socket
            ServerSocket serverSocket = new ServerSocket(8888);

            while(true){
                //2.接收客户端的socket
                Socket socket = serverSocket.accept();
                //3.开启线程 处理客户端的socket
                AcceptThread acceptThread = new AcceptThread(socket);
                acceptThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //创建一个接收线程，处理客户端的信息
    class AcceptThread extends Thread
    {
        Socket socket;

        public AcceptThread(Socket socket)
        {
            this.socket=socket;
        }

        public void run()
        {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                while(true)
                {
                    String msg = dataInputStream.readUTF();

                    //System.out.println(msg);

                    if(step==0) {
                        //创建player对象
                        Player player = new Player(index++, msg);
                        player.setSocket(socket);
                        //存入玩家列表
                        players.add(player);


                        System.out.println(msg + "上线了");
                        System.out.println("当前上线人数" + players.size());

                        //玩家人数到齐，发给五个玩家
                        if (players.size() == 5)
                        {
                            faPai();
                            step = 1;
                        }
                    }
                    else if(step==1)//接收选队友的信息
                    {
                        //System.out.println("接收选队友的消息");

                        JSONObject msgJsonObeject = JSON.parseObject(msg);

                        int typeid = msgJsonObeject.getInteger("typeid");

                        int playerid = msgJsonObeject.getInteger("playerid");

                        String content = msgJsonObeject.getString("content");


                        //将客户端发送过来的红桃3选择的队友的信息  群发给所有的玩家
                        sendMessageToClient(msg);

                        step=2;

                    }

                    //出牌和不出牌
                    else if(step==2)
                    {
                        /*JSONObject msgJsonObeject = JSON.parseObject(msg);

                        int typeid = msgJsonObeject.getInteger("typeid");

                        int playerid = msgJsonObeject.getInteger("playerid");

                        String content = msgJsonObeject.getString("content");*/

                        sendMessageToClient(msg);//转发给所有客户端

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //群发消息
    public void sendMessageToClient(String msg)
    {
        for (int i = 0; i < players.size(); i++)
        {
            DataOutputStream dataOutputStream = null;
            try {
                dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());
                dataOutputStream.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    //创建所有的扑克列表
    public void createPokers()
    {
        //创建大王，小王
        Poker dawang = new Poker(0,"大王",17);
        Poker xiaowang = new Poker(1,"小王",16);

        //添加到扑克列表中
        allpokers.add(dawang);
        allpokers.add(xiaowang);

        //创建其他扑克
        String[] names =new String[]{"3","2","A","K","Q","J","10","9","8","7","6","5","4"};
        String[] colors = new String[]{"黑桃","红桃","梅花","方块"};

        int id= 2;
        int num =15;
        //遍历扑克的种类
        for(String name:names)
        {
            //遍历每个种类的花色
            for(String color:colors)
            {
                Poker poker = new Poker(id++,color+name,num);

                allpokers.add(poker);
            }
            num--;
        }

        //洗牌
        Collections.shuffle(allpokers);
    }


    //发牌
    public void faPai()
    {
        //发给五个玩家
        for (int i = 0; i < allpokers.size(); i++) {

            if(i >= 50)
            {
                lordpokers.add(allpokers.get(i));
            }
            else{
                if(i%5==0)
                    players.get(0).getPokers().add(allpokers.get(i));
                else if(i%5==1)
                    players.get(1).getPokers().add(allpokers.get(i));
                else if(i%5==2)
                    players.get(2).getPokers().add(allpokers.get(i));
                else if(i%5==3)
                    players.get(3).getPokers().add(allpokers.get(i));
                else
                    players.get(4).getPokers().add(allpokers.get(i));

            }

        }

        for (int i = 0; i < lordpokers.size(); i++) {
            if(i%5==0)
                players.get(0).getPokers().add(lordpokers.get(i));
            else if(i%5==1)
                players.get(1).getPokers().add(lordpokers.get(i));
            else if(i%5==2)
                players.get(2).getPokers().add(lordpokers.get(i));
            else
                players.get(3).getPokers().add(lordpokers.get(i));
        }

        /*
         * 如果用对象流发送信息，需要将poker，player，socket等都进行序列化，比较麻烦
         * 用json字符串比较方便
         * {"id":1,"name":"aa","socket":"","pokers":[{"id":1,"name":"黑桃","num":13},{},{}]}
         * */
        //将玩家的信息发送到客户端


        for(int i=0; i < players.size();i++)
        {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(players.get(i).getSocket().getOutputStream());

                String jsonString = JSON.toJSONString(players);

                dataOutputStream.writeUTF(jsonString);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }






}
