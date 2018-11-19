package com.bisa.health.dao;

import com.bisa.health.model.dto.EventDto;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public interface IEventDao {
    public EventDto add(EventDto event);
    public List<EventDto> loadByUserGuid(int userGuid);
    public int loadByAllCount(int userGuid);
    public EventDto update(EventDto event);
    public EventDto updateOrSave(EventDto event);
    public void updateOrSave(List<EventDto> event);
    public boolean del(EventDto event);
}
