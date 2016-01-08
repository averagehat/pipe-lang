import ammonite.ops._ 
import ammonite.shell._ 
import ammonite.ops.ImplicitWd._
val wd = Path( new java.io.File(""))

(ls! wd/"." |? (_.last.startsWith("bwa")) ).head

read(wd/"foo.scala").split("\n").filter(!_.isEmpty) //.length ... 

(ls! wd/".").sortBy(_.size).head

cp(wd/"basic_pipeline.py", wd/"silly-file.py")


val regMatch = (re:  util.matching.Regex) => (s: String) =>
  s match {
      case re(_) => true
      case _ => false }

write(wd/"result.fastq", (ls! wd/"." |? (x => regMatch                           ("(.*.fastq)".r)(x.last))) .map(read) .foldRight(                               "")(_ + "\n" + _))


create a Make system for this Ammonite business.

//ls! "." |? regMatch(

//(ls! "." |? (x => regMatch("(.*.fastq)".r)(x.last))) .map(read) .foldRight("")(_ + "\n" + _) 

//ls.rec! wd | (x => x.size -> x) sortBy (-_._1) take 10

// size is really slow?

// how to create a Path
// Path(x).last
// how to get doc of function?
//ls! |? won't work b/c ls! will take |? as an arg in this case
//-> ls returns abs paths
//the filtering
//
//a.size
//
//wd/"foo.scala"  -> create path
// no CTR+r readline find backwards
// do cat * > without consuming memory? I dunno how bash works
// can "read", ls!, etc. strings (need not be path type).
// will throw "not exist" exception.
// no grep


// where are the wildcards?
//ls! "." |? A
//
//michael.panciera-bioluigi@ re.findFirstIn("asdf.txt") match {
//                           case Some(x) => 10}
//michael.panciera-bioluigi@ val re = "(.*.txt)".r
//re: util.matching.Regex = (.*.txt)
//michael.panciera-bioluigi@ "asdf.txt" match {
//                           case re(x) => true}
//
//michael.panciera-bioluigi@ "asdf.fo" match {
//                           case re(x) => true
//                                                      case _ => false }
//                                                      res166: Boolean = false
//
//the regex must have a group in order to be used in pattern matching in this way.
//michael.panciera-bioluigi@ val re = ".*.txt".r
//re: util.matching.Regex = .*.txt
//michael.panciera-bioluigi@ "asdf.fo" match {
//                           case re(_) => true
//                           case _ => false }
//                           res168: Boolean = false
//SyntaxError: found "match s {case re(_) ", expected If | While | Try | DoWhile | For | Throw | Return | ImplicitLambda | SmallerExprOrLambda | ")" at index 53
//
//
// List("asdf.txt").filter(foo("(.*.txt)".r))
//
//
