package org.nitlanguage.ndt.core;

/**
 * Helper for String Array operations such as Join or remove the
 * lastMember of a String Array
 * @author lucas.bajolet
 */
public class StringArrayHelp {

	/**
	 * Joins the Strings of an array with the glue token (Similar to the
	 * Javascript/PHP function)
	 * 
	 * @param array
	 *            The String array to join
	 * @param joinGlue
	 *            The glue token
	 * @return The string joined by the function with the Glue
	 */
	public String join(String[] array, String joinGlue) {

		String finalStr = "";

		for (String bit : array) {
			finalStr += bit + joinGlue;
		}

		return finalStr.substring(0, finalStr.length() - joinGlue.length());
	}

	/**
	 * Removes the last element of the array (~= pop, but does not return the
	 * last value in the array)
	 * 
	 * @param array
	 *            The array to pop
	 * @return The new array sized array.length-1 or the original array if it
	 *         cannot be shrunk (original size == 0)
	 */
	public String[] removeLast(String[] array) {
		if (array.length > 0) {
			String[] destinationArray = new String[array.length - 1];

			System.arraycopy(array, 0, destinationArray, 0, array.length - 1);

			return destinationArray;
		} else {
			return array;
		}
	}

}
