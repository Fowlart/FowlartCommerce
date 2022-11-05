package com.fowlart.main

import com.fowlart.main.in_mem_catalog.Item


class ScalaTextHelper {

  def getMainMenuText(name: String): String ={
    s"""|Привіт, $name!
        |Це бот для замовлення товарів, натискай кнопки для навігації по меню.
        |Або продовжуйте навігацію по каталогу, чи замовляйте товари, вводячи
        |їх ID номер(ID5,ID840..). Редагування кількостей відбувається
        |в корзині.
        |🔻🔻🔻""".stripMargin}

    def getItemAcceptedText(item: Item): String = {
      s"""
         |Товар ${item.toString} додано.
         |
         |Подальше редагування кількості відбувається у корзині.
         |""".stripMargin
    }
}
