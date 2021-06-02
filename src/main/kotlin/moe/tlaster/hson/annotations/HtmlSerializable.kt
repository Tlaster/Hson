package moe.tlaster.hson.annotations

import moe.tlaster.hson.HtmlSerializer
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class HtmlSerializable(
    vararg val selector: String,
    val attr: String = "",
    val serializer: KClass<out HtmlSerializer<*>> = HtmlSerializer::class,
)

