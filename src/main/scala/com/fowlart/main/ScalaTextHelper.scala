package com.fowlart.main


class ScalaTextHelper {

  def getMainMenuText(name: String): String ={
    s"""|Привіт [$name]!
        |Це бот для замовлення товарів, натискай кнопки для навігації по меню.
        |Або продовжуйте навігацію по каталогу, чи замовляйте товари, вводячи
        |їх ID номер(ID5,ID840,ID566...). Редагування кількостей відбувається в корзині.
        |🔻 🔻 🔻 🔻 🔻""".stripMargin
  }

}
