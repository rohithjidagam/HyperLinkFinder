import scala.collection.immutable.HashSet
import java.util.concurrent._

object Driver {

  def main(args: Array[String]) = {
  
    val seqCrawler = new SequentialCrawler
    val time = new Time
    val baseUrl = "http://www2.cs.uh.edu/~svenkat/fall2014pl/samplepages"
   
    val sequentialList = time.measureTime(() => { seqCrawler.sequentialRun(baseUrl) })
    val seqTime = sequentialList._1
    val seqLinks = sequentialList._2
    println(s"SequentialRun Time(sec)::$seqTime with links::$seqLinks")
	
    val conCrawler = new ConcurrentCrawler
    try {
      val concurrentList = time.measureTime(() => { conCrawler.runThreads(baseUrl) })
      val conTime = concurrentList._1
      val conLinks = concurrentList._2
      println(s"ConcurrentRun Time(sec)::$conTime with links::$conLinks")
    }
    catch {
      case e: Exception => println("exception caught in concurrent run: " + e.getMessage())
    }
    
    val stmCrawler = new StmCrawler
    try {
      val stmList = time.measureTime(() => { stmCrawler.runThreads(baseUrl) })
      val stmTime = stmList._1
      val stmLinks = stmList._2
      println(s"StmRun Time(sec)::$stmTime with links::$stmLinks")
    } 
	catch {
      case e: Exception => println("exception caught in stm run: " + e.getMessage());
    }
   
  }
}