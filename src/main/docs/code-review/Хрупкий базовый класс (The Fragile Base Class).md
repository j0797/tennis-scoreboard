### Хрупкий базовый класс (The Fragile Base Class)

Хрупкий базовый класс — это проблема проектирования в ООП, когда изменения в базовом (родительском) классе могут неожиданно сломать поведение производных (дочерних) классов, даже если код дочерних классов не изменялся.

Есть две основные формы проявления хрупкости: из-за состояния и из-за поведения.

### 1. Хрупкость из-за состояния (Coupling of Implementation)

Возникает, когда базовый класс предоставляет подклассам прямой доступ к своим внутренним полям, как правило, через модификатор `protected`.

#### Концепция

Когда подкласс напрямую обращается к полям своего родителя, он становится зависимым не от его публичного контракта (поведения), а от его **деталей реализации**. Базовый класс больше не может свободно изменять свою внутреннюю структуру, так как любое такое изменение рискует сломать все подклассы, которые на эту структуру завязаны.

#### Пример: `DocumentBuilder`

Представим базовый класс для построения документов:

```java
// Базовый класс
public abstract class DocumentBuilder {
    // Поля, "защищённые" для удобства наследников
    protected List<String> header = new ArrayList<>();
    protected List<String> body = new ArrayList<>();
    protected List<String> footer = new ArrayList<>();

    public abstract String build();
}
```

Подкласс для создания HTML-документов может напрямую использовать эти списки:

```java
// Подкласс
public class HtmlDocumentBuilder extends DocumentBuilder {
    @Override
    public String build() {
        StringBuilder doc = new StringBuilder("<html>\n");

        // Прямое обращение к полям родителя
        body.forEach(line -> doc.append("  <p>").append(line).append("</p>\n"));

        doc.append("</html>");
        return doc.toString();
    }
}
```

**В чем хрупкость?**

Код работает. Но со временем автор `DocumentBuilder` замечает, что для больших документов использование `List<String>` неэффективно по памяти. Он решает провести "безопасный" внутренний рефакторинг, заменив списки на `StringBuilder`:

```java
// Базовый класс после рефакторинга
public abstract class DocumentBuilder {
    // Внутренний рефакторинг для оптимизации
    protected StringBuilder header = new StringBuilder();
    protected StringBuilder body = new StringBuilder();
    protected StringBuilder footer = new StringBuilder();

    public abstract String build();
}
```

Это изменение, которое должно было быть внутренним делом `DocumentBuilder`, **полностью ломает `HtmlDocumentBuilder`**. Его метод `build()` больше не компилируется, так как у `StringBuilder` нет метода `forEach`. Базовый класс оказался "хрупким".

#### Решение

Решение заключается в строгой инкапсуляции. Состояние должно быть `private`, а взаимодействие с ним — через `protected` методы.

```java
// Крепкий базовый класс
public abstract class DocumentBuilder {
    private final StringBuilder content = new StringBuilder();

    // Наследники управляют состоянием через методы, не зная о его структуре
    protected void addHeaderLine(String line) {
        content.append("<header>").append(line).append("</header>\n");
    }

    protected void addBodyLine(String line) {
        content.append("<p>").append(line).append("</p>\n");
    }
    
    protected String getFinalContent() {
        return content.toString();
    }

    public abstract String build();
}
```

Теперь базовый класс может менять свою внутреннюю реализацию (например, заменить `StringBuilder` на массив байт), и пока контракт `protected` методов сохраняется, подклассы не сломаются.

### 2. Хрупкость из-за поведения (Implicit Contract Violation)

Возникает, когда подкласс переопределяет метод, нарушая неглавные предположения (implicit contract), на которые опирается базовый класс.

#### Концепция

Методы в базовом классе могут вызывать другие свои же методы (которые могут быть переопределены). Это называется "self-use". Если подкласс переопределяет такой метод и меняет его поведение (например, добавляет побочные эффекты), он может нарушить логику работы базового класса.

#### Пример: `InstrumentedSet` (пример из "Effective Java")

Предположим, мы хотим расширить `HashSet` и посчитать, сколько всего элементов было в него добавлено.

```java
// Неправильный подкласс HashSet
public class InstrumentedSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Collection<E> c) {
        super(c);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

Проверим его работу:
```java
public static void main(String[] args) {
    InstrumentedSet<String> set = new InstrumentedSet<>();
    set.addAll(List.of("apple", "banana", "orange"));
    System.out.println(set.getAddCount()); // Ожидаем 3, но получаем 6!
}
```

**В чем хрупкость?**

Проблема в том, что мы не знали (и не должны были знать) деталь реализации `HashSet`: его метод `addAll` внутри себя вызывает метод `add` для каждого элемента коллекции.

Что происходит:
1.  Вызывается переопределенный `addAll`. Он прибавляет к `addCount` 3.
2.  Затем он вызывает `super.addAll()`.
3.  `super.addAll()` внутри `HashSet` итерируется по списку и трижды вызывает метод `add()`. Но так как `add()` переопределен в классе-наследнике, вызывается `add()` наследника, который ещё три раза инкрементирует `addCount`.

Нарушен неявный контракт. Логика базового класса сломалась из-за вмешательства. Базовый класс (`HashSet`) оказался хрупким по отношению к расширению.

#### Решение

**Композиция**. Вместо наследования нужно создать новый класс, который **содержит** `HashSet` как приватное поле и делегирует ему вызовы.

```java
// Надёжная реализация с использованием композиции
public class CountingSet<E> implements Set<E> {
    private final Set<E> set; // Композиция, а не наследование
    private int addCount = 0;

    public CountingSet(Set<E> set) {
        this.set = set;
    }

    public int getAddCount() {
        return addCount;
    }

    @Override
    public boolean add(E e) {
        if (set.add(e)) {
            addCount++;
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) { // Вызываем свой собственный, контролируемый add
                modified = true;
            }
        }
        return modified;
    }
    
    // ... делегировать все остальные методы интерфейса Set<E> полю "set" ...
    @Override public int size() { return set.size(); }
    @Override public boolean isEmpty() { return set.isEmpty(); }
    // ... и так далее
}
```

При таком подходе подкласс полностью контролирует логику, а `HashSet` используется как "чёрный ящик".

### Резюме

1. **Хрупкость из-за состояния** возникает при использовании `protected` полей и решается строгой инкапсуляцией.
2. **Хрупкость из-за поведения** возникает из-за переопределения методов с неявными контрактами и решается переходом к композиции.
