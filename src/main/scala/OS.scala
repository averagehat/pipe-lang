package make
import java.nio.file.{Paths, Files}
import java.io.File
import scala.util.matching.Regex
import scala.util.parsing.json._

object OS {
  import sys.process._
  def makeTmpFile =
    java.nio.file.Files.createTempFile(java.nio.file.Paths.get(
      System.getProperty("java.io.tmpdir")), "pipelang", ".py")

  def runScript(interpreter: String)(contents :String) :Unit = {
    val path = makeTmpFile.getFileName().toString()
    scala.tools.nsc.io.File(path).writeAll(contents)
    f"$interpreter $path".!
    f"rm $path".!!
  }
  val runBash = runScript("bash")
  val runPython = runScript("python")

def getStateDeps(depName: String): Option[Boolean] = {
  val statePath = Paths.get(sys.env("HOME"), ".pipelang", "state.json").toFile
  val str = scala.io.Source.fromFile(statePath).mkString
  for {
    json <- JSON.parseFull(str).asInstanceOf[Option[Map[String, Map[String, Boolean]]]]
    deps <- json.get("orderDependencies")
    done <- deps.get(depName)
  } yield done // should throw error
  }

  def lsGlob(glob: String): List[String] =  Seq("bash", "-c", f"ls $glob").!!.split("\n").toList

  def lsRecursive(dir: File): List[File] =  {
    val these = dir.listFiles.toList
    these ++ these.filter(_.isDirectory).flatMap(lsRecursive)
  }

  def doesMatch(re: Regex, s: String): Boolean = re.findFirstIn(s.toCharArray).nonEmpty

  def lsRegex(re: Regex): List[String] =
    lsRecursive(new File(".")).map(_.getName).filter(doesMatch(re, _))

  def globToRegex(glob: List[Char]): List[String] = {
    def escape(c: Char): String = {
      val regexChars = "\\+()^$.{}]|"
      if (regexChars.contains(c)) f"\\$c" else f"$c"
    }

    def charClass(cs: List[Char]): List[String] = cs match {
      case (']' :: cs) => "]" :: globToRegex(cs)
      case (c :: cs) => c.toString :: charClass(cs)
      case Nil => throw new RuntimeException("untermined [ in glob")
    }

    glob match {
      case ('*' :: t)       => "[^/]+" :: globToRegex(t)
      case ('?' :: t)       => "." :: globToRegex(t)
      case ('['::'!'::c::t) => "[^" :: c.toString :: charClass(t)
      case '[' :: t         => throw new RuntimeException(f"untermined [ in glob $glob")
      case (c :: t)         => (escape (c) ) :: globToRegex (t)
    }
  }
}

