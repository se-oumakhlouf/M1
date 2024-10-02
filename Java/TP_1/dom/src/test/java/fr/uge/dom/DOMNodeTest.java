package fr.uge.dom;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Method;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

public class DOMNodeTest {
  @Nested
  public class Q1 {
    @Test
    public void createElement() {
      DOMDocument document = new DOMDocument();
      DOMNode node = document.createElement("div", Map.of());
      assertEquals("div", node.name());
    }

    @Test
    public void createTwoElements() {
      var document = new DOMDocument();
      var node1 = document.createElement("foo", Map.of());
      var node2 = document.createElement("bar", Map.of());
      assertAll(
          () -> assertEquals("foo", node1.name()),
          () -> assertEquals("bar", node2.name())
      );
    }

    @Test
    public void twoElementsCanNotBeEquals() {
      var document = new DOMDocument();
      var node1 = document.createElement("foo", Map.of());
      var node2 = document.createElement("foo", Map.of());
      assertAll(
          () -> assertFalse(node1 == node2),
          () -> assertFalse(node1.equals(node2))
      );
    }

    @Test
    public void createElementPreconditions() {
      var document = new DOMDocument();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> document.createElement(null, Map.of())),
          () -> assertThrows(NullPointerException.class, () -> document.createElement("foo", null))
      );

    }

    @Test
    public void createElementWithAttributes() {
      var document = new DOMDocument();
      var node = document.createElement("div", Map.of("color", "red", "visible", true));
      assertAll(
          () -> assertEquals("div", node.name()),
          () -> assertEquals(Map.of("color", "red", "visible", true), node.attributes())
      );
    }

    @Test
    public void createElementWithDifferentAttributes() {
      var document = new DOMDocument();
      assertAll(
          () -> assertEquals("bar",
              document.createElement("div", Map.of("foo", "bar")).attributes().get("foo")),
          () -> assertEquals(12,
              document.createElement("div", Map.of("foo", 12)).attributes().get("foo")),
          () -> assertEquals(12L,
              document.createElement("div", Map.of("foo", 12L)).attributes().get("foo")),
          () -> assertEquals(2.0,
              document.createElement("div", Map.of("foo", 2.0)).attributes().get("foo")),
          () -> assertEquals(2.0f,
              document.createElement("div", Map.of("foo", 2.0f)).attributes().get("foo")),
          () -> assertEquals(true,
              document.createElement("div", Map.of("foo", true)).attributes().get("foo")),
          () -> assertEquals(false,
              document.createElement("div", Map.of("foo", false)).attributes().get("foo"))
      );
    }

    @Test
    public void createElementWithAnEmptyAttributes() {
      var document = new DOMDocument();
      var node = document.createElement("div", Map.of());
      assertEquals(Map.of(), node.attributes());
    }

    @Test
    public void attributesIsAnUnmodifiableMap() {
      var document = new DOMDocument();
      var node = document.createElement("foo", Map.of("bar", true));
      var attributes = node.attributes();
      assertThrows(UnsupportedOperationException.class, () -> attributes.put("bar", false));
    }

    @Test
    public void elementAttributesIsNotModifiableAfterCreation() {
      var document = new DOMDocument();
      var attributes = new HashMap<String, Object>(Map.of("bar", true));
      var node = document.createElement("foo", attributes);

      attributes.put("baz", 42);
      assertEquals(Map.of("bar", true), node.attributes());
    }

    @Test
    public void createElementWithALotOfAttributes() {
      var document = new DOMDocument();
      var attributes = IntStream.range(0, 1_000_000)
          .mapToObj(Integer::toString)
          .collect(toMap(s -> s, s -> (Object) s));
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        var node = document.createElement("foo", attributes);
        assertNotNull(node);
      });
    }

    @Test
    public void createElementWithAttributesPreconditions() {
      var document = new DOMDocument();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> document.createElement("div", new HashMap<>() {{ put("foo", null); }})),
          () -> assertThrows(NullPointerException.class, () -> document.createElement("div", new HashMap<>() {{ put(null, "bar"); }})),
          () -> assertThrows(IllegalArgumentException.class, () -> document.createElement("div", Map.of("foo", new URI("bar")))),
          () -> assertThrows(IllegalArgumentException.class, () -> document.createElement("div", Map.of("foo", new Object())))
      );
    }
  }

  @Nested
  public class Q2 {
    @Test
    public void qualityOfImplementation() {
      var document = new DOMDocument();
      var node = document.createElement("foo", Map.of());
      assertAll(
          () -> assertTrue(DOMNode.class.isInterface()),
          () -> assertTrue(DOMNode.class.accessFlags().contains(AccessFlag.PUBLIC)),
          () -> assertTrue(DOMDocument.class.accessFlags().contains(AccessFlag.PUBLIC)),
          () -> assertTrue(DOMDocument.class.accessFlags().contains(AccessFlag.FINAL)),
          () -> assertFalse(node.getClass().accessFlags().contains(AccessFlag.PUBLIC)),
          () -> assertTrue(node.getClass().accessFlags().contains(AccessFlag.FINAL))
      );
    }

    @Test
    public void implementationHasPublicConstructors() {
      var node = new DOMDocument().createElement("foo", Map.of());
      assertEquals(0, node.getClass().getConstructors().length);
    }

    @Test
    public void implementationFieldsArePrivate() {
      var node = new DOMDocument().createElement("foo", Map.of());
      var fields = node.getClass().getDeclaredFields();
      assertTrue(Arrays.stream(fields).allMatch(f -> f.accessFlags().contains(AccessFlag.PRIVATE)));
    }

    @Test
    public void implementationFieldsThatAreEitherTheNameACollectionOrTheDomDocumentAreFinal() {
      var node = new DOMDocument().createElement("foo", Map.of());
      var fields = node.getClass().getDeclaredFields();
      assertTrue(Arrays.stream(fields)
          .filter(f -> f.getName().equals("name") || f.getType() == DOMDocument.class || f.getType().getPackageName().equals("java.util"))
          .allMatch(f -> f.accessFlags().contains(AccessFlag.FINAL)));
    }

    @Test
    public void interfaceHasOnlyOneImplementation() {
      var permittedSubclasses = DOMNode.class.getPermittedSubclasses();
      assertTrue(permittedSubclasses != null && permittedSubclasses.length == 1);
    }

    @Test
    public void interfaceMethodsLeakNonPublicTypes() {
      assertTrue(Arrays.stream(DOMNode.class.getMethods())
          .allMatch(m -> m.getReturnType().accessFlags().contains(AccessFlag.PUBLIC) &&
              Arrays.stream(m.getParameterTypes()).allMatch(p -> p.accessFlags().contains(AccessFlag.PUBLIC))));
    }

    @Test
    public void documentMethodsLeakNonPublicTypes() {
      assertTrue(Arrays.stream(DOMDocument.class.getMethods())
          .allMatch(m -> m.getReturnType().accessFlags().contains(AccessFlag.PUBLIC) &&
              Arrays.stream(m.getParameterTypes()).allMatch(p -> p.accessFlags().contains(AccessFlag.PUBLIC))));
    }

    @Test
    public void interfaceHasTooManyPublicMethods() {
      var methods = Arrays.stream(DOMNode.class.getMethods())
          .filter(m -> m.getDeclaringClass() != Object.class)
          .map(Method::getName)
          .collect(toSet());
      assertTrue(Set.of("name", "attributes", "children", "appendChild").containsAll(methods), "" + methods);
    }

    @Test
    public void documentHasTooManyPublicMethods() {
      var methods = Arrays.stream(DOMDocument.class.getMethods())
          .filter(m -> m.getDeclaringClass() != Object.class)
          .map(Method::getName)
          .collect(toSet());
      assertTrue(Set.of("createElement", "getElementById").containsAll(methods), "" + methods);
    }
  }

  @Nested
  public class Q3 {
    @Test
    public void createElementWithAnId() {
      var document = new DOMDocument();
      var node = document.createElement("div", Map.of("id", "foo42"));

      assertSame(node, document.getElementById("foo42"));
    }

    @Test
    public void createSeveralElementsWithTheSameId() {
      var document = new DOMDocument();
      var node1 = document.createElement("div", Map.of("id", "id12"));
      document.createElement("span", Map.of("id", "id12"));

      assertSame(node1, document.getElementById("id12"));
    }

    @Test
    public void getElementByIdFastEnough() {
      var document = new DOMDocument();
      for(var i = 0; i < 1_000_000; i++) {
        document.createElement("div", Map.of("id", "id" + i));
      }

      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for(var i = 0; i < 1_000_000; i++) {
          var id = "id" + i;
          var node = document.getElementById(id);
          assertEquals(id, node.attributes().get("id"));
        }
      });
    }

    @Test
    public void elementNotFound() {
      var document = new DOMDocument();
      assertNull(document.getElementById("hello"));
    }

    @Test
    public void createAnElementsWithAnIdWhichIsNotAString() {
      var document = new DOMDocument();
      assertThrows(IllegalArgumentException.class, () -> document.createElement("div", Map.of("id", 12)));
    }

    @Test
    public void createAnElementsWithAnIdWhichIsAnEmptyString() {
      var document = new DOMDocument();
      assertThrows(IllegalArgumentException.class, () -> document.createElement("div", Map.of("id", "")));
    }

    @Test
    public void getElementByIdPrecondition() {
      var document = new DOMDocument();
      assertThrows(NullPointerException.class, () -> document.getElementById(null));
    }
  }


