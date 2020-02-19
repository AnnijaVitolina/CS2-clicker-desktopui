package clicker.view

import io.socket.client.{IO, Socket}
import io.socket.emitter.Emitter
import javafx.application.Platform
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.cell.TextFieldTableCell
import play.api.libs.json.{JsValue, Json}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.VBox


class HandleMessagesFromServer() extends Emitter.Listener {
  override def call(objects: Object*): Unit = {

    // Use runLater when interacting with the GUI
    Platform.runLater(() => {
      // This method is called whenever a new game state is received from the server
      val jsonGameState = objects.apply(0).toString
      println(jsonGameState)

      // TODO: Display the game state on your GUI
      // You must display: current gold, and the name, number owned, and cost for each type of equipment

      // You can access any variables/methods in the DesktopGUI object from this class
//      DesktopGUI.goldTextField.text = goldFromGameState

      var parsed: JsValue = Json.parse(jsonGameState)

      var gold: Int = (parsed \ "gold").as[Int]
      DesktopGUI.currentGold.text = gold.toString

      var shovelID: String = (parsed \ "equipment" \ "shovel" \ "id").as[String]
      var shovelNumberOwned: Int = (parsed \ "equipment" \ "shovel" \ "numberOwned").as[Int]
      var shovelCost: Int = (parsed \ "equipment" \ "shovel" \ "cost").as[Int]
      DesktopGUI.equipment1.text = shovelID + " " + shovelNumberOwned.toString + " " + shovelCost.toString

      var excavatorID: String = (parsed \ "equipment" \ "excavator" \ "id").as[String]
      var excavatorNumberOwned: Int = (parsed \ "equipment" \ "excavator" \ "numberOwned").as[Int]
      var excavatorCost: Int = (parsed \ "equipment" \ "excavator" \ "cost").as[Int]
      DesktopGUI.equipment2.text = excavatorID + " " + excavatorNumberOwned.toString + " " + excavatorCost.toString

      var mineID: String = (parsed \ "equipment" \ "mine" \ "id").as[String]
      var mineNumberOwned: Int = (parsed \ "equipment" \ "mine" \ "numberOwned").as[Int]
      var mineCost: Int = (parsed \ "equipment" \ "mine" \ "cost").as[Int]
      DesktopGUI.equipment3.text = mineID + " " + mineNumberOwned.toString + " " + mineCost.toString
    })

  }
}


object DesktopGUI extends JFXApp {

  var socket: Socket = IO.socket("https://tictactoe.info/")
  socket.on("gameState", new HandleMessagesFromServer)
  socket.connect()

  // Change "test" to any username you'd like to start a new game
  socket.emit("register", "Tests")

  // Call this method whenever the user clicks your gold button
  def clickGold(): Unit = {
    socket.emit("clickGold")
  }

  // Call this method whenever the user clicks to purchase equipment
  // The parameter is the id of the equipment type to purchase
  def buyEquipment(equipmentId: String): Unit = {
    socket.emit("buy", equipmentId)
  }

  // TODO: Setup your GUI
  // You may create and place all GUI elements (TextFields, Buttons, etc) then only change the text on
  // each element when a new game state is received
  // You may assume that there will be exactly 3 types of equipment with ids of "shovel", "excavator", and "mine"

  var currentGold: TextField = new TextField {
    editable = false
    style = "-fx-font: 18 ariel;"
  }

  val goldButton: Button = new Button {
    minWidth = 100
    minHeight = 100
    style = "-fx-font: 28 ariel;"
    text = "GOLD"
    onAction = new ButtonListener()
  }

  val equipment1: Button = new Button {
    minWidth = 100
    minHeight = 100
    style = "-fx-font: 28 ariel;"
    text = "shovel"
    onAction = new BoughtShovel()
  }

  val equipment2: Button = new Button {
    minWidth = 100
    minHeight = 100
    style = "-fx-font: 28 ariel;"
    text = "excavator"
    onAction = new BoughtExcavator()

  }

  val equipment3: Button = new Button {
    minWidth = 100
    minHeight = 100
    style = "-fx-font: 28 ariel;"
    text = "mine"
    onAction = new BoughtMine()
  }

  val verticalBox = new VBox(){
    children = List(currentGold, goldButton, equipment1, equipment2, equipment3)
  }

  this.stage = new PrimaryStage {
    title = "CSE Clicker"
    scene = new Scene() {
      content = List(verticalBox)
    }
  }
}

class ButtonListener() extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit = {
    DesktopGUI.clickGold()
    println("gold clicked")
  }
}

class BoughtShovel() extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit = {
    DesktopGUI.buyEquipment(DesktopGUI.equipment1.text.getValue)
    println("shovel bought")
  }
}

class BoughtExcavator() extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit = {
    DesktopGUI.buyEquipment(DesktopGUI.equipment2.text.getValue)
    println("excavator bought")
  }
}

class BoughtMine() extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit = {
    DesktopGUI.buyEquipment(DesktopGUI.equipment3.text.getValue)
    println("mine bought")
  }
}