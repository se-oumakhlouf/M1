package fr.uge.runlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RunListTest {
  @Nested
  public class Q1 {
    @Test
    public void runListOfString() {
      RunList<String> runList = RunList.newRunLengthList();
      runList.addRun("foo", 2);
      assertAll(
          () -> assertEquals(2, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1))
      );
    }

    @Test
    public void runListOfLocalTime() {
      RunList<LocalTime> runList = RunList.newRunLengthList();
      runList.addRun(LocalTime.of(10, 0), 1);
      assertAll(
          () -> assertEquals(1, runList.size()),
          () -> assertEquals(LocalTime.of(10, 0), runList.get(0))
      );
    }

    @Test
    public void runListWithTwoStrings() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 1);
      runList.addRun("bar", 1);
      assertAll(
          () -> assertEquals(2, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("bar", runList.get(1))
      );
    }

    @Test
    public void runListWithThreeStrings() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      assertAll(
          () -> assertEquals(3, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1)),
          () -> assertEquals("bar", runList.get(2))
      );
    }

    @Test
    public void runListOfSeveralString() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      runList.addRun("baz", 4);
      assertAll(
          () -> assertEquals(7, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1)),
          () -> assertEquals("bar", runList.get(2)),
          () -> assertEquals("baz", runList.get(3)),
          () -> assertEquals("baz", runList.get(4)),
          () -> assertEquals("baz", runList.get(5)),
          () -> assertEquals("baz", runList.get(6))
      );
    }

    @Test
    public void runListOfSeveralLocalTime() {
      var runList = RunList.<LocalTime>newRunLengthList();
      runList.addRun(LocalTime.of(12, 0), 3);
      runList.addRun(LocalTime.of(14, 30), 1);
      assertAll(
          () -> assertEquals(4, runList.size()),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(0)),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(1)),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(2)),
          () -> assertEquals(LocalTime.of(14, 30), runList.get(3))
      );
    }

    @Test
    public void runListOneRunOfSizeMaxValue() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", Integer.MAX_VALUE);
      assertAll(
          () -> assertEquals(Integer.MAX_VALUE, runList.size()),
          () -> assertEquals("foo", runList.get(Integer.MAX_VALUE / 3)),
          () -> assertEquals("foo", runList.get(Integer.MAX_VALUE / 2))
      );
    }

    @Test
    public void runListSingleton() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("hello", 1);
      assertAll(
          () -> assertEquals(1, runList.size()),
          () -> assertEquals("hello", runList.get(0))
      );
    }

    @Test
    public void runListEmpty() {
      var runList = RunList.newRunLengthList();
      assertEquals(0, runList.size());
    }

    @Test
    public void runListQuiteABit() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000; i++) {
        runList.addRun(i, 1);
      }
      assertEquals(1_000, runList.size());
      for (var i = 0; i < 1_000; i++) {
        assertEquals(i, runList.get(i));
      }
    }

    @Test
    public void runListQuiteABit2() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000; i++) {
        runList.addRun(i, 2);
      }
      assertEquals(2_000, runList.size());
      for (var i = 0; i < 2_000; i++) {
        assertEquals(i / 2, runList.get(i));
      }
    }

    @Test
    public void runListQuiteABit3() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000; i++) {
        runList.addRun(i, 3);
      }
      assertEquals(3_000, runList.size());
      for (var i = 0; i < 3_000; i++) {
        assertEquals(i / 3, runList.get(i));
      }
    }

    @Test
    public void runListSizeIsFast() {
      var runList = RunList.<Integer>newRunLengthList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 1);
        }
      });
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          assertEquals(1_000_000, runList.size());
        }
      });
    }

    @Test
    public void runListGetPreconditions() {
      var runList = RunList.newRunLengthList();
      runList.addRun("foo", 3);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(-1)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(3))
      );
    }

    @Test
    public void runListAddRunPreconditions() {
      var runList = RunList.newRunLengthList();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> runList.addRun(null, 2)),
          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", -2)),
          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", 0))
      );
    }

    @Test
    public void runListRunLengthQualityImplementation() {
      var runList = RunList.newRunLengthList();
      var fields = runList.getClass().getDeclaredFields();
      assertAll(
          () -> assertNotNull(runList.getClass().getEnclosingMethod()),
          () -> assertTrue(Arrays.stream(fields)
              .anyMatch(field -> List.class.isAssignableFrom(field.getType())))
      );
    }
  }


  @Nested
  public class Q2 {
    @Test
    public void runListOfString() {
      RunList<String> runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      assertAll(
          () -> assertEquals(2, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1))
      );
    }

    @Test
    public void runListOfLocalTime() {
      RunList<LocalTime> runList = RunList.<LocalTime>newBinarySearchList();
      runList.addRun(LocalTime.of(10, 0), 1);
      assertAll(
          () -> assertEquals(1, runList.size()),
          () -> assertEquals(LocalTime.of(10, 0), runList.get(0))
      );
    }

    @Test
    public void runListWithTwoStrings() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 1);
      runList.addRun("bar", 1);
      assertAll(
          () -> assertEquals(2, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("bar", runList.get(1))
      );
    }

    @Test
    public void runListWithThreeStrings() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      assertAll(
          () -> assertEquals(3, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1)),
          () -> assertEquals("bar", runList.get(2))
      );
    }

    @Test
    public void runListOfSeveralString() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      runList.addRun("baz", 4);
      assertAll(
          () -> assertEquals(7, runList.size()),
          () -> assertEquals("foo", runList.get(0)),
          () -> assertEquals("foo", runList.get(1)),
          () -> assertEquals("bar", runList.get(2)),
          () -> assertEquals("baz", runList.get(3)),
          () -> assertEquals("baz", runList.get(4)),
          () -> assertEquals("baz", runList.get(5)),
          () -> assertEquals("baz", runList.get(6))
      );
    }

    @Test
    public void runListOfSeveralLocalTime() {
      var runList = RunList.<LocalTime>newBinarySearchList();
      runList.addRun(LocalTime.of(12, 0), 3);
      runList.addRun(LocalTime.of(14, 30), 1);
      assertAll(
          () -> assertEquals(4, runList.size()),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(0)),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(1)),
          () -> assertEquals(LocalTime.of(12, 0), runList.get(2)),
          () -> assertEquals(LocalTime.of(14, 30), runList.get(3))
      );
    }

    @Test
    public void runListSingleton() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("hello", 1);
      assertAll(
          () -> assertEquals(1, runList.size()),
          () -> assertEquals("hello", runList.get(0))
      );
    }

    @Test
    public void runListEmpty() {
      var runList = RunList.newBinarySearchList();
      assertEquals(0, runList.size());
    }

    @Test
    public void runListGetPreconditions() {
      var runList = RunList.newBinarySearchList();
      runList.addRun("foo", 3);
      assertAll(
          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(-1)),
          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(3))
      );
    }

    @Test
    public void runListAddRunPreconditions() {
      var runList = RunList.newBinarySearchList();
      assertAll(
          () -> assertThrows(NullPointerException.class, () -> runList.addRun(null, 2)),
          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", -2)),
          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", 0))
      );
    }

    @Test
    public void runListBinarySearchQualityImplementation() {
      var runList = RunList.newBinarySearchList();
      var fields = runList.getClass().getDeclaredFields();
      assertAll(
          () -> assertNotNull(runList.getClass().getEnclosingMethod()),
          () -> assertFalse(Arrays.stream(fields)
              .anyMatch(field -> List.class.isAssignableFrom(field.getType()))),
          () -> assertFalse(Arrays.stream(fields)
              .anyMatch(field -> Map.class.isAssignableFrom(field.getType()))),
          () -> assertTrue(Arrays.stream(fields)
              .anyMatch(field -> int[].class == field.getType())),
          () -> assertTrue(Arrays.stream(fields)
              .anyMatch(field -> Object[].class == field.getType()))
      );
    }

    @Test
    public void runListRunLengthDefaultArrayLength() throws IllegalAccessException {
      var runList = RunList.newBinarySearchList();
      var fields = runList.getClass().getDeclaredFields();
      for(var field : fields) {
        if (!field.getType().isArray()) {
          continue;
        }
        field.setAccessible(true);
        var array = field.get(runList);
        if (Array.getLength(array) != 4) {
          fail("an array has de wrong default length " + array);
        }
      }
    }
  }


  @Nested
  public class Q3 {
    @Test
    public void runListSizeIsFast() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 1);
        }
      });
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          assertEquals(1_000_000, runList.size());
        }
      });
    }

    @Test
    public void runListALotOf() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 1);
        }
      });
      assertEquals(1_000_000, runList.size());
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          assertEquals(i, runList.get(i));
        }
      });
    }

    @Test
    public void runListALotOf2() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 2);
        }
      });
      assertEquals(2_000_000, runList.size());
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 2_000_000; i++) {
          assertEquals(i / 2, runList.get(i));
        }
      });
    }

    @Test
    public void runListALotOf3() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 3);
        }
      });
      assertEquals(3_000_000, runList.size());
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 3_000_000; i++) {
          assertEquals(i / 3, runList.get(i));
        }
      });
    }
  }


  @Nested
  public class Q4 {
    @Test
    public void runListOfStringForEach() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 2);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo"), result);
    }

    @Test
    public void runListOfLocalTimeForEach() {
      var runList = RunList.<LocalTime>newRunLengthList();
      runList.addRun(LocalTime.of(10, 0), 1);
      var result = new ArrayList<LocalTime>();
      runList.forEach(result::add);
      assertEquals(List.of(LocalTime.of(10, 0)), result);
    }

    @Test
    public void runListWithTwoStringsForEach() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 1);
      runList.addRun("bar", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "bar"), result);
    }

    @Test
    public void runListWithThreeStringsForEach() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo", "bar"), result);
    }

    @Test
    public void runListOfSeveralStringForEach() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      runList.addRun("baz", 4);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo", "bar", "baz", "baz", "baz", "baz"), result);
    }

    @Test
    public void runListOfSeveralLocalTimeForEach() {
      var runList = RunList.<LocalTime>newRunLengthList();
      runList.addRun(LocalTime.of(12, 0), 3);
      runList.addRun(LocalTime.of(14, 30), 1);
      var result = new ArrayList<LocalTime>();
      runList.forEach(result::add);
      assertEquals(List.of(
          LocalTime.of(12, 0),
          LocalTime.of(12, 0),
          LocalTime.of(12, 0),
          LocalTime.of(14, 30)), result);
    }

    @Test
    public void runListSingletonForEach() {
      var runList = RunList.<String>newRunLengthList();
      runList.addRun("hello", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("hello"), result);
    }

    @Test
    public void runListEmptyForEach() {
      var runList = RunList.newRunLengthList();
      runList.forEach(_ -> fail());
    }

    @Test
    public void runListALotForEach() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000_000; i++) {
        runList.addRun(i, 1);
      }
      var box = new Object() {
        int count;
      };
      runList.forEach(i -> assertEquals(box.count++, i));
    }

    @Test
    public void runListALot2ForEach() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000_000; i++) {
        runList.addRun(i, 2);
      }
      var box = new Object() {
        int count;
      };
      runList.forEach(i -> assertEquals(box.count++ / 2, i));
    }

    @Test
    public void runListALot3ForEach() {
      var runList = RunList.<Integer>newRunLengthList();
      for (var i = 0; i < 1_000_000; i++) {
        runList.addRun(i, 3);
      }
      var box = new Object() {
        int count;
      };
      runList.forEach(i -> assertEquals(box.count++ / 3, i));
    }

    @Test
    public void runListForEachPreconditions() {
      var runList = RunList.newRunLengthList();
      assertThrows(NullPointerException.class, () -> runList.forEach(null));
    }
  }


  @Nested
  public class Q6 {
    @Test
    public void runListOfStringForEach() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo"), result);
    }

    @Test
    public void runListOfLocalTimeForEach() {
      var runList = RunList.<LocalTime>newBinarySearchList();
      runList.addRun(LocalTime.of(10, 0), 1);
      var result = new ArrayList<LocalTime>();
      runList.forEach(result::add);
      assertEquals(List.of(LocalTime.of(10, 0)), result);
    }

    @Test
    public void runListWithTwoStringsForEach() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 1);
      runList.addRun("bar", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "bar"), result);
    }

    @Test
    public void runListWithThreeStringsForEach() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo", "bar"), result);
    }

    @Test
    public void runListOfSeveralStringForEach() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("foo", 2);
      runList.addRun("bar", 1);
      runList.addRun("baz", 4);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("foo", "foo", "bar", "baz", "baz", "baz", "baz"), result);
    }

    @Test
    public void runListOfSeveralLocalTimeForEach() {
      var runList = RunList.<LocalTime>newBinarySearchList();
      runList.addRun(LocalTime.of(12, 0), 3);
      runList.addRun(LocalTime.of(14, 30), 1);
      var result = new ArrayList<LocalTime>();
      runList.forEach(result::add);
      assertEquals(List.of(
          LocalTime.of(12, 0),
          LocalTime.of(12, 0),
          LocalTime.of(12, 0),
          LocalTime.of(14, 30)), result);
    }

    @Test
    public void runListSingletonForEach() {
      var runList = RunList.<String>newBinarySearchList();
      runList.addRun("hello", 1);
      var result = new ArrayList<String>();
      runList.forEach(result::add);
      assertEquals(List.of("hello"), result);
    }

    @Test
    public void runListEmptyForEach() {
      var runList = RunList.newBinarySearchList();
      runList.forEach(_ -> fail());
    }

    @Test
    public void runListALotForEach() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 1);
        }
      });
      var box = new Object() {
        int count;
      };
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        runList.forEach(i -> assertEquals(box.count++, i));
      });
    }

    @Test
    public void runListALot2ForEach() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 2);
        }
      });
      var box = new Object() {
        int count;
      };
      runList.forEach(i -> assertEquals(box.count++ / 2, i));
    }

    @Test
    public void runListALot3ForEach() {
      var runList = RunList.<Integer>newBinarySearchList();
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        for (var i = 0; i < 1_000_000; i++) {
          runList.addRun(i, 3);
        }
      });
      var box = new Object() {
        int count;
      };
      runList.forEach(i -> assertEquals(box.count++ / 3, i));
    }

    @Test
    public void runListForEachPreconditions() {
      var runList = RunList.newBinarySearchList();
      assertThrows(NullPointerException.class, () -> runList.forEach(null));
    }
  }


