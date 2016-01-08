import ammonite.ops._


def fileReg (s:String) = ("(" ++ s.replaceAll("\\.", "\\.").replaceAll("\\*", ".*") ++ ")").r
val noExt =  ((x:String) => x.split('.').init.mkString(".")) 
val swapExt = ( (s:String, ext:String) => (noExt(s)) ++ "." ++ ext)
def dirname (p:Path) : Path = p.segments.init match { 
  case Vector() => wd; 
  case xs       => Path("/" ++ xs.mkString("/")) }
 //p.segments does not include root         

implicit class Iff(val a: Path) extends AnyVal { 
  def %- = dirname(a)/noExt(a.last)
  def %= (b: String) : Path  = dirname(a)/swapExt(a.last, b)
  def %+ (b: String) : Path  = dirname(a)/(a.last ++ "." ++ b)
  def ~= (b: String) : Boolean = fileReg(b).unapplySeq(a.last).isDefined
  def ~/ (b: String) : Boolean = !(fileReg(b).unapplySeq(a.last).isDefined)
        }

implicit class Iff(val fs: Seq[Path]) extends AnyVal { 
  def %- = a => fs.map(dirname(a)/noExt(a.last))
  def %= (b: String) : Seq[Path]  = fs.map( a => dirname(a)/swapExt(a.last, b))
  def %+ (b: String) : Seq[Path]  = fs.map( a => dirname(a)/(a.last ++ "." ++ b))
  def ~= (b: String) : Seq[Path] = fs.filter(a => fileReg(b).unapplySeq(a.last).isDefined)
  def ~/ (b: String) : Seq[Path] = fs.filterNot(a => fileReg(b).unapplySeq(a.last).isDefined)
        }

(ls!) ~= ".*.swp"


sealed abstract class OSError
case class PathNotExist(p: Path) extends OSError
case class PathAlreadyExist(p: Path) extends OSError
case class ExecNotExist(s: String) extends OSError
case class NonZeroReturnCode(s: Stirng, code: Int) extends OSError
case class MissingRequirement(s: Stirng, code: Int) extends OSError

Map(NoSuchFileException -> (x) => PathNotExist(Path(x.getFile)) )

(x:Int, y:Int) => x + y
res23: (Int, Int) => Int
val foo:Map[Int, (Int,Int) => Int] = Map( 3 -> (_+_), 2 -> (_-_))

//load.ivy("com.chuusai" %% "shapeless" % "2.2.5")
//import shapeless._  // overwrites Path
//import systnax.  
//import ammonite.ops._
//def FS(xs:Product): List[RelPath] = {
//  var list:List[RelPath] = List()
//  x.productIterator.foreach( (s:Any) => l +:= (RelPath(s.asInstanceOf[String])))
//  list
//  }
def FS(fs:String*): List[RelPath] = fs.map(RelPath(_)).toList

type Files = List[Path]
type JobFunc = (Files, Files) => Unit
type JobGraph = Map[Map[Files, Files], (Files, Files) => Unit]
val runPython = (List("python","-c",_:String)) andThen ( x => Try(%%(x)))
def convert(in:BasePath, out:BasePath) = runPython(f""""from Bio import SeqIO; SeqIO.convert($in, 'sff', $out, 'fastq');"""")
  
val sffs = (ls!) ~= "*.sff")
val fastqs = ( (ls!) ~= "*.fastq" ) ++ (sffs %= "fastq")
val unpaired = (ls!) ~/ "*_R[12]_*.fastq" ++ (sffs %= "fastq")
val paired = (ls!) ~= "*_R[12]_*.fastq"

val runCmd = %%
val unsafeOpts
val merge = (ins, out) => write(out,  ins map read(_) mkString("\n") )


