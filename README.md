
# MachineLearningReactiveBPM

This repo contains the code for the POC that is part of RedHat Session S1506:  Using machine learning, Red Hat JBoss BPM Suite, and reactive microservices

# BPM Suite Dockerfile and setup files
Dockerfile - This file can be used to build a docker imaging of BPM Suite.
For it to work, you need to create 3 directories:  patch, installers, and support.
It currently installs BPM Suite 6.3 and EAP 6.4.8.
You will need to download the binaries from the RedHat site and put them into the appropriate folders.

* Download the BPM Suite and EAP installers and upload them to the installers directory.
   * jboss-bpmsuite-6.3.0.GA-installer.jar
   * jboss-eap-6.4.0-installer.jar
* Download the patch for EAP 6.4.8 and upload this to the patch directory.
   * jboss-eap-6.4.8-patch.zip
* Download these files in the support folder:
    * eap.xml
    * eap.xml.variables
    * bpms.xml
    * bpms.xml.variables

After building the image, use the below command to run the docker container
```docker run -p 8080:8080 -p 8001:8001 -d bpmsuite```
Calc 
 f# Kie Server Kafka Extension
You can find David Murphy's kie server kafka extension at the below repo
https://gitlab.com/murph83/kie-server-kafka.git
The modified version used for the demo can be found here https://github.com/andy9876/kie-server-kafka


# BPM Suite project
This contains the source code for the bpm suite fraud example 

# RunModelMS
This is a java based microservice that invokes a H2o model.  The H2o model takes the follow parameters:  time, 28 various numerical inputs, and transaction amount.  It will return a p.label value for 1 if fraudulent or 0 for not fraudulent.  Below are some sample events that can be placed on the card.transaction topic for it to trigger from a unit testing perspective:

Fraudulent transaction
  ```CardKafka:2,{"id":"33bb75db-6e13-48ee-8a54-b3976d3d065b","action": "Features Calculated","data": {"timestamp": "2016-06-10T14:33:29.102-00:00", "time": "7891", "v1": "-1.585505367", "v2": "-3.261584548","v3": "-4.137421983","v4": "2.357096252","v5": "-1.405043314","v6": "-1.879437193","v7": "-3.513686871","v8": "1.515606746","v9": "-1.207166361","v10": "-6.234561332","v11": "5.450746067","v12": "-7.333714067","v13": "1.361193324","v14": "-6.608068252","v15": "-0.481069425","v16": "-2.60247787","v17": "-4.835112052","v18": "-0.553026089","v19": "0.351948943","v20": "0.315957259","v21": "0.501543149","v22": "-0.546868812","v23": "-0.076583636","v24": "-0.425550367","v25": "0.123644186","v26": "0.321984539","v27": "0.264028161","v28": "0.13281672","amount": "1"}}}```
  
Non-Fraudulent transaction
  ```CardKafka:2,{"id":"33bb75db-6e13-48ee-8a54-b3976d3d065b","action": "Features Calculated","data": {"timestamp": "2016-06-10T14:33:29.102-00:00", "time": "472", "v1": "-3.043540624", "v2": "-3.157307121","v3": "1.08846278","v4": "2.288643618","v5": "1.35980513","v6": "-1.064822523","v7": "0.325574266","v8": "-0.067793653","v9": "-0.270952836","v10": "-0.838586565","v11": "-0.414575448","v12": "-0.50314086","v13": "0.676501545","v14": "-1.692028933","v15": "2.000634839","v16": "0.666779696","v17": "0.599717414","v18": "1.725321007","v19": "0.28334483","v20": "2.102338793","v21": "0.661695925","v22": "0.435477209","v23": "1.375965743","v24": "-0.293803153","v25": "0.279798032","v26": "-0.145361715","v27": "-0.252773123","v28": "0.035764225","amount": "529"}}}```

* NOTE - if unit testing with the above message, YOU MUST PASS THE KAFKA key!! Otherwise the consumer will error.
Example unit test using kafka console producer to pass the key:
```./kafka-console-producer.sh --topic card.transaction --broker-list 18.204.180.80:9092 --property "parse.key=true" --property "key.separator=:"```

Leverage the Dockerfile to build the container.
To run the docker container:

  ```docker run -d runmodelms:latest```

# CalcFeaturesMS
This is a java based microservice that simulates calculating the 28 features needed by the H20 model.  It triggers on the action "Calc Features" and randomly returns a pre-baked set of 28 features that are known to either generate a fraud or non-fraud response from the model.  

Leverage the Dockerfile to build the container.
To run the docker container:

  ```docker run -d calcfeaturesms:latest```

*NOTE - make sure the kafka consumer group for CalcFeaturesMS and RunModelMS is different, otherwise only one of the services will be able to read the messages.

# Kafka and Zookeeper Dockerfiles
The included Dockerfiles require kafka_2.10-0.10.2.0.gz, you can download this off of https://kafka.apache.org/downloads

To run these containers:

  ```docker run -p 9092:9092 --entrypoint /opt/kafka_2.10-0.10.2.0.gz/kafka_2.10-0.10.2.0/bin/kafka-server-start.sh -d --name kafka kafka:latest /opt/kafka_2.10-0.10.2.0.gz/kafka_2.10-0.10.2.0/config/server.properties --override zookeeper.connect=<zookeeperIP>:2181 --override advertised.listeners=PLAINTEXT://<kafkaIP>:9092 --override advertised.host.name=<kafkaIP> --override advertised.port=9092```
  
  ```docker run -p 2181:2181 --name zookeeper -d zookeeper:latest```

# H2o components
* gbm_a65d8149_cfdc_4f33_bead_5d9456e4a93b.java - H2o generated pojo for model
* creditcard.csv - training dataset not in the repo due to size, but can be found here https://www.kaggle.com/mlg-ulb/creditcardfraud/downloads/creditcardfraud.zip/3
* Credit Card Fraud demo.flow - H2o flow notebook that shows the steps taken to build the model
* If you want to run the H20 model by itself , use these components
  * main.java - wrapper class that invokes the H2o model
  * h2o-genmodel.jar - shared library that contains all necessary dependencies for running model
    * To build:
      ```javac -cp h2o-genmodel.jar -J-Xmx2g -J-XX:MaxPermSize=128m gbm_a65d8149_cfdc_4f33_bead_5d9456e4a93b.java main.java```
    * To run:  
      ```java -cp .;h2o-genmodel.jar main```


