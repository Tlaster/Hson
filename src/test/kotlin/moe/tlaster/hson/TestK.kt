package moe.tlaster.hson

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestK {
    @Test
    fun test() {
        val html = "<b><a>asd</a></b>"
        val result = Hson.deserializeKData<Sample>(html)
        assertEquals("asd", result.data)
    }

    @Test
    fun attrTest() {
        val html = "<b><a c=\"c\">asd</a></b>"
        val result = Hson.deserializeKData<SampleAttr>(html)
        assertEquals("c", result.data)
    }

    @Test
    fun nullableTest() {
        val html = "<b><a>asd</a></b>"
        val result = Hson.deserializeKData<SampleNullable>(html)
        assertEquals("asd", result.data)
        assertNull(result.nullData)
    }

    @Test
    fun multipleTest1() {
        val html = "<c><b><a>asd1</a></b><b><a>asd2</a></b></c>"
        val result = Hson.deserializeKData<SampleMultiple>(html)
        assertEquals("asd1", result.data)
    }

    @Test
    fun multipleTest2() {
        val html = "<c><b><anull>asd1</anull></b><b><a>asd2</a></b></c>"
        val result = Hson.deserializeKData<SampleMultiple>(html)
        assertEquals("asd1", result.data)
    }

    @Test
    fun converterTest() {
        val now = Instant.now()
        val html = "<a>$now</a>"
        val result = Hson.deserializeKData<SampleWithConverter>(html)
        assertEquals(now, result.data)
    }

    @Test
    fun listTest() {
        val html = "<c><b><a>asd</a></b><b><a>asd</a></b></c>"
        val result = Hson.deserializeKData<ListSample>(html)
        assert(result.data.isNotEmpty())
        assert(result.data.all { it.data == "asd" })
    }

    @Test
    fun selfReferenceTest() {
        val html = "<a>a</a><b><a>b</a></b>"
        val result = Hson.deserializeKData<SampleSelfReference>(html)
        assertEquals("a", result.data)
        assertNotNull(result.reference)
        assertEquals("b", result.reference.data)
        assertNull(result.reference.reference)
    }
}