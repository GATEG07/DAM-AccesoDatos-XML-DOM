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

public class BolsaActualizar {

    public static void main(String[] args) throws Exception {

        String ruta = "src/Ejercicio4/";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document bolsa = builder.parse(new File(ruta + "Bolsa.xml"));
        Document datos = builder.parse(new File(ruta + "datos.xml"));

        NodeList operaciones = datos.getElementsByTagName("Operacion");

        for (int i = 0; i < operaciones.getLength(); i++) {

            Element operacion = (Element) operaciones.item(i);
            String tipo = operacion.getAttribute("id");

            String nombre = operacion.getElementsByTagName("Nombre")
                    .item(0).getTextContent();

            NodeList empresas = bolsa.getElementsByTagName("Empresa");

            Element encontrada = null;

            for (int j = 0; j < empresas.getLength(); j++) {

                Element empresa = (Element) empresas.item(j);

                String nombreEmpresa = empresa.getElementsByTagName("Nombre")
                        .item(0).getTextContent();

                if (nombreEmpresa.equalsIgnoreCase(nombre)) {
                    encontrada = empresa;
                    break;
                }
            }

            if (tipo.equals("A")) {

                if (encontrada != null) {
                    System.out.println("Error: ya existe " + nombre);
                } else {

                    String indice = operacion.getElementsByTagName("Indice")
                            .item(0).getTextContent();

                    String simbolo = operacion.getElementsByTagName("Simbolo")
                            .item(0).getTextContent();

                    String precio = operacion.getElementsByTagName("Precio")
                            .item(0).getTextContent();

                    Element empresa = bolsa.createElement("Empresa");
                    empresa.setAttribute("indice", indice);
                    bolsa.getDocumentElement().appendChild(empresa);

                    Element nombreNuevo = bolsa.createElement("Nombre");
                    nombreNuevo.setTextContent(nombre);
                    empresa.appendChild(nombreNuevo);

                    Element simboloNuevo = bolsa.createElement("Simbolo");
                    simboloNuevo.setTextContent(simbolo);
                    empresa.appendChild(simboloNuevo);

                    Element precioNuevo = bolsa.createElement("Precio");
                    precioNuevo.setTextContent(precio);
                    empresa.appendChild(precioNuevo);

                    System.out.println("Alta: " + nombre);
                }

            } else if (tipo.equals("B")) {

                if (encontrada == null) {
                    System.out.println("Error: no existe " + nombre);
                } else {
                    bolsa.getDocumentElement().removeChild(encontrada);
                    System.out.println("Baja: " + nombre);
                }

            } else if (tipo.equals("M")) {

                if (encontrada == null) {
                    System.out.println("Error: no existe " + nombre);
                } else {

                    String nuevoPrecio = operacion.getElementsByTagName("Precio")
                            .item(0).getTextContent();

                    Element precio = (Element) encontrada
                            .getElementsByTagName("Precio").item(0);

                    precio.setTextContent(nuevoPrecio);

                    System.out.println("Precio modificado: " + nombre);
                }
            }
        }

        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

        transformer.transform(new DOMSource(bolsa),
                new StreamResult(new File(ruta + "bolsaactualizada.xml")));

        System.out.println("bolsaactualizada.xml creado correctamente ");
    }
}