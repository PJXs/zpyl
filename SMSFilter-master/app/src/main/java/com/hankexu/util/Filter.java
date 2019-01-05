package com.hankexu.util;

import java.util.ArrayList;

/**
 * Created by hanke on 2015-10-22.
 */
public class Filter {

    /**
     * @param fromAddress 来源号码
     * @param addressList 要屏蔽的号码集
     * @return 如果匹配，返回true
     */
    public static boolean fromaddressFilter(String fromAddress, ArrayList<String> addressList) {
        boolean b = false;
        for (String s : addressList) {
            if (fromAddress.equals(s)) b = true;
        }
        return b;
    }/*号码拦截*/

    /**
     * @param messageBody 信息内容
     * @param keywordList 要屏蔽的关键字集
     * @return 如果信息内容包含关键字集任一元素，返回true
     */
    public static boolean keywordsFilter(String messageBody, ArrayList<String> keywordList) {
        boolean b = false;
        for (String s : keywordList) {
            if (messageBody.contains(s)) b = true;
        }
        return b;
    }
}
