package geo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoServiceImpl;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeoServiceTest {
    private static GeoServiceImpl sut;

    @BeforeEach
    public void init() {
        sut = new GeoServiceImpl();
    }

    @Test
    public void byCoordinatesTest() {
        assertThrows(RuntimeException.class,
                () -> sut.byCoordinates(150, 150));
    }

    @ParameterizedTest
    @MethodSource
    public void testLocale(String ip, Country expected) {
        Location result = sut.byIp(ip);
        assertEquals(expected, result.getCountry());
    }

    private static Stream<Arguments> testLocale() {
        return Stream.of(
                Arguments.of("127.0.0.1", null),
                Arguments.of("96.197.43.42", Country.USA),
                Arguments.of("172.5.4.245", Country.RUSSIA),
                Arguments.of("96.", Country.USA),
                Arguments.of("172.", Country.RUSSIA)
        );
    }

    @AfterEach
    public void finished() {
        sut = null;
    }
}