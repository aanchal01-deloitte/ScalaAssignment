import com.typesafe.config.ConfigFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.File

class DirectorFilterSpec extends AnyFunSpec with Matchers {
  describe("directorFilter") {
    it("should print the titles directed by D.W. Griffith between 1914 and 2000") {
      val applicationConf = ConfigFactory.load()
      val csvFilePath = applicationConf.getString("app.filepath")

      val file = new File(csvFilePath)
      val reader = MovieChecker.read(csvFilePath,file)

      val director = "D.W. Griffith"
      val startYear = 1920
      val endYear = 2000

      val outputStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outputStream) {
        MovieChecker.directorFilter(reader,director, startYear, endYear)
      }

      val expectedOutput = MovieChecker.directorFilter(reader,director, startYear, endYear)

      val actualOutput = outputStream.toString
      actualOutput shouldBe expectedOutput
    }
  }
}
