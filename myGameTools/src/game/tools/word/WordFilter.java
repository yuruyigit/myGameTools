package game.tools.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.Character;

class WordFilter 
{
	private static WordFilter WORD_FILTER;
	public final static void init(WordFilter wordFilter){
		WORD_FILTER = wordFilter; 
	}
	
	public final static String filterStr(String str){
		
		return WORD_FILTER.filter(str);
	}
	
	public final static boolean hasKeyWord(String str){
		
		return WORD_FILTER.has(str);
	}
	
    protected Map<Character,String[]> dict;
    protected Collection<String> list;
    
    private WordFilter(){
        this.dict = new ConcurrentHashMap<Character, String[]>();
        this.list = new ArrayList<String>();
    }
    public WordFilter(String[] arr){
        this(Arrays.asList(arr));
    }
    public WordFilter(Collection<String> arr){
        this.dict = WordFilterUtil.toDict(arr);
        this.list = arr;
    }
    
    
    public WordFilter(String wordPath)
    {
    	this(WordFilterUtil.read(wordPath));
    }
    
    /**
     * 添加敏感词
     * @param word
     */
    public boolean add(String word){
        if(list.contains(word)){
            return false;
        }
        WordFilterUtil.addToDict(word, dict);
        list.add(word);
        return true;
    }
    
    /**
     * 过滤敏感词，如果有敏感词则会被替换掉
     * @param src           要处理的语句
     * @return  过滤后的语句
     */
    public String filter(String msg){
        return WordFilterUtil.filter(msg, dict);
    }
    
    /**
     * 检查是否包含敏感词
     * @param src           要检查的语句
     * @return  true表示有敏感词false表示没有
     */
    public boolean has(String msg){
        return WordFilterUtil.has(msg, dict);
    }
    
}
