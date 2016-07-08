package com.scienjus.smartqq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.Category;
import com.scienjus.smartqq.model.Discuss;
import com.scienjus.smartqq.model.DiscussInfo;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.DiscussUser;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.Group;
import com.scienjus.smartqq.model.GroupInfo;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.GroupUser;
import com.scienjus.smartqq.model.IUser;
import com.scienjus.smartqq.model.Message;
import com.scienjus.smartqq.model.UserInfo;

/**
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Application {
	public static Map<Long, Friend> friends = new HashMap<>();
	public static Map<Long, UserInfo> users = new HashMap<>();
	public static Map<Long, Group> groups = new HashMap<>();
	public static Map<Long, Map<Long, GroupUser>> gUsers = new HashMap<>();
	public static Map<Long, Discuss> discuss = new HashMap<>();
	public static Map<Long, Map<Long, DiscussUser>> dUsers = new HashMap<>();

	static SmartQQClient client = null;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		// 创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
		client = new SmartQQClient(new MessageCallback() {
			@Override
			public void onMessage(Message message) {
				Friend user = getFriend(message.getUserId());
				String usern = user == null ? message.getUserId() + "" : user.getMarkname();
				System.out.printf("  %s [%s]\n>%s\n", usern, sdf.format(new Date(message.getTime())), message.getContent());
			}

			@Override
			public void onGroupMessage(GroupMessage message) {
				Group g = getGroup(message.getGroupId());
				GroupUser user = getGroupUser(message);

				String gn = g == null ? message.getGroupId() + "" : g.getName();
				String usern = user == null ? message.getUserId() + "" : user.getNick();

				System.out.println(gn);
				System.out.printf("  %s [%s]\n>%s\n", usern, sdf.format(new Date(message.getTime())), message.getContent());
			}

			@Override
			public void onDiscussMessage(DiscussMessage message) {
				Discuss d = getDiscuss(message.getDiscussId());
				DiscussUser user = getDiscussUser(message);

				String dn = d == null ? message.getDiscussId() + "" : d.getName();
				String usern = user == null ? message.getUserId() + "" : user.getNick();

				System.out.println(dn);
				System.out.printf("  %s [%s]\n>%s\n", usern, sdf.format(new Date(message.getTime())), message.getContent());
			}
		});
		// 登录成功后便可以编写你自己的业务逻辑了
		List<Category> categories = client.getFriendListWithCategory();
		for (Category category : categories) {
			System.out.println(category.getName());
			for (Friend friend : category.getFriends()) {
				friends.put(friend.getUserId(), friend);
			}
		}
		Scanner scan = new Scanner(System.in);
		while (true) {
			System.out.println("请输入指令：");
			String cmd = scan.nextLine();

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

	public static Friend getFriend(Long id) {
		return friends.get(id);
	}

	public static Group getGroup(Long id) {
		Group group = groups.get(id);
		try {
			if (group == null) {
				List<Group> gs = client.getGroupList();
				groups.clear();
				for (Group g : gs) {
					groups.put(g.getId(), g);
				}
				group = groups.get(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}

	public static Discuss getDiscuss(Long id) {
		Discuss dis = discuss.get(id);
		try {
			if (dis == null) {
				List<Discuss> gs = client.getDiscussList();
				discuss.clear();
				for (Discuss g : gs) {
					discuss.put(g.getId(), g);
				}
				dis = discuss.get(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dis;
	}

	private static GroupUser getGroupUser(GroupMessage msg) {
		Map<Long, GroupUser> um = gUsers.get(msg.getGroupId());
		if (um == null) {
			GroupInfo gi = client.getGroupInfo(msg.getGroupId());
			if (gi != null) {
				gUsers.put(gi.getGid(), listToMap(gi.getUsers()));
				um = gUsers.get(msg.getGroupId());
			} else {
				return null;
			}
		}
		GroupUser u = um.get(msg.getUserId());
		if (u == null) {
			GroupInfo gi = client.getGroupInfo(msg.getGroupId());
			gUsers.put(gi.getGid(), listToMap(gi.getUsers()));
		}
		um = gUsers.get(msg.getGroupId());
		u = um.get(msg.getUserId());
		return u;
	}

	private static DiscussUser getDiscussUser(DiscussMessage msg) {
		Map<Long, DiscussUser> um = dUsers.get(msg.getDiscussId());
		if (um == null) {
			DiscussInfo di = client.getDiscussInfo(msg.getDiscussId());
			if (di != null) {
				dUsers.put(msg.getDiscussId(), listToMap(di.getUsers()));
				um = dUsers.get(msg.getDiscussId());
			} else {
				return null;
			}

		}
		DiscussUser u = um.get(msg.getUserId());
		if (u == null) {
			DiscussInfo di = client.getDiscussInfo(msg.getDiscussId());
			if (di != null) {
				dUsers.put(msg.getDiscussId(), listToMap(di.getUsers()));
			}
			um = dUsers.get(msg.getDiscussId());
			u = um.get(msg.getUserId());
		}

		return u;
	}

	private static <T extends IUser> Map<Long, T> listToMap(List<T> list) {
		Map<Long, T> map = new HashMap<>();
		for (T t : list) {
			map.put(t.getUin(), t);
		}
		return map;
	}
}
