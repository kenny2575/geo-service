package sender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.i18n.LocalizationService;
import ru.netology.sender.MessageSenderImpl;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MessageSenderImplTest {

    MessageSenderImpl sut;

    @BeforeEach
    public void init(){
        GeoService geoService = Mockito.mock(GeoService.class);
        Mockito.when(geoService.byIp(Mockito.startsWith("172."))).thenReturn(new Location("Moscow", Country.RUSSIA, "Lenina", 15));
        Mockito.when(geoService.byIp(Mockito.matches("^((25[0-5]|(2[0-4]|1[0-6]|1[8-9]|[1-9]|)[0-9])|17[0-1]|17[3-9])\\.((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){3}$"))).thenReturn(new Location("New York", Country.USA, null,  0));
        LocalizationService localizationService = Mockito.mock(LocalizationService.class);
        Mockito.when(localizationService.locale(Country.RUSSIA)).thenReturn("Добро пожаловать");
        Mockito.when(localizationService.locale(Country.USA)).thenReturn("Welcome");
        sut = new MessageSenderImpl(geoService, localizationService);
    }
    @ParameterizedTest
    @MethodSource("generateIp")
    void testDifferentIpAddresses(Map<String, String> headers, String expected){
        Assertions.assertEquals(sut.send(headers), expected);
    }

    private static Stream<Arguments> generateIp(){
        return Stream.of(
                Arguments.of(Map.of("x-real-ip", "172.9.2.4"), "Добро пожаловать"),
                Arguments.of(Map.of("x-real-ip", "96.9.2.4"), "Welcome"),
                Arguments.of(Map.of("x-real-ip", "127.0.0.0"), "Welcome"),
                Arguments.of(Map.of("x-real-ip", "254.123.41.123"), "Welcome")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testRussiansIpAddresses(String ipAddress){
        String expected = "Добро пожаловать";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-real-ip", ipAddress);
        Assertions.assertEquals(sut.send(headers), expected);
    }

    static Stream<String> testRussiansIpAddresses(){
        List<String> list = new ArrayList<>();
        String ip;
        int addNum;
        for(int i = 0; i < 100; i++){
            ip = "172";
            for (int j = 0; j < 3; j++){
                addNum = (int) (Math.random()*256);
                ip+="."+addNum;
            }
            list.add(ip);
        }
        return list.stream();
    }

    @ParameterizedTest
    @MethodSource
    void testForeignAddresses(String ipAddress){
        String expected = "Welcome";
        Map<String, String> headers = new HashMap<>();
        headers.put("x-real-ip", ipAddress);
        Assertions.assertEquals(sut.send(headers), expected);
    }

    static Stream<String> testForeignAddresses(){
        List<String> list = new ArrayList<>();
        String ip;
        int addNum = 172;
        for(int i = 0; i < 100; i++){
            while(addNum == 172) {
                addNum = (int) (Math.random() * 256);
            }
            ip = String.valueOf(addNum);
            for (int j = 0; j < 3; j++){
                addNum = (int) (Math.random()*256);
                ip+="."+addNum;
            }
            list.add(ip);
        }
        return list.stream();
    }

    @AfterEach
    public void finalized(){
        sut = null;
    }
}