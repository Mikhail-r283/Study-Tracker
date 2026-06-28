package com.example.studytracker_1.config;


import com.example.studytracker_1.dto.LessonTemplateDto;
import com.example.studytracker_1.dto.QuestionTemplateDto;
import com.example.studytracker_1.entity.Tag;
import com.example.studytracker_1.model.Difficulty;
import com.example.studytracker_1.service.TagService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LessonData {
private final TagService tagService;

    public LessonData(TagService tagService) {
        this.tagService = tagService;

        // Инициализация тегов через tagService
        this.javaBasicsTag = tagService.findOrCreateTag("Java basics");
        this.oopTag = tagService.findOrCreateTag("OOP");
        this.collectionsTag = tagService.findOrCreateTag("Collections");
        this.exceptionsTag = tagService.findOrCreateTag("Exceptions");
        this.multithreadingTag = tagService.findOrCreateTag("Multithreading");
        this.streamsTag = tagService.findOrCreateTag("Streams & Lambda");
        this.springTag = tagService.findOrCreateTag("Spring Boot");
        this.sqlTag = tagService.findOrCreateTag("SQL & Databases");
        this.gitTag = tagService.findOrCreateTag("Git & Version Control");
        this.algorithmsTag = tagService.findOrCreateTag("Algorithms");
    }

    // ======================== ВСПОМОГАТЕЛЬНЫЙ МЕТОД ========================
    private static QuestionTemplateDto question(
            String text,
            String opt1, String opt2, String opt3, String opt4,
            int correctIndex,
            int points) {
        return new QuestionTemplateDto(text, opt1, opt2, opt3, opt4, correctIndex, points);
    }

    // ======================== МЕТОД ДЛЯ ПОЛУЧЕНИЯ ТЕГА ========================
    private Tag getTag(String tagName) {
        return switch (tagName) {
            case "Java basics" -> javaBasicsTag;
            case "OOP" -> oopTag;
            case "Collections" -> collectionsTag;
            case "Exceptions" -> exceptionsTag;
            case "Multithreading" -> multithreadingTag;
            case "Streams & Lambda" -> streamsTag;
            case "Spring Boot" -> springTag;
            case "SQL & Databases" -> sqlTag;
            case "Git & Version Control" -> gitTag;
            case "Algorithms" -> algorithmsTag;
            default -> tagService.findOrCreateTag(tagName);
        };
    }

    // ======================== ТЕГИ ========================

    // Поля с тегами (не статические)
    private final Tag javaBasicsTag;
    private final Tag oopTag;
    private final Tag collectionsTag;
    private final Tag exceptionsTag;
    private final Tag multithreadingTag;
    private final Tag streamsTag;
    private final Tag springTag;
    private final Tag sqlTag;
    private final Tag gitTag;
    private final Tag algorithmsTag;

    // ======================== УРОКИ ========================

    public List<LessonTemplateDto> getAllLessonTemplates() {
        return List.of(
                lesson1(), lesson2(), lesson3(),   // Java Basics
                lesson4(), lesson5(),              // OOP
                lesson6(), lesson7(),              // Collections
                lesson8(),                         // Exceptions
                lesson9(),                         // Multithreading
                lesson10(),                        // Streams
                lesson11(),                        // Spring
                lesson12(),                        // SQL
                lesson13(),                        // Git
                lesson14()                         // Algorithms
        );
    }

    // ======================== Урок 1: Java Basics ========================
    private LessonTemplateDto lesson1() {
        return new LessonTemplateDto(
                "Основы Java: Переменные и типы данных",
                "Изучите примитивные типы данных, переменные и их объявление в Java",
                Difficulty.EASY,
                List.of(
                        question("Какой из следующих типов данных является примитивным в Java?",
                                "String", "Integer", "int", "ArrayList", 2, 10),
                        question("Какое ключевое слово используется для объявления константы в Java?",
                                "static", "final", "const", "immutable", 1, 10),
                        question("Какой тип данных используется для хранения одного символа?",
                                "String", "char", "Character", "symbol", 1, 10),
                        question("Сколько байт занимает тип long в Java?",
                                "4", "8", "16", "2", 1, 15),
                        question("Какой из вариантов НЕ является примитивным типом?",
                                "boolean", "float", "void", "double", 2, 10),
                        question("Что выведет код: int x = 5; System.out.println(x++);",
                                "6", "5", "4", "Ошибка", 1, 15),
                        question("Как объявить переменную типа double?",
                                "double x = 5;", "Double x = 5;", "x = 5.0;", "number x = 5;", 0, 10),
                        question("Сколько бит в типе int?",
                                "16", "32", "64", "8", 1, 15),
                        question("Какое значение по умолчанию для boolean?",
                                "true", "false", "null", "0", 1, 10),
                        question("Можно ли присвоить double значение int без явного приведения?",
                                "Да", "Нет", "Только через new", "Зависит от версии Java", 0, 10)
                ),
                getTag("Java basics")  // ← используем getTag()
        );
    }

    // ======================== Урок 2: Условные операторы ========================
    private LessonTemplateDto lesson2() {
        return new LessonTemplateDto(
                "Основы Java: Условные операторы и циклы",
                "Изучите if-else, switch, for, while и do-while",
                Difficulty.EASY,
                List.of(
                        question("Какой оператор используется для проверки нескольких условий?",
                                "if", "else", "switch", "for", 2, 10),
                        question("Что выведет: int a = 5; if(a > 3 && a < 10) System.out.println('OK'); else System.out.println('NO');",
                                "OK", "NO", "Ошибка", "Ничего", 0, 10),
                        question("Сколько раз выполнится цикл for(int i=0; i<5; i++)?",
                                "4", "5", "6", "Бесконечно", 1, 10),
                        question("Какое условие выхода из цикла while(true)?",
                                "break", "continue", "exit", "return", 0, 15),
                        question("Что делает оператор continue?",
                                "Завершает цикл", "Пропускает итерацию", "Выходит из метода", "Ничего", 1, 15),
                        question("Какой цикл выполнится хотя бы один раз?",
                                "for", "while", "do-while", "foreach", 2, 10),
                        question("Что выведет: for(int i=0; i<3; i++) { if(i==1) continue; System.out.print(i); }",
                                "012", "02", "12", "0", 1, 15),
                        question("Можно ли использовать switch с String в Java?",
                                "Да, с Java 7", "Да, с Java 8", "Нет", "Только в Android", 0, 10),
                        question("Сколько секций default может быть в switch?",
                                "0", "1", "Неограниченно", "Зависит от типа", 1, 15),
                        question("Что выведет: int x = 2; switch(x) { case 1: System.out.print(1); case 2: System.out.print(2); default: System.out.print(3); }",
                                "2", "23", "12", "Ошибка", 1, 20)
                ),
                getTag("Java basics")
        );
    }

    // ======================== Урок 3: Массивы ========================
    private LessonTemplateDto lesson3() {
        return new LessonTemplateDto(
                "Основы Java: Массивы",
                "Одномерные и многомерные массивы, работа с ними",
                Difficulty.EASY,
                List.of(
                        question("Как создать массив из 5 элементов?",
                                "int[5] arr;", "int[] arr = new int[5];", "int arr = new int[5];", "int[] arr = [5];", 1, 10),
                        question("Какой индекс у первого элемента массива?",
                                "1", "0", "-1", "Зависит от JVM", 1, 10),
                        question("Что выведет: int[] arr = {1,2,3}; System.out.println(arr.length);",
                                "2", "3", "4", "Ошибка", 1, 10),
                        question("Как отсортировать массив?",
                                "arr.sort()", "Arrays.sort(arr)", "Collections.sort(arr)", "arr.order()", 1, 10),
                        question("Что такое ArrayIndexOutOfBoundsException?",
                                "Выход за границы массива", "Нулевой массив", "Массив полон", "Ошибка типов", 0, 10),
                        question("Можно ли изменить размер массива после создания?",
                                "Да", "Нет", "Через resize()", "Только с new", 1, 15),
                        question("Как скопировать массив?",
                                "arr.copy()", "Arrays.copyOf(arr, newLength)", "arr.clone()", "И (2) и (3)", 3, 15),
                        question("Что выведет: int[][] arr = {{1,2},{3,4}}; System.out.println(arr[1][0]);",
                                "1", "2", "3", "4", 2, 15),
                        question("Какой тип у переменной int[] arr?",
                                "int", "int[]", "Object", "Array", 1, 10),
                        question("Что означает null для массива?",
                                "Пустой массив", "Массив не существует", "Массив с одним null", "Ошибка", 1, 15)
                ),
                getTag("Java basics")
        );
    }

    // ======================== Урок 4: ООП ========================
    private LessonTemplateDto lesson4() {
        return new LessonTemplateDto(
                "ООП: Классы, объекты и инкапсуляция",
                "Создание классов, конструкторы, модификаторы доступа",
                Difficulty.MEDIUM,
                List.of(
                        question("Какое ключевое слово используется для наследования?",
                                "implements", "extends", "inherits", "super", 1, 10),
                        question("Какой модификатор доступа самый открытый?",
                                "private", "protected", "public", "default", 2, 10),
                        question("Что такое конструктор?",
                                "Метод для удаления объекта", "Метод для инициализации объекта",
                                "Статический метод", "Метод без тела", 1, 10),
                        question("Может ли класс быть объявлен как private?",
                                "Да", "Нет (только для вложенных)", "Только с final", "Зависит от JVM", 1, 15),
                        question("Что выведет: class A { A() { System.out.print(1); } } new A();",
                                "1", "Ничего", "Ошибка", "null", 0, 10),
                        question("Что такое инкапсуляция?",
                                "Наследование свойств", "Скрытие данных", "Полиморфизм", "Сборка мусора", 1, 10),
                        question("Какое ключевое слово вызывает конструктор родителя?",
                                "this()", "super()", "parent()", "base()", 1, 10),
                        question("Может ли абстрактный класс иметь конструктор?",
                                "Да", "Нет", "Только статический", "Только private", 0, 20),
                        question("Что такое геттеры и сеттеры?",
                                "Методы для доступа к полям", "Типы данных",
                                "Модификаторы доступа", "Циклы", 0, 10),
                        question("Сколько конструкторов может быть в классе?",
                                "1", "2", "Сколько угодно (перегрузка)", "Не больше 5", 2, 10)
                ),
                getTag("OOP")
        );
    }

    // ======================== Урок 5: Наследование и полиморфизм ========================
    private LessonTemplateDto lesson5() {
        return new LessonTemplateDto(
                "ООП: Наследование и полиморфизм",
                "Абстрактные классы, интерфейсы, переопределение методов",
                Difficulty.MEDIUM,
                List.of(
                        question("Какое ключевое слово используется для реализации интерфейса?",
                                "extends", "implements", "interface", "abstract", 1, 10),
                        question("Может ли класс наследовать несколько классов?",
                                "Да", "Нет (одиночное наследование)", "Через интерфейсы", "Зависит от версии", 1, 15),
                        question("Сколько интерфейсов может реализовать класс?",
                                "1", "2", "Неограниченно", "Не больше 5", 2, 10),
                        question("Что такое переопределение метода?",
                                "Создание нового метода", "Изменение реализации в наследнике",
                                "Удаление метода", "Вызов родительского метода", 1, 10),
                        question("Что выведет: class A { void foo() { System.out.print('A'); } } class B extends A { void foo() { System.out.print('B'); } } A a = new B(); a.foo();",
                                "A", "B", "AB", "Ошибка", 1, 20),
                        question("Какая аннотация для переопределения?",
                                "@Override", "@Overload", "@Deprecated", "@SuppressWarnings", 0, 10),
                        question("Можно ли переопределить статический метод?",
                                "Да", "Нет (скрытие, а не переопределение)", "Только в интерфейсах", "Зависит от модификатора", 1, 20),
                        question("Что такое полиморфизм?",
                                "Способность объекта принимать разные формы", "Скрытие данных",
                                "Наследование свойств", "Сборка мусора", 0, 10),
                        question("Может ли интерфейс содержать реализацию метода (default)?",
                                "Да, с Java 8", "Нет", "Только static", "Только private", 0, 15),
                        question("Какая разница между abstract class и interface?",
                                "Интерфейс может иметь только абстрактные методы",
                                "Абстрактный класс может иметь поля и конструкторы",
                                "Интерфейс поддерживает множественное наследование",
                                "Всё вышеперечисленное", 3, 20)
                ),
                getTag("OOP")
        );
    }

    // ======================== Урок 6: Collections ========================
    private LessonTemplateDto lesson6() {
        return new LessonTemplateDto(
                "Коллекции: List и ArrayList",
                "Интерфейс List, реализации ArrayList и LinkedList",
                Difficulty.MEDIUM,
                List.of(
                        question("Какой класс реализует динамический массив?",
                                "LinkedList", "ArrayList", "Vector", "Array", 1, 10),
                        question("Как добавить элемент в ArrayList?",
                                "arr.put()", "arr.add()", "arr.insert()", "arr.push()", 1, 10),
                        question("Какая временная сложность доступа по индексу в ArrayList?",
                                "O(n)", "O(log n)", "O(1)", "O(n^2)", 2, 15),
                        question("Что быстрее для вставки в середину списка?",
                                "ArrayList", "LinkedList", "Одинаково", "Vector", 1, 15),
                        question("Как получить размер списка?",
                                "arr.length", "arr.size()", "arr.count()", "arr.getSize()", 1, 10),
                        question("Какой метод удаляет элемент по индексу?",
                                "arr.delete(index)", "arr.remove(index)", "arr.pop(index)", "arr.clear(index)", 1, 10),
                        question("Реализует ли ArrayList интерфейс RandomAccess?",
                                "Да", "Нет", "Только в Java 8+", "Только для примитивов", 0, 15),
                        question("Что будет при добавлении null в ArrayList?",
                                "NullPointerException", "Добавится", "Заменится на 0", "Не скомпилируется", 1, 10),
                        question("Как отсортировать List?",
                                "list.sort()", "Collections.sort(list)", "Arrays.sort(list)", "И (1) и (2)", 3, 15),
                        question("Как создать ArrayList из массива?",
                                "new ArrayList<>(Arrays.asList(arr))", "ArrayList.from(arr)", "List.of(arr)", "Arrays.toList(arr)", 0, 20)
                ),
                getTag("Collections")
        );
    }

    // ======================== Урок 7: Map и Set ========================
    private LessonTemplateDto lesson7() {
        return new LessonTemplateDto(
                "Коллекции: Map, Set и HashMap",
                "Интерфейсы Map и Set, реализации HashMap и HashSet",
                Difficulty.MEDIUM,
                List.of(
                        question("Какая структура данных используется в HashMap?",
                                "Массив + связные списки", "Только массив", "Стек", "Очередь", 0, 15),
                        question("Какой метод добавляет пару в HashMap?",
                                "map.add()", "map.put()", "map.insert()", "map.set()", 1, 10),
                        question("Может ли HashMap содержать null ключ?",
                                "Да, один", "Нет", "Да, сколько угодно", "Только с Java 8", 0, 15),
                        question("Что такое коллизия в HashMap?",
                                "Одинаковые ключи", "Одинаковые хеши для разных ключей",
                                "Переполнение памяти", "Удаление элемента", 1, 20),
                        question("Какая временная сложность get() в HashMap (в среднем)?",
                                "O(n)", "O(log n)", "O(1)", "O(n^2)", 2, 15),
                        question("Какой Set гарантирует порядок элементов?",
                                "HashSet", "LinkedHashSet", "TreeSet", "EnumSet", 1, 15),
                        question("Как перебрать все элементы HashMap?",
                                "map.entrySet()", "map.values()", "map.keySet()", "Всё вышеперечисленное", 3, 15),
                        question("Что выведет: Set<Integer> set = new HashSet<>(); set.add(1); set.add(1); System.out.println(set.size());",
                                "0", "1", "2", "Ошибка", 1, 15),
                        question("Чем TreeSet отличается от HashSet?",
                                "TreeSet сортирует элементы",
                                "TreeSet быстрее",
                                "TreeSet допускает null",
                                "TreeSet не хранит уникальные элементы", 0, 20),
                        question("Что такое load factor в HashMap?",
                                "Максимальная загрузка",
                                "Фактор, при котором происходит расширение",
                                "Количество элементов",
                                "Размер массива", 1, 20)
                ),
                getTag("Collections")
        );
    }

    // ======================== Урок 8: Exceptions ========================
    private LessonTemplateDto lesson8() {
        return new LessonTemplateDto(
                "Исключения: try-catch-finally",
                "Checked и unchecked исключения, обработка ошибок",
                Difficulty.MEDIUM,
                List.of(
                        question("Какое исключение является checked?",
                                "NullPointerException", "ArrayIndexOutOfBoundsException", "IOException", "ArithmeticException", 2, 15),
                        question("Какой блок выполняется всегда?",
                                "try", "catch", "finally", "throw", 2, 10),
                        question("Можно ли иметь несколько catch блоков?",
                                "Да", "Нет", "Только 2", "Зависит от JVM", 0, 10),
                        question("Какое ключевое слово для создания своего исключения?",
                                "throw", "throws", "exception", "error", 0, 10),
                        question("Что произойдет, если в finally возникнет исключение?",
                                "Программа завершится", "Исключение из finally будет выброшено",
                                "Исключение будет проигнорировано", "finally не выполнится", 1, 20),
                        question("Какое исключение выбрасывается при делении на ноль?",
                                "NullPointerException", "ArithmeticException", "DivisionException", "MathException", 1, 10),
                        question("Что означает throws в сигнатуре метода?",
                                "Метод выбрасывает исключение", "Метод перехватывает исключение",
                                "Метод игнорирует исключение", "Метод логирует исключение", 0, 15),
                        question("Может ли catch быть без параметра?",
                                "Да, в Java 7+", "Нет", "Только для RuntimeException", "Только в finally", 0, 15),
                        question("Что такое try-with-resources?",
                                "Автоматическое закрытие ресурсов",
                                "Обработка ресурсов в finally",
                                "Создание ресурсов",
                                "Удаление ресурсов", 0, 20),
                        question("Какой интерфейс должен реализовать ресурс для try-with-resources?",
                                "Closeable", "AutoCloseable", "Runnable", "Serializable", 1, 20)
                ),
                getTag("Exceptions")
        );
    }

    // ======================== Урок 9: Multithreading ========================
    private LessonTemplateDto lesson9() {
        return new LessonTemplateDto(
                "Многопоточность: Thread и Runnable",
                "Создание потоков, синхронизация, проблемы многопоточности",
                Difficulty.HARD,
                List.of(
                        question("Как создать поток в Java?",
                                "extends Thread", "implements Runnable", "Через Executors", "Всё вышеперечисленное", 3, 10),
                        question("Какой метод запускает поток?",
                                "run()", "start()", "execute()", "begin()", 1, 10),
                        question("Что такое synchronized?",
                                "Ключевое слово для синхронизации доступа",
                                "Тип данных",
                                "Метод для паузы",
                                "Утилита для потоков", 0, 10),
                        question("Что такое deadlock?",
                                "Взаимная блокировка потоков",
                                "Завершение потока",
                                "Приоритет потока",
                                "Утечка памяти", 0, 20),
                        question("Какой метод переводит поток в состояние ожидания?",
                                "sleep()", "wait()", "join()", "Все вышеперечисленные", 3, 15),
                        question("Что делает ключевое слово volatile?",
                                "Обеспечивает видимость переменной между потоками",
                                "Делает переменную неизменяемой",
                                "Ускоряет доступ",
                                "Блокирует переменную", 0, 20),
                        question("Какой Executor создает пул фиксированного размера?",
                                "newCachedThreadPool()", "newFixedThreadPool()", "newSingleThreadExecutor()", "newScheduledThreadPool()", 1, 15),
                        question("Что такое race condition?",
                                "Состояние гонки (некорректный доступ к данным)",
                                "Тип исключения",
                                "Метод синхронизации",
                                "Алгоритм сортировки", 0, 15),
                        question("Какая аннотация для методов, которые должны быть потокобезопасными?",
                                "@ThreadSafe", "@GuardedBy", "Нет стандартной аннотации", "@Locked", 2, 15),
                        question("Что такое Future в Java?",
                                "Результат асинхронного вычисления",
                                "Тип данных",
                                "Исключение",
                                "Пустой поток", 0, 20)
                ),
                getTag("Multithreading")
        );
    }

    // ======================== Урок 10: Streams ========================
    private LessonTemplateDto lesson10() {
        return new LessonTemplateDto(
                "Stream API и лямбда-выражения",
                "Функциональное программирование в Java 8+",
                Difficulty.HARD,
                List.of(
                        question("Как создать Stream из списка?",
                                "list.stream()", "Stream.of(list)", "Arrays.stream(list)", "И (1) и (3)", 3, 10),
                        question("Что делает метод filter()?",
                                "Преобразует элементы", "Фильтрует элементы по условию",
                                "Сортирует элементы", "Удаляет дубликаты", 1, 10),
                        question("Что делает метод map()?",
                                "Преобразует каждый элемент", "Фильтрует элементы",
                                "Сортирует элементы", "Собирает в коллекцию", 0, 10),
                        question("Что такое лямбда-выражение?",
                                "Анонимная функция", "Тип данных",
                                "Класс-обертка", "Исключение", 0, 10),
                        question("Какой метод собирает Stream в List?",
                                "collect(Collectors.toList())", "toList()", "list()", "И (1) и (2)", 3, 15),
                        question("Что делает метод reduce()?",
                                "Сокращает количество элементов",
                                "Сводит элементы к одному значению",
                                "Удаляет элементы",
                                "Разделяет элементы", 1, 20),
                        question("Какая разница между intermediate и terminal операциями?",
                                "Intermediate ленивые, terminal запускают обработку",
                                "Terminal ленивые",
                                "Intermediate возвращают void",
                                "Нет разницы", 0, 20),
                        question("Можно ли использовать Stream повторно?",
                                "Да", "Нет (одноразовые)", "Только parallel Stream", "Зависит от данных", 1, 15),
                        question("Что делает метод flatMap()?",
                                "Преобразует и объединяет вложенные структуры",
                                "Фильтрует элементы",
                                "Сортирует элементы",
                                "Удаляет дубликаты", 0, 20),
                        question("Какой метод создает параллельный Stream?",
                                "parallel()", "parallelStream()", "И (1) и (2)", "multithread()", 2, 15)
                ),
                getTag("Streams & Lambda")
        );
    }

    // ======================== Урок 11: Spring ========================
    private LessonTemplateDto lesson11() {
        return new LessonTemplateDto(
                "Spring Boot: Основы",
                "Аннотации, DI, контроллеры, REST API",
                Difficulty.HARD,
                List.of(
                        question("Какая аннотация делает класс контроллером?",
                                "@Controller", "@RestController", "@Service", "И (1) и (2)", 3, 10),
                        question("Что такое Dependency Injection?",
                                "Внедрение зависимостей через конструктор или поля",
                                "Создание объектов напрямую",
                                "Удаление зависимостей",
                                "Логирование", 0, 10),
                        question("Какая аннотация для внедрения зависимости?",
                                "@Inject", "@Autowired", "@Resource", "Все вышеперечисленные", 3, 10),
                        question("Что делает @GetMapping?",
                                "Обрабатывает GET запросы", "Создает объект",
                                "Логирует запросы", "Валидирует данные", 0, 10),
                        question("Что такое Spring Bean?",
                                "Объект, управляемый Spring контейнером",
                                "Тип данных",
                                "HTTP запрос",
                                "База данных", 0, 15),
                        question("Какая аннотация для транзакции?",
                                "@Transactional", "@Transaction", "@Commit", "@Rollback", 0, 15),
                        question("Что такое application.properties?",
                                "Файл конфигурации Spring Boot",
                                "Файл с кодом",
                                "Библиотека",
                                "HTML шаблон", 0, 10),
                        question("Какой HTTP метод используется для обновления ресурса?",
                                "GET", "POST", "PUT", "DELETE", 2, 10),
                        question("Что делает @PathVariable?",
                                "Извлекает значение из URL",
                                "Создает путь",
                                "Удаляет файл",
                                "Логирует путь", 0, 15),
                        question("Что такое Spring Data JPA?",
                                "ORM для работы с базами данных",
                                "HTTP клиент",
                                "Конфигурация",
                                "Сборщик проекта", 0, 20)
                ),
                getTag("Spring Boot")
        );
    }

    // ======================== Урок 12: SQL ========================
    private LessonTemplateDto lesson12() {
        return new LessonTemplateDto(
                "SQL: Запросы и JOIN",
                "SELECT, INSERT, UPDATE, DELETE, JOIN, GROUP BY",
                Difficulty.HARD,
                List.of(
                        question("Какой оператор выбирает все столбцы из таблицы?",
                                "SELECT * FROM", "SELECT ALL FROM", "GET * FROM", "FIND * FROM", 0, 10),
                        question("Какой JOIN возвращает только совпадающие записи?",
                                "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "FULL OUTER JOIN", 2, 10),
                        question("Какой оператор используется для фильтрации?",
                                "WHERE", "HAVING", "FILTER", "И (1) и (2)", 3, 10),
                        question("Как отсортировать результаты по убыванию?",
                                "ORDER BY column DESC", "SORT BY column DESC",
                                "ORDER BY column ASC", "ORDER DESC column", 0, 10),
                        question("Как вставить новую запись?",
                                "INSERT INTO VALUES", "ADD INTO VALUES",
                                "INSERT NEW", "CREATE ROW", 0, 10),
                        question("Как обновить запись?",
                                "UPDATE SET WHERE", "MODIFY SET WHERE",
                                "CHANGE SET WHERE", "ALTER SET WHERE", 0, 15),
                        question("Что делает GROUP BY?",
                                "Группирует строки по столбцу",
                                "Сортирует строки",
                                "Фильтрует строки",
                                "Удаляет дубликаты", 0, 15),
                        question("Как удалить все записи из таблицы?",
                                "DELETE FROM table", "DROP TABLE table",
                                "TRUNCATE TABLE table", "REMOVE FROM table", 2, 15),
                        question("Что такое PRIMARY KEY?",
                                "Уникальный идентификатор записи",
                                "Внешний ключ",
                                "Индекс",
                                "Тип данных", 0, 10),
                        question("Что делает HAVING?",
                                "Фильтрует группы после GROUP BY",
                                "Фильтрует строки до GROUP BY",
                                "Сортирует группы",
                                "Объединяет таблицы", 0, 20)
                ),
                getTag("SQL & Databases")
        );
    }

    // ======================== Урок 13: Git ========================
    private LessonTemplateDto lesson13() {
        return new LessonTemplateDto(
                "Git: Основы работы",
                "Коммиты, ветки, слияние, разрешение конфликтов",
                Difficulty.MEDIUM,
                List.of(
                        question("Какая команда инициализирует репозиторий?",
                                "git init", "git start", "git create", "git new", 0, 10),
                        question("Как добавить файл в staging?",
                                "git add file", "git commit file", "git push file", "git stage file", 0, 10),
                        question("Как создать новый коммит?",
                                "git commit -m 'message'", "git save -m 'message'",
                                "git push -m 'message'", "git create -m 'message'", 0, 10),
                        question("Как создать новую ветку?",
                                "git branch new-branch", "git create-branch new-branch",
                                "git new-branch new-branch", "git switch --create new-branch", 3, 15),
                        question("Как переключиться на другую ветку?",
                                "git checkout branch", "git switch branch",
                                "git move branch", "И (1) и (2)", 3, 10),
                        question("Как смержить ветку в текущую?",
                                "git merge branch", "git combine branch",
                                "git join branch", "git integrate branch", 0, 10),
                        question("Что такое git pull?",
                                "Забирает изменения из удаленного репозитория",
                                "Отправляет изменения в удаленный репозиторий",
                                "Создает ветку",
                                "Удаляет файлы", 0, 10),
                        question("Что делает git stash?",
                                "Сохраняет незакоммиченные изменения временно",
                                "Удаляет изменения",
                                "Создает коммит",
                                "Отменяет последний коммит", 0, 20),
                        question("Как посмотреть историю коммитов?",
                                "git log", "git history", "git commits", "git show", 0, 10),
                        question("Что такое merge conflict?",
                                "Конфликт при слиянии (изменения в одних строках)",
                                "Ошибка в коде",
                                "Проблема с сетью",
                                "Удаление ветки", 0, 15)
                ),
                getTag("Git & Version Control")
        );
    }

    // ======================== Урок 14: Алгоритмы ========================
    private LessonTemplateDto lesson14() {
        return new LessonTemplateDto(
                "Алгоритмы: Сортировка и поиск",
                "Базовые алгоритмы сортировки и двоичный поиск",
                Difficulty.HARD,
                List.of(
                        question("Какая сложность у пузырьковой сортировки в худшем случае?",
                                "O(n)", "O(n log n)", "O(n^2)", "O(log n)", 2, 15),
                        question("Какая сортировка самая быстрая в среднем?",
                                "Bubble sort", "Quick sort", "Selection sort", "Insertion sort", 1, 15),
                        question("Что такое двоичный поиск?",
                                "Поиск в отсортированном массиве делением пополам",
                                "Линейный поиск",
                                "Поиск в неотсортированном массиве",
                                "Случайный поиск", 0, 10),
                        question("Какое условие необходимо для двоичного поиска?",
                                "Массив отсортирован", "Массив неотсортирован",
                                "Массив без дубликатов", "Массив четной длины", 0, 10),
                        question("Какая сложность двоичного поиска?",
                                "O(n)", "O(log n)", "O(n^2)", "O(1)", 1, 15),
                        question("Как называется сортировка, которая делит массив пополам и рекурсивно сортирует?",
                                "Merge sort", "Bubble sort", "Quick sort", "Selection sort", 0, 20),
                        question("Что такое алгоритм Дейкстры?",
                                "Поиск кратчайшего пути в графе",
                                "Сортировка массива",
                                "Поиск в глубину",
                                "Хеширование", 0, 20),
                        question("Какая структура данных используется в BFS?",
                                "Стек", "Очередь", "Массив", "Связный список", 1, 20),
                        question("Какая сложность линейного поиска?",
                                "O(n)", "O(log n)", "O(n^2)", "O(1)", 0, 10),
                        question("Что такое рекурсия?",
                                "Метод, вызывающий сам себя",
                                "Тип данных",
                                "Цикл",
                                "Исключение", 0, 15)
                ),
                getTag("Algorithms")
        );
    }
}