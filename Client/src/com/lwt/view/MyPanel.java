package com.lwt.view;

import javax.swing.*;
import java.awt.*;

public class MyPanel extends JPanel {

    public MyPanel(){
        this.setLayout(null);//如果需要用到setlocation() setBounds就需要设置布局为null
    }

    @Override
    protected void paintComponent(Graphics g) {
        Image image = new ImageIcon("Client/images/bg/bg2.jpg").getImage();

        g.drawImage(image,0, 0, this.getWidth(),this.getHeight(),null);
    }
}
