package com.FirstSpring.quizproject.config;

import com.FirstSpring.quizproject.Question;
import com.FirstSpring.quizproject.Quiz;
import com.FirstSpring.quizproject.dao.QuestionDao;
import com.FirstSpring.quizproject.dao.QuizDao;
import com.FirstSpring.quizproject.dao.UserDao;
import com.FirstSpring.quizproject.model.Role;
import com.FirstSpring.quizproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 70s Retro Quiz-Show Data Initializer.
 * Seeds full 20-question sets for Java, SpringBoot, and Python Developer tracks,
 * builds 20-question championship quizzes, and synchronizes PostgreSQL sequences.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // 1. Seed Default Demo Users
        seedDefaultUsers();

        // 2. Synchronize PostgreSQL sequence counter
        syncPostgreSqlSequences();

        // 3. Seed Python Questions (ensure at least 20 questions)
        long pythonCount = questionDao.findAll().stream().filter(q -> "Python".equalsIgnoreCase(q.getCategory())).count();
        if (pythonCount < 20) {
            seedPythonQuestions();
            syncPostgreSqlSequences();
        }

        // 4. Seed Java Questions (ensure at least 20 questions)
        long javaCount = questionDao.findAll().stream().filter(q -> "Java".equalsIgnoreCase(q.getCategory())).count();
        if (javaCount < 20) {
            seedJavaQuestions();
            syncPostgreSqlSequences();
        }

        // 5. Seed Spring Boot Questions (ensure at least 20 questions)
        long sbCount = questionDao.findAll().stream().filter(q -> "SpringBoot".equalsIgnoreCase(q.getCategory())).count();
        if (sbCount < 20) {
            seedSpringBootQuestions();
            syncPostgreSqlSequences();
        }

        // 6. Seed 20-question Assessments if missing
        seedBaselineQuizzes();
        syncPostgreSqlSequences();
    }

    private void syncPostgreSqlSequences() {
        if (jdbcTemplate != null) {
            try {
                jdbcTemplate.execute("SELECT setval('question_id_seq', (SELECT COALESCE(MAX(id), 1) FROM question));");
            } catch (Exception ignored) {}
            try {
                jdbcTemplate.execute("SELECT setval('quiz_id_seq', (SELECT COALESCE(MAX(id), 1) FROM quiz));");
            } catch (Exception ignored) {}
            try {
                jdbcTemplate.execute("SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users));");
            } catch (Exception ignored) {}
        }
    }

    private void seedDefaultUsers() {
        if (!userDao.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Show Host / Teacher")
                    .role(Role.ROLE_ADMIN)
                    .build();
            userDao.save(admin);
        }

        if (!userDao.existsByUsername("student")) {
            User student = User.builder()
                    .username("student")
                    .password(passwordEncoder.encode("student123"))
                    .fullName("Contestant / Student")
                    .role(Role.ROLE_STUDENT)
                    .build();
            userDao.save(student);
        }
    }

    private Question q(String title, String opt1, String opt2, String opt3, String opt4, String answer, String category, String diff) {
        Question question = new Question();
        question.setQuestionTitle(title);
        question.setOption1(opt1);
        question.setOption2(opt2);
        question.setOption3(opt3);
        question.setOption4(opt4);
        question.setRightAnswer(answer);
        question.setCategory(category);
        question.setDifficultyLevel(diff);
        return question;
    }

    // ── 20 Python Developer Interview Questions ─────────────────────────────
    private void seedPythonQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(q("What are Python's key built-in data types and their mutability?",
                "Lists/Dicts/Sets are mutable; Tuples/Strings/Ints/Floats are immutable",
                "All Python collections and primitives are completely mutable",
                "Strings and Tuples are mutable; Lists and Dictionaries are immutable",
                "Primitives are mutable; Compound data types are strictly immutable",
                "Lists/Dicts/Sets are mutable; Tuples/Strings/Ints/Floats are immutable", "Python", "Easy"));

        list.add(q("What is the purpose of the Global Interpreter Lock (GIL) in CPython?",
                "A mutex that ensures only one native thread executes Python bytecodes at a time",
                "A compiler that converts Python code into native machine assembly",
                "A security sandbox that disables unauthorized file system access",
                "A garbage collector algorithm for cyclic references",
                "A mutex that ensures only one native thread executes Python bytecodes at a time", "Python", "Medium"));

        list.add(q("How does memory management work in Python?",
                "Managed by Python Memory Manager using private heaps, reference counting, and cyclic GC",
                "Manual allocation and deallocation via malloc and free",
                "Garbage collection based entirely on stack unwinding",
                "Variables are stored in CPU registers and never collected",
                "Managed by Python Memory Manager using private heaps, reference counting, and cyclic GC", "Python", "Hard"));

        list.add(q("What is the difference between shallow copy and deep copy in Python?",
                "Shallow copy copies references to nested objects; deep copy recursively clones all nested objects",
                "Shallow copy modifies the original object; deep copy is read-only",
                "Deep copy works only on immutable types; shallow copy works only on mutable types",
                "They are synonyms provided by the copy module with identical behavior",
                "Shallow copy copies references to nested objects; deep copy recursively clones all nested objects", "Python", "Medium"));

        list.add(q("What are Python decorators?",
                "Functions that take another function as argument and extend its behavior without modifying it",
                "HTML CSS styling rules embedded in Python strings",
                "Type hints introduced in Python 3.5 for static checking",
                "Keywords that compile Python functions into C extensions",
                "Functions that take another function as argument and extend its behavior without modifying it", "Python", "Medium"));

        list.add(q("What is the difference between 'is' and '==' in Python?",
                "'is' checks object identity (same memory address); '==' checks value equality",
                "'is' compares values; '==' compares memory addresses",
                "'is' is for numeric comparison; '==' is for strings",
                "Both check identical object identities under all circumstances",
                "'is' checks object identity (same memory address); '==' checks value equality", "Python", "Easy"));

        list.add(q("How do Python generators work and what is the 'yield' keyword?",
                "Generators produce values lazily using 'yield', pausing execution state without loading everything into memory",
                "Generators pre-compute all items in RAM and return a frozen list",
                "The yield keyword terminates the Python script immediately",
                "Generators can only iterate over integer ranges",
                "Generators produce values lazily using 'yield', pausing execution state without loading everything into memory", "Python", "Medium"));

        list.add(q("What is the difference between list append() and extend() methods?",
                "append() adds the argument as a single element; extend() iterates over the collection and adds each item",
                "extend() replaces existing elements; append() inserts at index 0",
                "append() works only with numbers; extend() works only with strings",
                "Both methods perform identical operations on lists",
                "append() adds the argument as a single element; extend() iterates over the collection and adds each item", "Python", "Easy"));

        list.add(q("What are *args and **kwargs in Python function definitions?",
                "*args collects variable positional arguments as a tuple; **kwargs collects keyword arguments as a dict",
                "*args defines return types; **kwargs defines input arguments",
                "*args creates pointers; **kwargs creates references",
                "*args and **kwargs are reserved keywords for multi-threading",
                "*args collects variable positional arguments as a tuple; **kwargs collects keyword arguments as a dict", "Python", "Easy"));

        list.add(q("What is a lambda function in Python?",
                "An anonymous, inline single-expression function defined with the lambda keyword",
                "A built-in method for multi-threaded concurrency",
                "A mathematical operator for calculating matrix eigenvalues",
                "A private class method that cannot be accessed outside its module",
                "An anonymous, inline single-expression function defined with the lambda keyword", "Python", "Easy"));

        list.add(q("How does exception handling work with try-except-else-finally in Python?",
                "'else' runs if no exception occurred; 'finally' runs unconditionally regardless of exceptions",
                "'else' catches uncaught exceptions; 'finally' runs only if an error was raised",
                "'finally' runs only when try block succeeds; 'else' runs always",
                "The 'else' clause is illegal in Python try-except statements",
                "'else' runs if no exception occurred; 'finally' runs unconditionally regardless of exceptions", "Python", "Medium"));

        list.add(q("What is the purpose of the __init__ method in Python classes?",
                "The constructor/initializer called automatically when an instance of the class is created",
                "A destructor method called before garbage collection",
                "A class method that prevents inheritance",
                "A static method for converting objects to string format",
                "The constructor/initializer called automatically when an instance of the class is created", "Python", "Easy"));

        list.add(q("What is the difference between @staticmethod and @classmethod?",
                "@classmethod receives the class 'cls' as first argument; @staticmethod receives neither 'self' nor 'cls'",
                "@staticmethod can modify class state; @classmethod cannot",
                "@classmethod cannot be called on class instances",
                "@staticmethod is strictly deprecated in Python 3",
                "@classmethod receives the class 'cls' as first argument; @staticmethod receives neither 'self' nor 'cls'", "Python", "Medium"));

        list.add(q("What are list comprehensions and generator expressions in Python?",
                "List comprehensions build lists in memory [x for x in seq]; generator expressions create lazy iterators (x for x in seq)",
                "Generator expressions return tuples; list comprehensions return sets",
                "List comprehensions are slower than traditional for-loops in CPython",
                "Both consume equal memory space regardless of collection size",
                "List comprehensions build lists in memory [x for x in seq]; generator expressions create lazy iterators (x for x in seq)", "Python", "Medium"));

        list.add(q("How does Python handle multiple inheritance and Method Resolution Order (MRO)?",
                "Uses C3 Linearization algorithm to determine method lookup order, accessible via Class.__mro__",
                "Does not support multiple inheritance due to diamond problem",
                "Resolves methods strictly alphabetically by class name",
                "Randomly selects which parent method executes first",
                "Uses C3 Linearization algorithm to determine method lookup order, accessible via Class.__mro__", "Python", "Hard"));

        list.add(q("What is the purpose of the 'with' statement and Context Managers in Python?",
                "Ensures deterministic resource acquisition and release via __enter__ and __exit__ methods",
                "Creates multi-threaded synchronized blocks like Java",
                "Imports modules with aliases dynamically at runtime",
                "Defines type annotations for function parameters",
                "Ensures deterministic resource acquisition and release via __enter__ and __exit__ methods", "Python", "Medium"));

        list.add(q("What is the difference between multiprocessing and multithreading in Python?",
                "Multiprocessing creates separate OS processes bypassing GIL; multithreading shares one process and GIL",
                "Multithreading uses multiple CPU cores for CPU-bound tasks in CPython",
                "Multiprocessing is only supported on Linux; multithreading on Windows",
                "Both bypass the GIL equally in standard CPython",
                "Multiprocessing creates separate OS processes bypassing GIL; multithreading shares one process and GIL", "Python", "Hard"));

        list.add(q("How does the 'asyncio' module facilitate concurrency in Python?",
                "Provides an event loop using coroutines with async/await for cooperative single-threaded asynchronous I/O",
                "Launches kernel background threads for each async function",
                "Compiles Python bytecodes into asynchronous C code",
                "Is only applicable to UDP network sockets",
                "Provides an event loop using coroutines with async/await for cooperative single-threaded asynchronous I/O", "Python", "Hard"));

        list.add(q("What is the purpose of __slots__ in Python classes?",
                "Restricts allowed instance attributes to save memory by preventing __dict__ creation per instance",
                "Defines the number of threads that can access an object simultaneously",
                "Specifies maximum number of elements in a list instance",
                "Enables serialization to JSON without custom encoders",
                "Restricts allowed instance attributes to save memory by preventing __dict__ creation per instance", "Python", "Hard"));

        list.add(q("What is PEP 8 in Python?",
                "The official Style Guide for Python Code defining formatting, naming conventions, and best practices",
                "The specification for Python Virtual Machine bytecode execution",
                "The package manager tool used to install libraries from PyPI",
                "The security protocol for signing Python egg and wheel packages",
                "The official Style Guide for Python Code defining formatting, naming conventions, and best practices", "Python", "Easy"));

        questionDao.saveAll(list);
    }

    // ── 20 Java Interview Questions ─────────────────────────────────────────
    private void seedJavaQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(q("What are the main features of Java?",
                "Platform independence, Object-oriented, Robust, and Garbage collected",
                "Platform dependent, Procedure-oriented, Manual memory management",
                "Interpreted only, Single-threaded, No garbage collection",
                "Requires assembly code compilation per operating system",
                "Platform independence, Object-oriented, Robust, and Garbage collected", "Java", "Easy"));

        list.add(q("What is the primary difference between JDK, JRE, and JVM?",
                "JDK includes dev tools + JRE; JRE runs applications; JVM executes bytecode",
                "JVM is for compilation; JDK is for execution; JRE is the source code",
                "JRE is the complete SDK; JDK is the runtime engine; JVM is the compiler",
                "All three are identical terms used interchangeably",
                "JDK includes dev tools + JRE; JRE runs applications; JVM executes bytecode", "Java", "Easy"));

        list.add(q("What is the key difference between an abstract class and an interface?",
                "Abstract class can have state and constructor; interface defines contracts (supports multiple inheritance)",
                "Interface can have constructors; abstract class cannot",
                "Abstract class supports multiple inheritance; interface does not",
                "There is no difference between them in modern Java",
                "Abstract class can have state and constructor; interface defines contracts (supports multiple inheritance)", "Java", "Medium"));

        list.add(q("What distinguishes compile-time polymorphism from runtime polymorphism?",
                "Compile-time is method overloading; runtime is method overriding",
                "Compile-time is method overriding; runtime is method overloading",
                "Compile-time uses interfaces; runtime uses abstract classes",
                "Both are resolved strictly at execution time by JIT",
                "Compile-time is method overloading; runtime is method overriding", "Java", "Easy"));

        list.add(q("What is the difference between checked and unchecked exceptions?",
                "Checked exceptions are verified at compile time; unchecked occur at runtime (RuntimeException)",
                "Checked exceptions extend RuntimeException; unchecked extend Exception",
                "Unchecked exceptions must be declared with throws keyword",
                "Checked exceptions cannot be caught in try-catch blocks",
                "Checked exceptions are verified at compile time; unchecked occur at runtime (RuntimeException)", "Java", "Easy"));

        list.add(q("What are the access modifiers in Java and their scope?",
                "public (everywhere), protected (package+subclasses), default (package), private (class only)",
                "public (class only), private (everywhere), protected (subclasses only)",
                "global, local, internal, and private",
                "static, final, abstract, and volatile",
                "public (everywhere), protected (package+subclasses), default (package), private (class only)", "Java", "Easy"));

        list.add(q("What is the difference between String, StringBuilder, and StringBuffer?",
                "String is immutable; StringBuilder is mutable (non-thread-safe); StringBuffer is mutable (thread-safe)",
                "String is mutable; StringBuilder is immutable; StringBuffer is obsolete",
                "StringBuilder is thread-safe; StringBuffer is non-thread-safe",
                "All three are synchronized and immutable",
                "String is immutable; StringBuilder is mutable (non-thread-safe); StringBuffer is mutable (thread-safe)", "Java", "Easy"));

        list.add(q("What is the difference between '==' and '.equals()' method?",
                "== compares object memory references; .equals() compares object contents/state",
                "== compares object values; .equals() compares memory addresses",
                "== is for Strings only; .equals() is for primitives only",
                "They perform identical reference equality checks always",
                "== compares object memory references; .equals() compares object contents/state", "Java", "Easy"));

        list.add(q("What is the difference between ArrayList and LinkedList?",
                "ArrayList uses dynamic array (fast index access); LinkedList uses doubly-linked nodes (fast insert/delete)",
                "ArrayList is synchronized; LinkedList is not synchronized",
                "LinkedList uses continuous memory; ArrayList stores scattered pointers",
                "ArrayList allows duplicate elements while LinkedList does not",
                "ArrayList uses dynamic array (fast index access); LinkedList uses doubly-linked nodes (fast insert/delete)", "Java", "Medium"));

        list.add(q("What is the purpose of the 'final' keyword in Java?",
                "Constants for variables, prevents overriding for methods, prevents inheritance for classes",
                "Forces garbage collection immediately on an object",
                "Makes methods execute synchronously in multi-threading",
                "Declares abstract methods that must be overridden",
                "Constants for variables, prevents overriding for methods, prevents inheritance for classes", "Java", "Easy"));

        list.add(q("How does Java handle multiple inheritance of classes?",
                "Java does not support multiple class inheritance (avoids Diamond Problem), but allows multiple interface implementation",
                "Java supports multiple class inheritance using the 'extends all' keyword",
                "Java supports multiple inheritance only if all parent classes are abstract",
                "Java does not support any form of multiple inheritance, even with interfaces",
                "Java does not support multiple class inheritance (avoids Diamond Problem), but allows multiple interface implementation", "Java", "Medium"));

        list.add(q("What is the difference between String Pool and Heap storage for strings?",
                "String Pool stores shared string literals in Metaspace/Heap to optimize memory; 'new String()' allocates distinct heap object",
                "String Pool is stored in CPU registers; Heap is stored in swap space",
                "Heap stores only interned strings; String Pool stores all other objects",
                "Strings created with 'new' are placed in the String Pool automatically",
                "String Pool stores shared string literals in Metaspace/Heap to optimize memory; 'new String()' allocates distinct heap object", "Java", "Medium"));

        list.add(q("What is the difference between 'throw' and 'throws' keywords?",
                "'throw' explicitly throws an exception instance; 'throws' declares exceptions in the method signature",
                "'throw' is used in method headers; 'throws' is used inside method bodies",
                "'throw' handles exceptions; 'throws' causes checked exceptions",
                "Both keywords are synonyms and can be used interchangeably",
                "'throw' explicitly throws an exception instance; 'throws' declares exceptions in the method signature", "Java", "Easy"));

        list.add(q("How does the try-with-resources statement work?",
                "Automatically closes resources that implement AutoCloseable or Closeable at the end of the block",
                "Restarts failing database transactions automatically",
                "Allocates additional memory heap space for high-volume objects",
                "Surpresses all unchecked RuntimeExceptions automatically",
                "Automatically closes resources that implement AutoCloseable or Closeable at the end of the block", "Java", "Medium"));

        list.add(q("What is the difference between HashMap and Hashtable?",
                "HashMap is non-synchronized and allows one null key; Hashtable is synchronized and does not allow null keys",
                "Hashtable is faster and modern; HashMap is deprecated",
                "HashMap maintains natural sorted order; Hashtable does not",
                "Hashtable allows null keys; HashMap throws NullPointerException",
                "HashMap is non-synchronized and allows one null key; Hashtable is synchronized and does not allow null keys", "Java", "Medium"));

        list.add(q("How does the 'volatile' keyword work in Java?",
                "Guarantees visibility of changes across threads by reading/writing directly from/to main memory",
                "Provides mutual exclusion lock like synchronized block",
                "Serializes the object to disk automatically",
                "Prevents the variable from ever being modified",
                "Guarantees visibility of changes across threads by reading/writing directly from/to main memory", "Java", "Hard"));

        list.add(q("What is the difference between Comparable and Comparator interfaces?",
                "Comparable defines natural ordering (compareTo in class); Comparator defines custom/multiple ordering strategies (compare)",
                "Comparable is external to the class; Comparator is implemented by the domain class itself",
                "Comparable can sort by multiple fields; Comparator can only sort by one field",
                "Comparator cannot be used with lambda expressions",
                "Comparable defines natural ordering (compareTo in class); Comparator defines custom/multiple ordering strategies (compare)", "Java", "Medium"));

        list.add(q("What is the difference between fail-fast and fail-safe iterators?",
                "Fail-fast throws ConcurrentModificationException on concurrent modification; Fail-safe works on a clone/copy",
                "Fail-fast ignores modifications; Fail-safe aborts JVM process",
                "Fail-fast is only in java.util.concurrent; Fail-safe is in java.util",
                "Both behave identically during multi-threaded iteration",
                "Fail-fast throws ConcurrentModificationException on concurrent modification; Fail-safe works on a clone/copy", "Java", "Hard"));

        list.add(q("What is the purpose of the Optional class in Java 8+?",
                "Provides a container object to represent presence or absence of value, helping prevent NullPointerExceptions",
                "Replaces standard Java collections like List and Set",
                "Optimizes garbage collection cycles for nullable references",
                "Encodes optional JSON REST responses automatically",
                "Provides a container object to represent presence or absence of value, helping prevent NullPointerExceptions", "Java", "Easy"));

        list.add(q("What is Type Erasure in Java Generics?",
                "Process where compiler removes generic type parameters after type checking, replacing them with raw types or bounds",
                "A garbage collector cycle that cleans up unused generic classes",
                "A JVM runtime security exception thrown on invalid type casting",
                "A technique for converting Java classes into JSON schemas",
                "Process where compiler removes generic type parameters after type checking, replacing them with raw types or bounds", "Java", "Hard"));

        questionDao.saveAll(list);
    }

    // ── 20 Spring Boot Interview Questions ──────────────────────────────────
    private void seedSpringBootQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(q("What is Spring Boot, and how does it differ from core Spring framework?",
                "Spring Boot provides opinionated defaults, embedded server, and auto-configuration to eliminate boilerplate XML/Java config",
                "Spring Boot is a frontend JavaScript framework built by the Spring team",
                "Spring Boot replaces Java with Kotlin internally",
                "Spring Boot removes Dependency Injection from core Spring",
                "Spring Boot provides opinionated defaults, embedded server, and auto-configuration to eliminate boilerplate XML/Java config", "SpringBoot", "Easy"));

        list.add(q("How does Auto-Configuration work in Spring Boot?",
                "Scans classpath starters using @Conditional annotations to automatically register required beans in ApplicationContext",
                "Compiles Java source code directly to machine assembly at build time",
                "Generates database tables without needing Hibernate or JPA",
                "Downloads dependencies from Maven Central while the application is running",
                "Scans classpath starters using @Conditional annotations to automatically register required beans in ApplicationContext", "SpringBoot", "Medium"));

        list.add(q("What is the purpose of the @SpringBootApplication annotation?",
                "Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan into a single bootstrap annotation",
                "Marks a class as a REST endpoint controller",
                "Enables Spring Security authentication filters automatically",
                "Generates automated unit tests for all Spring beans",
                "Combines @Configuration, @EnableAutoConfiguration, and @ComponentScan into a single bootstrap annotation", "SpringBoot", "Easy"));

        list.add(q("What is the role of Spring Boot Actuator?",
                "Provides production-ready production monitoring endpoints (/health, /metrics, /info, /env) for the application",
                "Encrypts database passwords in application.properties",
                "Compiles TypeScript files into native HTML",
                "Acts as an embedded servlet container replacement for Tomcat",
                "Provides production-ready production monitoring endpoints (/health, /metrics, /info, /env) for the application", "SpringBoot", "Easy"));

        list.add(q("How do you handle exceptions globally in a Spring Boot REST application?",
                "Using @ControllerAdvice (or @RestControllerAdvice) with @ExceptionHandler methods returning structured error responses",
                "By wrapping every controller method in a try-catch block manually",
                "By writing custom uncaughtExceptionHandlers in ThreadGroup",
                "Spring Boot automatically hides all errors and returns HTTP 200",
                "Using @ControllerAdvice (or @RestControllerAdvice) with @ExceptionHandler methods returning structured error responses", "SpringBoot", "Medium"));

        list.add(q("What is the difference between @Controller and @RestController?",
                "@RestController is annotated with @Controller and @ResponseBody, returning serialized JSON/XML instead of HTML view names",
                "@Controller is for REST APIs; @RestController is for Thymeleaf views",
                "@RestController does not support HTTP GET requests",
                "There is no difference; they are exact aliases",
                "@RestController is annotated with @Controller and @ResponseBody, returning serialized JSON/XML instead of HTML view names", "SpringBoot", "Easy"));

        list.add(q("What is Dependency Injection (DI) and Inversion of Control (IoC)?",
                "IoC transfers object lifecycle control to a container; DI injects dependencies rather than objects creating them directly",
                "DI is manual object instantiation using the 'new' keyword everywhere",
                "IoC is a design pattern only applicable to database transactions",
                "They are mutually exclusive paradigms used in different frameworks",
                "IoC transfers object lifecycle control to a container; DI injects dependencies rather than objects creating them directly", "SpringBoot", "Medium"));

        list.add(q("What is the default scope of a Spring Bean?",
                "Singleton (a single shared instance per Spring ApplicationContext)",
                "Prototype (a new instance created every time requested)",
                "Request (one instance per HTTP request lifecycle)",
                "Session (one instance per HTTP user session)",
                "Singleton (a single shared instance per Spring ApplicationContext)", "SpringBoot", "Easy"));

        list.add(q("What is the purpose of the @Transactional annotation in Spring?",
                "Manages database transaction boundaries, committing on success and rolling back on unhandled RuntimeExceptions",
                "Encrypts database payloads over TLS",
                "Executes the method asynchronously in a separate worker thread",
                "Caches query results in Redis automatically",
                "Manages database transaction boundaries, committing on success and rolling back on unhandled RuntimeExceptions", "SpringBoot", "Medium"));

        list.add(q("What is the difference between @Component, @Service, and @Repository?",
                "@Component is generic managed bean; @Service indicates business logic; @Repository adds persistence exception translation",
                "@Service is for controllers; @Repository is for REST clients; @Component is deprecated",
                "@Component creates singletons; @Service creates prototypes",
                "All three have completely identical behavior with zero specialized semantics",
                "@Component is generic managed bean; @Service indicates business logic; @Repository adds persistence exception translation", "SpringBoot", "Medium"));

        list.add(q("What is the difference between @RequestParam and @PathVariable?",
                "@PathVariable extracts values from the URI path (/items/{id}); @RequestParam extracts query parameters (?id=123)",
                "@RequestParam is for POST bodies; @PathVariable is for GET headers",
                "@PathVariable can only extract integers; @RequestParam extracts strings",
                "Both extract headers from the incoming HTTP request",
                "@PathVariable extracts values from the URI path (/items/{id}); @RequestParam extracts query parameters (?id=123)", "SpringBoot", "Easy"));

        list.add(q("What is the purpose of @ConfigurationProperties in Spring Boot?",
                "Binds external configuration properties (YAML/properties) to strongly-typed hierarchical Java objects",
                "Configures JPA database connections at runtime",
                "Generates Swagger OpenAPI documentation automatically",
                "Restricts properties to read-only mode in memory",
                "Binds external configuration properties (YAML/properties) to strongly-typed hierarchical Java objects", "SpringBoot", "Medium"));

        list.add(q("What is Spring Security Filter Chain?",
                "A series of sequential servlet filters that intercept requests to perform authentication, authorization, and CSRF protection",
                "An algorithm for hashing passwords using BCrypt",
                "A database table storing user roles and permissions",
                "A firewall hardware appliance configured in front of Tomcat",
                "A series of sequential servlet filters that intercept requests to perform authentication, authorization, and CSRF protection", "SpringBoot", "Hard"));

        list.add(q("What is the difference between @OneToMany and @ManyToOne in JPA?",
                "@OneToMany defines a collection of child entities from a parent; @ManyToOne defines reference back to the single parent",
                "@OneToMany creates a foreign key column in the parent table",
                "@ManyToOne cannot be used with fetch = FetchType.LAZY",
                "They are mutually exclusive and cannot be used in bidirectional relationships",
                "@OneToMany defines a collection of child entities from a parent; @ManyToOne defines reference back to the single parent", "SpringBoot", "Medium"));

        list.add(q("What is the purpose of the CommandLineRunner interface in Spring Boot?",
                "Executes code blocks immediately after the Spring application context has successfully loaded on startup",
                "Accepts terminal CLI arguments while the app is running in background",
                "Compiles Java source files into bytecode from the command line",
                "Restarts the embedded web server if an error occurs",
                "Executes code blocks immediately after the Spring application context has successfully loaded on startup", "SpringBoot", "Medium"));

        list.add(q("What is the purpose of the @Qualifier annotation in Spring?",
                "Resolves ambiguity when multiple candidate beans of the same type exist by specifying bean name to inject",
                "Forces bean initialization to be lazy",
                "Validates input payload fields against constraints",
                "Converts XML payloads to JSON strings automatically",
                "Resolves ambiguity when multiple candidate beans of the same type exist by specifying bean name to inject", "SpringBoot", "Medium"));

        list.add(q("What is the difference between @Component and @Bean annotations?",
                "@Component is a class-level annotation for auto-detection; @Bean is a method-level annotation in @Configuration classes",
                "@Bean is for Spring MVC controllers; @Component is for database models",
                "@Component can only instantiate third-party library classes",
                "@Bean does not register the object in the ApplicationContext",
                "@Component is a class-level annotation for auto-detection; @Bean is a method-level annotation in @Configuration classes", "SpringBoot", "Medium"));

        list.add(q("How does Spring Boot support database migrations?",
                "Using tools like Flyway or Liquibase via automated starter dependencies and versioned SQL scripts",
                "Through Hibernate hbm2ddl.auto=create-drop in production",
                "By running manual ALTER TABLE commands in PostgreSQL CLI",
                "By exporting and importing JSON database dumps on startup",
                "Using tools like Flyway or Liquibase via automated starter dependencies and versioned SQL scripts", "SpringBoot", "Hard"));

        list.add(q("What is the purpose of the @Lazy annotation in Spring?",
                "Delays bean initialization until the bean is explicitly requested rather than during application startup",
                "Delays database query execution until a transaction commits",
                "Reduces CPU thread priority for background tasks",
                "Throttles incoming HTTP requests during high traffic",
                "Delays bean initialization until the bean is explicitly requested rather than during application startup", "SpringBoot", "Medium"));

        list.add(q("How does Spring Boot handle Profiles?",
                "Separates application configuration using @Profile annotations and environment-specific application-{profile}.properties",
                "Creates separate JVM processes for each user profile",
                "Stores user credentials encrypted in memory",
                "Controls thread pool executor profiles dynamically",
                "Separates application configuration using @Profile annotations and environment-specific application-{profile}.properties", "SpringBoot", "Medium"));

        questionDao.saveAll(list);
    }

    // ── 20-Question Assessments for Contestants ─────────────────────────────
    private void seedBaselineQuizzes() {
        // If we already have quizzes, ensure each has 20 questions or create new ones
        List<Question> javaQuestions = questionDao.findByCategory("Java");
        List<Question> sbQuestions = questionDao.findByCategory("SpringBoot");
        List<Question> pyQuestions = questionDao.findByCategory("Python");
        List<Question> allQuestions = questionDao.findAll();

        // 1. Java 20-Question Championship
        if (!javaQuestions.isEmpty() && quizDao.findAll().stream().noneMatch(qz -> qz.getTitle().contains("Java Developer Grand"))) {
            Quiz qz1 = new Quiz();
            qz1.setTitle("Java Developer Grand Championship (20 Questions)");
            int count = Math.min(20, javaQuestions.size());
            qz1.setQuestions(new ArrayList<>(javaQuestions.subList(0, count)));
            quizDao.save(qz1);
        }

        // 2. Spring Boot 20-Question Challenge
        if (!sbQuestions.isEmpty() && quizDao.findAll().stream().noneMatch(qz -> qz.getTitle().contains("Spring Boot & Cloud"))) {
            Quiz qz2 = new Quiz();
            qz2.setTitle("Spring Boot & Cloud Architect Arena (20 Questions)");
            int count = Math.min(20, sbQuestions.size());
            qz2.setQuestions(new ArrayList<>(sbQuestions.subList(0, count)));
            quizDao.save(qz2);
        }

        // 3. Python 20-Question Professional Assessment
        if (!pyQuestions.isEmpty() && quizDao.findAll().stream().noneMatch(qz -> qz.getTitle().contains("Python Developer Professional"))) {
            Quiz qz3 = new Quiz();
            qz3.setTitle("Python Developer Professional Assessment (20 Questions)");
            int count = Math.min(20, pyQuestions.size());
            qz3.setQuestions(new ArrayList<>(pyQuestions.subList(0, count)));
            quizDao.save(qz3);
        }

        // 4. Full-Stack 20-Question Invitational
        if (allQuestions.size() >= 20 && quizDao.findAll().stream().noneMatch(qz -> qz.getTitle().contains("Full-Stack Software Engineering"))) {
            Quiz qz4 = new Quiz();
            qz4.setTitle("Full-Stack Software Engineering Invitational (20 Questions)");
            qz4.setQuestions(new ArrayList<>(allQuestions.subList(0, 20)));
            quizDao.save(qz4);
        }
    }
}
