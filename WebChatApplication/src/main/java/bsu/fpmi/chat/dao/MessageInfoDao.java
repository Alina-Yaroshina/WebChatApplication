package bsu.fpmi.chat.dao;

import java.util.List;
import bsu.fpmi.chat.model.MessageInfo;

public interface MessageInfoDao {
	void add(MessageInfo message);

	void update(MessageInfo message);

	void delete(int id);

	MessageInfo selectById(MessageInfo message);

	List<MessageInfo> selectAll();
}
