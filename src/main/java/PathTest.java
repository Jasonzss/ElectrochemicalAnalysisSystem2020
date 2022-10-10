import org.junit.Test;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/9/21 0:02
 * @created:
 */
public class PathTest {

    public static void test(){
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("").getPath().replaceFirst("C:"," "));
    }

    public static void main(String[] args) {
        test();
    }
}
