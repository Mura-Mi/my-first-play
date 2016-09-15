package controllers

import javax.inject.{Inject, Singleton}

import model.{Coffees, Suppliers}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Action, Controller}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

/**
  * A sample api to connect database via DatabaseConfigProvider
  *
  * @author mura_mi
  */
@Singleton
class DbController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends Controller {

  import scala.concurrent.ExecutionContext.Implicits.global

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def createTable = Action {
    val suppliers = TableQuery[Suppliers]
    val coffees = TableQuery[Coffees]

    val setup = DBIO.seq(
      (suppliers.schema ++ coffees.schema).create,
      suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += (49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      coffees ++= Seq(
        ("Colombian", 101, 7.99, 0, 0),
        ("French_Roast", 49, 8.99, 0, 0),
        ("Espresso", 150, 9.99, 0, 0),
        ("Colombian_Decaf", 101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
    )

    val db = dbConfig.db.run(setup)

    Ok("created!")
  }

  def getCoffees = Action {
    val coffees = TableQuery[Coffees]

    val sb: StringBuilder = new StringBuilder
    /*val all: Future[Seq[(String, Int, Double, Int, Int)]] =*/
    dbConfig.db.run(coffees.result).map(_.foreach {
      case (name, _, _, _, _) => sb.append(name)
    })

    //    val hoge: PartialFunction[Seq[(String, Int, Double, Int, Int)], Unit] =  {
    //      case Seq(_*) => _.map(c => c._1).foreach(name => sb.append(name + ", "))
    //    }


    //      (cfs: Seq[(String, Int, Double, Int, Int)]) => cfs.map(c => c._1).foreach(name => sb.append(name + ", "))


    //    all.onSuccess(hoge)

    Ok(sb.toString())
  }

}

