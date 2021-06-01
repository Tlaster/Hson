package moe.tlaster.hson

import moe.tlaster.hson.HtmlConvert.deserializeObject
import moe.tlaster.hson.annotations.HtmlSerializable
import moe.tlaster.hson.annotations.HtmlSerializer
import org.jsoup.nodes.Element
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Test {
    @Test
    fun test() {
        val html = "<b><a>asd</a></b>"
        val result = deserializeObject<Sample>(html)
        assertEquals("asd", result.data)
    }

    @Test
    fun nullableTest() {
        val html = "<b><a>asd</a></b>"
        val result = deserializeObject<SampleNullable>(html)
        assertEquals("asd", result.data)
        assertNull(result.nullData)
    }

    @Test
    fun multipleTest1() {
        val html = "<c><b><a>asd1</a></b><b><a>asd2</a></b></c>"
        val result = deserializeObject<SampleMultiple>(html)
        assertEquals("asd1", result.data)
    }

    @Test
    fun multipleTest2() {
        val html = "<c><b><anull>asd1</anull></b><b><a>asd2</a></b></c>"
        val result = deserializeObject<SampleMultiple>(html)
        assertEquals("asd1", result.data)
    }

    @Test
    fun converterTest() {
        val now = Instant.now()
        val html = "<a>$now</a>"
        val result = deserializeObject<SampleWithConverter>(html)
        assertEquals(now, result.data)
    }

    @Test
    fun listTest() {
        val html = "<c><b><a>asd</a></b><b><a>asd</a></b></c>"
        val result = deserializeObject<ListSample>(html)
        assert(result.data.isNotEmpty())
        assert(result.data.all { it.data == "asd" })
    }
}

class SampleConverter : HtmlSerializer<Instant> {
    override fun decode(element: Element): Instant {
        return Instant.parse(element.wholeText())
    }
}

data class SampleWithConverter(
    @HtmlSerializable("a", serializer = SampleConverter::class)
    val data: Instant,
)

data class SampleMultiple(
    @HtmlSerializable("anull", "a")
    val data: String,
)

data class SampleNullable(
    @HtmlSerializable("a")
    val data: String,
    @HtmlSerializable("anull")
    val nullData: String? = null,
)

data class Sample(
    @HtmlSerializable("a")
    val data: String,
)

data class ListSample(
    @HtmlSerializable("b")
    val data: List<Sample>,
)