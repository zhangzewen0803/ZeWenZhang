package com.tahoecn.bo.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 将一个List集合拆分成指定长度的List
 *
 * @param list 母list
 * @param len 子list的长度
 * @return resultList 结果List<List>
 */
public class ListUtils {
    public static List<List> getSubList(List list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }
        List<List> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if ( i % len == 0 ) {
                int count = i/len;
                List subList = (List) list.stream().limit((count + 1) * len).skip(count * len).collect(Collectors.toList());
                resultList.add(subList);
            }
        }
        return resultList;
    }
}
