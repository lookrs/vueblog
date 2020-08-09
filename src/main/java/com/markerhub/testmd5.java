package com.markerhub;

import cn.hutool.crypto.SecureUtil;

public class testmd5 {
    public static void main(String[] args) {
        String src = "111111";
        String res = SecureUtil.md5(src);
        System.out.println(res);
    }
}
