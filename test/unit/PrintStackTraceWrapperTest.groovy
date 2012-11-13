import org.junit.Test
import ru.grails.PrintStackTraceWrapper

class PrintStackTraceWrapperTest {

    @Test
    void simpleTest() {
        try {
            PrintStackTraceWrapper.inject('java.', 'sun.', 'org.')
            def j = 0;
            def i = i/j
        } catch (e) {
            e.printStackTrace()
        }
    }

}
