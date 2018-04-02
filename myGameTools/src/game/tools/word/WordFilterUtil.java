package game.tools.word;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Character;

class WordFilterUtil 
{
	
	public static void main(String[] args) 
	{
		
		String path = System.getProperty("user.dir");
		System.out.println("path = " + path);
		
		WordFilter wordFilter = new WordFilter("conf/bad_word.txt");
		String string = "太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
				+ "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
				+ "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
		
		long startTime = System.currentTimeMillis();
		System.out.println(wordFilter.filter(string));;
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime));
		
	}

	public static List<String> read(String filePath) {
        try {
            return Files.readAllLines(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
    /**
     * 分词, 敏感词列表-转换成敏感词词典
     * @param wordList
     * @return
     */
    public static Map<Character,String[]> toDict(Collection<String> wordList){
        Map<Character,String[]> dict = new ConcurrentHashMap<Character,String[]>();
        for (String s:wordList){
            char c=s.charAt(0);
            String[] words = dict.get(c);
            if(words==null){
                words = new String[1];
                words[0] = s;
                dict.put(c,words);
            }else{
                String[] newStrs = new String[words.length+1];
                System.arraycopy(words, 0, newStrs, 0, words.length);
                newStrs[newStrs.length-1] = s;
                dict.put(c,newStrs);
            }
        }
        return dict;
    }
    /**
     * 向敏感词库追加词语
     * @param s
     * @param dict
     */
    public static void addToDict(String s, Map<Character,String[]> dict){
        char c=s.charAt(0);
        String[] words = dict.get(c);
        if(words==null){
            words = new String[1];
            words[0] = s;
            dict.put(c,words);
        }else{
            String[] newStrs = new String[words.length+1];
            System.arraycopy(words, 0, newStrs, 0, words.length);
            newStrs[newStrs.length-1] = s;
            dict.put(c,newStrs);
        }
    }
    /**
     * 是否包含敏感词
     * @param str
     * @param wordList
     * @return
     */
    public static boolean has(String src, List<String> wordList){
        Map<Character,String[]> wordMap = toDict(wordList);
        return has(src, wordMap);
    }
    /**
     * 是否包含敏感词
     * @param str
     * @param dict
     * @return
     */
    public static boolean has(String str, Map<Character,String[]> dict){
        int strLen = str.length();
        for (int i=0;i<strLen;i++){
            Character c = str.charAt(i);
            if (dict.containsKey(c)){
                String[] words = dict.get(c);
                for (String s:words){
                    if(equals(s, str, i)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private static boolean equals(String a, String str, int i){
//        String temp = str.substring(i,(a.length()<=(str.length()-i))?i+a.length():i);
//        return a.equals(temp);
        if((a.length()+i)>str.length()){
            return false;
        }
        int end = i+a.length();
        for(int j=0;i<end;i++){
            if(str.charAt(i)!=a.charAt(j)){
                return false;
            }
            j++;
        }
        return true;
    }
    /**
     * 过滤敏感词，如果有敏感词则会被替换掉
     * @param str  要处理的字符串
     * @param list 敏感词库
     * @return
     */
    public static String filter(String str, List<String> list){
        Map<Character,String[]> wordMap = toDict(list);
        return filter(str, wordMap);
    }
    /**
     * 过滤敏感词，如果有敏感词则会被替换掉
     * @param str  要处理的字符串
     * @param dict 敏感词库
     * @return
     */
    public static String filter(String str, Map<Character,String[]> dict){
        int strLen = str.length();
        StringBuilder strb = new StringBuilder(strLen+128);
        for (int i=0;i<strLen;i++){
            Character c = str.charAt(i);
            String find = null;
            if (dict.containsKey(c)){
                String[] words = dict.get(c);
                for (String s:words){
                    if(equals(s, str, i)){
                        find = s;
                        break;
                    }
                }
            }
            if (find!=null){
                strb.append(makeReplace(find));
                i += (find.length()-1);
            } else {
                strb.append(c.charValue());
            }
        }
        return strb.toString();
    }
    private static char[] makeReplace(char c, int len){
        char[] arr = new char[len];
        for(int i=0;i<len;i++){
            arr[i] = c;
        }
        return arr;
    }
    private static char[] makeReplace(String str){
        int len = str.length();
        if(len<fixFast.length){
            return fixFast[len-1];
        }
        return makeReplace(fixChar, len);
    }
    public static char fixChar = '*';
    public static char[][] fixFast = {"*".toCharArray(),
                                        "**".toCharArray(),
                                        "***".toCharArray(),
                                        "****".toCharArray(),
                                        "*****".toCharArray(),
                                        "******".toCharArray(),
                                        "*******".toCharArray(),
                                        "********".toCharArray(),
                                        "*********".toCharArray()};
    
}
