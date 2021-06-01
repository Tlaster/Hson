package moe.tlaster.hson.annotations

import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class HtmlSerializable(
    vararg val selector: String,
    val attr: String = "",
    val serializer: KClass<out HtmlSerializer<*>> = HtmlSerializer::class,
)

interface HtmlSerializer<T> {
    fun decode(element: Element): T
}
