package fr.uge.sed;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;

public class Main {

	public static void main(String[] args) throws IOException {
    var transformer = StreamEditor.createTransformer("u*2");
    var stringReader =
        new StringReader("*foo**\n");
    var writer = new CharArrayWriter();
    try(var reader = new BufferedReader(stringReader)) {
      StreamEditor.rewrite(reader, writer, transformer);
    }
    System.out.println(writer);

	}
}
