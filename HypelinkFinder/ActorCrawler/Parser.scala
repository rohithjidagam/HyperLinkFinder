import scala.io._
import scala.util.control.Exception._

class Parser{

  def getContent(url: String):String = {
    try { 
      Source.fromURL(url, "UTF-8").mkString
    } 
    catch { 
      case e: Exception => { "" }
    }
  }
  
  def getLinks( url: String ) = {
    val page = getContent(url)
    val pattern = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))".r       
    val links = (pattern.findAllIn(page)).matchData.toList.map(_.group(1)).toSet
    links
  }
}