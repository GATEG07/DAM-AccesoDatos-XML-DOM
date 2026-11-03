package Ejercicio4;

import java.io.File;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ZooContinente {

    public static void main(String[] args) throws Exception {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce un continente: ");
        String continente = teclado.nextLine();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.parse(new File("src/Ejercicio4/Zoo.xml"));

        NodeList felinos = documento.getElementsByTagName("Felino");

        for (int i = 0; i < felinos.getLength(); i++) {

            Element felino = (Element) felinos.item(i);

            String nombre = felino.getElementsByTagName("Nombre")
                    .item(0).getTextContent();

            String origen = felino.getElementsByTagName("Origen")
                    .item(0).getTextContent();

            if (origen.equalsIgnoreCase(continente)) {
                System.out.println(nombre + " - " + origen);
            }
        }
    }
}