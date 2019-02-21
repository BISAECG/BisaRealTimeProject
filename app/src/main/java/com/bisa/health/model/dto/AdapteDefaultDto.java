package com.bisa.health.model.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/25.
 */

public class AdapteDefaultDto implements Serializable{
  private String title;
  private String flag;
  private String index;
  private int status;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getFlag() {
    return flag;
  }

  public void setFlag(String flag) {
    this.flag = flag;
  }
}
