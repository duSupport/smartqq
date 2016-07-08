package com.scienjus.smartqq.model;

import lombok.Data;

/**
 * 讨论组成员
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
@Data
public class DiscussUser implements IUser {

	private Long uin;

	private String nick;

	private int clientType;

	private String status;

	@Override
	public String toString() {
		return "DiscussUser{" + "uin=" + uin + ", nick='" + nick + '\'' + ", clientType='" + clientType + '\'' + ", status='" + status + '\'' + '}';
	}

	public Long getUin() {
		return uin;
	}

	public void setUin(Long uin) {
		this.uin = uin;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
