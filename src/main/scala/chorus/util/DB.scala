package chorus.util

import java.sql.DriverManager
import java.sql.Connection
import scala.collection.mutable.MutableList

import chorus.sql.relational_algebra.{RelUtils, Relation}
import chorus.schema.Database

object DB {
  var connection: Connection = null

  case class Row(vals: List[String])

  def execute(q: Relation, database: Database): List[Row] = {
    def init() = {
      val driver   = "com.mysql.jdbc.Driver"
      val url      = "jdbc:mysql://localhost:3306/aitsam?useSSL=false"
      val username = "root"
      val password = "1234"

      try {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
      } catch {
        case e: Throwable => e.printStackTrace
      }
    }

    val useDummy = System.getProperty("db.use_dummy_database")
    if (useDummy != null && useDummy == "true") {

      val results: MutableList[Row] = MutableList()

      val l1: List[String] = List("1")
      results += Row(l1)

      return results.toList
    }

    if (connection == null)
      init()

    println(database.dialect)
    val sqlQuery = RelUtils.relToSql(q, database.dialect)

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(sqlQuery)

    val cols = resultSet.getMetaData().getColumnCount()

    val results: MutableList[Row] = MutableList()

    while (resultSet.next()) {
      val l: List[String] = (1 to cols).map(resultSet.getString(_)).toList
      results += Row(l)
    }

    results.toList
  }
}
