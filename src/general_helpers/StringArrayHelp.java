package general_helpers;

public class StringArrayHelp {
	
	public String join(String[] array, String joinGlue){
		
		String finalStr = "";
		
		for(String bit : array){
			finalStr += bit + joinGlue;
		}
		
		return finalStr.substring(0, finalStr.length() - joinGlue.length());
	}
	
	public String[] removeLast(String[] array){		
		String[] destinationArray = new String[array.length-1];
		
		System.arraycopy(array, 0, destinationArray, 0, array.length-1);
		
		return destinationArray;
	}
	
}
