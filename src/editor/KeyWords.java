package editor;

public class KeyWords {
	public static final String[] KEYWORDDEFAULT = { "abort", "abstract", "as",
			"assert", "continue", "enum", "externe", "import", "interface",
			"inter", "intrude", "is", "isa", "isset", "new", "label", "loop",
			"readable", "null", "nullable", "print", "redef", "fun", "type", "init",
			"var", "package" };
	public static final String[] KEYWORDSNITALUMINIUM = { "break", "do",
			"else", "end", "for", "if", "in", "private", "protected", "return",
			"then", "while", "and", "not", "module"};
	public static final String[] KEYWORDSNITORANGE = { "false", "true" };
	public static final String[] KEYWORDSNITSKYBLUE = { "super", "assert",
			"exception" };

	public static final String [] KEYWORDSNITCLASSES = {"FilterIStream","FilterOStream","StreamCat","StreamDemux","SDL_EventListener",
		"SDL_EventProcessor","TickCounter","Rectangle","Sprite","DummyArray","DummyIterator","Option","OptionText","OptionBool",
		"OptionCount","OptionParameter","OptionString","OptionEnum","OptionInt","OptionArray","OptionContext","Sys","HashMap[K: Object, V]",
		"HashMapNode[K: Object, V]","HashMapIterator[K: Object, V]","HashSet[E: Object]","HashSetNode[E: Object]","HashSetIterator[E: Object]",
		"AbstractSorter[E: Object]","ComparableSorter[E: Comparable]","Range[E: Discrete]","IteratorRange[E: Discrete]","AbstractArrayRead[E]",
		"AbstractArray[E]","Array[E]","ArrayIterator[E]","ArraySet[E: Object]","ArraySetIterator[E: Object]","ArrayMap[K: Object, E]",
		"Container[E]","ContainerIterator[E]","CoupleMapIterator[K: Object, E]","Couple[F, S]","List[E]","ListIterator[E]","BM_Pattern",
		"Match","IOS","IStream","OStream","BufferedIStream","IOStream","FDStream","FDIStream","FDOStream","FDIOStream","Symbol","String",
		"Buffer","NativeString","StringCapable","Process","IProcess","OProcess","IOProcess","FStream","IFStream","OFStream","Stdin","Stdout",
		"Stderr","Object","Comparable","Discrete","ArrayCapable[E]","Collection[E]","NaiveCollection[E]","Iterator[E]","RemovableCollection[E]",
		"SimpleCollection[E]","Set[E: Object]","MapRead[K: Object, E]","Map[K: Object, E]","MapIterator[K: Object, E]","SequenceRead[E]",
		"Sequence[E]","IndexedIterator[E]","CoupleMap[K: Object, E]","Pattern","SDL_Surface","SDL_Screen","SDL_Event","SDL_ButtonEvent",
		"SDL_MouseEvent","SDL_KeyboardEvent","SDL_MouseButtonEvent","SDL_MouseMotionEvent","Bool","Float","Int","Char","Pointer","NativeArray[E]",
		"FileStat"};
	public static final String [] KEYWORDSNITMETHODS = {"to_a", "to_s"};
	/*public HashMap<String, String[]> METHODTABLE = null;

	public static final String[] MARRAY = { "to_a", "to_s" };
	public static final String[] MTOTO = { "toto" };

	*/
	/**
	 * Return the list of keywords associated to classes
	 * @return
	 */
	/*public HashMap<String, String[]> getKeyWords() {
		if (METHODTABLE == null) {
			HashMap<String, String[]> kw = new HashMap<String, String[]>();

			kw.put("Array
			kw.put("Toto", MTOTO);

			METHODTABLE = kw;
		}

		return METHODTABLE;
	}*/
	
	/**
	 * Return the list of method of a specified class
	 * @param method
	 * @return
	 */
	/*public String[] getKeyWordsByClass(String className) {
		return METHODTABLE.get(className);
	}*/
}
