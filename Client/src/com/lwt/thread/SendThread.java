package com.lwt.thread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;

//发送消息的线程
public class SendThread extends Thread {

    private String msg;

    private Socket socket;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public SendThread(Socket socket){
        this.socket = socket;
    }

    public SendThread(Socket socket,String msg){
        this.socket = socket;
        this.msg = msg;
    }
    public SendThread(){

    }


    public void run()
    {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while(true)
            {
                //如果消息不为空
                if(msg!=null)
                {
                    System.out.println("消息在发送中："+msg);
                    //发送消息
                    dataOutputStream.writeUTF(msg);
                    //发送消息完毕，消息内容清空
                    msg = null;
                }
                    Thread.sleep(50);//暂定 等待新消息进来

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
