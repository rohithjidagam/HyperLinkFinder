import java.util.concurrent.Executors
import java.util.concurrent.CountDownLatch
import scala.concurrent.stm.TSet
import scala.concurrent.stm._
import scala.collection._

class StmCrawler{
  val parser = new Parser
  val utils = new Utility
  val visitedUrls = Ref(TSet.View[String]())
  val exceptionList = List()
  val pool = Executors.newFixedThreadPool(100)
  val taskCount = Ref(1)
  val latch = Ref(new CountDownLatch(1))
  
  def runThreads(baseUrl: String): Int = {
    stmCrawler(baseUrl)
    if(!exceptionList.isEmpty){
      throw exceptionList(0)
    }
    atomic{implicit txn =>
      latch().await()
      pool.shutdown
      visitedUrls().size
    }
  }
  
  def stmCrawler(url: String): Unit = {
    val links = parser.getLinks(url)
    val pattern = ".*/[A-Z]/25.html"
    val basePath = utils.filterUrl(url, pattern)
		
    links.foreach { subLink =>
        val relativeUrl = utils.prepareRelativeUrl(subLink, basePath)
        if( atomic{ implicit txn => !visitedUrls().contains(relativeUrl) }){
          atomic{ implicit txn =>
            visitedUrls() = visitedUrls() ++ TSet(relativeUrl)
            taskCount() = taskCount() + 1
          }
          try{
            pool.execute(new Runnable { def run { stmCrawler(relativeUrl) } } )
          }
          catch{
             case e: Exception => exceptionList + e.getMessage()
                    atomic{ implicit txn => latch().countDown() }
          }
        }
    }
    atomic{ implicit txn =>
      taskCount() = taskCount() - 1
      if(taskCount() == 0){
        latch().countDown()
      }
    }
  }
}