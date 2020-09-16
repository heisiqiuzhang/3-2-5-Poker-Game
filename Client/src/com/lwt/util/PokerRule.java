package com.lwt.util;

import com.lwt.model.Poker;
import com.lwt.model.PokerLabel;
import jdk.dynalink.CallSiteDescriptor;

import javax.swing.plaf.BorderUIResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokerRule {
    //判断牌型
    public static PokerType checkPokerType(List<PokerLabel> list){

        Collections.sort(list);
        int count=list.size();

        //一张牌
        if(count==1)
        {
            //单张
            return PokerType.l_1;
        }

        //两张牌
        else if(count==2)
        {
            //对子
            if(isSame(list, count)){
                return PokerType.l_2;
            }
            //王炸
            if(isWangZha(list)){
                return PokerType.l_2w;
            }
            //铲子
            if(isXiaoChanzi(list)){
                return PokerType.l_2c;
            }
            return PokerType.l_error;
        }

        //三张牌
        else if(count==3)
        {
            //炸
            if(isSame(list, count)){
                return PokerType.l_3;
            }//顺子
            else if(isShunZi(list)){
                return PokerType.l_n;
            }
            else if(isDaChanzi(list)){
                return PokerType.l_3c;
            }
            return PokerType.l_error;
        }

        //四张牌
        else if(count==4)
        {
            //蒙
            if(isSame(list, count)){
                return PokerType.l_4;
            }
            //连对
            else if(isLianDui(list)){
                return PokerType.l_1122;
            }
            else if(isShunZi(list)) {
                return PokerType.l_n;
            }
            return PokerType.l_error;

        }

        //五张牌以上
        else if(count>=5)
        {
            //顺子
            if(isShunZi(list)) {
                return PokerType.l_n;
            }
            //连对
            if(isLianDui(list)){
                return PokerType.l_1122;
            }
        }

        return PokerType.l_error;
    }




    public static boolean isWangZha(List<PokerLabel> list){
        if((list.get(0).getNum()==16&&list.get(1).getNum()==17)||(list.get(0).getNum()==17&&list.get(1).getNum()==16)){
            return true;
        }
        return false;
    }


/**
 * 判断list内扑克是否相同
 * @param list
 * @param
 * @return
 */

    public static boolean isSame(List<PokerLabel> list,int count){
        for(int j=0;j<count-1;j++){
            int a=list.get(j).getNum();
            int b=list.get(j+1).getNum();
            if(a!=b){
                return false;
            }
        }
        return true;
    }


/**
 * 判断是否是铲子
 * @param list                                weishixian
 * @return
 */

    public static boolean isXiaoChanzi(List<PokerLabel> list){
        for (int i = 0; i < list.size()-1; i++) {
            int a = list.get(i).getNum();
            int b = list.get(i+1).getNum();
            if(a==3&&b==3)
                return true;
        }
        return false;
    }

    public static boolean isDaChanzi(List<PokerLabel> list){
        for (int i = 0; i < list.size()-2; i++) {
            int a = list.get(i).getNum();
            int b = list.get(i+1).getNum();
            int c = list.get(i+2).getNum();
            if(a==3&&b==3&&c==3)
                return true;
        }
        return false;
    }



/**
 * 判断是否是顺子
 * @param list
 * @return
 */

    public static boolean isShunZi(List<PokerLabel> list){
        for(int i=0;i<list.size()-1;i++){
            int a=list.get(i).getNum();
            int b=list.get(i+1).getNum();
            if(b-a!=1){
                return false;
            }
        }
        return true;
    }


/**
 * 判断是否是连对
 * @param list
 * @return
 */

    public static boolean isLianDui(List<PokerLabel> list){
        int size=list.size();
        if(size<4&&size%2!=0){
            return false;
        }

        for(int i=0;i<size;i++){
            int a=list.get(i).getNum();
            int b=list.get(i+1).getNum();
            if(a!=b){
                return false;
            }
            i++;
        }


        for(int i=0;i<size-2;i++){
            int a=list.get(i).getNum();
            int b=list.get(i+2).getNum();
            if(b-a!=1){
                return false;
            }
            i++;
        }

        return true;
    }




    //比大小
    public static boolean isBigger(List<PokerLabel> prevList,List<PokerLabel> currentList){
        // 首先判断牌型是不是一样
        PokerType paiXing = checkPokerType(prevList);   //上家牌型

        //相同牌型之间比较大小
        if (paiXing.equals(checkPokerType(currentList)))
        {
            // 根据牌型来判断大小
            if (PokerType.l_1.equals(paiXing)) {
                // 单张
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            } else if (PokerType.l_2w.equals(paiXing)) {
                // 王炸
                return false;
            } else if (PokerType.l_2.equals(paiXing)) {
                // 对子
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            } else if (PokerType.l_3.equals(paiXing)) {
                // 炸
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            } else if (PokerType.l_4.equals(paiXing)) {
                // 蒙
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            } else if (PokerType.l_n.equals(paiXing)) {
                // 顺子
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            } else if (PokerType.l_1122.equals(paiXing)) {
                // 连对
                if (compareLast(prevList, currentList)) {
                    return true;
                }
                return false;
            }

        }

        //不同牌型比大小（2张）
        else if(currentList.size()==2)
        {
            //判断是不是王炸
            if(isWangZha(currentList)){
                return true;
            }
            if(PokerType.l_3.equals(paiXing)){
                if(isXiaoChanzi(currentList)){
                    return true;
                }
                return false;
            }
            return false;
        }

       //不同牌型比大小（4张）
        else if(currentList.size()==4)
        {
            //判断是不是蒙
            if(isSame(currentList, 4)){
                return true;
            }
            return false;
        }

        //不同牌型比大小（3张）
        else if(currentList.size()==3)
        {
            //判断是不是炸
            if(isSame(currentList, 3)){
                return true;
            }
            if(PokerType.l_4.equals(paiXing)){
                if(isDaChanzi(currentList)){
                    return true;
                }
                return false;
            }

            return false;
        }



        return false;
    }

    public static boolean compareLast(List<PokerLabel> prevList,List<PokerLabel> currentList){
        if(prevList.get(prevList.size()-1).getNum()<currentList.get(currentList.size()-1).getNum()){
            return true;
        }
        return false;
    }

    public static boolean compare(List<PokerLabel> prevList, List<PokerLabel> currentList){
        int a=zha(prevList);
        int b=zha(currentList);
        if(a==-1||b==-1){
            return false;
        }
        if(b>a){
            return true;
        }
        return false;
    }

    public static int zha(List<PokerLabel> list){
        for(int i=0;i<list.size()-2;i++){
            int a=list.get(i).getNum();
            int b=list.get(i+1).getNum();
            int c=list.get(i+2).getNum();
            if(a==b&&a==c){
                return a;
            }
        }
        return -1;
    }
}