//  @Nested
//  public class Q4 {
//    @Test
//    public void appendChild() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent.appendChild(child);
//
//      assertEquals(List.of(child), parent.children());
//    }
//
//    @Test
//    public void appendChildren() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child1 = document.createElement("bar", Map.of());
//      parent.appendChild(child1);
//      var child2 = document.createElement("bar", Map.of());
//      parent.appendChild(child2);
//
//      assertEquals(List.of(child1, child2), parent.children());
//    }
//
//    @Test
//    public void childrenIsUnmodifiable() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent.appendChild(child);
//      var node = document.createElement("baz", Map.of());
//
//      assertThrows(UnsupportedOperationException.class, () -> parent.children().add(node));
//    }
//
//    @Test
//    public void childrenIsAView() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var children = parent.children();
//
//      var child = document.createElement("bar", Map.of());
//      parent.appendChild(child);
//      assertEquals(List.of(child), children);
//    }
//
//    @Test
//    public void appendChildDifferentDocuments() {
//      var parent = new DOMDocument().createElement("foo", Map.of());
//      var child = new DOMDocument().createElement("bar", Map.of());
//
//      assertThrows(IllegalStateException.class, () -> parent.appendChild(child));
//    }
//
//    @Test
//    public void appendChildPrecondition() {
//      var document = new DOMDocument();
//      var node = document.createElement("foo", Map.of());
//
//      assertThrows(NullPointerException.class, () -> node.appendChild(null));
//    }
//  }
//
//  @Nested
//  public class Q5 {
//    @Test
//    public void appendChildRemoveItFromPreviousParent() {
//      var document = new DOMDocument();
//      var parent1 = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent1.appendChild(child);
//
//      var parent2 = document.createElement("baz", Map.of());
//      parent2.appendChild(child);
//
//      assertAll(
//          () -> assertEquals(List.of(), parent1.children()),
//          () -> assertEquals(List.of(child), parent2.children())
//      );
//    }
//  }
//
//
//  @Nested
//  public class Q6 {
//    @Test
//    public void createElementWithNoAttributesText() {
//      var document = new DOMDocument();
//      var node = document.createElement("div", Map.of());
//
//      assertEquals("""
//        <div></div>\
//        """, "" + node);
//    }
//
//    @Test
//    public void createElementWithAStringAttributeText() {
//      var document = new DOMDocument();
//      var node = document.createElement("div", Map.of("foo", "bar"));
//
//      assertEquals("""
//        <div foo="bar"></div>\
//        """, "" + node);
//    }
//
//    @Test
//    public void createElementWithAnIntegerAttributeText() {
//      var document = new DOMDocument();
//      var node = document.createElement("div", Map.of("hello", 42));
//
//      assertEquals("""
//        <div hello="42"></div>\
//        """, "" + node);
//    }
//
//    @Test
//    public void createElementWithSeveralAttributesText() {
//      var document = new DOMDocument();
//      var node = document.createElement("div", Map.of("a", 4.2, "b", false));
//      var text = "" + node;
//
//      assertTrue(
//          """
//          <div a="4.2" b="false"></div>\
//          """.equals(text) ||
//              """
//              <div b="false" a="4.2"></div>\
//              """.equals(text));
//    }
//
//    @Test
//    public void createElementWithSeveralAttributesText2() {
//      var document = new DOMDocument();
//      var node = document.createElement("div", Map.of("b", false, "a", 4.2));
//      var text = "" + node;
//
//      assertTrue(
//          """
//          <div a="4.2" b="false"></div>\
//          """.equals(text) ||
//              """
//              <div b="false" a="4.2"></div>\
//              """.equals(text));
//    }
//
//    @Test
//    public void appendChildText() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent.appendChild(child);
//
//      assertEquals(
//          """
//           <foo><bar></bar></foo>\
//           """,
//          "" + parent);
//    }
//
//    @Test
//    public void appendChildrenText() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child1 = document.createElement("bar", Map.of());
//      parent.appendChild(child1);
//      var child2 = document.createElement("bar", Map.of());
//      parent.appendChild(child2);
//
//      assertEquals(
//          """
//           <foo><bar></bar><bar></bar></foo>\
//           """,
//          "" + parent);
//    }
//
//    @Test
//    public void appendChildrenWithAttributesText() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child1 = document.createElement("bar", Map.of("baz", "3"));
//      parent.appendChild(child1);
//      var child2 = document.createElement("bar", Map.of("baz", "7"));
//      parent.appendChild(child2);
//
//      assertEquals(
//          """
//           <foo><bar baz="3"></bar><bar baz="7"></bar></foo>\
//           """,
//          "" + parent);
//    }
//  }
//
//
//  @Nested
//  public class Q7 {
//    @Test
//    public void textRepresentationIsCached() {
//      var document = new DOMDocument();
//      var node = document.createElement("foo", Map.of());
//
//      var text = node.toString();
//      var text2 = node.toString();
//      assertSame(text, text2);
//    }
//
//    @Test
//    public void childParentTextRepresentationIsCached() {
//      var document = new DOMDocument();
//      var parent = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent.appendChild(child);
//
//      assertAll(
//          () -> assertSame(child.toString(), child.toString()),
//          () -> assertSame(parent.toString(), parent.toString())
//      );
//    }
//  }
//
//
//  @Nested
//  public class Q8 {
//    @Test
//    public void appendChildDoestNotCreateLoop() {
//      var document = new DOMDocument();
//      var node = document.createElement("foo", Map.of());
//
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        assertThrows(IllegalStateException.class, () -> node.appendChild(node));
//      });
//    }
//
//    @Test
//    public void appendChildDoestNotCreateLoop2() {
//      var document = new DOMDocument();
//      var node = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      node.appendChild(child);
//
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        assertThrows(IllegalStateException.class, () -> child.appendChild(node));
//      });
//    }
//
//    @Test
//    public void appendChildInvalidCacheCorrectly() {
//      var document = new DOMDocument();
//      var parent1 = document.createElement("foo", Map.of());
//      var child = document.createElement("bar", Map.of());
//      parent1.appendChild(child);
//      parent1.toString();
//
//      var parent2 = document.createElement("baz", Map.of());
//      parent2.toString();
//      parent2.appendChild(child);
//
//      assertAll(
//          () ->
//              assertEquals(
//                  """
//                   <foo></foo>\
//                   """,
//                  parent1.toString()),
//          () ->
//              assertEquals(
//                  """
//                   <baz><bar></bar></baz>\
//                   """,
//                  parent2.toString()));
//    }
//
//    @Test
//    public void appendChildInvalidCacheOfParentsCorrectly() {
//      var document = new DOMDocument();
//      var node1 = document.createElement("foo", Map.of());
//      var node2 = document.createElement("bar", Map.of());
//      node1.appendChild(node2);
//      var node3 = document.createElement("baz", Map.of());
//      node2.appendChild(node3);
//
//      var anotherNode = document.createElement("whizz", Map.of());
//      anotherNode.appendChild(node3);
//
//      assertEquals(
//          """
//           <foo><bar></bar></foo>\
//           """,
//          node1.toString());
//    }
//  }
}
