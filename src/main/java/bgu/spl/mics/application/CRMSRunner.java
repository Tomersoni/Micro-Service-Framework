package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.objects.Data.Type;
import bgu.spl.mics.application.objects.Student.Degree;
import bgu.spl.mics.application.services.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.*;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    public static CountDownLatch countDownLatch;
    public static int terminateBatchNum=0;
    public static CountDownLatch terminatedCPUsLatch;
    public static int totalThreads;

    public static void main(String[] args) throws ParseException, IOException, org.json.simple.parser.ParseException {

        System.out.println("Hello World!");

        MessageBusImpl mb= new MessageBusImpl();

        LinkedList<Thread> runningThreads = new LinkedList<>();

        JSONParser parser = new JSONParser();

        FileReader reader = new FileReader("example_input.json");

        Object obj= parser.parse(reader);

        JSONObject jObject= (JSONObject) obj;

        JSONArray Students= (JSONArray) jObject.get("Students");
        LinkedList<Student> studentsForOutput= new LinkedList<>();

        for(int i=0; i<Students.size(); i++)
        {

            JSONObject Student = (JSONObject) Students.get(i);
            String name = (String) Student.get("name");
            String department = (String) Student.get("department");
            String status = (String) Student.get("status");
            Degree d;
            if(status.equals("MSc"))
            {
                 d = Degree.MSc;
            }
            else
            {
                 d = Degree.PhD;
            }
            Student student = new Student(name,department,d);
            StudentService studentService=new StudentService(name,student);
            studentsForOutput.addLast(student);
            JSONArray Models= (JSONArray) Student.get("models");
            LinkedList<Model> models=new LinkedList<>();
            for(int j=0; j<Models.size(); j++)
            {
                JSONObject Model = (JSONObject) Models.get(j);
                String modelName = (String) Model.get("name");
                String modelType = (String) Model.get("type");
                Long longModelSize = (Long) Model.get("size");
                int modelSize = (int) longModelSize.longValue();
                Type t;
                if(modelType.equals("images"))
                {
                    t=Type.Images;
                }
                else if(modelType.equals("Text"))
                {
                    t=Type.Text;
                }
                else
                {
                    t=Type.Tabular;
                }

                Data data = new Data(t,modelSize);
                Model m = new Model(modelName,data,student);
                models.addLast(m);
            }
            student.setModels(models);
            Thread studentServiceThread = new Thread(studentService);
            studentServiceThread.setName(name);
            runningThreads.addLast(studentServiceThread);


        }



        Cluster cluster = new Cluster();

        JSONArray GPUS= (JSONArray) jObject.get("GPUS");
        for(int i=0;i< GPUS.size();i++){
            String type= (String) GPUS.get(i);
            GPU.Type t;
            String typeName;
            if(type.equals("RTX3090")) {
                t = GPU.Type.RTX3090;
                typeName="RTX3090";
            }
            else if(type.equals("RTX2080")) {
                t = GPU.Type.RTX2080;
                typeName="RTX2080";
            }
            else {
                t = GPU.Type.GTX1080;
                typeName="GTX1080";
            }
            GPU gpu=new GPU(t,cluster);
            GPUService gpuService=new GPUService("GPU", gpu);

            Thread gpuServiceThread = new Thread(gpuService);
            gpuServiceThread.setName(typeName);
            runningThreads.addLast(gpuServiceThread);
        }

        JSONArray CPUS= (JSONArray) jObject.get("CPUS");
        for(int i=0;i< CPUS.size();i++){
            Long longCore=(Long)CPUS.get(i);
            int core = (int) longCore.longValue();
            CPU cpu=new CPU(core,cluster);
            CPUService cpuService=new CPUService("CPU",cpu);
            Thread cpuServiceThread = new Thread(cpuService);
            cpuServiceThread.setName(Integer.toString(core));
            runningThreads.addLast(cpuServiceThread);
            terminateBatchNum++;
        }
        terminatedCPUsLatch= new CountDownLatch(terminateBatchNum);

        JSONArray Conferences= (JSONArray) jObject.get("Conferences");
        LinkedList<ConfrenceInformation> confrencesForOutput= new LinkedList<>();
        for(int i=0;i<Conferences.size();i++){
            JSONObject Conference = (JSONObject) Conferences.get(i);
            String name=(String) Conference.get("name");
            Long longDate = (Long) Conference.get("date");
            int date = (int) longDate.longValue();
            ConfrenceInformation con=new ConfrenceInformation(name,date);
            confrencesForOutput.addLast(con);
            ConferenceService conferenceService=new ConferenceService(name,con);
            Thread conferenceServiceThread = new Thread(conferenceService);
            conferenceServiceThread.setName("conference"+i);
            runningThreads.addLast(conferenceServiceThread);
        }

        Long longSpeed= (Long) jObject.get("TickTime");
        int speed= (int) longSpeed.longValue();
        Long longDuration= (Long) jObject.get("Duration");
        int duration = (int) longDuration.longValue();
        TimeService timeService=new TimeService(speed,duration);

        Thread timeServiceThread = new Thread(timeService);
        timeServiceThread.setName("TimeService");
        runningThreads.addLast(timeServiceThread);

        totalThreads= runningThreads.size();

        countDownLatch=new CountDownLatch(runningThreads.size());

        for(Thread curr: runningThreads){
            curr.start();
        }


        for(Thread curr: runningThreads){
            try {
                curr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //OUTPUT//
        File file=new File("output.text");
        FileWriter fw=new FileWriter(file);
        PrintWriter pw=new PrintWriter(fw);
        pw.println("{");
        String out="    \"students\": [\n";
        for(Student currentStudent: studentsForOutput){
            out= out +  currentStudent.toString()  ;

        }

        pw.println(out);
        pw.println("    ],");
        String out2="    \"conferences\": [\n        {\n";
        for(int i=0;i<confrencesForOutput.size();i++){
            ConfrenceInformation current=confrencesForOutput.get(i);
            out2=out2+ "            \"name\": \"" + current.getName() + "\",\n            \"date\":" + current.getDate()+ "\n"+
            "            \"publications\": [\n";
            for(Model currentModel: current.publishedModels){
                out2=out2+currentModel.toString()+", ";
            }
            out2 = out2+"            ]\n";
            out2=out2+"        },\n";
        }
        pw.println(out2+"    ],\n");


        pw.println("    \"cpuTimeUsed\":" + cluster.stats.CPUtime+",");
        pw.println("    \"gpuTimeUsed\":" + cluster.stats.GPUtime+",");
        pw.println("    \"batchesProcessed\":" + cluster.stats.processedBatches);
        pw.println("}");
        pw.close();
        System.out.println("Finished");
    }
}
