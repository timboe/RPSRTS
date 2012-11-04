package com.timboe.rpsrts.enumerators;

public class Pwd {
	public static String GetPass() {
		return s("6PxE0B2SE9EsEwG"); //This is obviously not going in github - true password is 6CkR0O2FR9RfRjT
	}
	private static String s(String s){
		String ss = "";
        for (int S = 0; S < s.length(); ++S) {
            char c = s.charAt(S);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            ss += c;
        }
        return ss;
	}
}
