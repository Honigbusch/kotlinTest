package parsing

import model.CardEntry
import model.Deck
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by sb on 2/13/17.
 */
class Parser {
    private var parseNow = false
    private var parseSideboardNow = false
    private var nextCount = -1

    private val decklistList = mutableListOf<Deck>()
    private val nextMaindeck = mutableListOf<CardEntry>()
    private val nextSideboard = mutableListOf<CardEntry>()

    fun parseFile(urlStr: String): List<Deck> {
        parseNow = false
        parseSideboardNow = false
        nextCount = -1
        decklistList.clear()
        nextMaindeck.clear()
        nextSideboard.clear()

        val url = URL(urlStr)

        val con = url.openConnection()

        val br = BufferedReader(InputStreamReader(con.inputStream))


        while (true) {
            val line = br.readLine() ?: break
            parseLine(line)
        }

        return decklistList
    }

    private fun parseLine(line: String) {
        fun ifContains(text: String,f: () -> (Unit)) { if(line.contains(text)){ f.invoke() } }

//        ifContains("class=\"decklist\">Decklist</a>") { println("####DECKLIST") }
        ifContains("sorted-by-overview-container") { parseNow=true }
        if (parseNow) {
            ifContains("sorted-by-sideboard-container") { parseSideboardNow=true }
            ifContains("card-count") {
                var subline = line.substring(0,line.length-"</span>".length)
                val lastIndexOf = subline.lastIndexOf(">")
                nextCount = subline.substring(lastIndexOf + 1).toInt()
            }
            ifContains("card-name") {
                var subline = line.substring(0,line.length-"</a></span>".length)
                val lastIndexOf = subline.lastIndexOf(">")
                val name = subline.substring(lastIndexOf + 1)
                if (parseSideboardNow) {
                    nextSideboard.add(CardEntry(nextCount,name))
                } else {
//                    println("$nextCount $name")
                    nextMaindeck.add(CardEntry(nextCount,name))
                }
                nextCount=-1
            }
            ifContains("sorted-by-color-container") {
                val element = Deck(nextMaindeck.toList(), nextSideboard.toList())
                decklistList.add(element)
                parseNow=false
                parseSideboardNow = false
                nextMaindeck.clear()
                nextSideboard.clear()
            }
        }
    }
}