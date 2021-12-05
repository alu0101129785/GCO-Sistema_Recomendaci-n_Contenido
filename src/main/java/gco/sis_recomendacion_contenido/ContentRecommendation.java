package gco.sis_recomendacion_contenido;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
  static class Tuple<T1, T2, T3>{
    public T1 e1;
    public T2 e2;
    public T3 e3;
  }
  
  public static Tuple<List<List<String>>, List<String>, List<Map<Integer, Double>>> tfTokenizeDocuments(List<String> documents) {
    List<List<String>> result = new LinkedList<>();
    Pattern p = Pattern.compile("\\b[\\w'-]+\\b");
    LinkedList<String> terms = new LinkedList<String>();
    List<Map<Integer, Double>> tf = new LinkedList<>();
    for (String document : documents) {
      LinkedList<String> words = new LinkedList<String>();
      HashMap<Integer, Integer> count = new HashMap<Integer, Integer>();
      Matcher matcher = p.matcher(document);
      int numberterms = 0;
      while(matcher.find()) {
        String token = matcher.group().toLowerCase();
        if(terms.indexOf(token) == -1) {
          terms.add(token);
        }
        int index = terms.indexOf(token);
        count.put(index, count.getOrDefault(index, 0) +1);
        words.add(token);
        numberterms++;
      }
       HashMap<Integer, Double> freq = new HashMap<Integer, Double>();
      for(Integer index : count.keySet()) {
        freq.put(index, count.get(index)/(numberterms+0.0));
      }
      result.add(words);
      tf.add(freq);
    }
    var t = new Tuple<List<List<String>>, List<String>, List<Map<Integer, Double>>>();
    t.e1 = result;
    t.e2 = terms;
    t.e3 = tf;
    return t;
  }
  
  public static void main(String[] args) {
    if(args.length != 1){
      System.out.println("Número erróneo de argumentos");
      System.exit(1);
    }
    List<String> documents = readDocuments(args[0]);
  }
}


