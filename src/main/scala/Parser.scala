package make

//import parser._
//import validate._
//import shapeless.{HNil, ::,HList}
import scaledn.parser._
import dregex.Regex
import make._
import make.OS.{globToCompiledRegex, readFile}
import java.io.File
import scala.util.Success
//object Parser {
//
//  val fn = "data/working-exmaple.mk"
//  val str   = readFile(new File(fn))
//  parseEDN(str) match {
//    case Success(whole:List[List[Any]]) => {
//      val (imports, rules) = whole.tail.span(_.head != "rule")
//      val tasks = rules.map(_(1)).map(x => handleTask(x))
//    }
//  }
//
//
//  def handleTask(rule: List[List[String]]) = {
//    val (tgts, depsAndCode) = rule.span(_.head.head == "target")
//    val (deps, code) = depsAndCode.span(_.head.head == "dependency")
//    Task(deps.map(x => handleDep(x.tail)),
//         tgts.map(x => handleDep(x.tail)),
//         handleCode(code.tail))
//  }
//
//  def handleDep(dep: Seq[String]) =  {
//    val tgtMap: Map[String, String => Target] =
//    Map("filename" -> FileTarget ,
//        "glob"     -> (x => RegexTarget(globToCompiledRegex(x))),
//        "regex"    -> (x => RegexTarget(Regex.compile(x))),
//        "orderOnly" -> OrderTarget)
//    dep match {
//      case Seq(t, s) => tgtMap(t)(s) 
//    }
//}
//
//  def handleCode(stuff: Seq[Seq[String]]) = stuff match {
//    case Seq(Seq("pythonBlock"), lines) => PythonScript(lines.map(_(1)).mkString(""))
//    case Seq(Seq("bashBlock"), lines)  => BashScript(lines.map(_(1))mkString(""))
//  }
//}
//
//
//
//
//
////  def handleDep(dep: List[String]) = dep match {
////    case ("filename":: s::Nil) => FileTarget(s)
////    case ("glob"::s::Nil) => RegexTarget(globToCompiledRegex(s))
////    case ("regex"::s::Nil) => RegexTarget(Regex.compile(s))
////    case ("orderOnly"::s::Nil) => OrderTarget(s)
////  }
////
////  def handleCode(stuff: List[String]) = stuff match {
////    case ("pythonBlock" :: lines) => PythonScript(lines.mkString(""))
////    case ("bashBlock"  :: lines)  => BashScript(lines.mkString(""))
////  }
////
////}
