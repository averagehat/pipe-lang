trait AnEffect {
  def E(p: String): Effect = Effect( () => Files.exists(Paths.get(p)))
  def ME(r: Regex): MultiEffect = MultiEffect(
    () => {
      val files: List[String] = ("ls".!!) split("\n") toList
      val matches = files.filter(s => r.findFirstIn(s.toCharArray).nonEmpty)
      matches nonEmpty
    })
  def ME(p: String*): MultiEffect = MultiEffect(

  )
  def fold() = List[(Effect, Effect, JobFunc)]
  def run(target: Effect) = Unit
  def ->(in: Effect): Relation = OneToOne(this, in)
  def %>(in: MultiEffect): Relation = OneToMany(this, in)
  def ?>(in: Effect): Relation = OneToOne(this, toMaybe(in))
  def ?%>(in: MultiEffect): Relation = OneToMany(this, toMaybeMany(in))
}
//case class MultiEffect(r: Regex) extends Effect
case class MultiEffect(() => Boolean) extends AnEffect // (Regex) => Boolean?
case class Effect(() => Boolean) extends AnEffect
trait Relation
type P = File
type Ps = List[File]
type MP = Option[File]
type MPs = Option[List[File]]
case class OneToOne(out: Effect, in: Effect) extends Relation
case class OneToMany(out: Effect, in: MultiEffect) extends Relation
case class MaybeOne(out: Effect, in: Effect) extends Relation
case class MaybeMany(out: Effect, in: MultiEffect) extends Relation


trait Producer
case class JobFunc(func: (Files, Files) => ()) extends Producer
type File = Effect
type Files = List[File]
case class Edge(inputs: List[File], outputs: List[File], maybeInput = None : Option[List[File]]) {
  val i = inputs.head
  val o = outputs.head
  val is = inputs
  val os = outputs
  val maybeIn = maybeInput
}
import AnEffect._
E(".*\\.o".r) %> E("*.\\.c".r)
E(".*\\.o".r) -: (x => Seq("gcc", x.i, x.o))

E("merged.bam") ?> E("unpaired.bam") E("merged.bam") ?> E("paired.bam") // can't do
E("merged.bam") -> ( E("unpaired.bam") OR E("paired.bam") )
E("merged.bam") ?> (E("unpaired.bam"),  E("paired.bam"))
<'?'>[value]

merged.bam <- paired.bam OR merged.bam

paired.fastq <% *.fastq

*.fastq  ->.filtered
define =?
*.filtered =? *.fastq
*.filtered => *.fastq

.filtered .
// grammar could specify that

<'def'> funcname <'('> (arg <','>)+ ')'
paired.bam <-  *_R1_*.trimmed *_R2_*.trimmed | ref.fasta
*_R1_*.filtered *_R2_*.filtered <-  *_R1_*.fastq *_R2_*.fastq
.filtered .filtered <-  *_R1_*.fastq *_R2_*.fastq
merged.bam <- paired.bam ?unpaired.bam


class Foo {

  val p:((List[String], List[String]) => String) =  (x, y) => "cat $x > $y"

  val jg: ((String, String), (String, String), ((List[String], List[String]) => String)) =
    ("main.o" , "foo.o") -> ("main.c" , "foo.c") -> ((x, y) => "cat $x > $y")


  val jg: (Product2, Product2, (List[String], List[String]) => String) =
    ("main.o" , "foo.o") -> ("main.c" , "foo.c") -> ((main, foo) => f"cat $main > $foo")

  //  ("main.o" :: "foo.o") ->
  //  ("main.c" :: "foo.c") :: (main, foo) => f"cat $main > $foo".!
