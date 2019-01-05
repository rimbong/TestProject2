package common.util;

public class UtilString {

    
    public static String text2Html(String txt) {
        txt = txt.replaceAll(" ", "&nbsp;");
        txt = txt.replaceAll("<", "&lt;");
        return txt.replaceAll("\n", "<br>");
    } 
}
