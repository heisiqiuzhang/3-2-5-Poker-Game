package com.lwt.model;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id; //玩家id

    private String name;//玩家姓名

    private Socket socket;//玩家对应的socket

    private String role;//玩家的身份

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private List<Poker> pokers = new ArrayList<Poker>();//玩家的扑克列表（new防止出现空指针）

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public List<Poker> getPokers() {
        return pokers;
    }

    public void setPokers(List<Poker> pokers) {
        this.pokers = pokers;
    }

    public Player(){}

    public Player(int id,String name,Socket socket,List<Poker> pokers)
    {
        this.id = id ;
        this.name = name;
        this.socket = socket;
        this.pokers = pokers;

    }

    public Player(int id)
    {
        this.id = id;
    }

    public Player(int id,String name)
    {
        this.id = id;
        this.name= name;
    }
}
