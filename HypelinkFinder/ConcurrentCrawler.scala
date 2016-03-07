import scala.collection.immutable.HashSet
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicLong
		
class ConcurrentCrawler{
 
  val parser = new Parser
  val utils = new Utility
  var visitedUrls: Set[String] = new HashSet[String]()
  var exceptionList = List()
  val pool = Executors.newFixedThreadPool(100)
  val taskCount = new AtomicLong(1)
  val latch = new CountDownLatch(1)
  
  def runThreads(baseUrl: String): Int = {
    concurrentRun(baseUrl)
    if(!exceptionList.isEmpty){
      throw exceptionList(0)
    }
    latch.await()
    pool.shutdown
    visitedUrls.size
  }
   
  def concurrentRun(url: String): Unit = {
 
    val links = parser.getLinks(url)
    val pattern = ".*/[A-Z]/25.html"
    val basePath = utils.filterUrl(url, pattern)
		
    links.foreach { subLink =>
      val relativeUrl = utils.prepareRelativeUrl(subLink, basePath)
        this.synchronized{
          if(!visitedUrls.contains(relativeUrl)){
            visitedUrls = visitedUrls + relativeUrl
            taskCount.getAndIncrement()
            try{
               pool.execute(new Runnable { def run { concurrentRun(relativeUrl) } } )
            }
            catch{ 
                   case e: Exception => exceptionList + e.getMessage()
                           latch.countDown() 
            }
          }
        }
    }
    taskCount.getAndDecrement()
    if(taskCount.get() == 0){
      latch.countDown()
    }
  }
}
