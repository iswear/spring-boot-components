package me.iswear.springtestcontainer;

import org.springframework.boot.json.GsonJsonParser;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        String str = "\"c";
        String  key  = "test";
        Map<String ,String>  a = new HashMap<>();
        a.put(key, str);



        String out  = "{\""+ key +"\": \""+str+ "\"}";
        System.out.println(a.toString());
    }

}
