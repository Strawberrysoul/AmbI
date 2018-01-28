package igd.fraunhofer.pocket;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.lazy.KStar;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by Malte Lenhart on 02.08.17.
 *
 * Gets data and returns position
 */

class Classifier {
    private weka.classifiers.AbstractClassifier mClassifier = null;
    private Context context;

    public Classifier(Context mContext, int pos){
        AssetManager assetManager = mContext.getAssets();
        try {
            Log.d("Classifier ", "Loading model...");
            switch(pos) {
                case 0:
                    mClassifier = (SMO) weka.core.SerializationHelper.read(assetManager.open("Rot_SVM.model"));
                    break;
                case 1:
                    mClassifier = (SMO) weka.core.SerializationHelper.read(assetManager.open("Lila_SVM.model"));
                    break;
                case 2:
                    mClassifier = (SMO) weka.core.SerializationHelper.read(assetManager.open("Gruen_SVM.model"));
                    break;
                case 3:
                    mClassifier = (NaiveBayes) weka.core.SerializationHelper.read(assetManager.open("Blau_AdaBoost.model"));
                    break;
            }

            Log.d("Classifier ", "Loading model...DONE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO
    Position processValues(String message) {
        Position position = new Position(PositionOptions.STANDING);

        int res = Integer.parseInt(message.split(",")[0]);

       /* if (res < 40000){
            position.setPosition(PositionOptions.SITTING);
        }else if(res < 42000){
            position.setPosition(PositionOptions.PROXIMITY);
        } else if (res < 44000){
            position.setPosition(PositionOptions.STANDING);
        } */
        return position;
    }

    Position classifyValues(String message){
        Position position = new Position(PositionOptions.STANDING);
        double[] sensorReadings = new double[8];
        String[] split = message.split(",");

        if (split.length <8) {
            return position;
        }
        sensorReadings[0]= Integer.parseInt(split[0]);
        sensorReadings[1]= Integer.parseInt(split[1]);
        sensorReadings[2]= Integer.parseInt(split[2]);
        sensorReadings[3]= Integer.parseInt(split[3]);
        sensorReadings[4]= Integer.parseInt(split[4]);
        sensorReadings[5]= Integer.parseInt(split[5]);
        sensorReadings[6]= Integer.parseInt(split[6]);
        sensorReadings[7]= Integer.parseInt(split[7]);

        Log.d("Sensorreadings",""+sensorReadings[0]+", "+sensorReadings[1]+", "+sensorReadings[2]+", "+sensorReadings[3]);

        //Create variables of type Attribute
        final Attribute attr_sensor1 = new Attribute("Patch1");
        final Attribute attr_sensor2 = new Attribute("Patch2");
        final Attribute attr_sensor3 = new Attribute("Patch3");
        final Attribute attr_sensor4 = new Attribute("NoVal1");
        final Attribute attr_sensor5 = new Attribute("Patch5");
        final Attribute attr_sensor6 = new Attribute("NoVal2");
        final Attribute attr_sensor7 = new Attribute("Patch7");
        final Attribute attr_sensor8 = new Attribute("NoVal3");

        //ArrayList for classes
        final List<String> classes = new ArrayList<String>() {
            {
                add(PositionOptions.STANDING.toString());
                add(PositionOptions.SITTING.toString());
                add(PositionOptions.LEFTTABLE.toString());
                add(PositionOptions.RIGHTTABLE.toString());
                add(PositionOptions.BACK.toString());
                add(PositionOptions.FRONT.toString());
                add(PositionOptions.LEFTKNEE.toString());
                add(PositionOptions.RIGHTKNEE.toString());
            }
        };

        //AttributeList for attributes
        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(80) {
            {
                add(attr_sensor1);
                add(attr_sensor2);
                add(attr_sensor3);
                add(attr_sensor4);
                add(attr_sensor5);
                add(attr_sensor6);
                add(attr_sensor7);
                add(attr_sensor8);
                //Add class as an attribute
                Attribute attributeClass = new Attribute("class", classes);
                add(attributeClass);
            }
        };
        Instance instance = new DenseInstance(1.0, sensorReadings);   //Create an denseInstance
        Instances dataUnpredicted = new Instances("SmarteSitzauflage", attributeList, 1);   //Sets the header

        instance.setDataset(dataUnpredicted);   //associates instance to the dataset
        dataUnpredicted.add(instance);          //add evey new instance to dataset
        dataUnpredicted.setClassIndex(dataUnpredicted.numAttributes() - 1);

        Log.d("Classifier ", "Prepared classification...");
        double prediction = 1;      //Prediction in double//Prediction in double
        try {

            prediction = mClassifier.classifyInstance(instance);
            Log.d("prediction", ""+prediction);
            Log.d("Classifier ", "Classification...DONE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String className = classes.get(new Double(prediction).intValue());                     //Prediction in String

        Log.d("Predicted:  ", className);
        switch (className){
            case "STANDING": position = new Position(PositionOptions.STANDING); break;
            case "LEFTTABLE": position = new Position(PositionOptions.LEFTTABLE); break;
            case "RIGHTTABLE": position = new Position(PositionOptions.RIGHTTABLE); break;
            case "LEFTKNEE": position = new Position(PositionOptions.LEFTKNEE); break;
            case "RIGHTKNEE": position = new Position(PositionOptions.RIGHTKNEE); break;
            case "SITTING": position = new Position(PositionOptions.SITTING); break;
            case "BACK": position = new Position(PositionOptions.BACK); break;
            case "FRONT": position = new Position(PositionOptions.FRONT); break;
            default: position = new Position(PositionOptions.STANDING);
        }
        return position;
    }

}
