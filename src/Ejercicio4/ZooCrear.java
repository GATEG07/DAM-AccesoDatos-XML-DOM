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

public class ZooCrear {

    public static void main(String[] args) throws Exception {

        String ruta = "src/Ejercicio4/Zoo.xml";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.newDocument();

        Element zoo = documento.createElement("Zoo");
        documento.appendChild(zoo);

        Element felinos = documento.createElement("Felinos");
        zoo.appendChild(felinos);

        Element felino = documento.createElement("Felino");
        felinos.appendChild(felino);

        Element nombre = documento.createElement("Nombre");
        nombre.setTextContent("Leon");
        felino.appendChild(nombre);

        Element origen = documento.createElement("Origen");
        origen.setTextContent("Africa");
        felino.appendChild(origen);

        Element peso = documento.createElement("Peso");
        peso.setTextContent("155");
        felino.appendChild(peso);

        felino = documento.createElement("Felino");
        felinos.appendChild(felino);

        nombre = documento.createElement("Nombre");
        nombre.setTextContent("Tigre");
        felino.appendChild(nombre);

        origen = documento.createElement("Origen");
        origen.setTextContent("Asia");
        felino.appendChild(origen);

        peso = documento.createElement("Peso");
        peso.setTextContent("215");
        felino.appendChild(peso);

        felino = documento.createElement("Felino");
        felinos.appendChild(felino);

        nombre = documento.createElement("Nombre");
        nombre.setTextContent("Lince");
        felino.appendChild(nombre);

        origen = documento.createElement("Origen");
        origen.setTextContent("Europa");
        felino.appendChild(origen);

        peso = documento.createElement("Peso");
        peso.setTextContent("20");
        felino.appendChild(peso);

        felino = documento.createElement("Felino");
        felinos.appendChild(felino);

        nombre = documento.createElement("Nombre");
        nombre.setTextContent("PanteraTigris");
        felino.appendChild(nombre);

        origen = documento.createElement("Origen");
        origen.setTextContent("America");
        felino.appendChild(origen);

        peso = documento.createElement("Peso");
        peso.setTextContent("95");
        felino.appendChild(peso);

        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.transform(new DOMSource(documento),
                new StreamResult(new File(ruta)));

        System.out.println("Zoo.xml creado correctamente ");
    }
}