package igd.fraunhofer.pocket;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.J48;
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

    public Classifier(Context mContext){
        AssetManager assetManager = mContext.getAssets();
        try {
            Log.d("Classifier ", "Loading model...");
            mClassifier = (J48) weka.core.SerializationHelper.read(assetManager.open("result_modell.model"));
            Log.d("Classifier ", "Loading model...DONE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO
    Position processValues(String message) {
        Position position = new Position(PositionOptions.STANDING);

        int res = Integer.parseInt(message.split(",")[0]);

        if (res < 40000){
            position.setPosition(PositionOptions.SITTING);
        }else if(res < 42000){
            position.setPosition(PositionOptions.PROXIMITY);
        } else if (res < 44000){
            position.setPosition(PositionOptions.STANDING);
        }
        return position;
    }

    Position classifyValues(String message){
        Position position = new Position(PositionOptions.STANDING);
        double[] sensorReadings = new double[2];
        sensorReadings[0]= Integer.parseInt(message.split(",")[0]);
        sensorReadings[1] = Integer.parseInt(message.split(",")[1]);
        //Log.d("Sensorreadings",""+sensorReadings[0]+", "+sensorReadings[1]);

        //Create variables of type Attribute
        final Attribute attr_sensor1 = new Attribute("sensor1");
        final Attribute attr_sensor2 = new Attribute("sensor2");

        //ArrayList for classes
        final List<String> classes = new ArrayList<String>() {
            {
//                add(PositionOptions.STANDING.toString());
//                add(PositionOptions.SITTING.toString());
                add(PositionOptions.LEFTKNEE.toString());
                add(PositionOptions.RIGHTKNEE.toString());
                add(PositionOptions.LEFTTABLE.toString());
                add(PositionOptions.RIGHTTABLE.toString());
                add(PositionOptions.FRONT.toString());
                add(PositionOptions.BACK.toString());
            }
        };

        //AttributeList for attributes
        ArrayList<Attribute> attributeList = new ArrayList<Attribute>(20) {
            {
                add(attr_sensor1);
                add(attr_sensor2);
                //Add class as an attribute
                Attribute attributeClass = new Attribute("class", classes);
                add(attributeClass);
            }
        };
        Instance instance = new DenseInstance(1.0, sensorReadings);   //Create an denseInstance
        Instances dataUnpredicted = new Instances("SmartPocket", attributeList, 1);   //Sets the header

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
            default: position = new Position(PositionOptions.PROXIMITY);
        }
        return position;
    }

}
