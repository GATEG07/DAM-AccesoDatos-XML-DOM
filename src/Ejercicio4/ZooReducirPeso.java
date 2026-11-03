package Ejercicio4;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ZooReducirPeso {

    public static void main(String[] args) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.parse(new File("src/Ejercicio4/Zoo.xml"));

        NodeList felinos = documento.getElementsByTagName("Felino");

        System.out.println("Nuevos pesos: ");

        for (int i = 0; i < felinos.getLength(); i++) {

            Element felino = (Element) felinos.item(i);

            String nombre = felino.getElementsByTagName("Nombre")
                    .item(0).getTextContent();

            Element peso = (Element) felino.getElementsByTagName("Peso")
                    .item(0);

            int nuevoPeso = Integer.parseInt(peso.getTextContent()) - 5;

            peso.setTextContent("" + nuevoPeso);

            System.out.println(nombre + " - " + nuevoPeso + " kg");
        }

        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.transform(new DOMSource(documento),
                new StreamResult(new File("src/Ejercicio4/Zoo2.xml")));

        System.out.println("Zoo2.xml creado correctamente");
    }
}