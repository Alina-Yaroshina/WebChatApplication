package bsu.fpmi.chat.controller;

import bsu.fpmi.chat.model.MessageInfo;
import bsu.fpmi.chat.model.MessagesStorage;
import bsu.fpmi.chat.storage.XMLHistoryUtil;
import bsu.fpmi.chat.util.ServletUtil;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import static bsu.fpmi.chat.util.MessageUtil.MESSAGES;
import static bsu.fpmi.chat.util.MessageUtil.TOKEN;
import static bsu.fpmi.chat.util.MessageUtil.getIndex;
import static bsu.fpmi.chat.util.MessageUtil.getToken;
import static bsu.fpmi.chat.util.MessageUtil.jsonToMessage;
import static bsu.fpmi.chat.util.MessageUtil.stringToJson;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

@WebServlet("/chat")
public class MessageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(MessageServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
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
            String messages = formResponse(index);
            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();
            out.print(messages);
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        try {
            JSONObject json = stringToJson(data);
            MessageInfo messageInfo = jsonToMessage(json);
            System.out.println(messageInfo.getFormat());
            MessagesStorage.addMessage(messageInfo);
            XMLHistoryUtil.addData(messageInfo);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException | ParserConfigurationException | SAXException | TransformerException e) {
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
        if (XMLHistoryUtil.doesStorageExist()) {
            MessagesStorage.addAll(XMLHistoryUtil.getMessages());
            System.out.println(MessagesStorage.getFormattedView());
        } else {
            XMLHistoryUtil.createStorage();
        }
    }
}
