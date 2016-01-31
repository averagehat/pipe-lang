import scalaz._

val outputs = List("a.txt", "b.txt")
object Target {
  def filesOnly(xs: List[Target]) = xs.filter(
    {case FileTarget(_) => true
    case _ => false}
  )
}
trait Target {
  def shouldRun(t: Target): Boolean = t match {
    case FileTarget(fn) => true
  }
}
case class FileTarget(name: String) extends Target

FileTarget("Foo").name

