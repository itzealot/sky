/**
 * 
 */
package com.surfilter.mass.entity;

/**
 * @author hapuer
 * 报警通知信息
 * 1，可以做短信通知
 * 2，可以做邮件通知
 */
public class MsgNotify {

	/**
	 * 0: 表示短信
	 * 1：  表示邮件
	 * 2: 表示都通知
	 */
	private Integer type = 0;
	
	/**
	 * 通知账号:多个账号用，分隔
	 */
	private String notifyAccount;
	
	/**
	 * 通知内容，按照如下格式组织
	 * 
	 * 类型|匹配信息|时间戳|场所编码
	 */
	private String message;
	
	public MsgNotify(Integer type,String notifyAccount,String message){
		this.type = type;
		this.notifyAccount = notifyAccount;
		this.message = message;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getNotifyAccount() {
		return notifyAccount;
	}

	public void setNotifyAccount(String notifyAccount) {
		this.notifyAccount = notifyAccount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
