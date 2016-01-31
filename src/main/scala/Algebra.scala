package make
/**
  * Created by michaelpanciera on 1/26/16.
  */
/**
  * Created by michaelpanciera on 1/25/16.
  */
import dregex.Regex // not on Maven/Ivy
import scalaz._
import java.nio.file.{Paths, Files}
import scala.util.matching.Regex
import Scalaz._
import sys.process._

package object types {
  type Inputs = List[Target]
  type Outputs = List[Target]
}

import types._
// an effect has a way of determining that it has existed or not.
// and it has some kind of name, I suppose.
trait Target {
  import OS._

  def getStartFiles: List[FileTarget] = this match {
    case RegexTarget(re) => lsRegex(re).map(FileTarget(_))
    //case GlobTarget(s)  => lsGlob(s).map(FileTarget(_))
    case ft @ FileTarget(s)  => if (satisfied) List(ft) else List()
  }

  def satisfied: Boolean = this match {
    case FileTarget(fn) => Files.exists(Paths.get(fn))
    case OrderTarget(name) => getStateDeps(name).getOrElse(false)
    //case other => getStartFiles.flatMap(_.nonEmpty).getOrElse(false)
    case other => getStartFiles.nonEmpty
  }
  def isSubset(other: Target): Boolean = (this, other) match {
    case (FileTarget(fn1), FileTarget(fn2)) => fn1 == fn2
    case (FileTarget(fn1), RegexTarget(re)) => doesMatch(re, fn1)
    case (RegexTarget(r1), RegexTarget(r2)) => r1 isSubsetOf r2
    case (FileTarget(_), RegexTarget(_))    => false
  }
}

case class Task(inputs: Inputs, outputs: Outputs, script: Script) {

  /** need to take all the inputs that match the input targets
    * (they have been pre-grouped via hopeful recursion),
    * transform them to their new concrete names, decide if they exist.
    **/
  def shouldRun(inputs: Inputs): Boolean =  throw new NotImplementedError("")//os.any(!_.satisfied)
                                           


  def runIO: Unit = script.run(inputs, outputs) //implementations could go in ATask trait and use pattern matching
  def runDry: Unit = println(script.populate(inputs, outputs))
}

 object Target {
//   def filesOnly(xs: List[Target]): List[FileTarget] = xs.filter(
//     {case FileTarget(_) => true
//     case _ => false} )
   def fileNames(xs: List[Target]) = xs.filter(
     {case FileTarget(_) => true; case _ => false} )
        .map({case FileTarget(name) => name})
 }
case class FileTarget(name: String) extends Target
case class OrderTarget(name: String) extends Target
case class RegexTarget(re: Regex) extends Target
//case class GlobTarget(glob: Regex) extends Target

trait Script  {
  import Target._
  import make.OS._

  def populateNumberedTargets(s: String, files: List[String], prefix: String)  =
    files.zipWithIndex.foldRight(s)(
      {case ((o, i), s) =>
        s.replaceAll(("\\$" + prefix + (i+1)), o)}
    )

  def populate(inputs: Inputs, outputs: Outputs): String =  {
    val (is, os) = (fileNames(inputs), fileNames(outputs))
    this match {
      case BashScript(s) => populateNumberedTargets(
                              populateNumberedTargets(s, is, "IN"), os, "OUT")
                               .replaceAll("\\$INPUTS", is.mkString(" "))
                               .replaceAll("\\$OUTPUTS", os.mkString(" "))

      case PythonScript(s) => populateNumberedTargets(
                                populateNumberedTargets(s, is, "IN"), is, "OUT")
                                  .replaceAll("\\$INPUTS",  "[" + is.mkString(",") + "]")
                                  .replaceAll("\\$OUTPUTS", "[" + os.mkString(",") + "]")
    }
  }

  def run(inputs: Inputs, outputs: Outputs): Unit = this match {
    case BashScript(s) => runBash(populate(inputs, outputs))
    case PythonScript(s) => runPython(populate(inputs, outputs))}
}
case class BashScript(content: String) extends Script
case class PythonScript(content: String) extends Script

// match inputs and outputs via order
// take while != '.', wrap with (), use as RE to sub.
  // so *.to <- *.from ==
  // only needed if both sides have wildcards
  // basically, pass "until '.' " match from the right side to the left side,
// where it will swap out until '.'
 // *.good.fastq <- *.fastq :
// (s) => s.replaceFirst("([^/]*).fastq",  "$1.good.fastq")
  // but a file could produce two outputs, not sure if should support
  // *.good.fastq *.bad.fastq *.good.fastq *.bad.fastq <- *_R1_*.fastq *_R2_*.fastq
//  make sure to ignore orderDependencies
// but support this by splitting # outputs into groups, R1 -> good,bad, R2 -> good,bad
// split by diving number of globs on left by number of globss/regexes on right.
// *.out <- noglob.in is then sort of illegal or ambiguous.
// could even enforce this in the grammar.
// but *.out <- ~.*.txt is legal.
// regex on the left hand side is excluded by grammar
//

object Graph {
  import OS._

  def genConcreteTasks(tasks: List[Task]) = {
    // the below doesn't actually work. We need to generate all the concrete inputs 
    val (completedTasks, todoTasks) = tasks.span(t => t.inputs.forall(_.satisfied))
  }

    /** produces a function which, given the runtime input of a filename, produces
      * a filename resulting from applying the target glob pattern.
      */
    def genNameSwapper(i: String, o: String): String => String  =
      (s) => {
        val matchGroup = "([^/]*)" + i.dropWhile (_!= '.')
        val outTemplate = "$1" + o.dropWhile (_!= '.')
        s.replaceFirst(matchGroup, outTemplate)
      }
  val equalSort = topologicalSort((x,y) => y.outputs.contains(x))
  val noramalSort = topologicalSort((tgt, task) => (task.outputs.any(tgt.isSubset(_))))
//    val withRegexSort = topologicalSort(
//      (tgt, task) => (task.outputs.contains(tgt) ||
//                        tgt.outputs.any(isRegexSubset(tgt, _))))

  /** May return t empty list **/
  def topologicalSort(key: (Target, Task) => Boolean)( tasks: IList[Task], tgt: Target): IList[Task] = {

    val job = tasks.find(key(tgt, _))
    job.map( j =>
     j ::  j.inputs.toIList.map(topologicalSort(key)(tasks, _))
            .filter(_.nonEmpty).flatten)
      .getOrElse(IList[Task]())
  }

} // Graph object


//    def topologicalSort(key: (Target, Task) => Boolean)( tasks: List[Task], tgt: Target): Maybe[List[Task]] = {
//      job = tasks.find( key tgt _)
//      for {
//        depsOfJob <- job.inputs.map(topologicalSort(key)(tasks, _))
//        allDeps <- job :: depsOfJobDep 
//      } yeild allDeps
//    }
//  }
//      depsOfJobDep = job.flatMap(j => 
//        j.inputs.map(topologicalSort(key)(tasks, _))
//                       .map2(jobDep, depsOfJobDep)(_ :: _))




// a task has (optionally) [inputs], [outputs], a way to produce those (a script),
// and maybe an effect it expects to produce.

// A script has an interpreter, the script body, and variables.
