class Utility {

  def filterUrl(url: String, pattern: String): String = {
    if(url.matches(pattern)){	
      val chopurl = ".*/[A-Z]".r.findAllIn(url).toList
      chopurl(0).substring(0, chopurl(0).length() - 1)
    }
    else if(url.matches(".*.html")){ 
      val chopurl = ".*/".r.findAllIn(url).toList
      chopurl(0)
    }
    else{
	  url + "/"
    }	
  }
	
  def prepareRelativeUrl(subLink: String, basePath: String) : String = {
    if(subLink.contains("../")){
      val subLinkEdit = subLink.replaceAll("\\'","")
      basePath + subLinkEdit.replaceAll("../","")
    }
    else{
      basePath + subLink.replaceAll("\\'","")
    }
  }
}