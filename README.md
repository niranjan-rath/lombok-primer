Declutter Your POJOs with Lombok
I have a love/hate relationship with Java. On one hand, it’s a mature programming language with a diverse number of frameworks and libraries that make development relatively easy. On the other hand, it’s very verbose and requires writing massive amounts of boilerplate code for common tasks. The situation got better with the introduction of lambdas and streams in Java 8, but it is still sub-par in some areas, like writing plain old Java objects POJO. In this post, I’ll show you how to rewrite POJOs in only a few lines of code with Lombok.
Verbose POJOs

Take a look at this regular POJO class with just three fields: name, surname, and age. It has elements that are common to POJO classes: getters, setters, equals, hashCode, and toString method.

public class User {

    private String name;
    private String surname;
    private int age;

    public User(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age
                && Objects.equals(name, user.name)
                && Objects.equals(surname, user.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                '}';
    }

}

It took us 59 lines of code just to implement this simple class with three fields. Sure, an IDE could generate it for us, but remember that code is read much more often that it is written. So what is even worse than the sheer number of lines it takes to represent this simple concept is that the code fails to clearly communicate the implementation’s intent. How long will it take for you to answer these questions:

    What fields don’t have getters/setters?
    What fields are not used in equals/hashCode/toString methods?
    What fields have non-standard getters/setters?
    What getters/setters have non-public modifiers?

It’s not impossible to get these answers, but it is much harder than it should be. The signal-to-noise-ratio is just too low!

The situation gets even worse if we need to implement the builder pattern. Besides all the POJO code, we need to maintain similar boilerplate for one more class – roughly doubling the amount of borderline useless code. As if this is not bad enough, when we need to add a new field, we have to update both the builder and the POJO class in multiple places. This makes it is easy to miss something and introduce a bug.
More from this author

    Java's Synchronized Keyword in Three Minutes
    The Dangers of Race Conditions in Five Minutes
    Beyond POJOs - Ten More Ways to Reduce Boilerplate with Lombok

Introducing Lombok

Gladly there is a better way. Lombok is a library that allows us to define POJO classes using a set of straightforward, but powerful annotations. These annotations specify if a particular field should have getter/setter, if it should participate in equals/hashCode/toString methods, and so on. Lombok has been around for a few years and has been used in many commercial and open source projects.

So let’s add Lombok to our project. It’s very straightforward, just add the following dependency to your pom.xml file:

<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <version>1.16.10</version>
  <scope>provided</scope>
</dependency>

After this, your Lombok code will compile without issues, but if you are using an IDE you will be in trouble. Since Lombok annotations generate new code (see below for how Lombok works), the IDE should be aware what methods are implicitly added by what annotation. Fortunately, there are several plugins for different IDEs, so you can pick your favorite.

If you use Intellij IDEA, you need to download a third-party plugin for Lombok support. If you use Eclipse, you need to download the lombok.jar and just execute it. It will install the Eclipse plugin. For NetBeans, you need the same JAR and have to enable annotation processing.

Now we are ready to improve our code with Lombok.
Using Lombok

Lombok provides a few basic annotations to define if a particular field should have an accessor method or if it should be used in equals/hashCode/toString methods:

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class User {

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private String surname;

    @Setter
    @Getter
    private int age;

}

As you can see, we have five basic annotations. @EqualsAndHashCode and @ToString instruct Lombok to generate equals, hashCode, and toString methods that will use all fields in the class. @AllArgsConstructor will create a constructor that has as many arguments as there are fields in the class. @Getter/@Setter annotations are applied to every field and will create getters and setters.

Now we can use this class just as a regular POJO class:

User user = new User("John", "Doe", 32);
user.setAge(30);
user.equals(new User("John", "Doe", 30)); // true

If you don’t want an annotation to use all fields, you can provide an optional parameter that specifies what fields should be used for generating a method. So if we do not want to use the age field in the toString method you can do it like this:

@EqualsAndHashCode
@ToString(of = {"name", "surname"})
@AllArgsConstructor
public class User {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String surname;

    @Getter
    @Setter
    private int age;

}

I would argue this code is already much better than what we’ve started with, but it still does not look concise. What about these repetitive @Getter/@Setter annotations? It’s handy to use them like this if you need to expose only a couple of fields in your class, but if you need to generate accessors for all your fields, it becomes annoying.

To make this case even more concise, Lombok allows using these annotations on the class level. If @Getter/@Setter annotations are used on the class level Lombok will generate getters and setters for all fields in the class.

@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Getter
@Setter
public class User {

