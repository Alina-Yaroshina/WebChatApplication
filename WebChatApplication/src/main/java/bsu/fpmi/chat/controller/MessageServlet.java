package bsu.fpmi.chat.controller;

import bsu.fpmi.chat.model.MessageInfo;
import bsu.fpmi.chat.model.MessagesStorage;
import bsu.fpmi.chat.storage.XMLHistoryUtil;
import bsu.fpmi.chat.util.ServletUtil;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;
import bsu.fpmi.chat.dao.MessageInfoDao;
import bsu.fpmi.chat.dao.MessageInfoDaoImpl;

import static bsu.fpmi.chat.util.MessageUtil.MESSAGES;
import static bsu.fpmi.chat.util.MessageUtil.TOKEN;
import static bsu.fpmi.chat.util.MessageUtil.getIndex;
import static bsu.fpmi.chat.util.MessageUtil.getToken;
import static bsu.fpmi.chat.util.MessageUtil.jsonToMessage;
import static bsu.fpmi.chat.util.MessageUtil.stringToJson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());
    private MessageInfoDao messageDao;

    @Override
    public void init() throws ServletException {
        try {
            messageDao = new MessageInfoDaoImpl();
            loadHistory();
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException | NullPointerException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(TOKEN);
        if (token != null && !"".equals(token)) {
            int index = getIndex(token);
            if(MessagesStorage.getSubMessagesByIndex(index).size() == 0) {
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            }
            else {
                String messages = formResponse(index);
                response.setCharacterEncoding("UTF-8");
                response.setContentType(ServletUtil.APPLICATION_JSON);
                PrintWriter out = response.getWriter();
                out.print(messages);
                out.flush();
                logger.info("GET:\n" + messages);
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
            logger.error(HttpServletResponse.SC_BAD_REQUEST + "'token' parameter needed");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            MessageInfo messageInfo = jsonToMessage(json);
            System.out.println(messageInfo.getFormat());
            logger.info("POST: " + messageInfo.getFormat());
            MessagesStorage.addMessage(messageInfo);
            XMLHistoryUtil.addData(messageInfo);
            messageDao.add(messageInfo);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            MessageInfo messageInfo = jsonToMessage(json);
            logger.info("DELETE: " + messageInfo.getFormat());
            String id = messageInfo.getId();
            MessageInfo messageToUpdate = MessagesStorage.getMessageById(id);
            if (messageToUpdate != null) {
                messageToUpdate.setMessage("[deleted]");
                messageToUpdate.setDeleted(true);
                MessageInfo updatedMessage = XMLHistoryUtil.updateData(messageToUpdate);
                //MessagesStorage.addMessage(updatedMessage);
                messageDao.update(updatedMessage);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
            }
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            MessageInfo messageInfo = jsonToMessage(json);
            logger.info("PUT: " + messageInfo.getFormat());
            String id = messageInfo.getId();
            MessageInfo messageToUpdate = MessagesStorage.getMessageById(id);
            if (messageToUpdate != null) {
                messageToUpdate.setMessage(messageInfo.getMessageText());
                messageToUpdate.setEdited(true);
                MessageInfo updatedMessage = XMLHistoryUtil.updateData(messageToUpdate);
                //MessagesStorage.addMessage(updatedMessage);
                messageDao.update(updatedMessage);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message does not exist");
                logger.error(HttpServletResponse.SC_BAD_REQUEST + "Message does not exist");
            }
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
            logger.error(e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String formResponse(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(MESSAGES, MessagesStorage.getSubMessagesByIndex(index));
        jsonObject.put(TOKEN, getToken(MessagesStorage.getSize()));
        return jsonObject.toJSONString();
    }

    private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException  {
        /*if (XMLHistoryUtil.doesStorageExist()) {
            MessagesStorage.addAll(XMLHistoryUtil.getMessages());
            System.out.println(MessagesStorage.getFormattedView());
        } else {
            XMLHistoryUtil.createStorage();
        }*/
        List<MessageInfo> messages = messageDao.selectAll();
        MessagesStorage.addAll(messages);
        System.out.println(MessagesStorage.getFormattedView());
        if (!XMLHistoryUtil.doesStorageExist()) {
            XMLHistoryUtil.createStorage();
        }
        XMLHistoryUtil.addDataFromDB(messages);
    }
}