val graph:JobGraph = Map( 
  (unpaired -> sffs) -> (_, _).zipped map convert(_, _),

  (unpaired %+ "filtered" -> paired +: which("cutadapt") ) -> 
        (in, out) => (in.init, out).zipped map runCmd(in.last, unsafeOpts("cutadapt"), _, _)

  (paired %+ "filtered"  -> paired +: which("cutadapt") ) -> 
        (in, out) => { 
          val ((fwd, rev), (fwd_o, rev_o)) = (in.init.partition(_:Path ~= "*_R1_*"), out.partition(_:Path ~= "*_R1_*"))
          (fwd, rev, fwd_o, rev_o).zipped map runCmd(in.last, unsafeOpts("cutadapt"), _, _, _, _) }

  (paired %+ "filtered.trimmed" -> (fastqs %+ "filtered"  +: TRIM_JAR) -> 
      (in, out) => (in.init, out).zipped map runCmd("java", "-jar", in.last, _, _)

  (FS("unpaired.fastq") -> unpaired %+ "filtered" +: TRIM_JAR) -> 
     (in, out) =>  { in.init map runCmd("java", "-jar", in.last, _ %+ "tmp");
                     merge(out.head, in.init %+ "tmp"); }
  (FS("paired.bam") -> paired %+ "filtered.trimed" +: which("bwa") ) ->
     (in, out) =>  runCmd(in.last, in.init.mkString(" "), "-pe", "-o", out.head)
  
  (FS("unpaired.bam") -> FS("unpaired.fastq") +: which("bwa") ) ->
     (in, out) =>  runCmd(in.last, in.init.mkString(" "), "-o", out.head) // pattern-match on unpaired.

  (FS("merged.bam") -> FS("unpaired.bam", "paired.bam")
     


var controller = null
def getTraget(m:JobGraph, tgt:Path): Map[(Paths, Paths), (Paths, Paths) => ()]
  
def require(target:Path): =  {
  assume(controller != null)
  for {
     job <- controller.find(_._1.contains(target))
     ((tgts, reqs), func) <- job._1, job._2
     } yield func(reqs, tgts)
}

controller = jobGraph     


  controller match {
    Some(graph) => graph(job)(
// something like 
  FS("merged.bam") -> "paired.bam", Try("unpaired.bam")
// otherwise might need a `require` function
// but how can you require somthing from within the funciton itself?
  ("merged.bam" -> "paired.bam") -> (in, out) => { 
    match opts("unpaired-exist") {
      case true => { require("unpaired.bam"); runCmd("samtools", "merge", "unpaired.bam", in, ">", out)  }
      case false => mv! in out } }


      
         
// might be able to get rid of FS using traits so that can do:
// wd/"merged.bam" -> 
for { 
  unpaired <- sffs.map(convert   // keep going even if unpaired is None, just dont' need it 

val graph:JobGraph = Map( 
  (fastqs -> sffs) -> convert(_, _),
  (fastqs %+ "filtered" +: which("cutadapt") -> fastqs) -> (in, out) => runCmd(in.last
  
  (fastqs %+ "filtered.trimmed" +: TRIM_JAR -> (fastqs %+ "filtered") -> 
      (in, out) => (in.init, out).zipped map runCmd("java", "-jar", in.last, 


val graph:JobGraph = (  
    (FS("foo.fastq") -> FS("foo.sff") ) -> convert(_, _),

    (FS("paired.bam") -> fastqs
                       )


println(tsort(Seq((1, 2), (2, 4), (3, 4), (3, 2), (1,3))))


//https://github.com/scala-graph/scala-graph/issues/22
val jobGraph:JobGraph = Map( 
  (unpaired -> sffs) -> (_, _).zipped map (cp! _ _))


val jobGraph2:JobGraph = Map( 
  (unpaired -> sffs) -> (_, _).zipped map (cp! _ _))
  (AlterPath("merged.bam") -> FS("merged.bam")) -> runCmd("samtools", "sort", _)

  // just decompose the graph so that it's only single m->n edges, then use a tsort
//(t) => for { x <- t._1; y <- t._2 } yield (x, y)

val M = Map(List("fastq") -> List("sff"), List("paired.bam") -> List("fastq"), List("merged.bam") -> List("unpaired.bam", "paired.bam"), List("unpaired.fastq") -> List("sff"), List("paired.bam") -> List("fastq"))



def tsort[A](edges: Traversable[(A, A)]): Iterable[A] = {
    //@tailrec
    def tsort(toPreds: Map[A, Set[A]], done: Iterable[A]): Iterable[A] = {
        val (noPreds, hasPreds) = toPreds.partition { _._2.isEmpty }
        if (noPreds.isEmpty) {
            if (hasPreds.isEmpty) done else sys.error(hasPreds.toString)
        } else {
            val found = noPreds.map { _._1 }
            tsort(hasPreds.mapValues { _ -- found }, done ++ found)    
        }
    }

    val toPred = edges.foldLeft(Map[A, Set[A]]()) { (acc, e) =>
        acc + (e._1 -> acc.getOrElse(e._1, Set())) + (e._2 -> (acc.getOrElse(e._2, Set()) + e._1))
    }
    tsort(toPred, Seq())
}


M.map((t) => for { x <- t._1; y <- t._2 } yield (y, x)).flatten

//M._1._1 = target
//M._1._2 = req
//M._2    = func

//val job = getProducingRule(M, target)

def getProducingRule(m: JobGraph, x:Path) : Job = m.find(_._1._1.contains(x)) 

def execute(M: JobGraph, target :Path) :Either[OSError, Unit]  = {
  match  getProducingRule(M, target)  {
     Some(job) => Right(job._2(job._1._2, job._1._1))
     None      => Left(MissingRequirement)
     }
  //val ((tgts, reqs), func) = job._1, job._2
}


type Job = Map[Files, Files], (Files, Files) => Unit
def runMap(M :JobGraph) = {
    val flattened = M.map((t) => for { x <- t._1; y <- t._2 } yield (x, y)).flatten 
   for (needed <- flattened) {
      if (!Files.exists(needed.nio)) execute(M, needed)
      else Unit
      }
    }


def makeTmpFile = java.nio.file.Files.createTempFile(      java.nio.file.Paths.get(System.getProperty                           ("java.io.tmpdir")), "ammonite", ".py")

def py(s :String) :Unit = {
  val file = Path(makeTmpFile)
  write(file, s)
  Try(%%('python, file))
  rm! file 
  }
