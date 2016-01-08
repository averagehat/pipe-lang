import ammonite.ops._
import sys.env
import scala.util._
      
// Usage: ./amm runbeast.scala /xml/path "some beast args"
def main(outdir: Path, xml: Path, beastArgs: Seq[String]) = {
  //NOTE:  beastArgs must be quoted 
  
   val cmd = s"module load bio_pieces beast && beast_wrapper ${ beastArgs.mkString(" ") } ${ xml.last }"
  
   def runBash(s: String) = %%( List("bash", "-c", s) )
  
    pbsID = sys.env("PBS_JOBID")
    stageDir = if (sys.env.contains("WORKDIR")) sys.env("WORKDIR")/pbsID  else outdir/pbsID
    cd! outdir
    println(f"Staging files into $stageDir")
    mkdir! stageDir
    cp(xml, stageDir/xml.last)
    println(f"Entering $stageDir")
    cd! stageDir
    println(f"Running: $cmd")
    runBash(cmd)
    if (stageDir != outdir/pbsID)  cp(stageDir, outdir/pbsID)
}
