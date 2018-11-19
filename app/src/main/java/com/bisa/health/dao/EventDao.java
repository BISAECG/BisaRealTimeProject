package com.bisa.health.dao;

import android.content.Context;
import android.net.Uri;

import com.bisa.health.model.dto.EventDto;
import com.bisa.health.provider.event.EventContentValues;
import com.bisa.health.provider.event.EventCursor;
import com.bisa.health.provider.event.EventSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public class EventDao implements IEventDao {

    public EventDao() {
        //this.context = FactoryBeans.getAppContext();
    }

    private Context context;

    @Override
    public EventDto add(EventDto event) {
        EventContentValues eventContentValues = event.toEventContentValues();
        Uri uri = context.getContentResolver().insert(eventContentValues.uri(), eventContentValues.values());
        if (uri == null) {
            return null;
        }
        return event;
    }



    @Override
    public List<EventDto> loadByUserGuid(int userGuid) {
        List<EventDto> listDto=new ArrayList<EventDto>();
        EventSelection where=new EventSelection();
        where.userGuid(userGuid);
        EventCursor eventCursor=where.query(context);

        while (eventCursor.moveToNext()){
            EventDto eventDto=new EventDto().toEvent(eventCursor);
            listDto.add(eventDto);
        }
        eventCursor.close();
        return listDto;
    }

    @Override
    public int loadByAllCount(int userGuid) {
        EventSelection where=new EventSelection();
        where.userGuid(userGuid);
        EventCursor eventCursor=where.query(context);
        int count=eventCursor.getCount();
        eventCursor.close();
        return count;
    }

    @Override
    public EventDto update(EventDto event) {
        EventSelection where = new EventSelection();
        where.userGuid(event.getUser_guid()).and().eventNum(event.getEvent_num());
        EventContentValues eventContentValues = event.toEventContentValues();
        int dbCount = eventContentValues.update(context, where);
        if (dbCount <= 0) {
            return null;
        }
        return event;
    }

    @Override
    public EventDto updateOrSave(EventDto event) {
        try {
            EventSelection where=new EventSelection();
            where.userGuid(event.getUser_guid()).and().eventNum(event.getEvent_num());
            EventCursor eventCursor= where.query(context);
            int count=eventCursor.getCount();
            eventCursor.close();
            if(count>0){
                update(event);
            }else{
                add(event);
            }


            return event;
        }catch (Exception e){
            return null;
        }
    }


    @Override
    public void updateOrSave(List<EventDto> eventList) {

        for(EventDto eventDto : eventList){
            EventSelection where=new EventSelection();
            where.userGuid(eventDto.getUser_guid()).and().eventNum(eventDto.getEvent_num());
            EventCursor eventCursor= where.query(context);
            if(eventCursor.getCount()>0){
                update(eventDto);
            }else{
                add(eventDto);
            }
            eventCursor.close();
        }

    }

    @Override
    public boolean del(EventDto event) {
        try {
            EventSelection where=new EventSelection();
            where.userGuid(event.getUser_guid()).and().eventNum(event.getEvent_num());
            context.getContentResolver().delete(where.uri(),where.sel(),where.args());
            return true;
        }catch (Exception e){
            return false;
        }


    }
}
