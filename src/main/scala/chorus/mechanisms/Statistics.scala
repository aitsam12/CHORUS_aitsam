
package chorus.mechanisms
class Statistics (var data: Array[Double]) {

  var size: Int = data.length

  def getMean(): Double = {
    var sum: Double = 0.0
    for (a <- data) sum += a
    sum / size
  }

  def getVariance(): Double = {
    val mean: Double = getMean
    var temp: Double = 0
    for (a <- data) temp += (a - mean) * (a - mean)
    temp / (size - 1)
  }

  def getStdDev(): Double = Math.sqrt(getVariance)



}