package examples


// one of the main challenge is to build connection with database.
//here I used MySQL for database connection. In MySQL Workbench I already have my desired tables.
// I defined their schema in 'aitsam.yaml' file. You can edit it according to your database.
// make sure your MySQL workbench is running when your run this file.
// DB.scala handle all database connection stuff. you have to provide details of your MySQL for connection.


// importing all required files
import java.io.{FileWriter, PrintWriter}
import chorus.mechanisms.{AverageMechClipping, EpsilonCompositionAccountant, LaplaceMechClipping}
import chorus.rewriting.RewriterConfig
import chorus.schema.Schema
import chorus.sql.QueryParser
import scala.io.Source
import chorus.mechanisms.Statistics

// creating 'hello' object object name must be same as file name.
object hello extends App {
    //this will create empty csv files for later use.
    val co = new PrintWriter("DP_chorus_count_10000.csv")
    co.close()
    val su = new PrintWriter("DP_chorus_sum_10000.csv")
    su.close()
    val av = new PrintWriter("DP_chorus_mean_10000.csv")
    av.close()
    //outer loop to iterate through each value of epsilon
    for (epsilon <- (0.1 to 1.0 by 0.01) map ( x=> "%.2f".formatLocal(java.util.Locale.ROOT,x).toDouble)) {
        println("Epsilon value is: " + epsilon)
        //emptying the csv files
        val outputCOUNT = new PrintWriter("COUNT_chorus.csv")
        outputCOUNT.close()
        val outputSUM = new PrintWriter("SUM_chorus.csv")
        outputSUM.close()
        val outputAVG = new PrintWriter("AVG_chorus.csv")
        outputAVG.close()
        // inner loop to run each query 500 times. (same for all compared libraries)
        for (w <- 0 to 500) {
            System.setProperty("dp.elastic_sensitivity.check_bins_for_release", "false")
            System.setProperty("db.use_dummy_database", "false")

            // Use the table schemas and metadata defined by the test classes
            System.setProperty("schema.config.path", "src/test/resources/aitsam.yaml") //edit this file when you have to add another table or database.
            val database = Schema.getDatabase("distributions_1000") //this is the name of dataset in aitsam.yaml

            val config = new RewriterConfig(database)
            // SUM query
            val query1 = "SELECT SUM(speed) FROM car_speed1"
            val root1 = QueryParser.parseToRelTree(query1, database) //root is mandatory
            // COUNT query
            val query2 = "SELECT COUNT(speed) FROM car_speed1"
            val root2 = QueryParser.parseToRelTree(query2, database)
            // MEAN query
            val query3 = "SELECT AVG(speed) FROM car_speed1"
            val root3 = QueryParser.parseToRelTree(query3, database)

            // Define the privacy accountant
            val accountant = new EpsilonCompositionAccountant()

            // Run the mechanisms
            // Laplace mechanism handle count, sum and mean queries.
            //in AverageMechClipping they also consider Sum and Count query and divide them to get Mean.
            val r1 = new LaplaceMechClipping(epsilon, 40.699, 78.748, root1, config).execute(accountant) //SUM query. 40.6 and 78.74 as bounds for our dataset (distributions_10000)
            val r2 = new LaplaceMechClipping(epsilon, 40.699, 78.748, root2, config).execute(accountant) //COUNT query
            val r3 = new AverageMechClipping(epsilon, 40.699, 78.748, root3, config).execute(accountant) //MEAN query

            //saving results in csv files

            //println("Sum query result: " + r1)
            val outputSUM = new PrintWriter(new FileWriter("SUM_chorus.csv", true))
            outputSUM.println(r1(0).vals(0))
            outputSUM.close()

            //println("Count query result: " + r2)
            val outputCOUNT = new PrintWriter(new FileWriter("COUNT_chorus.csv", true))
            outputCOUNT.println(r2(0).vals(0))
            outputCOUNT.close()

            //println("Average query result: " + r3)
            val outputAVG = new PrintWriter(new FileWriter("AVG_chorus.csv", true))
            outputAVG.println(r3(0).vals(0))
            outputAVG.close()
        }

        // reading csv files which we just created to get their std.

        val data1 = "COUNT_chorus.csv"
        var r1: Array[Double] = Array()
        for (line <- Source.fromFile(data1).getLines) {
          val s1 = line
          //println(s.toDouble)
          r1 = r1 :+ s1.toDouble
        }

        val data2 = "SUM_chorus.csv"
        var r2: Array[Double] = Array()
        for (line <- Source.fromFile(data2).getLines) {
          val s2 = line
          //println(s.toDouble)
          r2 = r2 :+ s2.toDouble
        }

        val data3 = "AVG_chorus.csv"
        var r3: Array[Double] = Array()
        for (line <- Source.fromFile(data3).getLines) {
          val s3 = line
          //println(s.toDouble)
          r3 = r3 :+ s3.toDouble
        }

    // as scala doesn't have builtin function for std so I created a class "Statistics.scala" for std and used it here.
    val statistics1 = new Statistics(r1)
    println("COUNT = " + statistics1.getStdDev())
    val count = new PrintWriter(new FileWriter("DP_chorus_count_10000.csv", true))
    count.println(statistics1.getStdDev())
    count.close()

    val statistics2 = new Statistics(r2)
    println("SUM = " + statistics2.getStdDev())
    val sum = new PrintWriter(new FileWriter("DP_chorus_sum_10000.csv", true))
    sum.println(statistics2.getStdDev())
    sum.close()

    val statistics3 = new Statistics(r3)
    println("Average = " + statistics3.getStdDev())
    val mean = new PrintWriter(new FileWriter("DP_chorus_mean_10000.csv", true))
    mean.println(statistics3.getStdDev())
    mean.close()
    }
}
