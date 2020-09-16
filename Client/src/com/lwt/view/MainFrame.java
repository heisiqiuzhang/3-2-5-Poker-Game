package com.lwt.view;

import com.lwt.model.Player;
import com.lwt.model.Poker;
import com.lwt.model.PokerLabel;
import com.lwt.thread.*;
import com.lwt.util.GameUtil;
import com.lwt.util.PokerRule;
import com.lwt.util.PokerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.management.BufferPoolMXBean;
import java.net.Socket;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    public MyPanel myPanel;

    public String uname;

    public Socket socket;

    public SendThread sendThread;//发送消息的线程

    public ReceiveThread receiveThread;//接收消息的线程

    public SendIdOfPartnerThread sendIdThread;//发送红3及队友的id的线程

    public Player currentPlayer;//存放当前玩家对象

    public Player partnerPlayer;//存放红3玩家的队友对象

    public int prevPlayerid = -1;//上一个出牌玩家的id

    public int prevPlayerPokers=11;//上一个出牌玩家的扑克数量

    public List<PokerLabel> pokerLabels = new ArrayList<PokerLabel>();//存放扑克标签列表

    public JLabel heiTaoLabel; //队友选择黑桃标签

    public JLabel meiHuaLabel;//队友选择梅花标签

    public JLabel fangKuaiLabel;//队友选择方块标签

    public JLabel feisiLabel;//红3废除4按钮

    public JLabel nofeisiLabel;//红3不废除4按钮

    public JLabel timeLabel;//计时器

    public CountThread countThread;//计数器的线程

    public FeisiThread feisiThread;//废4的线程

    public DudaThread dudaThread;//独打与否的线程

    public JieShuThread jieShuThread;//结束出牌的线程

    public boolean isHeitao = false;//选择的队友是否是黑桃

    public boolean isMeihua = false;//选择的队友是否是梅花

    public boolean isFangkuai = false;//选择的队友是否是方块

    public JLabel hongsanIconLabel;//红三图标

    public JLabel partnerIconLabel_fangkuai;//红3队友花色图标 之方块

    public JLabel partnerIconLabel_heitao;//红3队友花色图标 之黑桃

    public JLabel partnerIconLabel_meihua;//红3队友花色图标 之梅花

    public JLabel dengdaiLabel;//等待标签

    public JLabel buchuIconLabel;//各个玩家界面显示的不出标签

    public JLabel chupaiJLabel;//出牌标签

    public JLabel buchuJLabel;//不出牌标签

    public JLabel jiedongfengJLabel;//借东风标签

    public ChuPaiThread chuPaiThread;//出牌定时器线程

    public List<PokerLabel> selectPokerLabels= new ArrayList<PokerLabel>();//存放选中的扑克列表

    public List<PokerLabel> pokerOf4Labels=new ArrayList<PokerLabel>();//存放4的扑克列表

    public boolean isFeisi;//是否废除4

    public boolean isDuda;

    public boolean isOut;//选择的是出牌还是不出牌

    public boolean isLastOut;//所出的牌，是否为最后的牌

    public List OverPlayerid = new ArrayList();//已经出完牌的玩家id的集合

    public List<PokerLabel> showOutPokerLabels = new ArrayList<PokerLabel>();//存放当前出牌的列表

    public MainFrame(String uname, Socket socket){
        this.uname = uname;
        this.socket = socket;


        //设置窗口的属性
        this.setSize(1280,720); //窗口大小
        this.setVisible(true); //窗口可见
        this.setLocationRelativeTo(null);//居于屏幕中间
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//窗口的关闭操作

        //添加mypanel
        myPanel = new MyPanel();
        myPanel.setBounds(0,0,1280,720);
        this.add(myPanel);

        //初始化窗口信息
        init();

        //启动发消息的线程
        sendThread=new SendThread(socket,uname);
        sendThread.start();

        //启动接收消息的线程
        receiveThread = new ReceiveThread(socket,this);
        receiveThread.start();


    }

    public void init()
    {

        //创建不出的消息框
        buchuIconLabel = new JLabel();

        //创建等待的消息框
        dengdaiLabel = new JLabel();

        chupaiJLabel = new JLabel();
        chupaiJLabel.setBounds(490,340,110,53);
        chupaiJLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\chupai.png"));
        chupaiJLabel.addMouseListener(new ChupaiAndBuchuEvent());//将出牌标签保定到鼠标监听器上
        chupaiJLabel.setVisible(false);
        this.myPanel.add(chupaiJLabel);

        buchuJLabel = new JLabel();
        buchuJLabel.setBounds(640,340,110,53);
        buchuJLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\buchupai.png"));
        buchuJLabel.addMouseListener(new ChupaiAndBuchuEvent());//将不出牌标签保定到鼠标监听器上
        buchuJLabel.setVisible(false);
        this.myPanel.add(buchuJLabel);


        timeLabel = new JLabel();
        timeLabel.setBounds(780 ,330,60,60);
        timeLabel.setFont(new Font("Dialog",0,50));   //0不加粗  1加粗
        timeLabel.setForeground(Color.RED);  //设置字体背景色
        timeLabel.setVisible(false);
        this.myPanel.add(timeLabel);

        jiedongfengJLabel = new JLabel();
        jiedongfengJLabel.setBounds(590,340,110,48);
        jiedongfengJLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\jiedongfeng.jpg"));
        jiedongfengJLabel.addMouseListener(new ChupaiAndBuchuEvent());//将出牌标签保定到鼠标监听器上
        jiedongfengJLabel.setVisible(false);
        this.myPanel.add(jiedongfengJLabel);

    }

    public void showAllPlayersInfo(List<Player> players) {

        //1.显示5个玩家的名称

        //2.显示当前玩家的扑克列表
        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).getName().equals(uname)) {
                currentPlayer = players.get(i);
            }
        }

        List<Poker> pokers = currentPlayer.getPokers();

        //将不可显示的poker类，转换为可以将扑克显示出来pokerlabel类
        for (int i = 0; i < pokers.size(); i++) {
            //创建扑克标签
            Poker poker = pokers.get(i);

            PokerLabel pokerLabel = new PokerLabel(poker.getId(), poker.getName(), poker.getNum(),poker.getIsOut());

            pokerLabel.turnUp();//显示正面图
            //添加到面板中
            this.myPanel.add(pokerLabel);

            this.pokerLabels.add(pokerLabel);

            //动态显示出来
            this.myPanel.setComponentZOrder(pokerLabel, 0);
            //一张一张显示出来
            GameUtil.move(pokerLabel, 420 + 30 * i, 450);

        }


        //对扑克列表排序
        Collections.sort(pokerLabels);
        Collections.reverse(pokerLabels);

        //重新移动位置
        for (int i = 0; i < pokerLabels.size(); i++) {
            this.myPanel.setComponentZOrder(pokerLabels.get(i), 0);
            GameUtil.move(pokerLabels.get(i), 420 + 30 * i, 450);
        }
        //System.out.println(currentPlayer);


        //谁拥有红桃3，谁就进行队友的选择
        for (int i = 0; i < pokerLabels.size(); i++)
        {
            if(currentPlayer.getPokers().get(i).getName().equals("红桃3"))
            {
                currentPlayer.setRole("team1");
                System.out.println(currentPlayer.getRole());
                getPartner();   //选队友
            }
        }



    }


    private void getPartner() {

        //显示可选花色和定时器按钮
        heiTaoLabel = new JLabel();
        heiTaoLabel.setBounds(490,380,50,50);
        heiTaoLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\heitao.jpg"));
        heiTaoLabel.addMouseListener(new MyMouseEvent());//将黑桃标签保定到鼠标监听器上
        this.myPanel.add(heiTaoLabel);

        meiHuaLabel = new JLabel();
        meiHuaLabel.setBounds(590,380,50,50);
        meiHuaLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\meihua.jpg"));
        meiHuaLabel.addMouseListener(new MyMouseEvent());//将梅花标签保定到鼠标监听器上
        this.myPanel.add(meiHuaLabel);

        fangKuaiLabel = new JLabel();
        fangKuaiLabel.setBounds(690,380,50,50);
        fangKuaiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\fangkuai.jpg"));
        fangKuaiLabel.addMouseListener(new MyMouseEvent());//将方块标签保定到鼠标监听器上
        this.myPanel.add(fangKuaiLabel);

       //显示定时器的图标
        this.timeLabel.setVisible(true);

        //重绘
        this.repaint();

        //启动计时器的线程
        countThread = new CountThread(10,this);
        countThread.start();

    }

    //废除4
    public void FeiSi() {
        //显示废除4的按钮
        feisiLabel = new JLabel();
        feisiLabel.setBounds(520,330,100,100);
        feisiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\fei4.jpg"));
        feisiLabel.addMouseListener(new MyMouseEvent_Feisi());//将废除4标签保定到鼠标监听器上
        this.myPanel.add(feisiLabel);

        //显示不废除4的按钮
        nofeisiLabel = new JLabel();
        nofeisiLabel.setBounds(640,330,100,100);
        nofeisiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\bufei4.jpg"));
        nofeisiLabel.addMouseListener(new MyMouseEvent_Feisi());//将不废除4标签保定到鼠标监听器上
        this.myPanel.add(nofeisiLabel);

        //显示定时器的图标
        this.timeLabel.setVisible(true);

        //重绘
        this.repaint();

        //启动计时器的线程
        feisiThread = new FeisiThread(8,this);
        feisiThread.start();
    }



    //桌面正中央显示红3队友花色
    public void showPartnerIcon(String contentString) {

        //创建红3队友花色图标

        partnerIconLabel_fangkuai= new JLabel();
        partnerIconLabel_fangkuai.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\fangkuai_partner.jpg"));
        partnerIconLabel_fangkuai.setSize(70,70);

        partnerIconLabel_meihua= new JLabel();
        partnerIconLabel_meihua.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\meihua_partner.jpg"));
        partnerIconLabel_meihua.setSize(70,70);

        partnerIconLabel_heitao= new JLabel();
        partnerIconLabel_heitao.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\heitao_partner.jpg"));
        partnerIconLabel_heitao.setSize(70,70);


        if("方块".equals(contentString)){
            partnerIconLabel_fangkuai.setLocation(590,280);
            //添加图标到面板上
            this.myPanel.add(partnerIconLabel_fangkuai);
        }else if ("梅花".equals(contentString)){
            partnerIconLabel_meihua.setLocation(590,280);
            //添加图标到面板上
            this.myPanel.add(partnerIconLabel_meihua);
        }else {
            partnerIconLabel_heitao.setLocation(590,280);
            //添加图标到面板上
            this.myPanel.add(partnerIconLabel_heitao);
        }

        //重绘
        this.repaint();
    }

    //在界面左上角显示废4与否的结果
    public void showFeisiResultIcon(String contentString) {

        //显示废除4的按钮
        feisiLabel = new JLabel();
        feisiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\fei4.jpg"));
        feisiLabel.setSize(100,100);

        //显示不废除4的按钮
        nofeisiLabel = new JLabel();
        nofeisiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\bufei4.jpg"));
        nofeisiLabel.setSize(100,100);

        if("废除4".equals(contentString)){
            feisiLabel.setLocation(0,0);
            //添加图标到面板上
            this.myPanel.add(feisiLabel);
        }else if("不废除4".equals(contentString)) {
            nofeisiLabel.setLocation(0, 0);
            //添加图标到面板上
            this.myPanel.add(nofeisiLabel);
        }
        //重绘
        this.repaint();
    }


    //每个玩家界面正确显示谁是红三的图标
    public void showHongsanIcon(int playerid)
    {
        //创建红三图标
        hongsanIconLabel = new JLabel();
        hongsanIconLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\hongtao.jpg"));
        hongsanIconLabel.setSize(50,50);


        //根据玩家id显示到具体位置
        if(playerid == currentPlayer.getId())//当前玩家
        {
            hongsanIconLabel.setLocation(330,460);
        }
        if(playerid -4==currentPlayer.getId() || playerid +1 ==currentPlayer.getId())//上家玩家
        {
            hongsanIconLabel.setLocation(110,320);
        }
        if(playerid -3==currentPlayer.getId() || playerid + 2==currentPlayer.getId())//上上家玩家
        {
            hongsanIconLabel.setLocation(350,40);
        }
        if(playerid - 2==currentPlayer.getId() || playerid +3 ==currentPlayer.getId())//下下家玩家
        {
            hongsanIconLabel.setLocation(900,40);
        }if(playerid - 1==currentPlayer.getId() || playerid +4==currentPlayer.getId())//下家
        {
                hongsanIconLabel.setLocation(1080,320);
        }

        //添加红三图标到面板上
        this.myPanel.add(hongsanIconLabel);

        //重绘
        this.repaint();

    }

    //给扑克牌添加单击事件
    public void addClickEventToPoker() {
        for (int i = 0; i < pokerLabels.size(); i++) {
            pokerLabels.get(i).addMouseListener(new PokerEvent());
        }
    }

    //显示出牌或不出牌的标签
    public void showChuPaiAndBuchuPaiLabel() {

        if(prevPlayerid==currentPlayer.getId())
        {
            //从窗口上移除之前的出牌列表
            for (int i = 0; i < showOutPokerLabels.size(); i++)
            {
                myPanel.remove(showOutPokerLabels.get(i));

            }
            //清空之前出牌的列表
            showOutPokerLabels.clear();

        }

        //显示出牌和不出牌 以及定时器的按钮
        chupaiJLabel.setVisible(true);
        buchuJLabel.setVisible(true);
        timeLabel.setVisible(true);

        //重绘
        this.repaint();

        //启动计时器的线程
       chuPaiThread = new ChuPaiThread(30,this);
       chuPaiThread.start();

    }

    //已结束玩家等待标签
    public void dengDaiThread(){

        if(prevPlayerid==currentPlayer.getId())
        {
            //从窗口上移除之前的出牌列表
            for (int i = 0; i < showOutPokerLabels.size(); i++)
            {
                myPanel.remove(showOutPokerLabels.get(i));

            }
            //清空之前出牌的列表
            showOutPokerLabels.clear();

        }

        dengdaiLabel.setVisible(true);


        //重绘
        this.repaint();

        //启动计时器的线程
        jieShuThread = new JieShuThread(this);
        jieShuThread.start();


    }

    //在各个玩家界面正确显示不出牌的不出标签
    public void showBuchuIcon(int playerid) {

        buchuIconLabel.setVisible(true);
        buchuIconLabel.setBounds(500,300,93,52);
        buchuIconLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\buchu.png"));
        //buchuIconLabel.setSize(93,52);


        //根据玩家id显示到具体位置
        if(playerid == currentPlayer.getId())//当前玩家
        {
            buchuIconLabel.setLocation(590,340);
        }
        if(playerid - 4==currentPlayer.getId() || playerid + 1==currentPlayer.getId())//上家玩家
        {
            buchuIconLabel.setLocation(210,320);
        }
        if(playerid - 3==currentPlayer.getId() || playerid + 2 ==currentPlayer.getId())//上上家玩家
        {
            buchuIconLabel.setLocation(350,140);
        }
        if(playerid - 2==currentPlayer.getId() || playerid + 3 ==currentPlayer.getId())//下下家玩家
        {
            buchuIconLabel.setLocation(900,140);
        }if(playerid - 1==currentPlayer.getId() || playerid + 4==currentPlayer.getId())//下家
        {
            buchuIconLabel.setLocation(980,320);
        }

        //添加红三图标到面板上
        this.myPanel.add(buchuIconLabel);

        //重绘
        this.repaint();

    }

    //显示出牌列表
    public void showOutPokerList(int playerid, List<Poker> outPokers) {

        //从窗口上移除之前的出牌的列表
        for(int i=0;i<showOutPokerLabels.size();i++)
        {
            myPanel.remove(showOutPokerLabels.get(i));
        }

        //清空之前出牌的列表
        showOutPokerLabels.clear();

        //这一步可能不需要
        //buchuIconLabel.setVisible(false);//之前有不出的消息，应该进行隐藏

        //显示当前出牌的列表
        for (int i = 0; i < outPokers.size(); i++)
        {
            Poker poker = outPokers.get(i);

            PokerLabel pokerLabel = new PokerLabel(poker.getId(),poker.getName(),poker.getNum(),poker.getIsOut());

            if(playerid == currentPlayer.getId())
            {
                pokerLabel.setLocation(420+30*i,250);
            }
            if(playerid - 4==currentPlayer.getId() || playerid + 1 ==currentPlayer.getId())
            {
                pokerLabel.setLocation(210+30*i,270);
            }
            if(playerid - 3==currentPlayer.getId() || playerid +2==currentPlayer.getId())
            {
                pokerLabel.setLocation(300+30*i,130);
            }
            if(playerid -2==currentPlayer.getId() || playerid +3==currentPlayer.getId())
            {
                pokerLabel.setLocation(800+30*i,130);
            }
            if(playerid -1==currentPlayer.getId() || playerid + 4 ==currentPlayer.getId())
            {
                pokerLabel.setLocation(900+30*i,270);
            }

            pokerLabel.turnUp();

            if(pokerLabel.getId()==2 || pokerLabel.getId()==4 || pokerLabel.getId()==5){
                pokerLabel.turnDown();
            }

            myPanel.add(pokerLabel);

            showOutPokerLabels.add(pokerLabel);

            myPanel.setComponentZOrder(pokerLabel,0);
        }
        this.repaint() ; //窗口重绘

    }

    //出牌后  将出的牌从当前玩家的扑克列表中移除
    public void removeOutPokerFromPokerList() {

        //从当前玩家的扑克列表中移除
        pokerLabels.removeAll(selectPokerLabels);
        //从面板中移除
        for (int i = 0; i < selectPokerLabels.size(); i++) {
            myPanel.remove(selectPokerLabels.get(i));
        }
        //剩下的扑克列表重新定位
        for (int i = 0; i < pokerLabels.size(); i++) {
            myPanel.setComponentZOrder(pokerLabels.get(i),0);
            GameUtil.move(pokerLabels.get(i),420+30*i, 450);
        }

        //清空选择的扑克牌列表
        selectPokerLabels.clear();

        this.repaint();
    }

    public void showDengDaiLabel(List playerids) {

        dengdaiLabel = new JLabel();
        dengdaiLabel.setVisible(true);
        dengdaiLabel.setBounds(500,300,122,50);
        dengdaiLabel.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\bg\\dengdai.jpg"));



        for(Object s:playerids) {
            //根据玩家id显示到具体位置
            if (s.equals(currentPlayer.getId()))//当前玩家
            {
                dengdaiLabel.setLocation(530, 410);
            }
            if (s.equals(currentPlayer.getId()+4) || s.equals(currentPlayer.getId()-1))//上家玩家
            {
                dengdaiLabel.setLocation(110, 320);
            }
            if (s.equals(currentPlayer.getId()+3) || s.equals(currentPlayer.getId()-2))//上上家玩家
            {
                dengdaiLabel.setLocation(350, 140);
            }
            if(s.equals(currentPlayer.getId()+2) || s.equals(currentPlayer.getId()-3))//下下家玩家
            {
                dengdaiLabel.setLocation(900, 140);
            }
            if (s.equals(currentPlayer.getId()+1) || s.equals(currentPlayer.getId()-4))//下家
            {
                dengdaiLabel.setLocation(980, 320);
            }

            //添加等待图标到面板上
            this.myPanel.add(dengdaiLabel);
        }


        //重绘
        //this.repaint();
    }

    //在上一家已经出完牌（轮空）时，上家会一直传过来“跳过”的信息，
    //那么，在上上一家出完牌之后的轮次，所有玩家都不出，就轮到当前玩家借东风
    //当前玩家需要有一个触发借东风的条件判断
    //此函数功能：
    //上家跳过，上上家也为跳过
    public boolean shangShangIsNull(int prevPlayerid) {
        return false;
    }


    public void jieDongFengLabel() {

        //显示借东风 以及定时器的按钮
        jiedongfengJLabel.setVisible(true);
        timeLabel.setVisible(true);

        //重绘
        this.repaint();

        //启动计时器的线程
        chuPaiThread = new ChuPaiThread(30,this);
        chuPaiThread.start();

    }

    //将手中的4从扑克列表移除
    public void remove4fromPokerLabels() {

        for (int i = pokerLabels.size()-1; i > pokerLabels.size()-5 ; i--) {
            if (pokerLabels.get(i).getNum()==3) {
                pokerOf4Labels.add(pokerLabels.get(i));
            }
        }
        //System.out.println(pokerOf4Labels);
        //从当前玩家的扑克列表中移除
        pokerLabels.removeAll(pokerOf4Labels);

        //从面板中移除
        for (int i = 0; i < pokerOf4Labels.size(); i++) {
            myPanel.remove(pokerOf4Labels.get(i));
        }

        //剩下的扑克列表重新定位
        for (int i = 0; i < pokerLabels.size(); i++) {
            myPanel.setComponentZOrder(pokerLabels.get(i),0);
            GameUtil.move(pokerLabels.get(i),420+30*i, 450);
        }

        this.repaint();
    }

    public void isDuDa(String contentString) {
        boolean ishongsan=false;
        for (int i = 0; i < pokerLabels.size(); i++)
        {
            if(currentPlayer.getPokers().get(i).getName().equals("红桃3"))
            {
                ishongsan=true;
            }
        }
        if(ishongsan){
            for (int j = 0; j < pokerLabels.size(); j++) {
                if(currentPlayer.getPokers().get(j).getName().equals(contentString+"3")){
                   isDuda = true;
                   break;
                }else{
                    isDuda=false;
                }
            }
        }

        //启动计时器的线程
        dudaThread = new DudaThread(this,isDuda);
        dudaThread.start();

    }

    public void sendId(int partnerPlayerId) {

        sendIdThread = new SendIdOfPartnerThread(this,partnerPlayerId);
        sendIdThread.start();
    }

    public boolean makeSurePartnerPlayer(String contentString) {
        for (int i = 0; i <pokerLabels.size(); i++) {
            if(currentPlayer.getPokers().get(i).getName().equals(contentString+"3")){
                return true;
            }
        }
        return false;
    }


    //创建鼠标事件监听器（花色选择）
    class MyMouseEvent implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) {


            //如果点击的是黑桃
            if(e.getSource().equals(heiTaoLabel))
            {
                //停止计时器
                countThread.setRun(false);

                isHeitao=true;
                //设置花色选择器不可见
                heiTaoLabel.setVisible(false);
                meiHuaLabel.setVisible(false);
                fangKuaiLabel.setVisible(false);
                timeLabel.setVisible(false);
            }

            //如果点击的是梅花
            else if(e.getSource().equals(meiHuaLabel))
            {
                //停止计时器
                countThread.setRun(false);

                isMeihua = true;
                //设置花色选择器不可见
                heiTaoLabel.setVisible(false);
                meiHuaLabel.setVisible(false);
                fangKuaiLabel.setVisible(false);
                timeLabel.setVisible(false);

            }

            //如果点击的是方块
            else if(e.getSource().equals(fangKuaiLabel))
            {
                //停止计时器
                countThread.setRun(false);

                isFangkuai=true;
                //设置花色选择器不可见
                heiTaoLabel.setVisible(false);
                meiHuaLabel.setVisible(false);
                fangKuaiLabel.setVisible(false);
                timeLabel.setVisible(false);
            }else{

                isFangkuai=true;
                //设置花色选择器不可见
                heiTaoLabel.setVisible(false);
                meiHuaLabel.setVisible(false);
                fangKuaiLabel.setVisible(false);
                timeLabel.setVisible(false);
            }


        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public boolean isHave4(){
        for (int i = pokerLabels.size()-1; i > pokerLabels.size()-5 ; i--) {
            if (pokerLabels.get(i).getNum()==3) {
               return true;
            }
        }
        return false;
    }

    //创建废除4的鼠标事件监听器
    class MyMouseEvent_Feisi implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource().equals(feisiLabel)){
                //如果红3有任意一张4
                if(isHave4()) {
                    isFeisi = true;

                    feisiThread.setRun(false);
                    feisiLabel.setVisible(false);
                    nofeisiLabel.setVisible(false);
                    timeLabel.setVisible(false);
                }else{
                    JOptionPane.showMessageDialog(null,"对不起你没有扑克牌“4”，\n！！没有权限废除4！！\n***请点击“不废”按钮***");
                }
            }
            if(e.getSource().equals(nofeisiLabel)){
                isFeisi = false;

                feisiThread.setRun(false);
                feisiLabel.setVisible(false);
                nofeisiLabel.setVisible(false);
                timeLabel.setVisible(false);

            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }


    }

    //创建出牌不出牌的事件监听器类
    class ChupaiAndBuchuEvent implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            //如果点击的是出牌
            if(e.getSource().equals(chupaiJLabel)){

                PokerType pokerType = PokerRule.checkPokerType(selectPokerLabels);

                //判断是否符合牌型
                if(!pokerType.equals(pokerType.l_error)) {
                    //符合牌型，判断是不是比上家大或者上家就是自己
                    if (prevPlayerid == -1 || prevPlayerid == currentPlayer.getId() || PokerRule.isBigger(showOutPokerLabels, selectPokerLabels)) {

                        if (selectPokerLabels.size()-pokerLabels.size()==0) {

                            isLastOut = true;

                            //设置已出的牌的属性isOut为true
                            for (int i = 0; i < selectPokerLabels.size(); i++) {
                                selectPokerLabels.get(i).setOut(true);
                            }
                            //计时器停止
                            chuPaiThread.setRun(false);
                            chupaiJLabel.setVisible(false);
                            buchuJLabel.setVisible(false);
                            timeLabel.setVisible(false);

                        } else{
                            isOut = true;
                        //设置已出的牌的属性isOut为true
                        for (int i = 0; i < selectPokerLabels.size(); i++) {
                            selectPokerLabels.get(i).setOut(true);
                        }
                        //计时器停止
                        chuPaiThread.setRun(false);
                        chupaiJLabel.setVisible(false);
                        buchuJLabel.setVisible(false);
                        timeLabel.setVisible(false);
                    }
                }else {
                        JOptionPane.showMessageDialog(null,"请按规则出牌");
                    }
                }else{
                    JOptionPane.showMessageDialog(null,"不符合牌型");
                }

            }

            //如果点击的是不出牌
            if(e.getSource().equals(buchuJLabel)){
                isOut = false;
                //计时器停止
                chuPaiThread.setRun(false);
                chupaiJLabel.setVisible(false);
                buchuJLabel.setVisible(false);
                timeLabel.setVisible(false);
            }
            //点击的是借东风按钮
            if(e.getSource().equals(jiedongfengJLabel)){

                PokerType pokerType = PokerRule.checkPokerType(selectPokerLabels);

                //判断是否符合牌型
                if(!pokerType.equals(pokerType.l_error)) {
                    if (selectPokerLabels.size()-pokerLabels.size()==0) {

                        isLastOut = true;

                        //设置已出的牌的属性isOut为true
                        for (int i = 0; i < selectPokerLabels.size(); i++) {
                            selectPokerLabels.get(i).setOut(true);
                        }
                        //计时器停止
                        chuPaiThread.setRun(false);
                        jiedongfengJLabel.setVisible(false);
                        timeLabel.setVisible(false);

                    } else{
                        isOut = true;
                        //设置已出的牌的属性isOut为true
                        for (int i = 0; i < selectPokerLabels.size(); i++) {
                            selectPokerLabels.get(i).setOut(true);
                        }
                        //计时器停止
                        chuPaiThread.setRun(false);
                        jiedongfengJLabel.setVisible(false);
                        timeLabel.setVisible(false);
                    }

                }else{
                    JOptionPane.showMessageDialog(null,"不符合牌型");
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //创建扑克牌的事件监听器类
    class PokerEvent implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent e) {

            PokerLabel pokerLabel = (PokerLabel)e.getSource();
            //如果之前选择过了，则取消选择（设置选中属性为false，位置回到原位，从选择的扑克牌列表中移除）
            if(pokerLabel.isSelected())
            {
                pokerLabel.setSelected(false);
                pokerLabel.setLocation(pokerLabel.getX(),pokerLabel.getY()+30);
                selectPokerLabels.remove(pokerLabel);
            }
            //如果之前没有选择，则选中（设置选中属性为true，位置往上移动一点，添加到选择的扑克牌列表中）
            else{
                pokerLabel.setSelected(true);
                pokerLabel.setLocation(pokerLabel.getX(),pokerLabel.getY()-30);
                selectPokerLabels.add(pokerLabel);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
