package config

import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.ext.EnumNameSerializer
import net.liftweb.json.Serialization.{read, write}
// write(NGSFilter(


//TODO: accept Json OR YAML 
object MakeConfig {

object Platform extends Enumeration  {
     type Platform = Value
     val MiSeq  =    Value("MiSeq")
     val Sanger =     Value("Sanger")
     val Roche454 =   Value("Roche454")
     val IonTorrent = Value("IonTorrent") 
  }

  val json = """ 
  { "platforms" : ["MiSeq", "IonTorrent"],
    "dropNs"    : true,
    "minQual"   : 42 }"""

  //tell 

  implicit val formats = DefaultFormats + new EnumNameSerializer(Platform)
  val result = parse(json).extract[NGSFilter]
  println(result) 


  case class Bwa(id: Long, numbers: List[Int])
  case class NGSFilter(minQual: Int, dropNs: Boolean, platforms: List[Platform.Platform])


}
     


//  def json2yaml(in: Path, out: Path):
//    // pip install pyyaml
//    py f"""
//        import json
//        import yaml
//         
//        data = json.loads(open('$in').read())
//        yml = yaml.safe_dump(data)
//        with open('$out', 'w') as out:
//            out.write(yml)"""
//  val json = 
//    ("lotto" ->
//      ("lotto-id" -> lotto.id) ~
//      ("winning-numbers" -> lotto.winningNumbers) ~
//      ("draw-date" -> lotto.drawDate.map(_.toString)) ~
//      ("winners" ->
//        lotto.winners.map { w =>
//          (("winner-id" -> w.id) ~
//           ("numbers" -> w.numbers))}))
//
//  println(compact(render(json)))


// "net.liftweb" %% "lift-json" % "2.6"
// "net.liftweb" %% "lift-json-ext" % "2.6"

//Serialization.write(Platform.MiSeq)

//libraryDependencies := Seq( "net.liftweb" %% "lift-json" % "2.6", "net.liftweb" %% "lift-json-ext" % "2.6")

// 'configuration: main' error solved by removing ~/.ivy2/chache/whatever
//implicit value formats is not applicable here because it comes after the application point and it lacks an explicit result type
