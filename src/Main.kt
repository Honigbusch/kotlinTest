import model.CardEntry
import model.Deck
import parsing.Parser
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.comparisons.compareBy

/**
 * Created by sb on 2/13/17.
 */

val modernDir = Paths.get("decks","modern")

fun main(args: Array<String>) {
    val now = GregorianCalendar.getInstance()
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val basicUrl = "http://magic.wizards.com/en/articles/archive/mtgo-standings/competitive-modern-constructed-league-"

    val urls = mutableListOf<String>()
    now.add(Calendar.DATE,-1)

//    for (i in 1..83) {
    for (i in 1..1) {
        now.add(Calendar.DATE,-1)
        urls.add(simpleDateFormat.format(now.time))
    }

    val parser = Parser()

    urls.forEach { println(it) }

//    val decklistLists: List<Deck> = listOf()
    val decklistLists = parser.parseFile("http://magic.wizards.com/en/articles/archive/mtgo-standings/competitive-modern-constructed-league-2017-01-24")
//    parser.parseFile("http://magic.wizards.com/en/articles/archive/mtgo-standings/competitive-modern-constructed-league-2017-01-23")
//    val decklistLists = parser.parseFile("http://magic.wizards.com/en/articles/archive/mtgo-standings/competitive-standard-constructed-league-2017-01-23")

//    decklistLists.forEach { println(it) }


    val mainList = getAccumulatedList(decklistLists, Deck::maindeck)
    val sideList = getAccumulatedList(decklistLists, Deck::sideboard)

    save("2017-01-24", mainList)
    save("2017-01-24side", sideList)

}

fun getAccumulatedList(deckLists: List<Deck>, getMap: (Deck) -> (List<CardEntry>)): List<Pair<Int, String>> {
    val map = mutableMapOf<String, Int>()

    for (deckList in deckLists) {
        getMap(deckList).forEach {
            if(!map.containsKey(it.name)) { map.put(it.name,0) }
            map[it.name]= map[it.name]!!+it.count
        }
    }

    val mainList = mutableListOf<Pair<Int, String>>()

    map.forEach { mainList.add(it.value to it.key) }

    mainList.sortWith(compareBy { it.first })
    mainList.reverse()

    mainList.forEach { println(it) }

    return mainList
}


fun save(date: String, cards: List<Pair<Int,String>>) {
    val file = modernDir.resolve(date+".txt")

    val pw = PrintWriter(file.toFile())

    cards.forEach {
        val value = it.first.toString() + " " + it.second
        pw.println(value)
    }
    pw.flush()
}