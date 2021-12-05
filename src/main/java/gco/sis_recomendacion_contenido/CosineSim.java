package gco.sis_recomendacion_contenido;

import java.util.List;

/**
 *
 * @author Saúl
 */
public class CosineSim {
  
  public static double cosineSim(double[] doc1, double[] doc2) {
    double scalarProduct = 0;
    double module1 = 0;
    double module2 = 0;
    for(int i = 0; i < doc1.length; i++) {
      scalarProduct += doc1[i] * doc2[i];
      module1 += doc1[i] * doc1[i];
      module2 += doc2[i] * doc2[i];
    }
    module1 = Math.sqrt(module1);
    module2 = Math.sqrt(module2);
    return scalarProduct/(module1 * module2);
  }
  
  public static void printCosineSim(double[][] tfIdf) { 
    for (int doc1 = 0; doc1 < tfIdf.length; doc1++) {
      for (int doc2 = doc1 + 1; doc2 < tfIdf.length; doc2++) {
        double sim = cosineSim(tfIdf[doc1], tfIdf[doc2]);
        System.out.println("Similitud entre doc" + doc1 + " y doc " + doc2 + ": " + sim);
      }
    }
    
  }
}