//  @Nested
//  public class Q7 {
//    public static Stream<Supplier<RunList<?>>> factory() {
//      return Stream.of(
//          new Supplier<>() {
//            public RunList<?> get() {
//              return RunList.newRunLengthList();
//            }
//
//            public String toString() {
//              return "newRunLengthList";
//            }
//          },
//          new Supplier<>() {
//            public RunList<?> get() {
//              return RunList.newBinarySearchList();
//            }
//
//            public String toString() {
//              return "newBinarySearchList";
//            }
//          });
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListOfStringAsList(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListOfLocalTimeAsList(Supplier<RunList<LocalTime>> factory) {
//      var runList = factory.get();
//      runList.addRun(LocalTime.of(10, 0), 1);
//      assertEquals(List.of(LocalTime.of(10, 0)), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListWithTwoStringsAsList(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      runList.addRun("foo", 1);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "bar"), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListWithThreeStringsAsList(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListAsListIsAView(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      var list = runList.asList();
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), list);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), list);
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListOfSeveralStringAsListIsAView(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      var list = runList.asList();
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), list);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), list);
//      runList.addRun("baz", 4);
//      assertEquals(List.of("foo", "foo", "bar", "baz", "baz", "baz", "baz"), list);
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListOfSeveralLocalTimeAsListIsAView(Supplier<RunList<LocalTime>> factory) {
//      var runList = factory.get();
//      var list = runList.asList();
//      runList.addRun(LocalTime.of(12, 0), 3);
//      assertEquals(List.of(
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0)), list);
//      runList.addRun(LocalTime.of(14, 30), 1);
//      assertEquals(List.of(
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(14, 30)), list);
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListSingletonAsList(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      runList.addRun("hello", 1);
//      assertEquals(List.of("hello"), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListEmptyAsList(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      assertEquals(List.of(), runList.asList());
//    }
//
//    @Test
//    public void runListALotAsList() {
//      var runList = RunList.<Integer>newBinarySearchList();
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 1);
//        }
//      });
//      assertEquals(IntStream.range(0, 1_000_000).boxed().toList(), runList.asList());
//    }
//
//    @Test
//    public void runListALot2AsList() {
//      var runList = RunList.<Integer>newBinarySearchList();
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 2);
//        }
//      });
//      assertEquals(IntStream.range(0, 2_000_000).mapToObj(v -> v / 2).toList(), runList.asList());
//    }
//
//    @Test
//    public void runListALot3AsList() {
//      var runList = RunList.<Integer>newBinarySearchList();
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 3);
//        }
//      });
//      assertEquals(IntStream.range(0, 3_000_000).mapToObj(v -> v / 3).toList(), runList.asList());
//    }
//
//    @ParameterizedTest
//    @MethodSource("factory")
//    public void runListAsListImplementationQuality(Supplier<RunList<String>> factory) {
//      var runList = factory.get();
//      var list = runList.asList();
//      assertAll(
//          () -> assertTrue(list instanceof List<?>),
//          () -> assertNotNull(list.getClass().getEnclosingMethod())
//      );
//    }
//  }
//
//
//  @Nested
//  public class Q8 {
//    @Test
//    public void runListExample() {
//      var runList = RunList.wrap(
//          new TreeMap<>(Map.of(2, "foo", 3, "bar", 4, "baz")));
//      assertAll(
//          () -> assertEquals(4, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("foo", runList.get(1)),
//          () -> assertEquals("bar", runList.get(2)),
//          () -> assertEquals("baz", runList.get(3))
//      );
//    }
//
//    @Test
//    public void runListOfString() {
//      RunList<String> runList = RunList.wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      assertAll(
//          () -> assertEquals(2, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("foo", runList.get(1))
//      );
//    }
//
//    @Test
//    public void runListOfLocalTime() {
//      RunList<LocalTime> runList = RunList.wrap(new TreeMap<>());
//      runList.addRun(LocalTime.of(10, 0), 1);
//      assertAll(
//          () -> assertEquals(1, runList.size()),
//          () -> assertEquals(LocalTime.of(10, 0), runList.get(0))
//      );
//    }
//
//    @Test
//    public void runListWithTwoStrings() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 1);
//      runList.addRun("bar", 1);
//      assertAll(
//          () -> assertEquals(2, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("bar", runList.get(1))
//      );
//    }
//
//    @Test
//    public void runListWithThreeStrings() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      assertAll(
//          () -> assertEquals(3, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("foo", runList.get(1)),
//          () -> assertEquals("bar", runList.get(2))
//      );
//    }
//
//    @Test
//    public void runListOfSeveralString() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      runList.addRun("baz", 4);
//      assertAll(
//          () -> assertEquals(7, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("foo", runList.get(1)),
//          () -> assertEquals("bar", runList.get(2)),
//          () -> assertEquals("baz", runList.get(3)),
//          () -> assertEquals("baz", runList.get(4)),
//          () -> assertEquals("baz", runList.get(5)),
//          () -> assertEquals("baz", runList.get(6))
//      );
//    }
//
//    @Test
//    public void runListOfSeveralLocalTime() {
//      var runList = RunList.<LocalTime>wrap(new TreeMap<>());
//      runList.addRun(LocalTime.of(12, 0), 3);
//      runList.addRun(LocalTime.of(14, 30), 1);
//      assertAll(
//          () -> assertEquals(4, runList.size()),
//          () -> assertEquals(LocalTime.of(12, 0), runList.get(0)),
//          () -> assertEquals(LocalTime.of(12, 0), runList.get(1)),
//          () -> assertEquals(LocalTime.of(12, 0), runList.get(2)),
//          () -> assertEquals(LocalTime.of(14, 30), runList.get(3))
//      );
//    }
//
//    @Test
//    public void runListSingleton() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("hello", 1);
//      assertAll(
//          () -> assertEquals(1, runList.size()),
//          () -> assertEquals("hello", runList.get(0))
//      );
//    }
//
//    @Test
//    public void runListEmpty() {
//      var runList = RunList.wrap(new TreeMap<>());
//      assertEquals(0,  runList.size());
//    }
//
//    @Test
//    public void runListSizeIsFast() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for(var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 1);
//        }
//      });
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for(var i = 0; i < 1_000_000; i++) {
//          assertEquals(1_000_000, runList.size());
//        }
//      });
//    }
//
//    @Test
//    public void runListALotOf() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 1);
//        }
//      });
//      assertEquals(1_000_000, runList.size());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          assertEquals(i, runList.get(i));
//        }
//      });
//    }
//
//    @Test
//    public void runListALotOf2() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 2);
//        }
//      });
//      assertEquals(2_000_000, runList.size());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 2_000_000; i++) {
//          assertEquals(i / 2, runList.get(i));
//        }
//      });
//    }
//
//    @Test
//    public void runListALotOf3() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 3);
//        }
//      });
//      assertEquals(3_000_000, runList.size());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 3_000_000; i++) {
//          assertEquals(i / 3, runList.get(i));
//        }
//      });
//    }
//
//    @Test
//    public void runListOfStringAsList() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), runList.asList());
//    }
//
//    @Test
//    public void runListOfLocalTimeAsList() {
//      var runList = RunList.<LocalTime>wrap(new TreeMap<>());
//      runList.addRun(LocalTime.of(10, 0), 1);
//      assertEquals(List.of(LocalTime.of(10, 0)), runList.asList());
//    }
//
//    @Test
//    public void runListWithTwoStringsAsList() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 1);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "bar"), runList.asList());
//    }
//
//    @Test
//    public void runListWithThreeStringsAsList() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), runList.asList());
//    }
//
//    @Test
//    public void runListAsListIsAView() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      var list = runList.asList();
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), list);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), list);
//    }
//
//    @Test
//    public void runListOfSeveralStringAsListIsAView() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      var list = runList.asList();
//      runList.addRun("foo", 2);
//      assertEquals(List.of("foo", "foo"), list);
//      runList.addRun("bar", 1);
//      assertEquals(List.of("foo", "foo", "bar"), list);
//      runList.addRun("baz", 4);
//      assertEquals(List.of("foo", "foo", "bar", "baz", "baz", "baz", "baz"), list);
//    }
//
//    @Test
//    public void runListOfSeveralLocalTimeAsListIsAView() {
//      var runList = RunList.<LocalTime>wrap(new TreeMap<>());
//      var list = runList.asList();
//      runList.addRun(LocalTime.of(12, 0), 3);
//      assertEquals(List.of(
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0)), list);
//      runList.addRun(LocalTime.of(14, 30), 1);
//      assertEquals(List.of(
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(14, 30)), list);
//    }
//
//    @Test
//    public void runListSingletonAsList() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("hello", 1);
//      assertEquals(List.of("hello"), runList.asList());
//    }
//
//    @Test
//    public void runListEmptyAsList() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      assertEquals(List.of(), runList.asList());
//    }
//
//    @Test
//    public void runListALotAsList() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 1);
//        }
//      });
//      assertEquals(IntStream.range(0, 1_000_000).boxed().toList(), runList.asList());
//    }
//
//    @Test
//    public void runListALot2AsList() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 2);
//        }
//      });
//      assertEquals(IntStream.range(0, 2_000_000).mapToObj(v -> v / 2).toList(), runList.asList());
//    }
//
//    @Test
//    public void runListALot3AsList() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 3);
//        }
//      });
//      assertEquals(IntStream.range(0, 3_000_000).mapToObj(v -> v / 3).toList(), runList.asList());
//    }
//
//    @Test
//    public void runListAsListImplementationQuality() {
//      var runList = RunList.wrap(new TreeMap<>());
//      var list = runList.asList();
//      assertAll(
//          () -> assertTrue(list instanceof List<?>),
//          () -> assertNotNull(list.getClass().getEnclosingMethod())
//      );
//    }
//
//    @Test
//    public void runListWrapPreconditions() {
//      assertThrows(NullPointerException.class, () -> RunList.wrap(null));
//    }
//
//    @Test
//    public void runListWrapSupportSeveralImplementations() {
//      assertAll(
//          () -> assertEquals(0, RunList.wrap(new ConcurrentSkipListMap<>()).size())
//      );
//    }
//
//    @Test
//    public void runListIsAView() {
//      var map = new TreeMap<>(Map.of(3, "foo"));
//      RunList<String> runList = RunList.wrap(map);
//      runList.addRun("bar", 2);
//      assertAll(
//          () -> assertEquals(5, runList.size()),
//          () -> assertEquals("foo", runList.get(0)),
//          () -> assertEquals("foo", runList.get(1)),
//          () -> assertEquals("foo", runList.get(2)),
//          () -> assertEquals("bar", runList.get(3)),
//          () -> assertEquals("bar", runList.get(4)),
//          () -> assertEquals(Map.of(3, "foo", 5, "bar"), map)
//      );
//    }
//
//    @Test
//    public void runListGetPreconditions() {
//      var runList = RunList.wrap(new TreeMap<>());
//      runList.addRun("foo", 3);
//      assertAll(
//          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(-1)),
//          () -> assertThrows(IndexOutOfBoundsException.class, () -> runList.get(3))
//      );
//    }
//
//    @Test
//    public void runListAddRunPreconditions() {
//      var runList = RunList.wrap(new TreeMap<>());
//      assertAll(
//          () -> assertThrows(NullPointerException.class, () -> runList.addRun(null, 2)),
//          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", -2)),
//          () -> assertThrows(IllegalArgumentException.class, () -> runList.addRun("foo", 0))
//      );
//    }
//
//    @Test
//    public void runListWrapQualityImplementation() {
//      var runList = RunList.wrap(new TreeMap<>());
//      var fields = runList.getClass().getDeclaredFields();
//      assertAll(
//          () -> assertTrue(runList.getClass().isAnonymousClass()),
//          () -> assertTrue(Arrays.stream(fields)
//              .anyMatch(field -> Map.class.isAssignableFrom(field.getType())))
//      );
//    }
//
//    @Test
//    public void runListOfStringForEach() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      var result = new ArrayList<String>();
//      runList.forEach(result::add);
//      assertEquals(List.of("foo", "foo"), result);
//    }
//
//    @Test
//    public void runListOfLocalTimeForEach() {
//      var runList = RunList.<LocalTime>wrap(new TreeMap<>());
//      runList.addRun(LocalTime.of(10, 0), 1);
//      var result = new ArrayList<LocalTime>();
//      runList.forEach(result::add);
//      assertEquals(List.of(LocalTime.of(10, 0)), result);
//    }
//
//    @Test
//    public void runListWithTwoStringsForEach() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 1);
//      runList.addRun("bar", 1);
//      var result = new ArrayList<String>();
//      runList.forEach(result::add);
//      assertEquals(List.of("foo", "bar"), result);
//    }
//
//    @Test
//    public void runListWithThreeStringsForEach() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      var result = new ArrayList<String>();
//      runList.forEach(result::add);
//      assertEquals(List.of("foo", "foo", "bar"), result);
//    }
//
//    @Test
//    public void runListOfSeveralStringForEach() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("foo", 2);
//      runList.addRun("bar", 1);
//      runList.addRun("baz", 4);
//      var result = new ArrayList<String>();
//      runList.forEach(result::add);
//      assertEquals(List.of("foo", "foo", "bar", "baz", "baz", "baz", "baz"), result);
//    }
//
//    @Test
//    public void runListOfSeveralLocalTimeForEach() {
//      var runList = RunList.<LocalTime>wrap(new TreeMap<>());
//      runList.addRun(LocalTime.of(12, 0), 3);
//      runList.addRun(LocalTime.of(14, 30), 1);
//      var result = new ArrayList<LocalTime>();
//      runList.forEach(result::add);
//      assertEquals(List.of(
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(12, 0),
//          LocalTime.of(14, 30)), result);
//    }
//
//    @Test
//    public void runListSingletonForEach() {
//      var runList = RunList.<String>wrap(new TreeMap<>());
//      runList.addRun("hello", 1);
//      var result = new ArrayList<String>();
//      runList.forEach(result::add);
//      assertEquals(List.of("hello"), result);
//    }
//
//    @Test
//    public void runListEmptyForEach() {
//      var runList = RunList.wrap(new TreeMap<>());
//      runList.forEach(_ -> fail());
//    }
//
//    @Test
//    public void runListALotForEach() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 1);
//        }
//      });
//      var box = new Object() {
//        int count;
//      };
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        runList.forEach(i -> assertEquals(box.count++, i));
//      });
//    }
//
//    @Test
//    public void runListALot2ForEach() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 2);
//        }
//      });
//      var box = new Object() {
//        int count;
//      };
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        runList.forEach(i -> assertEquals(box.count++ / 2, i));
//      });
//    }
//
//    @Test
//    public void runListALot3ForEach() {
//      var runList = RunList.<Integer>wrap(new TreeMap<>());
//      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
//        for (var i = 0; i < 1_000_000; i++) {
//          runList.addRun(i, 3);
//        }
//      });
//      var box = new Object() {
//        int count;
//      };
//      runList.forEach(i -> assertEquals(box.count++ / 3, i));
//    }
//
//    @Test
//    public void runListForEachPreconditions() {
//      var runList = RunList.wrap(new TreeMap<>());
//      assertThrows(NullPointerException.class, () -> runList.forEach(null));
//    }
//  }
}