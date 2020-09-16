package com.lwt.model;

import javax.swing.*;



//扑克标签类
public class PokerLabel extends JLabel implements Comparable{

    private int id;

    private String name;

    private int num;

    private boolean isOut;

    private boolean isUp;

    private boolean isSelected;//是否选中

    public PokerLabel(int id, String name, int num, boolean isOut) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.isOut=isOut;
        this.setSize(105,150);
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean isUp) {
        this.isUp = isUp;
    }

    public PokerLabel() {
        this.setSize(105,150);
    }

    public PokerLabel(int id, String name, int num){
        this.id = id;
        this.name = name;
        this.num = num;
        this.setSize(105,150);

    }



    public PokerLabel(int id, String name, int num,boolean isOut, boolean isUp) {

        this.id = id;
        this.name = name;
        this.num = num;
        this.isOut = isOut;
        this.isUp = isUp;

        if(isUp)
            turnUp();
        else {
            turnDown();
        }

        this.setSize(105,150);
    }

    public void turnUp(){
        this.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\poker\\" + id + ".jpg"));
    }

    public void turnDown(){
        this.setIcon(new ImageIcon("E:\\SanDaErKaiFa\\Client\\images\\poker\\down.jpg"));
    }

    @Override
    public int compareTo(Object o) {

        PokerLabel pokerLabel = (PokerLabel)o;

        if(this.num>pokerLabel.num)
            return 1;
        else if(this.num<pokerLabel.num)
            return -1;
        else
            return 0;

    }
}
