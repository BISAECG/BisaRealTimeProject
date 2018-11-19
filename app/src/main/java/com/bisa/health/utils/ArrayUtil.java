package com.bisa.health.utils;

import com.bisa.health.ecg.model.OTGECGDto;
import com.bisa.health.model.enumerate.OrderEnum;
import com.bisa.health.usb.fat.Fat128Sys;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/10.
 */

public class ArrayUtil {

    private static final String TAG = "ArrayUtil";


    public static Map<String,String> sort(String[] keys, String[] values){
       Map<String,String> param=new HashMap<String,String>();
        for(int i=0;i<keys.length;i++){
            param.put(keys[i],values[i]);
        }
        return param;
    }
    
    public static void  SortFat128List(List<Fat128Sys> list,final OrderEnum order){

            Collections.sort(list, new Comparator<Fat128Sys>() {
                @Override
                public int compare(Fat128Sys o1, Fat128Sys o2) {

                    String[] ecgNameArrayO1=FunUtil.ecgNameSpilt(o1.getFilename());
                    String[] ecgNameArrayO2=FunUtil.ecgNameSpilt(o2.getFilename());

                    if(ecgNameArrayO1==null||ecgNameArrayO2==null){
                        return 0;
                    }

                    long o1Long=Long.parseLong(ecgNameArrayO1[1]);
                    long o2Long=Long.parseLong(ecgNameArrayO2[1]);

                    if(order==OrderEnum.DESC){
                        if(o1Long<o2Long){
                            return 1;
                        }else if(o1Long>o2Long){
                            return -1;
                        }
                    }else{
                        if(o1Long<o2Long){
                            return -1;
                        }else if(o1Long>o2Long){
                            return 1;
                        }
                    }

                    return 0;
                }
            });

    }

    public static void  SortOTGECGDtoList(List<OTGECGDto> list, final OrderEnum order){

        Collections.sort(list, new Comparator<OTGECGDto>() {
            @Override
            public int compare(OTGECGDto o1, OTGECGDto o2) {



                int o1Long=o1.getYmd();
                int o2Long=o2.getYmd();

                if(order==OrderEnum.DESC){

                    if(o1Long<o2Long){
                        return 1;
                    }else if(o1Long>o2Long){
                        return -1;
                    }
                }else{

                    if(o1Long<o2Long){
                        return -1;
                    }else if(o1Long>o2Long){
                        return 1;
                    }
                }

                return 0;
            }
        });
    }
}
