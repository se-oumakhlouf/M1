package fr.uge.gatherer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AccessFlag;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("preview")
public class GathererDemoTest {
  @Nested
  public class Q1 {
    @Test
    public void filterIntegersEven() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.filterIntegers(x -> x % 2 == 0)).toList();
      assertEquals(List.of(2, 10), result);
    }

    @Test
    public void filterIntegersOdd() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.filterIntegers(x -> x % 2 == 1)).toList();
      assertEquals(List.of(1, 3, 7), result);
    }

    @Test
    public void filterIntegersAll() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.filterIntegers(_ -> true)).toList();
      assertEquals(list, result);
    }

    @Test
    public void filterIntegersNone() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.filterIntegers(_ -> false)).toList();
      assertEquals(List.of(), result);
    }

    @Test
    public void filterIntegersPrecondition() {
      assertThrows(NullPointerException.class, () -> GathererDemo.filterIntegers(null));
    }

    @Test
    public void qualityOfImplementation() {
      Arrays.stream(GathererDemo.class.getMethods())
          .filter(m -> m.getName().equals("filterIntegers") && m.getParameterCount() == 1)
          .forEach(m -> assertNotEquals(Predicate.class, m.getParameters()[0].getType()));
    }
  }


  @Nested
  public class Q2 {
    @Test
    public void takeWhileIntegersEven() {
      var list = List.of(2, 1, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhileIntegers(x -> x % 2 == 0)).toList();
      assertEquals(List.of(2), result);
    }

    @Test
    public void takeWhileIntegersOdd() {
      var list = List.of(1, 3, 2, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhileIntegers(x -> x % 2 == 1)).toList();
      assertEquals(List.of(1, 3), result);
    }

    @Test
    public void takeWhileIntegersAll() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhileIntegers(_ -> true)).toList();
      assertEquals(list, result);
    }

    @Test
    public void takeWhileIntegersNone() {
      var list = List.of(1, 2, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhileIntegers(_ -> false)).toList();
      assertEquals(List.of(), result);
    }

    @Test
    public void takeWhileIntegersPrecondition() {
      assertThrows(NullPointerException.class, () -> GathererDemo.takeWhileIntegers(null));
    }

    @Test
    public void qualityOfImplementation() {
      Arrays.stream(GathererDemo.class.getMethods())
          .filter(m -> m.getName().equals("takeWhileIntegers") && m.getParameterCount() == 1)
          .forEach(m -> assertNotEquals(Predicate.class, m.getParameters()[0].getType()));
    }
  }


  @Nested
  public class Q3 {
    @Test
    public void takeWhileEven() {
      var list = List.of(2, 1, 3, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhile(x -> x % 2 == 0)).toList();
      assertEquals(List.of(2), result);
    }

    @Test
    public void takeWhileOdd() {
      var list = List.of(1, 3, 2, 7, 10);
      var result = list.stream().gather(GathererDemo.takeWhile(x -> x % 2 == 1)).toList();
      assertEquals(List.of(1, 3), result);
    }

    @Test
    public void takeWhileAll() {
      var list = List.of("foo", "bar", "baz");
      var result = list.stream().gather(GathererDemo.takeWhile(_ -> true)).toList();
      assertEquals(list, result);
    }

    @Test
    public void takeWhileIntegersNone() {
      var list = List.of(1, "foo", true, 4.0);
      var result = list.stream().gather(GathererDemo.takeWhile(_ -> false)).toList();
      assertEquals(List.of(), result);
    }

    @Test
    public void takeWhileSignature() {
      var list = List.of("a", "b", "c", "dd", "e");
      var result = list.stream().gather(GathererDemo.takeWhile((Object o) -> o.toString().length() == 1)).toList();
      assertEquals(List.of("a", "b", "c"), result);
    }

    @Test
    public void takeWhileIntegersPrecondition() {
      assertThrows(NullPointerException.class, () -> GathererDemo.takeWhile(null));
    }
  }


  @Nested
  public class Q4 {
    @Test
    public void indexed() {
      var list = List.of("foo", "bar");
      var result = list.stream().gather(GathererDemo.indexed()).toList();
      var expected = List.of(new GathererDemo.Indexed<>("foo", 0), new GathererDemo.Indexed<>("bar", 1));
      assertEquals(expected, result);
    }

    @Test
    public void indexedInteger() {
      var list = List.of(100);
      var result = list.stream().gather(GathererDemo.indexed()).toList();
      var expected = List.of(new GathererDemo.Indexed<>(100, 0));
      assertEquals(expected, result);
    }

    @Test
    public void indexedTenValues() {
      var list = IntStream.range(0, 10).boxed().toList();
      var result = list.stream().gather(GathererDemo.indexed()).toList();
      var expected = IntStream.range(0, 10).mapToObj(i -> new GathererDemo.Indexed<>(i, i)).toList();
      assertEquals(expected, result);
    }

    @Test
    public void indexedIsARecord() {
      var type = GathererDemo.Indexed.class;
      assertAll(
          () -> assertTrue(type.accessFlags().contains(AccessFlag.PUBLIC)),
          () -> assertTrue(type.accessFlags().contains(AccessFlag.FINAL)),
          () -> assertTrue(type.isRecord())
      );
    }
  }


