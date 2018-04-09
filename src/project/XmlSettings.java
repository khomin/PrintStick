package project;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class XmlSettings {
    // tty
    public String readXML_tty(String xml) {
        String tty_name = "";
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
            Element doc = dom.getDocumentElement();
            tty_name = getTextValue(tty_name, doc, "tty_name");
            return tty_name;
        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return tty_name;
    }
    public void saveToXML_tty(String tty_name, String xml) {
        Document dom;
        Element e = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();
            // create the root element
            Element rootEle = dom.createElement("tty");
            // create data elements and place them under root
            e = dom.createElement("tty_name");
            e.appendChild(dom.createTextNode(tty_name));
            rootEle.appendChild(e);
            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "hibernate-configuration-3.0.dtd");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(xml)));
            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    // db
    public ArrayList<String> readXML_dataBase(String xml) {
        ArrayList<String> rolev;
        rolev = new ArrayList<String>();
        String str = "";
        Document dom;
        // Make an  instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the
            // XML file
            dom = db.parse(xml);
            Element doc = dom.getDocumentElement();
            str = getTextValue(str, doc, "name");
            rolev.add(str);
            //
            str = getTextValue(str, doc, "host");
            rolev.add(str);
            //
            str = getTextValue(str, doc, "passwd");
            rolev.add(str);
            //
            str = getTextValue(str, doc, "port");
            rolev.add(str);
            return rolev;

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
        return rolev;
    }

    public void saveToXML_dataBase(String name,
            String host,
            String port,
            String passwd,
            String xml) {
        Document dom;
        Element e = null;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            dom = db.newDocument();

            // create the root element
            Element rootEle = dom.createElement("db");

            // create data elements and place them under root
            e = dom.createElement("name");
            e.appendChild(dom.createTextNode(name));
            rootEle.appendChild(e);

            e = dom.createElement("host");
            e.appendChild(dom.createTextNode(host));
            rootEle.appendChild(e);

            e = dom.createElement("passwd");
            e.appendChild(dom.createTextNode(passwd));
            rootEle.appendChild(e);

            e = dom.createElement("port");
            e.appendChild(dom.createTextNode(port));
            rootEle.appendChild(e);

            dom.appendChild(rootEle);

            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "hibernate-configuration-3.0.dtd");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                // send DOM to file
                tr.transform(new DOMSource(dom),
                        new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException te) {
                System.out.println(te.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    private String getTextValue(String def, Element doc, String tag) {
        String value = def;
        NodeList nl;
        nl = doc.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).hasChildNodes()) {
            value = nl.item(0).getFirstChild().getNodeValue();
        }
        return value;
    }
}
