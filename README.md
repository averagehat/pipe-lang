Targets
=======
user can pass in either paths or regex, but functions always accept two lists of paths.
if a regex is a subset of another regex, then those two are considered "equal" when it comes to the topological sort. i.e.
a regex is a subset if its diff matches anything.
Use union type on Path, Regex/string. if regex, then ls-grep the string; if empty, find the rule that yeilds the superset of the regex; if the ls-grep succeeds, the rule is fulfilled.
If the type is path, just check if the path(s) exist. If not, the yielding rule is equal to the path OR matches the path as a regex.

#Regexes
Regexes built via [dregex](https://github.com/marianobarrios/dregex)


Regexes mimic bash-style: `p"*_R[12]_*.fastq"`, `p!"*_R[12]_*.fastq"` the later is a `not` regex

if `r2.diff(r1).doIntersect(".*")`  and `r1.doIntersect(r2)`, then r1 is a subset of r2
the opposite of a regex can be found by diffing Regex(".*").diff(myRegex)

```scala
import dregex.Regex
("*.filtered.fastq" -> "*.fastq",
"whatever" -> "*_R1_*.filtered.fastq") // this rule requires "*.filtered.fastq"
val Seq(r1, r2) = Regex.compile(Seq("a", "[ab]"))
r2._2.diff(r1._2).matches("b")

// Regexes that are compared must be built in the same "universe"

import dregex.Regex
val (r1, r2) = 
scala> r1.diff(r2).matches("fR1f.fastq")
res19: Boolean = false

scala> r1.diff(r2).matches("fR2f.fastq")
res20: Boolean = true

scala> val Seq(r1, r2) = Regex.compile(Seq(".*\\.fastq", ".*_R[12]_.*\\.fastq")).unzip._2 
```

#Python inter-op

Two options: 


###Jep 
not work java 8
requires `pip install jep`
No module named jep -> `pip install jep`
[example](https://github.com/eugeneiiim/scala-python-example)
undefined symbol: PyUnicodeUCS4_AsEncodedString
 ->
   $ LD_LIBRARY_PATH=$HOME/anaconda/lib:$LD_LIBRARY_PATH ../bin/sbt run
allows arbitrary imports as long as that environment is activated & LD_LIBRARY_PATH is correct
also allows CPython stuff (unlike jython)

###Create command scripts
The `py` command will write the command to a temporary file and execute it. (syntax highilight ala` drake)
```
py f"""
from Bio import SeqIO
SeqIO.convert($in, 'sff', $out, 'fasta')"""" 
``` 

#Dynamic Changes
Using: [scala.rx](https://github.com/lihaoyi/scala.rx)
The JobGraph gets wrapped in an Rx object, and all its components may be wrapped in Vars, if the user wishes. So if the user alters the Var the graph gets updated appropriately. The graph itself can be updated as well such that edges can be removed or added, or changed. Using the empty list, an edges requirements could be removed. Rx usage:

```scala
import rx._
val a = Var(1); val b = Var(2)
val jobMap = Rx{ Map( 'a -> a, 'b -> b) }
// functions get run with the Vars dereference
a() // derference a
a() = 4 // change a, now jobMap has changed
```

Additionally, it's easy to make a rule depend in some way on user options:

```scala
val opts = Map('mapper -> 'bwa
val Mapper:JobGraph = Map(
   ("paired.bam", List("*.fastq", opts.get('mapper)) -> 
      match { 
        case (out, (in :: 'bwa)) => runBwa 
        }// etc.
        )
```

###Command line and Settings integration
see config.scala. Jackson can conver yaml to json.


###Union types
```scala
import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union.{ union, prove }

type ISB = union[Int]#or[String]#or[Boolean]

def f[T: prove[ISB]#containsType](t: T) {
  t match {
    case x:Int => println(x)
    case x:Boolean => println(1)
    case x:String => println(x.length) } }
}
```
#Resources 
Union types: http://stackoverflow.com/questions/3508077/how-to-define-type-disjunction-union-types#comment26966453_6312508

regex inclusion problem:

http://stackoverflow.com/questions/6363397/how-to-tell-if-one-regular-expression-matches-a-subset-of-another-regular-expres
http://stackoverflow.com/questions/18729015/determining-whether-a-regex-is-a-subset-of-another/19013279#19013279
http://repository.upenn.edu/cgi/viewcontent.cgi?article=1089&context=cis_papers
http://www.ii.uib.no/~dagh/presInclusionWroclaw.pdf
http://qerub.se/improving-scalas-regex-support

pimp my library
http://www.artima.com/weblogs/viewpost.jsp?thread=179766

http://twitter.github.io/effectivescala/

#Notes
- unary operators ~"*.scala"

- String interpolation macro for paths or regexes. https://www.safaribooksonline.com/blog/2013/12/20/scala-macros-that-wont-kill-you/
- json/yaml "net.liftweb" %% "lift-json" % "2.6"
