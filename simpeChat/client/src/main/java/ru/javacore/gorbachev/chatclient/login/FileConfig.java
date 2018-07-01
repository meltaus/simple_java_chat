package ru.javacore.gorbachev.chatclient.login;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.javacore.gorbachev.chatclient.essence.SettingsXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileConfig {

    //Путь к серверу
    private String serverChat;

    //имя файла настроек
    public static final String FILECONFIG = "config.xml";

    //Создаем файл настроек
    public void createXML(String serverChat, String user) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();

            // создаем пустой объект Document, в котором будем
            // создавать наш xml-файл
            Document doc = builder.newDocument();
            // создаем корневой элемент
            Element rootElement =
                    doc.createElementNS("help", "SettingsProgram");
            // добавляем корневой элемент в объект Document
            doc.appendChild(rootElement);

            // добавляем первый дочерний элемент к корневому
            rootElement.appendChild(getElement(doc, serverChat, user));

            //создаем объект TransformerFactory для печати в консоль
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // для красивого вывода в консоль
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //печатаем в консоль или файл
            StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File(FILECONFIG));

            //записываем данные
            transformer.transform(source, console);
            transformer.transform(source, file);
            System.out.println("Создание XML файла закончено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // метод для создания нового узла XML-файла
    private static Node getElement(Document doc, String serverChat, String user) {
        Element element = doc.createElement("SettingsXML");

        // устанавливаем атрибут id
        //language.setAttribute("id", id);

        // создаем элемент name
        element.appendChild(getXMLElements(doc, element, "serverChat", serverChat));

        // создаем элемент age
        element.appendChild(getXMLElements(doc, element, "user", user));
        return element;
    }


    // утилитный метод для создание нового узла XML-файла
    private static Node getXMLElements(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    //Прочитать данные с файла настроек
    public SettingsXML loadXML() {
        File xmlFile = new File(FILECONFIG);
        SettingsXML result = new SettingsXML();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Корневой элемент: " + doc.getDocumentElement().getNodeName());
            // получаем узлы с именем SettingsXML
            // теперь XML полностью загружен в память
            // в виде объекта Document
            NodeList nodeList = doc.getElementsByTagName("SettingsXML");

            // создадим из него список объектов SettingsXML
            List<SettingsXML> settingsList = new ArrayList<SettingsXML>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                settingsList.add(getElement(nodeList.item(i)));
            }

            // печатаем в консоль информацию по каждому объекту SettingsXML
            for (SettingsXML settings : settingsList) {
                result = settings;
                break;
            }
            return result;
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return null;
    }

    // создаем из узла документа объект SettingsXML
    private static SettingsXML getElement(Node node) {
        SettingsXML settingsXML = new SettingsXML();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            settingsXML.setServerChat(getTagValue("serverChat", element));
            settingsXML.setUser(getTagValue("user", element));
        }

        return settingsXML;
    }

    // получаем значение элемента по указанному тегу
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    public void addXML(String serverChat, String user) {
        File xmlFile = new File(FILECONFIG);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // обновляем значения
            updateElementValue(doc);

            // добавляем новый элемент
            addElement(doc, serverChat, user);

            // запишем отредактированный элемент в файл
            // или выведем в консоль
            doc.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(FILECONFIG));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            System.out.println("XML успешно изменен!");

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    // добавили элемент paradigm
    private static void addElement(Document doc, String serverChat, String user) {
        Element rootElement = doc.getDocumentElement();

        rootElement.appendChild(getElement(doc, serverChat, user));
    }

    // изменяем значение существующего элемента name
    private static void updateElementValue(Document doc) {
        NodeList languages = doc.getElementsByTagName("SettingsXML");
        Element lang = null;
        // проходим по каждому элементу Language
        for(int i=0; i<languages.getLength();i++){
            lang = (Element) languages.item(i);
            Node name = lang.getElementsByTagName("serverChat").item(0).getFirstChild();
            name.setNodeValue(name.getNodeValue().toUpperCase());
        }
    }
}
