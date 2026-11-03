package Ejercicio4;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ZooMayores100 {

    public static void main(String[] args) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.parse(new File("src/Ejercicio4/Zoo.xml"));

        NodeList felinos = documento.getElementsByTagName("Felino");

        System.out.println("Felinos que pesan mas de 100 kg: ");

        for (int i = 0; i < felinos.getLength(); i++) {

            Element felino = (Element) felinos.item(i);

            String nombre = felino.getElementsByTagName("Nombre")
                    .item(0).getTextContent();

            String textoPeso = felino.getElementsByTagName("Peso")
                    .item(0).getTextContent();

            int peso = Integer.parseInt(textoPeso);

            if (peso > 100) {
                System.out.println(nombre + " - " + peso + " kg");
            }
        }
    }
}