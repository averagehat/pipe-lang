import ammonite.ops._ 
import ammonite.shell._ 
import ammonite.ops.ImplicitWd._
val wd = Path( new java.io.File(""))
// http://lihaoyi.github.io/Ammonite/
(ls! wd/"." |? (_.last.startsWith("bwa")) ).head

read(wd/"foo.scala").split("\n").filter(!_.isEmpty) //.length ... 

(ls! wd/".").sortBy(_.size).head

cp(wd/"basic_pipeline.py", wd/"silly-file.py")

val regMatch = (re:  util.matching.Regex) => (s: String) =>
  s match {
      case re(_) => true
      case _ => false }

write(wd/"result.fastq", (ls! wd/"." |? (x => regMatch("(.*.fastq)".r)(x.last))) .map(read) .foldRight("")(_ + "\n" + _))


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
/*
ls :: FilePattern -> [File]

grep :: TextPattern -> FilePattern -> [Text]

cat :: [FilePattern] -> Text

> :: Text -> (Program | File) -> NIL

echo :: Text -> NIL

file = expr
expr = addSubExpr | mulExpr | parenExpr | literal
addSubExpr = subExpr | addExpr
subExpr = mulExpr '-' mulExpr
addExpr = mulExpr '+' mulExpr
mulExpr = parenExpr '*' parenExpr
parenExpr = '(' expr ')'

literal = [0-9]+

 mkdir ~/.ammonite; curl -L -o ~/.ammonite/predef.scala http://git.io/vBTz7
cur -L -o amm http://git.io/vBTzM; chmod +x amm; ./amm
*/

// fix the .gitignore problem. add reverse searching.









 mkdir ~/.ammonite; curl -L -o ~/.ammonite/predef.scala http://git.io/vBTz7
cur -L -o amm http://git.io/vBTzM; chmod +x amm; ./amm

get mutations for each read from the reference.. A read is now a tuple of (int, base) 
position is absolute based on the reference.
-- LR = longread LH = local haplotype GH = global haplotype
dont't worry about insertions yet
data Base = A | C | G | T

lhn = 3
ghn = 3

shareCount = count . intersection 
miseq = filterByPlatform "MiSeq"
pacbio = filterByPlatform "PacBio" 

ghs = filter ((> ghn) . shareCount) $ cartProd lrs lhs 
lhs = filter ((> ghl) . shareCount) $ cartProd srs srs

(Int, Base)

a shared mutation = +1, a missing mutation = -1, differing mutation at x = -1 -->? but additional mutations are okay really becaus could be further evolved.

are the GHs determined using the set inertesection of the LH or can a LR match to a LH using other random mutations?

idris effect types for IO 
