package pers.xanadu.enderdragon.maven;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLParser {
    public static String getString(Element element,String tag){
        Element element_section = getElement(element,tag);
        return element_section==null?null:element_section.getTextContent();
    }
    public static Element getElement(Element element,String tag){
        NodeList nodeList = element.getElementsByTagName(tag);
        return (Element) nodeList.item(0);
    }
}