    private String name;
    private String surname;
    private int age;

}

This does not change the behavior of the class, but now the code is even more succinct.
Implementing Builder

At the beginning of the post, I argued that builders add another level of boilerplate. Lombok turns the exercise of writing a builder into a one-liner. To do this, just add the @Builder annotation on top of a class, and you will have a builder with a fluid interface at your disposal:

@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    private String name;
    private String surname;
    private int age;

}

Now we can create an instance of our class using a builder:

User user = User.builder()
      .name("John")
      .surname("Doe")
      .age(32)
      .build();

Reducing Lombok Boilerplate

The result code is more readable, but what about this repetitive set of annotations? Most POJOs have all these methods, and it is still cumbersome to use the same annotations again and again. Lombok provides two annotations that can make this even simpler:

    @Data – an annotation that replaces @Getter, @Setter, @EqualsAndHashCode, @ToString and @RequiredArgsConstructor
    @Value – same as before but generates an immutable class with no setters

@Data and @Value annotations got their names from so-called data classes and value classes. Data classes are mutable classes that expose their fields through getters and setters. Value classes on the other hand, are immutable and usually have no logic except equals, hashCode, and toString methods.

So we can rewrite our class with just two annotations:

@Data
@Builder
public class User {

    private String name;
    private String surname;
    private int age;

}

Not only is this example much shorter, but it also conveys the meaning of this code much better. It is clear that this class has three fields, all of them have getters and setters, all of them are used in hashCode/equals/toString methods, and there is a builder class for it.
Defining Custom Methods with Lombok

One question you can ask is, what if you need to define a custom getter or setter? What if you need to add a custom boundaries checking for the age value?

Lombok has quite a straightforward behavior in this case: If a method Lomobok is going to generate is already in the class, it does not generate a new one. Therefore, it never overrides your methods and only adds new methods to a class.

--ADVERTISEMENT--
Ads by
How Lombok Works

So how does Lombok work its magic? It turns out that it relies on one official Java standard and one huge hack.

But before we come to that we have to understand the Java compiler a little better. During compilation, it does some heavy lifting and converts the source code into a tree structure called abstract syntax tree (AST). An AST contains all information about the original code such as classes, methods, and program statements. On it, the compiler performs various checks and transformations and then uses the resulting AST to generate the final bytecode instructions.

Based in this, Java 6 has introduced the Pluggable Annotation Processing API (standardized by JSR 269), which allows Java libraries to execute custom code during compilation. The standard’s original intention is that libraries would only inspect ASTs, particularly annotations, and use them to implement custom code validations or generation of new source files.

But Lombok creators figured out that they can use this feature differently. By using a non-public API, they managed to modify ASTs provided by the compiler to add new methods, fields, and even classes during the compilation phase. The compiler then uses this modified AST to generate bytecode for Lombok-generated methods and fields as if they were written by a developer.
Delombok

Not every dependency added to the project stays there forever and sometimes we need to get rid of one to move forward. Fortunately Lombok easily allows us to revert all “lomboked” classes to vanilla Java using a process called delombok. To do this you need to call the delombok command provided in the lombok.jar:

java -jar lombok.jar delombok src -d src-delomboked

This generates the source code for all the methods and classes Lombok usually spins during compilation and writes it to the specified folder.

You can even add delombok task to your Ant script as well:

<mkdir dir="build/src-delomboked" />
<delombok verbose="true" encoding="UTF-8"
          to="build/src-delomboked" from="src">
    <format value="suppressWarnings:skip" />
</delombok>

The latter method is handy if you want to keep your code as is but need to generate JavaDoc for the code with Lombok annotations.
Summary

As you can see, Lombok can significantly de-clutter your Java code. It uses succinct annotations to generate repetitive and verbose builder classes and methods such as getters, setters, and constructors. It can easily help you get rid of thousands of lines of boilerplate code, even in a medium size project. Lombok also allows you to make your code more expressive, concise and can help you avoid some bugs. Finally, it is also versatile enough to be used in a wide variety of projects from console apps to web apps.