//  @Nested
//  public class Q5 {
//    @Test
//    public void indexed() {
//      var list = List.of("foo", "bar");
//      var result = list.stream().gather(GathererDemo.indexed(GathererDemo.Indexed::new)).toList();
//      var expected = List.of(new GathererDemo.Indexed<>("foo", 0), new GathererDemo.Indexed<>("bar", 1));
//      assertEquals(expected, result);
//    }
//
//    @Test
//    public void indexedInteger() {
//      var list = List.of(100);
//      var result = list.stream().gather(GathererDemo.indexed(GathererDemo.Indexed::new)).toList();
//      assertEquals(result, List.of(new GathererDemo.Indexed<>(100, 0)));
//    }
//
//    @Test
//    public void indexedTenValues() {
//      var list = IntStream.range(0, 10).boxed().toList();
//      var result = list.stream().gather(GathererDemo.indexed(GathererDemo.Indexed::new)).toList();
//      var expected = IntStream.range(0, 10).mapToObj(i -> new GathererDemo.Indexed<>(i, i)).toList();
//      assertEquals(expected, result);
//    }
//
//    @Test
//    public void indexedIdentity() {
//      var list = IntStream.range(0, 10).boxed().toList();
//      var result = list.stream().gather(GathererDemo.indexed((e, _) -> e)).toList();
//      assertEquals(list, result);
//    }
//
//    @Test
//    public void indexedSignature() {
//      var list = List.of("a", "b", "c", "dd", "e");
//      var result = list.stream().gather(GathererDemo.indexed((Object _, int index) -> index)).toList();
//      assertEquals(List.of(0, 1, 2, 3, 4), result);
//    }
//
//    @Test
//    public void indexedPrecondition() {
//      assertThrows(NullPointerException.class, () -> GathererDemo.indexed(null));
//    }
//
//    @Test
//    public void parameterIsAFunctionalInterface() {
//      var method = Arrays.stream(GathererDemo.class.getMethods())
//          .filter(m -> m.getName().equals("indexed") && m.getParameterCount() == 1)
//          .findFirst().orElseThrow();
//      var parameterType = method.getParameterTypes()[0];
//      assertAll(
//          () -> assertTrue(parameterType.accessFlags().contains(AccessFlag.PUBLIC)),
//          () -> assertTrue(parameterType.isAnnotationPresent(FunctionalInterface.class))
//      );
//    }
//
//    @Test
//    public void noBoxing() {
//      var method = Arrays.stream(GathererDemo.class.getMethods())
//          .filter(m -> m.getName().equals("indexed") && m.getParameterCount() == 1)
//          .findFirst().orElseThrow();
//      var parameterType = method.getParameterTypes()[0];
//      assertNotEquals(BiFunction.class, parameterType);
//    }
//  }
//
//
//  @Nested
//  public class Q6 {
//    @Test
//    public void squashTwoIntegersFourIntegers() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.squashTwoIntegers(Integer::sum)).toList();
//      assertEquals(List.of(3, 7), result);
//    }
//
//    @Test
//    public void squashTwoIntegersTwoIntegers() {
//      var list = List.of(5, 5);
//      var result = list.stream().gather(GathererDemo.squashTwoIntegers(Math::addExact)).toList();
//      assertEquals(List.of(10), result);
//    }
//
//    @Test
//    public void squashTwoIntegersEmpty() {
//      var list = List.<Integer>of();
//      var result = list.stream().gather(GathererDemo.squashTwoIntegers(Math::addExact)).toList();
//      assertEquals(List.of(), result);
//    }
//
//    @Test
//    public void filterIntegersPrecondition() {
//      assertThrows(NullPointerException.class, () -> GathererDemo.squashTwoIntegers(null));
//    }
//
//    static void fail(Object o) {
//      Assertions.fail("should not be called");
//    }
//
//    @Test
//    public void filterIntegersPrecondition2() {
//      var list = List.of(1, 2, 3);
//      assertThrows(IllegalStateException.class, () -> {
//        fail(list.stream().gather(GathererDemo.squashTwoIntegers(Math::addExact)).toList());
//      });
//    }
//
//    @Test
//    public void qualityOfImplementation() {
//      Arrays.stream(GathererDemo.class.getMethods())
//          .filter(m -> m.getName().equals("squashTwoIntegers") && m.getParameterCount() == 1)
//          .forEach(m -> assertNotEquals(Function.class, m.getParameters()[0].getType()));
//    }
//  }
//
//
//  @Nested
//  public class Q7 {
//    @Test
//    public void squashTwoFourIntegers() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.squashTwo(Integer::sum)).toList();
//      assertEquals(List.of(3, 7), result);
//    }
//
//    @Test
//    public void squashTwoTwoIntegers() {
//      var list = List.of(5, 5);
//      var result = list.stream().gather(GathererDemo.squashTwo(Math::addExact)).toList();
//      assertEquals(List.of(10), result);
//    }
//
//    @Test
//    public void squashTwoTwoStrings() {
//      var list = List.of("foo", "bar");
//      var result = list.stream().gather(GathererDemo.squashTwo(String::concat)).toList();
//      assertEquals(List.of("foobar"), result);
//    }
//
//    @Test
//    public void squashTwoStringEmpty() {
//      var list = List.<String>of();
//      var result = list.stream().gather(GathererDemo.squashTwo(String::concat)).toList();
//      assertEquals(List.of(), result);
//    }
//
//    @Test
//    public void squashTwoSignature() {
//      var list = List.of("foo", "bar");
//      var result = list.stream().gather(GathererDemo.squashTwo((Object o1, Object o2) -> o1 + "" + o2)).toList();
//      assertEquals(List.of("foobar"), result);
//    }
//
//    @Test
//    public void squashTwoWorksWithNull() {
//      var list = Arrays.asList(null, null, 1, null);
//      var result = list.stream().gather(GathererDemo.squashTwo(Objects::equals)).toList();
//      assertEquals(List.of(true, false), result);
//    }
//
//    @Test
//    public void squashTwoPrecondition() {
//      assertThrows(NullPointerException.class, () -> GathererDemo.squashTwo(null));
//    }
//
//    static void fail(Object o) {
//      Assertions.fail("should not be called");
//    }
//
//    @Test
//    public void squashTwoPrecondition2() {
//      var list = List.of("foo", "bar", "baz");
//      assertThrows(IllegalStateException.class, () -> {
//        fail(list.stream().gather(GathererDemo.squashTwo(String::concat)).toList());
//      });
//    }
//  }
//
//
//  @Nested
//  public class Q8 {
//    @Test
//    public void windowSquashFourIntegers() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.windowSquash(2, identity())).toList();
//      assertEquals(List.of(List.of(1, 2), List.of(3, 4)), result);
//    }
//
//    @Test
//    public void windowSquashThreeIntegers() {
//      var list = List.of(1, 2, 3);
//      var result = list.stream().gather(GathererDemo.windowSquash(2, identity())).toList();
//      assertEquals(List.of(List.of(1, 2), List.of(3)), result);
//    }
//
//    @Test
//    public void windowSquashTwoStrings() {
//      var list = List.of("foo", "bar");
//      var result = list.stream().gather(GathererDemo.windowSquash(2, identity())).toList();
//      assertEquals(List.of(List.of("foo", "bar")), result);
//    }
//
//    @Test
//    public void windowSquashOneIntegers() {
//      var list = List.of(5);
//      var result = list.stream().gather(GathererDemo.windowSquash(3, identity())).toList();
//      assertEquals(List.of(List.of(5)), result);
//    }
//
//    @Test
//    public void windowSquash() {
//      var list = List.of("a", "b", "c", "d");
//      var result = list.stream().gather(GathererDemo.windowSquash(3, Collection::size)).toList();
//      assertEquals(List.of(3, 1), result);
//    }
//
//    @Test
//    public void windowSquashOfStrings() {
//      var list = List.of("foo", "bar", "baz");
//      var result = list.stream().gather(GathererDemo.windowSquash(1, List::getFirst)).toList();
//      assertEquals(list, result);
//    }
//
//    @Test
//    public void windowSquashStringEmpty() {
//      var list = List.<String>of();
//      var result = list.stream().gather(GathererDemo.windowSquash(2, identity())).toList();
//      assertEquals(List.of(), result);
//    }
//
//    @Test
//    public void windowSquashWorksWithNull() {
//      var list = Arrays.asList(null, null, 1, null);
//      var result = list.stream().gather(GathererDemo.windowSquash(2, identity())).toList();
//      assertEquals(List.of(Arrays.asList(null, null), Arrays.asList(1, null)), result);
//    }
//
//    @Test
//    public void windowSquashSignature() {
//      var list = List.of("foo", "bar", "baz");
//      var result = list.stream().gather(GathererDemo.windowSquash(2, (List<?> l) -> l.size())).toList();
//      assertEquals(List.of(2, 1), result);
//    }
//
//    @Test
//    public void windowSquashPreconditions() {
//      assertAll(
//          () -> assertThrows(IllegalArgumentException.class, () -> GathererDemo.windowSquash(-1, identity())),
//          () -> assertThrows(NullPointerException.class, () -> GathererDemo.windowSquash(2, null))
//      );
//    }
//  }
//
//
//  @Nested
//  public class Q9 {
//    @Test
//    public void windowFixedToList() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.toList())).toList();
//      assertEquals(List.of(List.of(1, 2), List.of(3, 4)), result);
//    }
//
//    @Test
//    public void windowFixedToSet() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.toSet())).toList();
//      assertEquals(List.of(Set.of(1, 2), Set.of(3, 4)), result);
//    }
//
//    @Test
//    public void windowFixedThreeIntegersToList() {
//      var list = List.of(1, 2, 3);
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.toList())).toList();
//      assertEquals(List.of(List.of(1, 2), List.of(3)), result);
//    }
//
//    @Test
//    public void windowFixedThreeIntegersToSet() {
//      var list = List.of(1, 2, 3);
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.toUnmodifiableSet())).toList();
//      assertEquals(List.of(Set.of(1, 2), Set.of(3)), result);
//    }
//
//    @Test
//    public void windowFixedJoining() {
//      var list = List.of("a", "b", "c");
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.joining())).toList();
//      assertEquals(List.of("ab", "c"), result);
//    }
//
//    @Test
//    public void windowFixedTwoStrings() {
//      var list = List.of("bar", "baz", "foo");
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.groupingBy(s -> s.charAt(0)))).toList();
//      assertEquals(List.of(Map.of('b', List.of("bar", "baz")), Map.of('f', List.of("foo"))), result);
//    }
//
//    @Test
//    public void windowFixedOneIntegers() {
//      var list = List.of(5);
//      var result = list.stream().gather(GathererDemo.windowFixed(3, Collectors.toList())).toList();
//      assertEquals(List.of(List.of(5)), result);
//    }
//
//    @Test
//    public void windowFixed() {
//      var list = List.of("a", "b", "c", "d");
//      var result = list.stream().gather(GathererDemo.windowFixed(3, Collectors.counting())).toList();
//      assertEquals(List.of(3L, 1L), result);
//    }
//
//    @Test
//    public void windowFixedOfStrings() {
//      var list = List.of("foo", "bar", "baz");
//      var result = list.stream().gather(GathererDemo.windowFixed(1, Collectors.toList())).toList();
//      assertEquals(List.of(List.of("foo"), List.of("bar"), List.of("baz")), result);
//    }
//
//    @Test
//    public void windowFixedStringEmpty() {
//      var list = List.<String>of();
//      var result = list.stream().gather(GathererDemo.windowFixed(2, Collectors.toList())).toList();
//      assertEquals(List.of(), result);
//    }
//
//    @Test
//    public void windowFixedPreconditions() {
//      assertAll(
//          () -> assertThrows(IllegalArgumentException.class, () -> GathererDemo.windowFixed(-1, Collectors.toList())),
//          () -> assertThrows(NullPointerException.class, () -> GathererDemo.windowFixed(2, null))
//      );
//    }
//  }
//
//  @Nested
//  public class Q10 {
//    @Test
//    public void windowFixedToList() {
//      var list = List.of(1, 2, 3, 4);
//      var result = list.stream().gather(GathererDemo.<Integer, List<Integer>>windowFixed(2, Collectors.toList())).toList();
//      assertEquals(List.of(List.of(1, 2), List.of(3, 4)), result);
//    }
//  }
}