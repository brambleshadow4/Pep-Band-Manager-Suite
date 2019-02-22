package pepband3.gui.extra;

import java.text.*;

public class URITools {
	
	public static String replaceEscapeCharacters(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		StringCharacterIterator charIterator = new StringCharacterIterator(string);
		Character character =  charIterator.current();
		while (character != CharacterIterator.DONE ) {
			if (character == ' ') {
				stringBuilder.append("%20");
			} else if (character == '\"') {
				stringBuilder.append("%22");
			} else if (character == '#') {
				stringBuilder.append("%23");
			} else if (character == '$') {
				stringBuilder.append("%24");
			} else if (character == '%') {
				stringBuilder.append("%25");
			} else if (character == '&') {
				stringBuilder.append("%26");
			} else if (character == '+') {
				stringBuilder.append("%2B");
			} else if (character == ',') {
				stringBuilder.append("%2C");
			} else if (character == '/') {
				stringBuilder.append("%2F");
			} else if (character == ':') {
				stringBuilder.append("%3A");
			} else if (character == ';') {
				stringBuilder.append("%3B");
			} else if (character == '<') {
				stringBuilder.append("%3C");
			} else if (character == '=') {
				stringBuilder.append("%3D");
			} else if (character == '>') {
				stringBuilder.append("%3E");
			} else if (character == '?') {
				stringBuilder.append("%3F");
			} else if (character == '@') {
				stringBuilder.append("%40");
			} else if (character == '[') {
				stringBuilder.append("%5B");
			} else if (character == '\\') {
				stringBuilder.append("%5C");
			} else if (character == ']') {
				stringBuilder.append("%5D");
			} else if (character == '^') {
				stringBuilder.append("%5E");
			} else if (character == '`') {
				stringBuilder.append("%60");
			} else if (character == '{') {
				stringBuilder.append("%7B");
			} else if (character == '|') {
				stringBuilder.append("%7C");
			} else if (character == '}') {
				stringBuilder.append("%7D");
			} else if (character == '~') {
				stringBuilder.append("%7E");
			} else {
				stringBuilder.append(character);
			}
			character = charIterator.next();
		}
		return stringBuilder.toString();
	}
}