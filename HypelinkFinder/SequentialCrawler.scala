import scala.collection.immutable.HashSet

class SequentialCrawler{
 
  val parser = new Parser
  val utils = new Utility
  var visitedUrls: Set[String] = new HashSet[String]()

  def sequentialRun(url: String): Int = {
    val links = parser.getLinks(url)
    val pattern = ".*/[A-Z]/25.html"
    val basePath = utils.filterUrl(url, pattern)		
    links.foreach { subLink =>
      val relativeUrl = utils.prepareRelativeUrl(subLink, basePath)
      if(!visitedUrls.contains(relativeUrl)){
        visitedUrls = visitedUrls + relativeUrl
        sequentialRun(relativeUrl)
      }
    }
    visitedUrls.size
  }
}
