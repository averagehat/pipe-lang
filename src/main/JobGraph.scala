type Files = List[Path]
type JobFunc = (Files, Files) => Unit
type JobGraph = Map[Map[Files, Files], (Files, Files) => Unit]
type Job = Map[Files, Files], (Files, Files) => Unit

abstract class GraphError extends ProjectError
case class MissingRequirement(s: Stirng, code: Int) extends GraphError


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


def getProducingRule(m: JobGraph, x:Path) : Job = m.find(_._1._1.contains(x)) 

def execute(M: JobGraph, target :Path) :Either[GraphError, Unit]  = {
  match  getProducingRule(M, target)  {
     Some(job) => Right(job._2(job._1._2, job._1._1))
     None      => Left(MissingRequirement)
     }
  //val ((tgts, reqs), func) = job._1, job._2
}
def runMap(M :JobGraph) = {
    val flattened = M.map((t) => for { x <- t._1; y <- t._2 } yield (x, y)).flatten 
   for (needed <- flattened) {
      if (!Files.exists(needed.nio)) execute(M, needed)
      else Unit
      }
    }
