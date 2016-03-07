class Time {

  def measureTime(supplierFunction:() => Int) = {
    val startTime = System.currentTimeMillis()
    val count = supplierFunction()
    val endTime = System.currentTimeMillis()
    ((endTime - startTime)/1000, count)
  }
}