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

public class AlumnosNotas {

    public static void main(String[] args) throws Exception {

        String ruta = "src/Ejercicio4/";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document documento = builder.parse(new File(ruta + "alumnos.xml"));

        NodeList alumnos = documento.getElementsByTagName("alumno");

        Document resultado = builder.newDocument();

        Element notas = resultado.createElement("notas");
        resultado.appendChild(notas);

        double suma = 0;

        for (int i = 0; i < alumnos.getLength(); i++) {

            Element alumno = (Element) alumnos.item(i);

            String dni = alumno.getAttribute("dni");

            double practica1 = Double.parseDouble(alumno
                    .getElementsByTagName("practica1").item(0).getTextContent());

            double practica2 = Double.parseDouble(alumno
                    .getElementsByTagName("practica2").item(0).getTextContent());

            double examen = Double.parseDouble(alumno
                    .getElementsByTagName("examen").item(0).getTextContent());

            double practicaFinal = Double.parseDouble(alumno
                    .getElementsByTagName("final").item(0).getTextContent());

            double mediaPracticas = (practica1 + practica2) / 2;

            double nota = mediaPracticas * 0.30
                    + examen * 0.40
                    + practicaFinal * 0.30;

            suma = suma + nota;

            System.out.println(dni + " = " + nota);

            Element alumnoNuevo = resultado.createElement("alumno");
            alumnoNuevo.setAttribute("dni", dni);
            notas.appendChild(alumnoNuevo);

            Element media = resultado.createElement("media");
            media.setTextContent("" + nota);
            alumnoNuevo.appendChild(media);
        }

        double mediaClase = suma / alumnos.getLength();

        String modulo = documento.getElementsByTagName("modulo")
                .item(0).getTextContent();

        System.out.println("Media total del modulo " + modulo
                + " = " + mediaClase);

        Element mediaTotal = resultado.createElement("mediaClase");
        mediaTotal.setTextContent("" + mediaClase);
        notas.appendChild(mediaTotal);

        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.transform(new DOMSource(resultado),
                new StreamResult(new File(ruta + "notasmedias.xml")));

        System.out.println("notasmedias.xml creado correctamente ");
    }
}