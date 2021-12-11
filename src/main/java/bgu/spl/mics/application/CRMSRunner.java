package bgu.spl.mics.application;

//import jdk.nashorn.internal.parser.JSONParser;

import java.io.FileReader;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        JSONParser praser= new JSONParser();
        try{
            Object obj= new JSONParser().parse(new FileReader("example_input.json"));
            JSONObject jsonObject= (JSONObject) obj;

        }



        

    }
}
