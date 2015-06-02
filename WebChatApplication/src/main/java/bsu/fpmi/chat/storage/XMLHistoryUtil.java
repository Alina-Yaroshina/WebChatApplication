package bsu.fpmi.chat.storage;

import bsu.fpmi.chat.model.MessageInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLHistoryUtil {
    private static final String STORAGE_LOCATION = System.getProperty("user.home") +  File.separator + "history.xml";
    private static final String MESSAGES = "messages";
    private static final String MESSAGE = "message";
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String MESTEXT = "messageText";
    private static final String EDITED = "edited";
    private static final String DELETED = "deleted";
    private static final String DATE = "date";

    private XMLHistoryUtil() {
    }

    public static synchronized void createStorage() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement(MESSAGES);
        doc.appendChild(rootElement);

        Transformer transformer = getTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
        transformer.transform(source, result);
    }

    public static synchronized void addData(MessageInfo messageInfo) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement(); // Root <messages> element

        Element messageElement = document.createElement(MESSAGE);
        root.appendChild(messageElement);

        messageElement.setAttribute(ID, messageInfo.getId());

        Element usernameElement = document.createElement(USER);
        usernameElement.appendChild(document.createTextNode(messageInfo.getUser()));
        messageElement.appendChild(usernameElement);

        Element textElement = document.createElement(MESTEXT);
        textElement.appendChild(document.createTextNode(messageInfo.getMessageText()));
        messageElement.appendChild(textElement);

        Element editElement = document.createElement(EDITED);
        editElement.appendChild(document.createTextNode(Boolean.toString(messageInfo.isEdited())));
        messageElement.appendChild(editElement);

        Element removeElement = document.createElement(DELETED);
        removeElement.appendChild(document.createTextNode(Boolean.toString(messageInfo.isDeleted())));
        messageElement.appendChild(removeElement);

        Element dateElement = document.createElement(DATE);
        dateElement.appendChild(document.createTextNode(messageInfo.getDate()));
        messageElement.appendChild(dateElement);

        DOMSource source = new DOMSource(document);

        Transformer transformer = getTransformer();

        StreamResult result = new StreamResult(STORAGE_LOCATION);
        transformer.transform(source, result);
    }

    private static Node getNodeById(Document doc, String id) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression expr = xpath.compile("//" + MESSAGE + "[@id='" + id + "']");
        return (Node) expr.evaluate(doc, XPathConstants.NODE);
    }

    public static void addDataFromDB(List<MessageInfo> messages) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        int i = 0;
        while(i < messages.size()) {
            addData(messages.get(i));
            i++;
            }
        }

    public static synchronized MessageInfo updateData(MessageInfo messageInfo) throws ParserConfigurationException, SAXException,
            IOException, TransformerException, XPathExpressionException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Node messageToUpdate = getNodeById(document, messageInfo.getId());

        if (messageToUpdate != null) {
            NodeList childNodes = messageToUpdate.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (ID.equals(node.getNodeName())) {
                    node.setTextContent(messageInfo.getId());
                }
                if (USER.equals(node.getNodeName())) {
                    node.setTextContent(messageInfo.getUser());
                }
                if (MESTEXT.equals(node.getNodeName())) {
                    node.setTextContent(messageInfo.getMessageText());
                }
                if (DATE.equals(node.getNodeName())) {
                    node.setTextContent(messageInfo.getDate());
                }
                if (EDITED.equals(node.getNodeName())) {
                    node.setTextContent(Boolean.toString(messageInfo.isEdited()));
                }
                if (DELETED.equals(node.getNodeName())) {
                    node.setTextContent(Boolean.toString(messageInfo.isDeleted()));
                }
            }

            Transformer transformer = getTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(STORAGE_LOCATION));
            transformer.transform(source, result);
        } else {
            throw new NullPointerException();
        }
        return new MessageInfo(messageInfo.getId(), messageInfo.getUser(), messageInfo.getMessageText(), messageInfo.isDeleted(),
                messageInfo.isEdited(), messageInfo.getDate());
    }

    public static synchronized boolean doesStorageExist() {
        File file = new File(STORAGE_LOCATION);
        return file.exists();
    }

    public static synchronized List<MessageInfo> getMessages() throws SAXException, IOException, ParserConfigurationException {
        List<MessageInfo> messages = new ArrayList<MessageInfo>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(STORAGE_LOCATION);
        document.getDocumentElement().normalize();
        Element root = document.getDocumentElement(); // Root <messages> element
        NodeList messageList = root.getElementsByTagName(MESSAGE);
        for (int i = 0; i < messageList.getLength(); i++) {
            Element taskElement = (Element) messageList.item(i);
            String id = taskElement.getAttribute(ID);
            String user = taskElement.getElementsByTagName(USER).item(0).getTextContent();
            String message = taskElement.getElementsByTagName(MESTEXT).item(0).getTextContent();
            boolean edited = Boolean.valueOf(taskElement.getElementsByTagName(EDITED).item(0).getTextContent());
            boolean deleted = Boolean.valueOf(taskElement.getElementsByTagName(DELETED).item(0).getTextContent());
            String date = taskElement.getElementsByTagName(DATE).item(0).getTextContent();
            messages.add(new MessageInfo(id, user, message, deleted, edited, date));
        }
        return messages;
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

}
