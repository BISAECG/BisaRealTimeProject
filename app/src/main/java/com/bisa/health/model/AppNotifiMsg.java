package com.bisa.health.model;

import java.io.Serializable;

/**
 * 用于ECG通知
 * 是否添加过紧急联系人
 * 是否有未读报告
 * @author Administrator
 *
 */
public class AppNotifiMsg implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int  isContact;
	public int unReadCount;
	public int getIsContact() {
		return isContact;
	}
	public void setIsContact(int isContact) {
		this.isContact = isContact;
	}
	public int getUnReadCount() {
		return unReadCount;
	}
	public void setUnReadCount(int unReadCount) {
		this.unReadCount = unReadCount;
	}

	@Override
	public String toString() {
		return "AppNotifiMsg{" +
				"isContact=" + isContact +
				", unReadCount=" + unReadCount +
				'}';
	}
}
