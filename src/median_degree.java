/*
     This Program reads in the file venmo-trans.txt calculates a rolling median based on a 60 second window from the maximum timestamp present
     and prints a graph of the transactions in order.
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class median_degree
{
  public static void main(String[] args) throws FileNotFoundException              //declares a method called main
  { 
    String inputFileName = args[0];
    String outputFileName = args[1];

    //Construct the Scanner and PrintWriter objects for reading and writing
    File inputFile = new File(inputFileName);
    Scanner in = new Scanner(inputFile);
    PrintWriter out = new PrintWriter(outputFileName);

    ArrayList<String> created_time = new ArrayList<String>();
    ArrayList<String> Actors = new ArrayList<String>();
    ArrayList<String> Targets = new ArrayList<String>();
                                                                         //Read the input and write the output
    while (in.hasNext())                                                
    {                                                                    //Reads the String "created_time"
       String timeField = in.next();                                   
       String TimeField = timeField.substring(2,timeField.length() - 2);
                                                                        //Reads in the TimeStamp
       String timeStamp = in.next();
       String TimeStamp = timeStamp.substring(1,timeStamp.length() - 2);
      
       String targetField = in.next();                                  //Reads the String "target"
       String TargetField = targetField.substring(1,targetField.length() - 2);

       String target = in.next();
       String Target = target.substring(1,target.length() - 2);        
                                                                        //Reads the String "actor"
       String actorField = in.next();
       String ActorField = actorField.substring(1,actorField.length() - 2);

       String actor = in.next();                                       //Reads the Actor Name
       String Actor = actor.substring(1,actor.length() - 2);
       
       created_time.add(timeStamp);
       Actors.add(Actor);
       Targets.add(Target);
    }
       created_time = TimeReader(created_time);                        
       ArrayList<Double> Medians = OrderTransactions(created_time, Actors, Targets);
       for(int i = 0; i < Medians.size(); i++)
         out.printf("%.2f \n", Medians.get(i));
   
    in.close();                                       //Closes Scanner File
    out.close();                                      //Closes PrintWriter File
  }
  //Reads in a TimeStamp String and stores it in a usable form in this case a string of seconds
  public static ArrayList<String> TimeReader(ArrayList<String> created_time) 
  {
    ArrayList<String> CalculatedTime = new ArrayList<String>();
    int basis = 2016 - 3;
    
    for(int i = 0; i < created_time.size(); i++)
    {
      String timeStamp = created_time.get(i);
      int year = Integer.parseInt(created_time.get(i).substring(1,5).trim());
      int month = Integer.parseInt(created_time.get(i).substring(6,8).trim());
      int day = Integer.parseInt(created_time.get(i).substring(9,11).trim());
      int hour = Integer.parseInt(created_time.get(i).substring(12,14).trim());
      int minute = Integer.parseInt(created_time.get(i).substring(15,17).trim());
      int second = Integer.parseInt(created_time.get(i).substring(18,20).trim());
  
      CalculatedTime.add(TimeProcessor(year, month, day, hour, minute, second, basis));
    }    
    return CalculatedTime;
  }

  //Processes a TimeStamp into a mathematical quantity to be used in Analysis (i.e. total seconds from a defined basis) 
  public static String TimeProcessor(int year, int month, int day, int hour, int minute, int second, int basis)
  {
    int CalculatedTime = ((basis % 4) == 0) ? 366 : 365;
  
    while(++basis < year)
      CalculatedTime = ((basis % 4) == 0) ? CalculatedTime + 366 : CalculatedTime + 365;
  
    for(int i = 1; i < month; i++)
     if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) CalculatedTime += 31;
       else if(i == 4 || i == 6 || i == 9 || i == 11) CalculatedTime += 30; 
         else if(i == 2) CalculatedTime = (year % 4) == 0 ? CalculatedTime + 29 : CalculatedTime + 28;
  
    return Integer.toString(((((((CalculatedTime + (day - 1)) * 24) + hour) * 60) + minute) * 60) + second);
  }
  
  //Organizes Transactions based on the TimeStamp
  public static ArrayList<Double> OrderTransactions(ArrayList<String> created_time, ArrayList<String> Actors, ArrayList<String> Targets)
  { 
    ArrayList<Double> Medians = new ArrayList<Double>();
    int Transaction = 1;
    while(Transaction <= created_time.size())
    {
    for(int i = 0; (i < created_time.size() - 1 && i < Transaction - 1); i++)
      for(int j = i + 1; j < created_time.size() && j < Transaction; j++)
        if(Integer.parseInt(created_time.get(i)) > Integer.parseInt(created_time.get(j)))
        {
          created_time.add(i, created_time.get(j));
          created_time.remove(j+1);
          
          Actors.add(i, Actors.get(j));
          Actors.remove(j+1);
          
          Targets.add(i, Actors.get(j));
          Targets.remove(j+1);
       }
      Medians.add(ScaledTransactions(created_time, Actors, Targets, Transaction));
      Transaction++;
    }
    return Medians;
  }
  //Eliminates Transactions outside of the 60 second window
  public static Double ScaledTransactions(ArrayList<String> created_time, ArrayList<String> Actors, ArrayList<String> Targets, int Transaction)
  {
    int i = 0;
    while(i < Transaction)
     if(Integer.parseInt(created_time.get(i)) >= (Integer.parseInt(created_time.get(Transaction - 1)) - 60))
     {
       Actors.get(i);
       Targets.get(i);
       i++;
     }
     else if(Integer.parseInt(created_time.get(i)) < (Integer.parseInt(created_time.get(Transaction - 1)) - 60))
     {
     created_time.remove(i);
     Actors.remove(i);
     Targets.remove(i);
     }
   return Analyzer(Actors, Targets, Transaction);
  }
  //Generates Graph and Calculates Degrees to compute Rolling Median
  public static Double Analyzer(ArrayList<String> actors, ArrayList<String> targets, int Transaction)
  {
   ArrayList<String>  UniqueName = new ArrayList<String>();
   ArrayList<Integer> Degrees = new ArrayList<Integer>();
   
   UniqueName.add(actors.get(0)); Degrees.add(1);
   UniqueName.add(targets.get(0)); Degrees.add(1);
  
   int transaction = 1;
   int totaltransactions = Transaction;
   while(++transaction <= totaltransactions)
   {
     for(int i = 0; i < UniqueName.size(); i++)
      if(actors.get(transaction - 1).equals(UniqueName.get(i)))
        {Degrees.set(i, Degrees.get(i) + 1); break;}
      else if(i == (UniqueName.size() - 1))
        {UniqueName.add(actors.get(transaction - 1)); Degrees.add(1); break;}
    
     for(int i = 0; i < UniqueName.size(); i++)
      if(targets.get(transaction - 1).equals(UniqueName.get(i)))
        {Degrees.set(i, Degrees.get(i) + 1); break;}
      else if(i == (UniqueName.size() - 1))
        {UniqueName.add(targets.get(transaction - 1)); Degrees.add(1); break;}
    }
    return median(Degrees);
  }
  //Calculates the median per a transaction
  public static Double median(ArrayList<Integer> Degrees)
  {
    Integer[] SortedDegrees = Degrees.toArray(new Integer[Degrees.size()]);
    Arrays.sort(SortedDegrees);
    
    return (Degrees.size() % 2 == 0) ? ((SortedDegrees[Degrees.size()/2] + SortedDegrees[Degrees.size()/2 - 1])/2.00) : SortedDegrees[Degrees.size()/2]/1.00;
  }
}
