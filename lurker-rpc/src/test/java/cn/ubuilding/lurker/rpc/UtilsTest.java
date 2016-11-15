package cn.ubuilding.lurker.rpc;

import org.junit.Test;

/**
 * @author Wu Jianfeng
 * @since 16/8/31 21:53
 */

public class UtilsTest {

    @Test
    public void testUtils() {
        String path = "/a/b/c/d";
        index(path);
    }

    private void index(String path) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            String sub = path.substring(0, i);
            System.out.println(sub);
            index(sub);
        }
    }
}
