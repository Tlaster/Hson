package moe.tlaster.hson

import moe.tlaster.hson.annotations.HtmlSerializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.reflect.ParameterizedType
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> deserializeObject(html: String): T {
    val doc = Jsoup.parse(html)
    val kClass = T::class
    val jClass = kClass.java
    require(!kClass.isAbstract)
    return deserializeObject(doc, jClass) as T
}

fun deserializeObject(element: Element, clazz: Class<*>): Any {
    val maps = clazz.declaredFields
        .filter { it.isAnnotationPresent(HtmlSerializable::class.java) }
        .associateWith { field ->
            val annotation = field.getAnnotation(HtmlSerializable::class.java)
            if (List::class.java.isAssignableFrom(field.type)) {
                field.genericType.let {
                    it as ParameterizedType
                }.let {
                    it.actualTypeArguments[0] as Class<*>
                }.let { type ->
                    element.select(annotation.selector).map {
                        deserialize(it, annotation, type)
                    }
                }
            } else {
                val elements = element.select(annotation.selector)
                if (!elements.any()) {
                    null
                } else {
                    deserialize(elements.first(), annotation, field.type)
                }
            }
        }
    val ctor = try {
        clazz.getConstructor(*maps.keys.map { it.type }.toTypedArray())
    } catch (e: NoSuchMethodException) {
        null
    } catch (e: SecurityException) {
        null
    }
    return if (ctor != null) {
        ctor.newInstance(*maps.values.toTypedArray())
    } else {
        clazz.getConstructor().newInstance().let { instance ->
            maps.forEach {
                it.key.set(instance, it.value)
            }
        }
    }
}

private fun Element.select(selector: Array<out String>): List<Element> {
    selector.forEach {
        val elements = select(it)
        if (elements.any()) {
            return elements
        }
    }
    return emptyList()
}


private fun deserialize(
    element: Element,
    annotation: HtmlSerializable,
    clazz: Class<*>,
): Any {
    val raw = element.let {
        if (annotation.attr.isNotEmpty()) {
            it.attr(annotation.attr)
        } else {
            it.wholeText()
        }
    }
    val ctor = annotation.serializer.primaryConstructor
    return if (!annotation.serializer.isAbstract && ctor != null) {
        val converter = ctor.call()
        converter.decode(element) as Any
    } else {
        when (clazz) {
            Long::class.java -> raw.toLong()
            Double::class.java -> raw.toDouble()
            Float::class.java -> raw.toFloat()
            String::class.java -> raw.toString()
            Short::class.java -> raw.toShort()
            Boolean::class.java -> raw.toBoolean()
            UInt::class.java -> raw.toUInt()
            else -> deserializeObject(element, clazz)
        }
    }
}
