package bsu.fpmi.chat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bsu.fpmi.chat.model.MessageInfo;
import org.apache.log4j.Logger;
import bsu.fpmi.chat.db.ConnectionManager;

public class MessageInfoDaoImpl implements MessageInfoDao {
	private static Logger logger = Logger.getLogger(MessageInfoDaoImpl.class.getName());

	@Override
	public void add(MessageInfo messageInfo) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ConnectionManager.getConnection();

            preparedStatement = connection.prepareStatement
                    ("INSERT INTO messages (id, user, messageText, date, edited, deleted) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, messageInfo.getId());
            preparedStatement.setString(2, messageInfo.getUser());
            preparedStatement.setString(3, messageInfo.getMessageText());
            preparedStatement.setString(4, messageInfo.getDate());
            preparedStatement.setBoolean(5, messageInfo.isEdited());
            preparedStatement.setBoolean(6, messageInfo.isDeleted());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
			logger.error(e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
	}

	@Override
	public void update(MessageInfo messageInfo) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("Update messages SET " + "user = ?, " + "messageText = ?, " +
                    "date = ?, " + "edited = ?, " + "deleted = ? WHERE id = ?");
            preparedStatement.setString(1, messageInfo.getUser());
            preparedStatement.setString(2, messageInfo.getMessageText());
            preparedStatement.setString(3, messageInfo.getDate());
            preparedStatement.setBoolean(4, messageInfo.isEdited());
            preparedStatement.setBoolean(5, messageInfo.isDeleted());
            preparedStatement.setString(6, messageInfo.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
			logger.error(e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}

			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
	}

	@Override
	public MessageInfo selectById(MessageInfo messageInfo) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<MessageInfo> selectAll() {
		List<MessageInfo> messages = new ArrayList<>();
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = ConnectionManager.getConnection();
			statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM messages ORDER BY date");
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String user = resultSet.getString("user");
                String message = resultSet.getString("messageText");
                String date = resultSet.getString("date");
                boolean edited = resultSet.getBoolean("edited");
                boolean deleted = resultSet.getBoolean("deleted");
                messages.add(new MessageInfo(id, user, message, deleted, edited, date));
            }

        } catch (SQLException e) {
			logger.error(e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return messages;
	}

	@Override
	public void delete(int id) {
		throw new UnsupportedOperationException();
	}

}
