package gco.sis_recomendacion_contenido;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Saúl
 */
public class ContentRecommendation {
  public static List<String> readDocuments(String filename) {
    // Fichero del que queremos leer
    File matriz_entrada = new File (filename);
    Scanner s = null;
    LinkedList<String> documents = new LinkedList<String> ();
    
    try {
      // Leemos el contenido del fichero
      //System.out.println("\nLeyendo el contenido del fichero ...\n");
      s = new Scanner(matriz_entrada);

      // Leemos linea a linea el fichero
      while (s.hasNextLine()) {
        String doc = s.nextLine(); 	// Guardamos la linea en un String
        documents.add(doc); 
      }
    } catch (Exception ex) {
        System.out.println("Mensaje: " + ex.getMessage());
    }  finally {
      // Cerramos el fichero tanto si la lectura ha sido correcta o no
        try {
          if (s != null)
            s.close();
        } catch (Exception ex2) {
            System.out.println("Mensaje 2: " + ex2.getMessage());
        }
    }
    return documents;
  }
    
  public static void main(String[] args) {
    if(args.length != 1){
      System.out.println("Número erróneo de argumentos");
      System.exit(1);
    }
    List<String> documents = readDocuments(args[0]);
  }
}


