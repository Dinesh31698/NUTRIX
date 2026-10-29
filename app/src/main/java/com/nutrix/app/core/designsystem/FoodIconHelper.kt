package com.nutrix.app.core.designsystem

import com.nutrix.app.core.model.FoodCategory
import java.util.Locale

object FoodIconHelper {

    fun getFoodEmoji(name: String, category: FoodCategory): String {
        val n = name.lowercase(Locale.ROOT)

        return when {
            // Specific Fruits
            n.contains("apple") -> "🍎"
            n.contains("banana") -> "🍌"
            n.contains("mango") -> "🥭"
            n.contains("orange") || n.contains("mosambi") -> "🍊"
            n.contains("papaya") -> "🍈"
            n.contains("guava") || n.contains("kiwi") -> "🥝"
            n.contains("pomegranate") -> "🫐"
            n.contains("watermelon") -> "🍉"
            n.contains("pineapple") -> "🍍"
            n.contains("grape") || n.contains("grapes") -> "🍇"
            n.contains("strawberry") || n.contains("berry") -> "🍓"
            n.contains("dates") || n.contains("khajoor") -> "🌴"

            // Specific Snacks & Chips (check before drinks/vegetables so potato chips gets 🍟)
            n.contains("chip") || n.contains("chips") || n.contains("crisps") -> "🍟"
            n.contains("chocolate") -> "🍫"
            n.contains("biscuit") || n.contains("cookie") -> "🍪"
            n.contains("almond") || n.contains("cashew") || n.contains("walnut") || n.contains("peanut") || n.contains("makhana") -> "🥜"
            n.contains("samosa") || n.contains("pakora") || n.contains("kachori") -> "🥟"
            n.contains("noodle") || n.contains("maggi") || n.contains("pasta") -> "🍜"
            n.contains("chaat") || n.contains("bhel") || hasWord(n, "puri") -> "🥗"

            // Specific Beverages
            n.contains("coca-cola") || n.contains("coke") || n.contains("pepsi") || n.contains("soda") || n.contains("thums up") -> "🥤"
            hasWord(n, "tea") || hasWord(n, "chai") -> "☕"
            n.contains("coffee") -> "☕"
            n.contains("coconut water") -> "🥥"
            n.contains("juice") || n.contains("smoothie") || n.contains("shake") -> "🧃"
            hasWord(n, "water") -> "💧"
            n.contains("milk") || hasWord(n, "lassi") || n.contains("buttermilk") -> "🥛"

            // Biryani & Rice Dishes (before chicken/mutton so Chicken Biryani gets 🍛)
            n.contains("biryani") -> "🍛"
            n.contains("pulao") || n.contains("pongal") || hasWord(n, "rice") -> "🍚"

            // Dairy & Protein
            hasWord(n, "egg") || n.contains("eggs") -> "🥚"
            n.contains("paneer") -> "🧀"
            n.contains("curd") || hasWord(n, "dahi") || n.contains("yogurt") -> "🥣"
            n.contains("tofu") -> "🧈"
            n.contains("fish") || n.contains("prawn") || n.contains("seafood") -> "🐟"
            n.contains("chicken") -> "🍗"
            n.contains("mutton") || n.contains("meat") || n.contains("keema") || n.contains("lamb") -> "🍖"

            // Grains & Breads
            n.contains("roti") || n.contains("chapati") || n.contains("phulka") || n.contains("thepla") -> "🫓"
            n.contains("naan") || n.contains("paratha") || n.contains("kulcha") -> "🫓"
            n.contains("bread") || n.contains("toast") || n.contains("sandwich") -> "🥪"
            n.contains("dosa") || n.contains("uttapam") || n.contains("chilla") -> "🥞"
            n.contains("idli") -> "⚪"
            n.contains("oats") || n.contains("porridge") || n.contains("cereal") -> "🥣"
            n.contains("poha") || n.contains("upma") -> "🍚"

            // Curries & Soups
            hasWord(n, "dal") || n.contains("sambar") || n.contains("rasam") || hasWord(n, "soup") || n.contains("kadhi") -> "🥣"
            n.contains("curry") || n.contains("masala") || n.contains("korma") || n.contains("bharta") || n.contains("gravy") -> "🍛"

            // Vegetables
            n.contains("broccoli") -> "🥦"
            n.contains("corn") -> "🌽"
            n.contains("carrot") -> "🥕"
            n.contains("spinach") || n.contains("palak") || n.contains("methi") -> "🥬"
            n.contains("salad") || n.contains("cucumber") -> "🥗"
            n.contains("potato") || hasWord(n, "aloo") -> "🥔"
            n.contains("tomato") -> "🍅"

            // Fallback by Category
            else -> when (category) {
                FoodCategory.FRUITS -> "🍎"
                FoodCategory.VEGETABLES -> "🥦"
                FoodCategory.CURRIES -> "🍛"
                FoodCategory.RICE_ITEMS -> "🍚"
                FoodCategory.GRAINS -> "🫓"
                FoodCategory.DAIRY -> "🥛"
                FoodCategory.PROTEIN -> "🍗"
                FoodCategory.BEVERAGES -> "🥤"
                FoodCategory.SNACKS -> "🍫"
                FoodCategory.PACKAGED -> "📦"
                FoodCategory.ALL -> "🍽️"
            }
        }
    }

    private fun hasWord(text: String, word: String): Boolean {
        val tokens = text.split(" ", "-", "_", "(", ")", ",", "/", ".", "'")
        return tokens.any { it.equals(word, ignoreCase = true) }
    }
}
