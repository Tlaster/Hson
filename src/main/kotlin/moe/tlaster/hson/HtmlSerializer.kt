package moe.tlaster.hson

import org.jsoup.nodes.Element

interface HtmlSerializer<T> {
    fun decode(element: Element, wholeText: String): T
}