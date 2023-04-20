import java.io.{File, FileNotFoundException, IOException}
import com.typesafe.config.{Config, ConfigFactory}
import scala.io.Source

object MovieChecker {
  def main(args: Array[String]) {
    val applicationConf = ConfigFactory.load("application.conf")
    val filepath = applicationConf.getString("app.filepath")
    val fileOption = open(filepath)
    var reader  = read(filepath,fileOption)
    //    to ignore column names
    reader = reader.drop(1)
    try {
      directorFilter(reader, "D.W. Griffith", 1920, 2006)
      reviewFilter(reader, 1000)
      budgetFilter(reader, 1914, "USA")
      longestDurationFilter(reader, "USA", 100000)
      languageBudgetReport(reader, "USA", 10 to 1000000)
    }
      //error handling for all other functions
    catch {
      case x: FileNotFoundException => {
        println(x)
      }
      case y: NumberFormatException => {
        println(y)
      }
    }
  }

  //  to open csv file
  def open(path: String): File = {
    new File(path)
  }

  //  to read a csv file
  def read(filepath:String,file: File) = {
    //      exception handling
    try {
      val bufferedIterator = Source.fromFile(filepath)
      //    taking top 10000 records and split with commas
      bufferedIterator.getLines.take(10000).toList.map(_.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?<![ ])(?![ ])"))
    }
    catch {
      case ex: FileNotFoundException => {
        println("Missing file")
        sys.exit(0)
      }
      case ex: IOException => {
        println("IOException")
        sys.exit(0)
      }
    }
  }

    // Checkpoint 1: Titles directed by given director in the given year range
    def directorFilter(reader: List[Array[String]], director: String, startYear: Int, endYear: Int) {
      printSpaces(1)
      val titlesByDirector = reader.filter(row => row(9) == director && row(3).toInt >= startYear && row(3).toInt <= endYear)
        .map(row => s"${row(1)}")
      println(s"Titles directed by $director between $startYear and $endYear:")
      titlesByDirector.foreach(println)
    }


    // Checkpoint 2: English titles with user reviews more than given filter, sorted by descending user reviews
    def reviewFilter(reader: List[Array[String]], userReviewFilter: Int) {
      printSpaces(2)
      val englishTitlesByUserReview = reader.filter { row =>
        row(8) == "English" && row.lift(20).flatMap(_.toIntOption).getOrElse(0) > userReviewFilter
      }.sortBy(row => -row(20).toInt)
        .map { row =>
          row(1)
        }
      println(s"English titles with more than $userReviewFilter user reviews:")
      englishTitlesByUserReview.foreach(println)
    }


    // Checkpoint 3: Highest budget titles for the given year and country filters
    def budgetFilter(reader: List[Array[String]], year: Int, country: String) = {
      printSpaces(3)
      var highestBudgetTitle = reader
      highestBudgetTitle = highestBudgetTitle.filter(_ (3).toInt == year).filter(_ (7).equals(country)).filter(_ (16).nonEmpty)
        .sortBy(row => -row(16).replaceAll("[^\\d]", "").toInt)
        .take(1)
      println(s"Highest budget titles in $country for $year:")
      highestBudgetTitle.foreach(res => println(res(1)))
    }


    // Checkpoint 4: Longest duration title for the given country filter, minimum votes filter, sorted by descending duration
    def longestDurationFilter(reader: List[Array[String]], country: String, minVotes: Int): Unit = {
      printSpaces(4)
      val longestDurationTitle = reader.filter(row => row(7).equals(country)).filter(row => row(15).toInt >= minVotes)
        .sortBy(row => -row(6).toInt)
        .map(row => s"${row(1)}")
      println(s"Longest duration titles in $country with at least $minVotes votes:")
      longestDurationTitle.foreach(println)
    }


    // Checkpoint 5: Generate language wise report to count the titles for the given budget range and country filter and sort with count descending
    def languageBudgetReport(reader: List[Array[String]], country: String, budgetRange: Range.Inclusive): Unit = {
      printSpaces(5)
      val languageCount = reader
        .filter(_.length >= 17)
        .filter(_ (16).nonEmpty)
        .filter(row => budgetRange.contains(row(16).replaceAll("[^\\d]", "").toInt))
        .filter(_ (7).equals(country))
        .filter(_ (8).nonEmpty)
        .flatMap(_ (8).split(",")) // split languages by comma and create a new row for each language
        .map(_.trim.replaceAll("\"", "")) // trim leading/trailing spaces from each language, replaceAll to remove extra "
        .groupBy(identity) // group by language
        .mapValues(_.size)
        .toList
        .sortBy(-_._2)
      println(s"Number of rows for $country: ${languageCount.size}")
      println(s"Language-wise report for titles in $country between $budgetRange:")
      languageCount.foreach(println)
    }


    // printing output of each function with describing checkpoints
    def printSpaces(checkpoint: Int): Unit = {
      println()
      println(s"************-----------CHECKPOINT $checkpoint----------************")
      println()
    }
}