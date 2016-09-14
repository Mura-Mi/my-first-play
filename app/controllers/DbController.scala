package controllers

import javax.inject.{Inject, Singleton}

import model.{Coffees, Suppliers}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Controller
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.H2Driver.api._

/**
  * A sample api to connect database via DatabaseConfigProvider
  *
  * @author mura_mi
  */
@Singleton
class DbController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends Controller {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def createTable = {
    val suppliers = TableQuery[Suppliers]
    val coffees = TableQuery[Coffees]

    val setup = DBIO.seq(
      (suppliers.schema ++ coffees.schema).create,
      suppliers += (101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199"),
      suppliers += ( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460"),
      suppliers += (150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966"),
      coffees ++= Seq(
        ("Colombian",         101, 7.99, 0, 0),
        ("French_Roast",       49, 8.99, 0, 0),
        ("Espresso",          150, 9.99, 0, 0),
        ("Colombian_Decaf",   101, 8.99, 0, 0),
        ("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
    )

    val setupFuture = dbConfig.run(setup)
  }

}
