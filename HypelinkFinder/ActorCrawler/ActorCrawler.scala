import akka.actor._
import akka.routing.RoundRobinRouter
import scala.collection.immutable.HashSet
import akka.routing._
import scala.concurrent.duration._

object ActorCrawler {
 
  val parser = new Parser
  val utils = new Utility
  val baseUrl = "http://www2.cs.uh.edu/~svenkat/fall2014pl/samplepages"
  var visitedUrls: Set[String] = new HashSet[String]()
  var taskCount = 0
  val start = System.nanoTime()
 
  case object Calculate 
  case class Work(sublinks: String)
  case class Result(links: Set[String]) 
  case class TotalCount(count: Int)
   
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("ActorSystem")
    val master = system.actorOf(Props(new Master), name = "master")
    master ! Result(Set(baseUrl))
  }
   
  class Master extends Actor {
    var tovisitUrls: Set[String] = new HashSet[String]()
    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinPool(100)), name = "workerRouter")
	val start = System.currentTimeMillis()

    def receive = {
      case Result(tovisitUrls) =>
	      taskCount = taskCount + tovisitUrls.size
          if (!tovisitUrls.isEmpty) {
            tovisitUrls.foreach {link => 
            workerRouter ! Work(link)
          }
	    }
        taskCount = taskCount - 1
        if(taskCount == 0) {
		   println("Total Actor Count::" + visitedUrls.size +  " Time::" + ((System.currentTimeMillis() - start)/1000))
           //context.system.shutdown()
		}
		  
      case ex: Exception => throw ex
    }
  }
		
  class Worker extends Actor {
    val parser = new Parser
    val utils = new Utility
    def actorRun(url: String): Set[String]= {
      val pattern = ".*/[A-Z]/25.html"
      val basePath = utils.filterUrl(url, pattern)
      val links = parser.getLinks(url)
      var tovisitUrls: Set[String] = new HashSet[String]()
      links.foreach { subLink =>
        val relativeUrl = utils.prepareRelativeUrl(subLink, basePath)
        if(!visitedUrls.contains(relativeUrl)){
          visitedUrls = visitedUrls + relativeUrl
          tovisitUrls = tovisitUrls + relativeUrl
        }
      }
       tovisitUrls
    }
    
    def receive = {
      case Work(sublink) =>  
        val actorrf : ActorRef = sender
        actorrf ! Result(actorRun(sublink)) 
      case ex: Exception => throw ex
    }
	  
  }
}