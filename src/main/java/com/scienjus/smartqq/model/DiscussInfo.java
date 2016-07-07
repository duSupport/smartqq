package com.scienjus.smartqq.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 讨论组资料
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
@Data
public class DiscussInfo {

	@JSONField(name = "did")
	private long id;

	@JSONField(name = "discu_name")
	private String name;

	private List<DiscussUser> users = new ArrayList<>();

	public void addUser(DiscussUser user) {
		this.users.add(user);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DiscussUser> getUsers() {
		return users;
	}

	public void setUsers(List<DiscussUser> users) {
		this.users = users;
	}

}
