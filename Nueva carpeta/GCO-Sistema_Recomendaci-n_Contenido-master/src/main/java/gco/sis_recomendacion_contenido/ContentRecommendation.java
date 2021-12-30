package gco.sis_recomendacion_contenido;

import static gco.sis_recomendacion_contenido.CosineSim.printCosineSim;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;

/**
 *
 * @author Saúl
 */
public class ContentRecommendation {
  
  // Función para leer el fichero. Toma cada línea como un documento distinto
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
  
  // Clase auxiliar para representar una tupla de datos
  static class Tuple<T1, T2, T3>{
    public T1 e1;   // índice
    public T2 e2;   // término
    public T3 e3;   // valor TF
  }
  
  // Función que devuelve una tupla de datos siendo e1 el índice, e2 el término y e3 el valor del TF 
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
  
  // Función para el cálculo del IDF
  public static List<Double> idf(Tuple<List<List<String>>, List<String>, List<Map<Integer, Double>>> t) {
    final int N = t.e1.size();
    List<Double> idf = new LinkedList<Double>();
    for (String word : t.e2) {
      int index = t.e2.indexOf(word);
      int dfx = 0;
      for (Map<Integer, Double> freq : t.e3) {
        if(freq.getOrDefault(index, 0.) > 0) {
          dfx++;
        }
      }
      double idfx = Math.log10(N/(dfx+0.0));
      idf.add(idfx);
      //System.out.println("dfx:" + dfx);
    }

    return idf;
  }
  
  // Función para el cálculo del TF-IDF
  public static double[][] tfIdf(List<Map<Integer, Double>> tf, List<Double> idf, int uniqueTerms) {
    int numDocs = tf.size();
    double[][] tfIdf = new double[numDocs][uniqueTerms];
    for(int doc = 0; doc < numDocs; doc++) {
      for(int term = 0; term < uniqueTerms; term++) {
        tfIdf[doc][term] = tf.get(doc).getOrDefault(term, 0.) * idf.get(term);
      }
    }
    return tfIdf;
  }
  
  // Función para imprimir los resultados en forma de tabla
  public static void printTable(List<List<String>> docTokens, 
                                List<String> docTerms, 
                                List<Map<Integer, Double>> tf, 
                                List<Double> idf, double[][] tfIdf) {
    
    for(int doc = 0; doc < docTokens.size(); doc++) {
      System.out.println("|  " +  String.format("%5s","ID") + "   |" + " |   " +  String.format("%15s","Término") + "   |" + "|" + String.format("%7s","TF") + "     |" + "|" + String.format("%7s","IDF") + "     |" + "| " + String.format("%8s","TF-IDF") + "   |");
      var tokens = docTokens.get(doc);
      List<String> checker = new LinkedList<>();
      for (String token : tokens) {
        if(checker.indexOf(token) == -1) {
          checker.add(token);
          int index = docTerms.indexOf(token);
          double tfx = tf.get(doc).getOrDefault(index, 0.);
          double idfx = idf.get(index);
          double tfIdfx = tfIdf[doc][index];
          System.out.println("|  " + String.format("%5s",index) + "   | " + "|   " + String.format("%15s",token) + "   |" + "|   " + String.format("%.4f",tfx) +  "   |" + "|   " + String.format("%.4f",idfx) +  "   |" + "|   " + String.format("%.4f",tfIdfx) +"   |");
        }
      }
      System.out.println("\n");
    }
  }
  
  /**********************
   * Programa Principal *
  ***********************/
  
  public static void main(String[] args) {
    if(args.length != 1){
      System.out.println("\n*********************************************");
      System.out.println("Número erróneo de argumentos");
      System.out.print("Se requiere que se indique el nombre del fichero.\n");
      System.out.print("Ejemplo de uso:");
      System.out.print("\n\tdocuments-01.txt\n");
      System.out.println("*********************************************\n");
      System.exit(1);
    }
    List<String> documents = readDocuments(args[0]);
    System.out.println("\n---------------------------------------------------");
    System.out.println("Resultado del Sistema de Recomentación de Contenido");
    System.out.println("---------------------------------------------------\n");
    Tuple<List<List<String>>, List<String>, List<Map<Integer, Double>>> t = tfTokenizeDocuments(documents);
    List<Double> idf = idf(t);
    double[][] tfIdf = tfIdf(t.e3, idf, t.e2.size());
    printTable(t.e1, t.e2,t.e3, idf, tfIdf);
    printCosineSim(tfIdf);
  }
}


