package fr.uge.sed;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.AccessFlag;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StreamEditorTest {
  @Nested
  public class Q1 {
    @Test
    public void rewriteOneLineUpperCase() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var stringReader = new StringReader("hello\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("HELLO\n", writer.toString());
    }

    @Test
    public void rewriteOneLineLowerCase() throws IOException {
      var transformer = StreamEditor.createTransformer("l");
      var stringReader = new StringReader("HeLlo\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("hello\n", writer.toString());
    }

    @Test
    public void rewriteOneLineStarTwo() throws IOException {
      var transformer = StreamEditor.createTransformer("*2");
      var stringReader =
          new StringReader("*foo**\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("**foo****\n", writer.toString());
    }

    @Test
    public void rewriteOneLineStarNine() throws IOException {
      var transformer = StreamEditor.createTransformer("*9");
      var stringReader = new StringReader("*foo**\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("*********foo******************\n", writer.toString());
    }

    @Test
    public void rewriteOneLineStarZero() throws IOException {
      var transformer = StreamEditor.createTransformer("*0");
      var stringReader = new StringReader("*bar**\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("bar\n", writer.toString());
    }

    @Test
    public void invalidTransformerUnknownCommand() {
      assertThrows(IllegalArgumentException.class, () -> StreamEditor.createTransformer("z"));
    }

    @Test
    public void rewriteSeveralLinesUpperCase() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var reader = new StringReader("""
          foo
          bar
          baz
          """);
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals("""
          FOO
          BAR
          BAZ
          """, writer.toString());
    }

    @Test
    public void rewriteSeveralLinesLowerCase() throws IOException {
      var transformer = StreamEditor.createTransformer("l");
      var reader = new StringReader("""
          foo
          BaR
          BAZ
          """);
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals("""
          foo
          bar
          baz
          """, writer.toString());
    }

    @Test
    public void rewriteSeveralLinesStarFive() throws IOException {
      var transformer = StreamEditor.createTransformer("*5");
      var reader = new StringReader("""
          f*o
          bar
          **Z
          """);
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals("""
          f*****o
          bar
          **********Z
          """, writer.toString());
    }

    @Test
    public void rewriteEmpty() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var reader = Reader.nullReader();
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals("", writer.toString());
    }

    @Test
    public void rewriteALotOfLines() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var text = IntStream.range(0, 100_000).mapToObj(i -> i + "\n").collect(joining(""));
      var reader = new StringReader(text);
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals(text, writer.toString());
    }

    @Test
    public void rewriteAnUngodlyNumberOfLines() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var reader = new Reader() {
        private int index;
        private boolean closed;

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
          Objects.checkFromIndexSize(off, len, cbuf.length);
          if (closed) {
            throw new IOException("closed");
          }
          var last = Math.min(1_000_000_000, index + len);
          var toRead = Math.min(last - index, len);
          for(var i = 0; i < toRead; i++) {
            cbuf[off + i] = (index + i) % 20 == 0 ? '\n' : 'A';
          }
          if (toRead == 0) {
            return -1;
          }
          index += toRead;
          return toRead;
        }

        @Override
        public void close() {
          closed = true;
        }
      };
      var writer = Writer.nullWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
    }

    @Test
    public void streamEditorClassIsFinal() {
      assertAll(
          () -> assertTrue(StreamEditor.class.accessFlags().contains(AccessFlag.FINAL)),
          () -> assertTrue(StreamEditor.class.accessFlags().contains(AccessFlag.PUBLIC))
      );
    }

    @Test
    public void streamEditorConstructorIsNotVisible() {
      assertAll(
          () -> assertEquals(0, StreamEditor.class.getConstructors().length)
      );
    }

    @Test
    public void streamEditorTransformerImplementationAreFinals() {
      assertAll(
          () -> assertTrue(StreamEditor.createTransformer("u").getClass().accessFlags().contains(AccessFlag.FINAL)),
          () -> assertTrue(StreamEditor.createTransformer("l").getClass().accessFlags().contains(AccessFlag.FINAL)),
          () -> assertTrue(StreamEditor.createTransformer("*7").getClass().accessFlags().contains(AccessFlag.FINAL))
      );
    }

    @Test
    public void preconditions() {
      var transformer = StreamEditor.createTransformer("u");
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.createTransformer(null)),
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(null, Writer.nullWriter(), transformer)),
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(new BufferedReader(Reader.nullReader()), null, transformer)),
          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(new BufferedReader(Reader.nullReader()), Writer.nullWriter(), null))
      );
    }
  }

  @Nested
  public class Q2 {
    @Test
    public void createTransformerUppercaseDotlessI() throws IOException {
      // see https://en.wikipedia.org/wiki/Dotless_I
      var transformer = StreamEditor.createTransformer("u");
      var stringReader = new StringReader("i\n");
      var writer = new CharArrayWriter();

      var oldLocale = Locale.getDefault();
      Locale.setDefault(Locale.forLanguageTag("tr-tr"));
      try {
        try(var reader = new BufferedReader(stringReader)) {
          StreamEditor.rewrite(reader, writer, transformer);
        }
      } finally {
        Locale.setDefault(oldLocale);
      }
      assertEquals("I\n", writer.toString());
    }

    @Test
    public void createTransformerLowercaseDotlessI() throws IOException {
      // see https://en.wikipedia.org/wiki/Dotless_I
      var transformer = StreamEditor.createTransformer("l");
      var stringReader = new StringReader("I\n");
      var writer = new CharArrayWriter();

      var oldLocale = Locale.getDefault();
      Locale.setDefault(Locale.forLanguageTag("tr-tr"));
      try {
        try(var reader = new BufferedReader(stringReader)) {
          StreamEditor.rewrite(reader, writer, transformer);
        }
      } finally {
        Locale.setDefault(oldLocale);
      }
      assertEquals("i\n", writer.toString());
    }

    @Test
    public void createTransformerUpperCaseNonLatin() throws IOException {
      var transformer = StreamEditor.createTransformer("u");
      var stringReader = new StringReader("\u03bb\n");
      var writer = new CharArrayWriter();

      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }

      assertEquals("\u039b\n", writer.toString());
    }

    @Test
    public void createTransformerLowerCaseNonLatin() throws IOException {
      var transformer = StreamEditor.createTransformer("l");
      var stringReader = new StringReader("\u039b\n");
      var writer = new CharArrayWriter();

      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }

      assertEquals("\u03bb\n", writer.toString());
    }
  }


  @Nested
  public class Q3 {
    @Test
    public void ifTheInterfaceIsSealedItShouldNotHaveAnyMethod() {
      var transformerInterface = StreamEditor.createTransformer("u").getClass().getInterfaces()[0];
      if (transformerInterface.isSealed()) {
        assertEquals(0, transformerInterface.getMethods().length);
      }
    }
  }

  @Nested
  public class Q4 {
    @Test
    public void ifTheImplementationUsesALambdaProxyTheInterfaceShouldBeFunctional() throws IOException {
      var transformerClass = StreamEditor.createTransformer("u").getClass();
      if (transformerClass.isHidden()) {
        var transformerInterface = transformerClass.getInterfaces()[0];
        assertTrue(transformerInterface.isAnnotationPresent(FunctionalInterface.class));
      }
    }
  }


  @Nested
  public class Q5 {
    @Test
    public void rewriteOneLineUpperCaseThenLowerCase() throws IOException {
      var transformer = StreamEditor.createTransformer("ul");
      var stringReader = new StringReader("hEllo\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("hello\n", writer.toString());
    }

    @Test
    public void rewriteOneLineLowerCaseThenUpperCase() throws IOException {
      var transformer = StreamEditor.createTransformer("lu");
      var stringReader = new StringReader("HeLlo\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("HELLO\n", writer.toString());
    }

    @Test
    public void rewriteOneLineStarTwoThenUpperCase() throws IOException {
      var transformer = StreamEditor.createTransformer("*2u");
      var stringReader =
          new StringReader("*foo**\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("**FOO****\n", writer.toString());
    }

    @Test
    public void rewriteOneLineUpperCaseThenStarNine() throws IOException {
      var transformer = StreamEditor.createTransformer("u*9");
      var stringReader = new StringReader("*foo**\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("*********FOO******************\n", writer.toString());
    }

    @Test
    public void rewriteSeveralLinesLowerCaseThenUpperCaseThenAgain() throws IOException {
      var transformer = StreamEditor.createTransformer("lulu");
      var reader = new StringReader("""
          fOO
          bAr
          Baz
          """);
      var writer = new CharArrayWriter();
      try(var bufferedReader = new BufferedReader(reader)) {
        StreamEditor.rewrite(bufferedReader, writer, transformer);
      }
      assertEquals("""
          FOO
          BAR
          BAZ
          """, writer.toString());
    }

    @Test
    public void rewriteOneLineNoCommand() throws IOException {
      var transformer = StreamEditor.createTransformer("");
      var stringReader = new StringReader("HeLLo\n");
      var writer = new CharArrayWriter();
      try(var reader = new BufferedReader(stringReader)) {
        StreamEditor.rewrite(reader, writer, transformer);
      }
      assertEquals("HeLLo\n", writer.toString());
    }
  }


  @Nested
  public class Q6 {
    @Test
    public void parserClass() throws IOException {
      assertAll(
          () -> assertTrue(StreamEditor.Parser.class.accessFlags().contains(AccessFlag.STATIC)),
          () -> assertTrue(StreamEditor.Parser.class.accessFlags().contains(AccessFlag.FINAL))
      );
    }
  }

//
//  @Nested
//  public class Q8 {
//    private static String rewriteUsingFiles(String text, String commands) throws IOException {
//      var directory = Files.createTempDirectory("stream-editor-test");
//      try {
//        var inputPath = directory.resolve("input.txt");
//        var outputPath = directory.resolve("output.txt");
//        try {
//          Files.writeString(inputPath, text);
//          StreamEditor.rewrite(inputPath, outputPath, StreamEditor.createTransformer(commands));
//          return Files.readString(outputPath);
//        } finally {
//          Files.deleteIfExists(outputPath);
//          Files.deleteIfExists(inputPath);
//        }
//      } finally {
//        Files.delete(directory);
//      }
//    }
//
//    @Test
//    public void rewriteOneLineUpperCase() throws IOException {
//      var text = """
//          hello my name is Joey
//          """;
//      var result = rewriteUsingFiles(text, "u");
//      assertEquals("""
//        HELLO MY NAME IS JOEY
//        """, result);
//    }
//
//    @Test
//    public void rewriteALotOfLinesUpperCase() throws IOException {
//      var text = IntStream.range(0, 100_000).mapToObj(i -> i + "\n").collect(joining(""));
//      var result = rewriteUsingFiles(text, "u");
//      assertEquals(text, result);
//    }
//
//    @Test
//    public void rewriteOneLineLowerCase() throws IOException {
//      var text = """
//          hello my name is Joey
//          """;
//      var result = rewriteUsingFiles(text, "l");
//      assertEquals("""
//        hello my name is joey
//        """, result);
//    }
//
//    @Test
//    public void rewriteALotOfLinesLowerCase() throws IOException {
//      var text = IntStream.range(0, 100_000).mapToObj(i -> i + "\n").collect(joining(""));
//      var result = rewriteUsingFiles(text, "l");
//      assertEquals(text, result);
//    }
//
//    @Test
//    public void rewriteOneLineStarTwo() throws IOException {
//      var text = """
//          hello my *name* is Joey
//          """;
//      var result = rewriteUsingFiles(text, "*2");
//      assertEquals("""
//        hello my **name** is Joey
//        """, result);
//    }
//
//    @Test
//    public void rewriteALotOfLinesStarTwo() throws IOException {
//      var text = IntStream.range(0, 100_000).mapToObj(i -> i + "\n").collect(joining(""));
//      var result = rewriteUsingFiles(text, "*2");
//      assertEquals(text, result);
//    }
//
//    @Test
//    public void rewriteOneLineStarTwoStarFive() throws IOException {
//      var text = """
//          hello my *name* is Joey
//          """;
//      var result = rewriteUsingFiles(text, "*2*5");
//      assertEquals("""
//        hello my **********name********** is Joey
//        """, result);
//    }
//
//    @Test
//    public void rewriteALotOfLinesStarTwoStarFive() throws IOException {
//      var text = IntStream.range(0, 100_000).mapToObj(i -> i + "\n").collect(joining(""));
//      var result = rewriteUsingFiles(text, "*2*5");
//      assertEquals(text, result);
//    }
//
//    @Test
//    public void rewritePreconditions() {
//      var transformer = StreamEditor.createTransformer("u");
//      assertAll(
//          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(null, Path.of("."), transformer)),
//          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(Path.of("."), null, transformer)),
//          () -> assertThrows(NullPointerException.class, () -> StreamEditor.rewrite(Path.of("."), Path.of("."), null))
//      );
//    }
//
//    @Test
//    public void rewriteOneLineDelete() throws IOException {
//      var transformer = StreamEditor.createTransformer("d");
//      var stringReader = new StringReader("hEllo\n");
//      var writer = new CharArrayWriter();
//      try (var reader = new BufferedReader(stringReader)) {
//        StreamEditor.rewrite(reader, writer, transformer);
//      }
//      assertEquals("", writer.toString());
//    }
//
//    @Test
//    public void rewriteSeveralLinesDelete() throws IOException {
//      var transformer = StreamEditor.createTransformer("d");
//      var stringReader =
//          new StringReader("""
//            I do not want to die
//            I do not want to die
//            I do not want to die
//            """);
//      var writer = new CharArrayWriter();
//      try (var reader = new BufferedReader(stringReader)) {
//        StreamEditor.rewrite(reader, writer, transformer);
//      }
//      assertEquals("", writer.toString());
//    }
//
//    @Test
//    public void rewriteSeveralLinesUpperCaseThenDelete() throws IOException {
//      var transformer = StreamEditor.createTransformer("ud");
//      var stringReader =
//          new StringReader("""
//            This is america
//            Don't catch you slippin' now
//            """);
//      var writer = new CharArrayWriter();
//      try (var reader = new BufferedReader(stringReader)) {
//        StreamEditor.rewrite(reader, writer, transformer);
//      }
//      assertEquals("", writer.toString());
//    }
//
//    @Test
//    public void rewriteSeveralLinesDeleteThenUpperCaseThenLowerCaseThenLowerCase() throws IOException {
//      var transformer = StreamEditor.createTransformer("dull");
//      var stringReader =
//          new StringReader("""
//            This is america
//            Don't catch you slippin' now
//            """);
//      var writer = new CharArrayWriter();
//      try (var reader = new BufferedReader(stringReader)) {
//        StreamEditor.rewrite(reader, writer, transformer);
//      }
//      assertEquals("", writer.toString());
//    }
//  }
}