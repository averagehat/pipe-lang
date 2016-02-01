package make
import java.nio.file.{Paths, Files}
import java.io.{File,FileWriter}
import scala.util.parsing.json._
import dregex.Regex // not on Maven/Ivy
import scala.language.reflectiveCalls // for the.close in using

object OS {
  val optionsFolder = Paths.get(sys.env("HOME"), ".pipelang")
  val stateJson = optionsFolder.resolve("state.json")
  val optsJson = optionsFolder.resolve("opts.json")

  import sys.process._

  def using[A <: {def close(): Unit}, B](resource: A)(f: A => B): B =
    try f(resource) finally resource.close()

  def writeToFile(file: File, data: String): Unit =
    using(new FileWriter(file))(_.write(data))

  def readFile(file: File): String = scala.io.Source.fromFile(file).mkString("")
  
  def makeTmpFile =
    java.nio.file.Files.createTempFile(java.nio.file.Paths.get(
      System.getProperty("java.io.tmpdir")), "pipelang", ".py")

  def runScript(interpreter: String)(contents :String) :Unit = {
    val file = makeTmpFile toFile()
    writeToFile(file, contents)
    f"$interpreter $file".!
    f"rm $file".!!
  }

  val runBash = runScript("bash") _
  val runPython = runScript("python") _

  def getStateDeps(depName: String): Option[Boolean] = {
    val str = readFile(stateJson.toFile)
    for {
      json <- JSON.parseFull(str).asInstanceOf[Option[Map[String, Map[String, Boolean]]]]
      deps <- json.get("orderDependencies")
      done <- deps.get(depName)
    } yield done // should throw error if failed somewhere
  }

  def lsGlob(glob: String): List[String] =  Seq("bash", "-c", f"ls $glob").!!.split("\n").toList

  def lsRecursive(dir: File): List[File] =  {
    val these = dir.listFiles.toList
    these ++ these.filter(_.isDirectory).flatMap(lsRecursive)
  }

  def lsRegex(re: Regex): List[String] =
    lsRecursive(new File(".")).map(_.getName).filter(re.matches _)

  def globToCompiledRegex(glob: String): Regex = {
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
        case Nil              => Nil
      }
    }
    val raw = globToRegex(glob.toCharArray.toList).mkString("")
    Regex.compile(raw)
  }

  }
