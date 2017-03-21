package com.jinshu.xuzhi.feeling;

import android.os.Message;

/**
 * Created by xuzhi on 2017/3/14.
 */

public class MyThread implements Runnable{
    @Override
    public void run() {
// TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(500);//线程暂停0.5秒，单位毫秒
                Message message=new Message();
                message.what=1;
                FragmentFishing.handler.sendMessage(message);//发送消息
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
