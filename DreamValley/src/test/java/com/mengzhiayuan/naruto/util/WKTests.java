package com.mengzhiayuan.naruto.util;

import java.io.IOException;

/**
 * @Auther: 梦致A远
 * @Date: 2021/9/20 11:47
 * @Description:
 */

public class WKTests {

    public static void main(String[] args) {
        String cmd = "d:/work/wkhtmltopdf/bin/wkhtmltoimage --quality 75  https://www.nowcoder.com d:/work/data/wk-images/3.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("ok.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
