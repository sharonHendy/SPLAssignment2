package bgu.spl.mics.application;

//import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        LinkedList<Student> studentList=null;
        LinkedList<GPU> GPUS=null;
        LinkedList<CPU> CPUS=null;
        LinkedList<ConferenceInformation> conferenceInformations=null;
        int tickTime=0;
        int duration=0;
        Cluster cluster= Cluster.getInstance();
        try{
            Object obj= new JSONParser().parse(new FileReader(args[0])); //TODO
            JSONObject jsonObject= (JSONObject) obj;
            JSONArray Students =(JSONArray) jsonObject.get("Students");
            for(int i=0; i<Students.size(); i++){
                JSONObject student= (JSONObject)Students.get(i);
                String StudentName= (String)student.get("name");
                String department= (String)student.get("department");
                Student.Degree status= (Student.Degree)student.get("status");
                ArrayList<Model> models=null;
                JSONArray JSONmodels =(JSONArray) student.get("models");
                for(int j=0; j<JSONmodels.size(); j++){
                    JSONObject model= (JSONObject)JSONmodels.get(i);
                    String ModelName= (String)model.get("name");
                    Data.Type type= (Data.Type)model.get("type");
                    int size= (int)student.get("size");
                    models.add(new Model(ModelName, new Data(type,size)));
                }
                Student s= new Student(StudentName, department,status, models);
                for(Model m: s.getModels()){
                    m.setStudent(s);
                }
                studentList.add(s);
            }
            JSONArray JSONGPUS =(JSONArray) jsonObject.get("GPUS");
            for(int i=0; i<JSONGPUS.size();i++){
                GPU.Type type= (GPU.Type) JSONGPUS.get(i);
                GPUS.add(new GPU(type,cluster));
            }
            JSONArray JSONCPUS =(JSONArray) jsonObject.get("CPUS");
            for(int i=0; i<JSONCPUS.size();i++){
                int cores= (int) JSONCPUS.get(i);
                CPUS.add(new CPU(cores,cluster));
            }
            JSONArray Conferences =(JSONArray) jsonObject.get("Conferences");
            for(int i=0; i<Conferences.size();i++){
                JSONObject conference =(JSONObject) Conferences.get(i);
                String name= (String) conference.get("name");
                int date= (int) conference.get("date");
                conferenceInformations.add(new ConferenceInformation(name,date));
            }
            tickTime= (int) jsonObject.get("TickTime");
            duration= (int) jsonObject.get("Duration");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cluster.addCPUSandGPUS(GPUS,CPUS);
        ExecutorService executorService= Executors.newCachedThreadPool();
        for(CPU cpu: CPUS){
            String name="CPUService " +cpu.getCores();
            executorService.execute(new CPUService(name,cpu));
        }
        for(GPU gpu: GPUS){
            String name="GPUService " +gpu.getType();
            executorService.execute(new GPUService(name,gpu));
        }
        for(ConferenceInformation conference: conferenceInformations ){
            String name= "ConferenceInformation"+ conference.getName();
            executorService.execute(new Thread(new ConferenceService(name, conference.getDate(),tickTime)));
        }
        for(Student s:studentList){
            String name= "StudentService "+ s.getName();
            executorService.execute(new StudentService(name, s));
        }
        executorService.execute(new TimeService(tickTime,duration));
        executorService.shutdown();

        JSONObject main= new JSONObject();
        JSONArray s= new JSONArray();
        Map m= new LinkedHashMap(studentList.size());
        for(Student st: studentList) {
            m.put("name", st.getName());
            m.put("department", st.getDepartment());
            m.put("status", st.getStatus());
            m.put("publications", st.getPublications());
            m.put("papersRead", st.getPapersRead());
            JSONArray train = new JSONArray();
            Map m2 = new LinkedHashMap(studentList.size()); //check
            for (Model mo : st.getModels()) {
                if (mo.getStatus() == Model.Status.Trained || mo.getStatus() == Model.Status.Tested) {
                    m2.put("name", mo.getName());
                    JSONObject d = new JSONObject();
                    d.put("type", mo.getData().getType());
                    d.put("size", mo.getData().getSize());
                    m2.put("data", d);
                    m2.put("status", mo.getStatus());
                    m2.put("results", mo.getResult());
                }
            }
            m.put("trainedModels", m2);
        }
        s.add(m);
        main.put("students",s);
        JSONArray c= new JSONArray();
        Map m3= new LinkedHashMap(conferenceInformations.size());
        for(ConferenceInformation conf :conferenceInformations){
            m3.put("name",conf.getName());
            m3.put("date",conf.getDate());
            JSONArray pub= new JSONArray();
            Map m4= new LinkedHashMap(conf.getModels().size());
            for(Model mod: conf.getModels()){
                m4.put("name",mod.getName());
                JSONObject d= new JSONObject();
                d.put("type",mod.getData().getType() );
                d.put("size", mod.getData().getSize());
                m4.put("data",d);
                m4.put("status", mod.getStatus());
                m4.put("results",mod.getResult());
            }
            m3.put("publications",m4);
        }
        c.add(m3);
        main.put("conferences",c);
        main.put("cpuTimeUsed",cluster.getCPUTimeUnitsUsed());
        main.put("gpuTimeUsed", cluster.getGPUTimeUnitsUsed());
        main.put("batchesProcessed", cluster.getNumOfProcessedByCPU());
        PrintWriter pw= new PrintWriter("ouptput2.json");
        pw.write(main.toJSONString());
        pw.flush();
        pw.close();
    }

}
