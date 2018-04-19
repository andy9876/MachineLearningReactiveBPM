import java.io.*;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.*;

public class main {
  private static String modelClassName = "gbm_a65d8149_cfdc_4f33_bead_5d9456e4a93b";

  public static void main(String[] args) throws Exception {
    hex.genmodel.GenModel rawModel;
    rawModel = (hex.genmodel.GenModel) Class.forName(modelClassName).newInstance();
    EasyPredictModelWrapper model = new EasyPredictModelWrapper(rawModel);

    RowData row = new RowData();
  
    //nonfraud transaction
    row.put("Time", "472");
    row.put("V1", "-3.043540624");
    row.put("V2", "-3.157307121");
    row.put("V3", "1.08846278");
    row.put("V4", "2.288643618");
    row.put("V5", "1.35980513");
    row.put("V6", "-1.064822523");
    row.put("V7", "0.325574266");
    row.put("V8", "-0.067793653");
    row.put("V9", "-0.270952836");
    row.put("V10", "-0.838586565");
    row.put("V11", "-0.414575448");
    row.put("V12", "-0.50314086");
    row.put("V13", "0.676501545");
    row.put("V14", "-1.692028933");
    row.put("V15", "2.000634839");
    row.put("V16", "0.666779696");
    row.put("V17", "0.599717414");
    row.put("V18", "1.725321007");
    row.put("V19", "0.28334483");
    row.put("V20", "2.102338793");
    row.put("V21", "0.661695925");
    row.put("V22", "0.435477209");
    row.put("V23", "1.375965743");
    row.put("V24", "-0.293803153");
    row.put("V25", "0.279798032");
    row.put("V26", "-0.145361715");
    row.put("V27", "-0.252773123");
    row.put("V28", "0.035764225");
    row.put("Amount", "529");

	//fraud transaction															
    /*row.put("Time", "7891");
    row.put("V1", "-1.585505367");
    row.put("V2", "-3.261584548");
    row.put("V3", "-4.137421983");
    row.put("V4", "2.357096252");
    row.put("V5", "-1.405043314");
    row.put("V6", "-1.879437193");
    row.put("V7", "-3.513686871");
    row.put("V8", "1.515606746");
    row.put("V9", "-1.207166361");
    row.put("V10", "-6.234561332");
    row.put("V11", "5.450746067");
    row.put("V12", "-7.333714067");
    row.put("V13", "1.361193324");
    row.put("V14", "-6.608068252");
    row.put("V15", "-0.481069425");
    row.put("V16", "-2.60247787");
    row.put("V17", "-4.835112052");
    row.put("V18", "-0.553026089");
    row.put("V19", "0.351948943");
    row.put("V20", "0.315957259");
    row.put("V21", "0.501543149");
    row.put("V22", "-0.546868812");
    row.put("V23", "-0.076583636");
    row.put("V24", "-0.425550367");
    row.put("V25", "0.123644186");
    row.put("V26", "0.321984539");
    row.put("V27", "0.264028161");
    row.put("V28", "0.13281672");
    row.put("Amount", "1");
*/
    
    BinomialModelPrediction p = model.predictBinomial(row);
    System.out.println("Label (aka prediction) is fraudulent transaction: " + p.label);
    System.out.print("Class probabilities: ");
    for (int i = 0; i < p.classProbabilities.length; i++) {
      if (i > 0) {
        System.out.print(",");
      }
      System.out.print(p.classProbabilities[i]);
    }
    System.out.println("");
  }
}