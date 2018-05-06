package com.decisioning;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.*;
import com.decisioning.drf_c79982d1_29c6_47bd_8950_897ba97ba737;

public class RunModelMS {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaInstance.class);	
	
	public RunModelMS() {
		// TODO Auto-generated constructor stub
	}

	//execute rules
	public String execute(String line, String previousState)
	{		                                   		
		String appid,time,v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,v12,v13,v14,v15,v16,v17,v18,v19,v20,v21,v22,v23,v24,v25,v26,v27,v28,amount;				       

        //parse out the variables needed from the kafka stream
        appid = line.split(":")[1].trim().split(",")[0].split("\"")[1];
        LOG.info("appid is "+appid);
        time = line.split(":")[7].trim().split(",")[0].split("\"")[1];
        LOG.info("time is "+time);
        v1 = line.split(":")[8].trim().split(",")[0].split("\"")[1];
        LOG.info("v1 is "+v1);
        v2 = line.split(":")[9].trim().split(",")[0].split("\"")[1];
        v3 = line.split(":")[10].trim().split(",")[0].split("\"")[1];
        v4 = line.split(":")[11].trim().split(",")[0].split("\"")[1];
        v5 = line.split(":")[12].trim().split(",")[0].split("\"")[1];
        v6 = line.split(":")[13].trim().split(",")[0].split("\"")[1];
        v7 = line.split(":")[14].trim().split(",")[0].split("\"")[1];
        v8 = line.split(":")[15].trim().split(",")[0].split("\"")[1];
        v9 = line.split(":")[16].trim().split(",")[0].split("\"")[1];
        v10 = line.split(":")[17].trim().split(",")[0].split("\"")[1];
        v11 = line.split(":")[18].trim().split(",")[0].split("\"")[1];
        v12 = line.split(":")[19].trim().split(",")[0].split("\"")[1];
        v13 = line.split(":")[20].trim().split(",")[0].split("\"")[1];
        v14 = line.split(":")[21].trim().split(",")[0].split("\"")[1];
        v15 = line.split(":")[22].trim().split(",")[0].split("\"")[1];
        v16 = line.split(":")[23].trim().split(",")[0].split("\"")[1];
        v17 = line.split(":")[24].trim().split(",")[0].split("\"")[1];
        v18 = line.split(":")[25].trim().split(",")[0].split("\"")[1];
        v19 = line.split(":")[26].trim().split(",")[0].split("\"")[1];
        v20 = line.split(":")[27].trim().split(",")[0].split("\"")[1];
        v21 = line.split(":")[28].trim().split(",")[0].split("\"")[1];
        v22 = line.split(":")[29].trim().split(",")[0].split("\"")[1];
        v23 = line.split(":")[30].trim().split(",")[0].split("\"")[1];
        v24 = line.split(":")[31].trim().split(",")[0].split("\"")[1];
        v25 = line.split(":")[32].trim().split(",")[0].split("\"")[1];
        v26 = line.split(":")[33].trim().split(",")[0].split("\"")[1];
        v27 = line.split(":")[34].trim().split(",")[0].split("\"")[1];
        v28 = line.split(":")[35].trim().split(",")[0].split("\"")[1];
        amount = line.split(":")[36].trim().split(",")[0].split("\"")[1];
        
		hex.genmodel.GenModel rawModel = null;
		rawModel = (hex.genmodel.GenModel) new drf_c79982d1_29c6_47bd_8950_897ba97ba737();	    
		EasyPredictModelWrapper model = new EasyPredictModelWrapper(rawModel);
	    RowData row = new RowData();
	  
	    //set the variables that will be passed to the H20 model
	    row.put("Time", time);
	    row.put("V1", v1);
	    row.put("V2", v2);
	    row.put("V3", v3);
	    row.put("V4", v4);
	    row.put("V5", v5);
	    row.put("V6", v6);
	    row.put("V7", v7);
	    row.put("V8", v8);
	    row.put("V9", v9);
	    row.put("V10", v10);
	    row.put("V11", v11);
	    row.put("V12", v12);
	    row.put("V13", v13);
	    row.put("V14", v14);
	    row.put("V15", v15);
	    row.put("V16", v16);
	    row.put("V17", v17);
	    row.put("V18", v18);
	    row.put("V19", v19);
	    row.put("V20", v20);
	    row.put("V21", v21);
	    row.put("V22", v22);
	    row.put("V23", v23);
	    row.put("V24", v24);
	    row.put("V25", v25);
	    row.put("V26", v26);
	    row.put("V27", v27);
	    row.put("V28", v28);
	    row.put("Amount", amount);		
	    
	    //invoke the model
	    BinomialModelPrediction p = null;
		try {
			p = model.predictBinomial(row);
		} catch (PredictException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		//Label (aka prediction) is fraudulent transaction: 0
		//Class probabilities: 0.999762092129436,2.3790787056395188E-4
		
		Date date = new Date();
		long currentTime = date.getTime();
		Timestamp ts = new Timestamp(currentTime);		
		
			//if the H2o model thinks its fraud, return Fraudulent Transaction
	  		if (p.label.equalsIgnoreCase("1"))
	    		line = "{\"id\":\"" + appid + "\",\"action\": \"Fraudulent Transaction\",\"data\": {\"timestamp\": \"" + ts + "\"},\"p.label\":\"" + p.label + "\",\"p.classProbability\":\"," + p.classProbabilities[0] + "\"}";
	  		else  //if h2o model does not think its fraud, return Transaction OK
	  			line = "{\"id\":\"" + appid + "\",\"action\": \"Transaction OK\",\"data\": {\"timestamp\": \"" + ts + "\"},\"p.label\":\"" + p.label + "\",\"p.classProbability\":\"," + p.classProbabilities[0] + "\"}";	  			
	  		return line;
	}
}
