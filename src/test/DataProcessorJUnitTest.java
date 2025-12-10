import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import java.util.stream.*;

public class DataProcessorJUnitTest {
    @Test
    public void testEvenSquares() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> evenSquares = numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(4, 16, 36), evenSquares);
    }
}
