package editor;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
public class WordProvider {
	
	//public KeyWords nitKeywords = new KeyWords();
 
    public List< String > suggestClasses(String word) {
        ArrayList< String >wordBuffer = new ArrayList< String >();
        //for(String s:nitKeywords.getKeyWords().keySet())
        for(String s:KeyWords.KEYWORDSNITCLASSES)
            if(s.startsWith(word))
                wordBuffer.add(s);
        Collections.sort(wordBuffer);
        return wordBuffer;
    }
    
    //public List< String > suggestMethods(String className, String word) {
    public List< String > suggestMethods(String word) {
        ArrayList< String >wordBuffer = new ArrayList< String >();
        //for(String s:nitKeywords.getKeyWordsByClass(className))
        for(String s:KeyWords.KEYWORDSNITMETHODS)
            if(s.startsWith(word))
                wordBuffer.add(s);
        Collections.sort(wordBuffer);
        return wordBuffer;
    }
}
