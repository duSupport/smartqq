package com.scienjus.smartqq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.Discuss;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.Group;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import com.scienjus.smartqq.model.UserInfo;

/**
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Application {
	public static Map<Long, UserInfo> users = new HashMap<>();
	public static Map<String, Group> groups = new HashMap<>();
	public static Map<String, Discuss> discuss = new HashMap<>();

	static SmartQQClient client = null;

	public static void main(String[] args) {
		// 创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
		client = new SmartQQClient(new MessageCallback() {
			@Override
			public void onMessage(Message message) {
				UserInfo user = users.get(message.getUserId());
				if (user == null) {
					user = client.getFriendInfo(message.getUserId());
					users.put(message.getUserId(), user);
				}
				System.out.printf("%s [%s]\n\t%s\n", message.getUserId(), message.getTime(), message.getContent());
			}

			@Override
			public void onGroupMessage(GroupMessage message) {
				System.out.println(message.getGroupId());
				System.out.printf("%s [%s]\n\t%s\n", message.getUserId(), message.getTime(), message.getContent());
			}

			@Override
			public void onDiscussMessage(DiscussMessage message) {
				System.out.println(message.getDiscussId());
				System.out.printf("%s [%s]\n\t%s\n", message.getUserId(), message.getTime(), message.getContent());
			}
		});
		// 登录成功后便可以编写你自己的业务逻辑了
		List<Category> categories = client.getFriendListWithCategory();
		for (Category category : categories) {
			System.out.println(category.getName());
			for (Friend friend : category.getFriends()) {
				System.out.println("————" + friend.getNickname());
			}
		}
		Scanner scan = new Scanner(System.in);
		while (true) {

			String cmd = scan.next();

			try {
				if ("exit".equals(cmd)) {
					client.close();
				}
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
				scan.close();
			}
		}
		// 使用后调用close方法关闭，你也可以使用try-with-resource创建该对象并自动关闭

	}
}
