package io.github.guoshiqiufeng.loki.core.toolkit;

import lombok.experimental.UtilityClass;

/**
 * 字符串工具类
 * @author yanghq
 * @version 1.0
 * @since 2023/12/26 16:44
 */
@UtilityClass
public class StringUtils {

    /**
     * 判断字符串是否为空
     * @param cs 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     * @param cs 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }
}
