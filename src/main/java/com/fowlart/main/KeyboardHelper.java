package com.fowlart.main;

import com.fowlart.main.state.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import com.fowlart.main.state.cosmos.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyboardHelper {

    private final Catalog catalog;

    private final String host;

    public KeyboardHelper(@Autowired Catalog catalog, @Value("${app.bot.host.url}") String host) {
        this.catalog = catalog;
        this.host = host;
    }

    private InlineKeyboardButton buildButton(String text, String callBackText) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackText);
        return button;
    }

    private InlineKeyboardButton buildUrlButton(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return button;
    }


    public InlineKeyboardMarkup buildInPhoneEditingModeMenu() {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        var rowInline = new ArrayList<InlineKeyboardButton>();
        rowInline.add(buildButton("Вийти", "EDIT_PHONE_EXIT"));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildPersonalDataEditingMenu() {
        var markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        var rowInline1 = new ArrayList<InlineKeyboardButton>();
        var rowInline2 = new ArrayList<InlineKeyboardButton>();
        var rowInline3 = new ArrayList<InlineKeyboardButton>();

        rowInline1.add(buildButton("☎️ Змінити номер телефону", "EDIT_PHONE"));
        rowInline2.add(buildButton("🚹🚺Змінити ФІО", "EDIT_NAME"));
        rowsInline.addAll(List.of(rowInline1, rowInline2, rowInline3));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildBucketReply() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(buildButton("\uD83D\uDED2 Корзина", "BUCKET"));
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>(List.of(rowInline1));
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildMainMenuReply(long userId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(buildButton("\uD83D\uDCD7 Каталог", "CATALOG"));
        rowInline1.add(buildButton("\uD83D\uDED2 Корзина", "BUCKET"));
        rowInline1.add(buildButton("☎️ Контакти", "CONTACTS"));

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(buildButton("\uD83D\uDC64 Мої данні", "MYDATA"));
        rowInline2.add(buildUrlButton("🔎 Пошук", host+"/search?userId="+userId));

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>(List.of(rowInline1, rowInline2));
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildCatalogItemsMenu() {

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (String groupItem : catalog
                .getItemList()
                .stream()
                .map(Item::group)
                .collect(Collectors.toSet())) {
            rowInline.add(buildButton(groupItem+"⬇️️", groupItem));
            rowsInline.add(rowInline);
            rowInline = new ArrayList<>();
        }
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildBucketItemKeyboardMenu(String itemId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(buildButton("\uD83D\uDEAE Видалити", "DISCARD_ITEM__" + itemId));
        rowInline1.add(buildButton("\uD83E\uDDEE Кількість", "GOODS_QTY_EDIT__" + itemId));
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildAddToBucketItemKeyboardMenu(String itemId) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(buildButton("\uD83D\uDEAE Відмінити додавання", "DISCARD_ITEM__" + itemId));
        rowsInline.add(rowInline1);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public InlineKeyboardMarkup buildBucketKeyboardMenu() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline1.add(buildButton("Очистити \uD83D\uDEAE", "DISCARD"));
        rowInline2.add(buildButton("ЗАМОВИТИ \uD83C\uDD97", "SUBMIT"));

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }
}
