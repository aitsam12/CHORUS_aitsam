/*
 * Copyright (c) 2017 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package examples

import chorus.schema.Schema
import chorus.util.ElasticSensitivity

/** A simple differential privacy example using elastic sensitivity.
  *
  * This example code supports queries that return a single column and single row. The code can be extended to support
  * queries returning multiple columns and rows by generating independent noise samples for each cell based the
  * appropriate column sensitivity.

  */
object ElasticSensitivityExample extends App {
  // Use the table schemas and metadata defined by the test classes
  System.setProperty("schema.config.path", "src/test/resources/schema.yaml")
  val database = Schema.getDatabase("test")

  // example query: How many US customers ordered product #1?
  val query = """
    SELECT COUNT(*) FROM orders
    JOIN customers ON orders.customer_id = customers.customer_id
    WHERE orders.product_id = 1 AND customers.address LIKE '%United States%'
  """

  // query result when executed on the database
  val QUERY_RESULT = 100

  // privacy budget
  val EPSILON = 0.1
  // delta parameter: use 1/n^2, with n = 100000
  val DELTA = 1 / (math.pow(100000,2))


  println(s"Query: $query")
  println(s"Private result: $QUERY_RESULT\n")
  println(s"Epsilon: $EPSILON")

  (1 to 10).foreach { i =>
    val noisyResult = ElasticSensitivity.addNoise(query, database, QUERY_RESULT, EPSILON, DELTA)
    println(s"Noisy result (run $i): %.0f".format(noisyResult))
  }
}
