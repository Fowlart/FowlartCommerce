package com.fowlart.main

import com.fowlart.main.in_mem_catalog.Item
import com.fowlart.main.state.BotVisitor
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

import java.util.regex.Pattern
import scala.collection.JavaConverters._

class ScalaHelper {

  def buildReplyMessage(userId: Long, text: String, markUp: InlineKeyboardMarkup): SendMessage = {

    SendMessage
      .builder
      .chatId(userId)
      .text(text)
      .replyMarkup(markUp)
      .build
  }

  def getBucketMessage(visitor: BotVisitor,
                       userId: String,
                       keyboardHelper: KeyboardHelper): SendMessage = {

    val itemList = visitor.getBucket.asScala.filter(it => it != null).map(item => s" ⏺ $item").toList
    val textInBucket = if (itemList.isEmpty) "[Корзина порожня]" else itemList.reduce((i1, i2) => s"$i1\n\n$i2")

    SendMessage.builder.chatId(userId)
      .text(
        s"""
           |Корзина:
           |
           |$textInBucket
           |""".stripMargin)
      .replyMarkup(keyboardHelper.buildBucketKeyboardMenu).build
  }

  def isNumeric(strNum: String): Boolean = {
    val pattern = Pattern.compile("-?\\d+")
    if (strNum == null) return false
    pattern.matcher(strNum).matches
  }


  def getSubMenuText(itemList: java.util.List[Item], group: String): Array[String] = {
    val maxItemsPerReply = 15;
    val itemSeq = itemList.asScala.filter(item=>group.equals(item.group()))
    // ordering for pretty printing
    implicit val orderingForItem: Ordering[Item] = (x: Item, y: Item) =>
      x.name().trim.length.compareTo(y.name().trim.length)
    val reOrderedList = itemSeq.sorted
    val grouped = reOrderedList.grouped(maxItemsPerReply)
    val res = grouped.toList.filter(it => it.nonEmpty).map(it => {
      it.map(item =>
        s"""
           |⏺ ${item.name.trim.toLowerCase}
           |💳${item.price} грн
           |⏩/${item.id}
           |""".stripMargin).reduce((v1, v2) => s"$v1$v2")
    })
    res.toArray
  }

  def getPhoneEditingText(botVisitor: BotVisitor): String = {

    s"""|😎Данні користувача ${botVisitor.getUserId}/телефон:
        | Введіть, будь ласка, номер телефону
        | в наступному форматі: xxx(оператор)-xxxxxxx(номер)
        |""".stripMargin
  }
  def getPersonalDataEditingSectionText(botVisitor: BotVisitor): String = {

    val userLastName = if (botVisitor.getUser.getLastName==null) "" else botVisitor.getUser.getLastName

    s"""|😎Данні користувача ${botVisitor.getUserId}:
        |
        | Ім'я/Прізвище: ${botVisitor.getUser.getFirstName} $userLastName
        | Телфон:
        | Email:
        |""".stripMargin
  }
  def getMainMenuText(name: String): String ={
    s"""|Привіт, $name!
        |
        |Це бот для замовлення товарів, натискай кнопки для навігації по меню.
        |Або продовжуйте навігацію по каталогу, чи замовляйте товари, натискаючи
        |їх ID номер.
        |
        |Редагування кількостей відбувається в корзині.""".stripMargin}

    def getItemAcceptedText(item: Item): String = {
      s"""
         |Додано:
         |
         |${item.toString}
         |
         |Подальше редагування кількості відбувається у корзині.
         |""".stripMargin
    }

  def getItemNotAcceptedText(): String = {
    s"""
       |Ви або ввели некоректне ID,
       |або додатковий текст крім самого ID товару
       |або зробили щось таке, чого ми не передбачили.
       |Спробуйте, будь-ласка ще разок.
       |""".stripMargin
  }

  def getEditItemQtyMsg(item: Item): String =
    s"""
       |Редагуємо товар:
       |
       |$item
       |
       |Введіть кількість товару цілим позитивним числом.
       |
       |/remove - видалити з корзини.
       |""".stripMargin

  def getContactsMsg(): String =
    s"""
       |Власник: Андрій Скіра Володимирович
       |тел: 097-257-0077
       |""".stripMargin

}
