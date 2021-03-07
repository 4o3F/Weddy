package moe.exusiai.handler.messagefilter;

import moe.exusiai.handler.messagefilter.algorithm.SensitivewordFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageFilter {
    public static List<String> banCharList = new ArrayList<String>();
    public static String maskSymbol = "爱你";

    String REGEX_1 = "[a-zA-Z\\u4e00-\\u9fa5]"; // DFA Cycle 1 RegEx
    String REGEX_2 = "[\\u4e00-\\u9fa5]"; // DFA Cycle 2 RegEx
    String REGEX_3 = "[操干日]";  // 中性词 1 类 [排除单用/混用问题]
    String REGEX_4 = "[比币逼笔爹娘爸妈爷奶儿孙哥姐弟妹姑叔舅姨祖宗先辈人狗猪鸡鸭的吗a-zA-Z&\\&0-9]"; // 中性词 2类 [排除激烈/中性问题]

    /**
     * 忽略大小写进行文本替换
     *
     * @Description: Replace string without case sensitive
     */
//    public static String ignoreCaseReplace(String source, String oldstring, String newstring) {
//        Pattern p = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(source);
//        String ret = m.replaceAll(newstring);
//        return ret;
//    }

    public Boolean MessageFilter(String message) {
        boolean isSensitive = false;
        SensitivewordFilter filter = new SensitivewordFilter();

        /**
         * 正则过滤 初步干扰
         * @Description: 忽略所有数字、符号，查找：    草fu！ck拟123123#@￥吗 【不更改inputMsg，只做替换】 目的：找出 fuck
         */

        String regEx0 = REGEX_1;
        Pattern p = Pattern.compile(regEx0);
        Matcher m = p.matcher(message);
        StringBuffer finalStr = new StringBuffer();
        while (m.find()) {
            finalStr.append(m.group());
        }
        String buffString_1 = finalStr.toString();    // 用于处理的字符串 【仅在检测到敏感词后使用】

        /**
         * 中性词过滤
         * @Description: 判断中性词在不同场合的运用，若单独一个中性词则代表攻击词 【仅在忽略干扰后存在的单个字符】 排除：操     但不排除：操场
         */

        if (buffString_1.length() == 1) {
            String regExLvl_0 = REGEX_3;
            Pattern p0 = Pattern.compile(regExLvl_0);
            Matcher m0 = p0.matcher(message);
            buffString_1 = m0.replaceAll(maskSymbol).trim();
        }


        /**
         * DFA Cycle-1
         * @Description: 全句多字检查   第一批 【主要针对英文】
         */

        Set<String> set = filter.getSensitiveWord(buffString_1.toLowerCase(), 1);
        if (!set.isEmpty()) { // 敏感句处理：替换
            isSensitive = true;
            return isSensitive;
        }

//
//        /**
//         * postProcess DFA Cycle-1
//         * @Description: 若非敏感，恢复原句；若是敏感，强制替换二级敏感词：爸妈等。
//         */
//
//        if (!isSensitive) {
//            buffString_1 = inputMsg;
//        } else {
//            String regExLvl_3 = REGEX_4;
//            Pattern p3 = Pattern.compile(regExLvl_3);
//            Matcher m3 = p3.matcher(buffString_1);
//            buffString_1 = m3.replaceAll("").trim();
//        }

        /**
         * 正则过滤 第二层干扰
         * @Description: 忽略所有英文、数字、符号，查找：    草←asd@ #拟1-2sadefvse3吗 【不更改inputMsg，只做替换】 目的：找出  草拟吗
         */

        String regEx1 = REGEX_2;
        Pattern p1 = Pattern.compile(regEx1);
        Matcher m1 = p1.matcher(message);
        StringBuffer finalStr2 = new StringBuffer();
        while (m1.find()) {
            finalStr2.append(m1.group());
        }
        buffString_1 = finalStr2.toString();  // 用于处理的字符串 【仅在检测到敏感词后使用】


        /**
         * DFA Cycle-2
         * @Description: 全句多字检查   第二批 【主要针对  中文、以及中英文混淆敏感词】
         */

        SensitivewordFilter filter2 = new SensitivewordFilter();
        Set<String> set2 = filter2.getSensitiveWord(buffString_1.toLowerCase(), 1);

        if (!set2.isEmpty()) { // 敏感句处理：替换
            isSensitive = true;
            return isSensitive;
            // 先替换全中文，再替换中英混合，避免因小失大
//            for (String x : set2) {
//                buffString_1 = ignoreCaseReplace(buffString_1, x, ConfigHandler.maskSymbol);
//            }
//            for (String y : set) {
//                buffString_1 = ignoreCaseReplace(buffString_1, y, ConfigHandler.maskSymbol);
//            }
        }

        /**
         * postProcess DFA Cycle-2
         * @Description: 全句单字检查： 激烈单字匹配（弥补DFA算法局限性）
         */

        if (!isSensitive) {                // 非敏感句  用原始句子来检查
            isSensitive = doCharCheck(isSensitive, message);
            return isSensitive;
        } else {                            // 敏感句  用强制删除干扰符、二级敏感词之后的句子来检查
            String regExLvl_3 = REGEX_4;
            Pattern p3 = Pattern.compile(regExLvl_3);
            Matcher m3 = p3.matcher(buffString_1);
            buffString_1 = m3.replaceAll("").trim();
            isSensitive = doCharCheck(isSensitive,buffString_1);
            return isSensitive;
        }
    }

    /**
     * 激烈单字匹配（弥补DFA算法局限性）
     *
     * @Description: Check the single Chinese letter which is highly aggressive (fix DFA)
     */
    private boolean doCharCheck(boolean isSensitive, String pMessage) {
        for (String charBan : MessageFilter.banCharList) {
            if (pMessage.contains(charBan)) {
                isSensitive = true;
            }
        }
        return isSensitive;
    }

}