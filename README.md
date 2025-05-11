# Injections
Simple Library to use Annotations for injecting, invoking and instantiation.
It allows you to initialize whole projects annotation based.
You don't even have to worry about dependencies, this library supports circular dependencies.
## Table of Contents

- [Dependency](#dependency)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Setup](#setup)
- [Default AnnotationProcessors](#default-annotation-processors)
  - [@Instantiate](#instantiate)
  - [@Inject](#inject)
  - [@Startup](#startup)
  - [@LateStartup](#latestartup)
  - [@Shutdown](#shutdown)
  - [@Timer](#timer)
- [Custom AnnotationProcessors](#custom-annotation-processors)
  - [for Fields](#fields)
  - [for Methods](#methods)
  - [for Classes](#fields)
- [Migration](#migration)
  - [from 1.x.x to 2.0.0](#from-1xx-to-200)
  - [from 2.x.x to 3.0.0](#from-2xx-to-300)

## Dependency
### Maven
```
<repository>
  <id>flammenfuchs-repo-public</id>
  <name>Flammenfuchs_YT's Repository</name>
  <url>https://repo.flammenfuchs.de/public</url>
</repository>
```
```
<dependency>
    <groupId>de.flammenfuchs</groupId>
    <artifactId>injections</artifactId>
    <version>3.2.0</version>
</dependency>
```
### Gradle
```
maven {
	url "https://repo.flammenfuchs.de/public"
}
```
```
implementation("de.flammenfuchs:injections:3.2.0")
```
## Setup
Example Setup:
```
//Initialize Manager 
//You can override some settings in the bootstrap, like shown below, but it is not needed.
InjectionsManager manager = InjectionsBuilder
                                .create() //change config in builder after this line
                                .addTarget(this.getClass().getClassLoader, 
                                    "net.example", "net.example.ignored") //Add targets
                                .build();
       
//Here you can add custom annotation processors.
       
//To start scanning an processing
manager.start();
```
### Important Node
In your instantiated Classes. You are not allowed to use injected fields in the constructor.
Please use @Invoke instead.<br>
Here is an example:
```
@Instantiate
public class ExampleClass {
            
    @Inject
    OtherExampleClass example;
    
    @Invoke
    private void init() {
        example.doSmth();
    }
}
```
## Default Annotation Processors
### @Scoped
Example Code:
```
@Scoped
public class ExampleClass {
}
```
All classes annotated with `@Scoped` will be processed 
and supports the other Annotations in it. **Node:** These classes
need an empty constructor.
### @Inject
Example Code:
```
@Inject
private TypeConsumerRegistry registry;
```
It allows you to Inject all processed Classes in other processed Classes.<br>
You can add additional objects manually with the following code:
```
manager.getDependencyRegistry().register(object); //type is class of object

manager.getDependencyRegistry().register(Instance.class, object); //type can be defined manually
```
### @ConfigProperty
Example Code:
```
@ConfigProperty("example.property", save=false)
private String example;
```
It will inject a value from the config file.<br>
**Node:** This needs to be enabled in the InjectionsBuilder.<br>
**Node:** The default file location is `./application.json`
### @Startup
Example Code:
```
@Startup
private void init() {
  //do something
}
```
Methods with `@Startup` will be invoked, after all dependencies are injected
Here you can use injected fields safely.
**Node:** These methods can have parameters. The ProcessorAdapter will use
the injectable objects to invoke the method.
### @LateStartup
Example Code:
```
@LateStartup
private void init() {
  //do something
}
```
Methods with `@LateStartup` will be invoked, after everything else is done.
Here you can use injected fields safely.
**Node:** These methods can have parameters. The ProcessorAdapter will use
the injectable objects to invoke the method.
### @Shutdown
Example Code:
```
@Shutdown
private void init() {
  //do something
}
```
Methods with `@Shutdown` will be invoked as a shutdown hook.
**Node:** These methods can have parameters. The ProcessorAdapter will use
the injectable objects to invoke the method.
### @Timer
Example Code:
```
@Timer(delay=1000, period = 10)
private void init() {
  //do something
}
```
Methods with `@Timer` will be invoked as a java timer.
The delay is the initial delay after injection when the method is called.
The period is the time between the calls in milliseconds. A value below 1 will disable repeating.
**Node:** These methods can have parameters. The ProcessorAdapter will use
the injectable objects to invoke the method.
## Custom Annotation Processors
You can add custom annotations for fields, methods and classes<br>
You need the AnnotationRegistry for it
```
AnnotationRegistry registry = manager.getAnnotationRegistry();
```
### Fields
```
registry.registerFieldAnnotation(FieldAnnotation.class, (field, o) -> new Object());
```
The Method `processField(Field field, Object instance)` is the lambda
expression used above. The Object is the instance of the owning class of the field.
The returned Object will be set as value for the field.
### Methods
```
registry.registerMethodAnnotation(MethodAnnotation.class, (method, o) -> method.invoke(o);
```
The Method `processMethod(Method method, Object instance)` is the lambda
expression used above. The Object is the instance of the owning class of the method.
It is a void method, and it is used to invoke Methods anywhere else.
For Example to run the method at a specific time.
If you want to allow methods with parameters, your Annotation needs 
to be annotated with `@AllowParameters`, otherwise these Methods are not allowed
to have parameters and will throw an Exception while scanning.
### Classes
```
registry.registerClassAnnotation(ClassAnnotation.class, (class, o) -> true);
```
The Method `processClass(Class clazz)` is the lambda
expression used above. The Class is not instantiated. The return value is a
boolean. If it returns true, the class will be processed.
If it returns false, the class will not be processed.<br>
**Note:** Class Annotations can be annotated with `@AlternativeTypeDef`, this will set the method name of the class
annotation which returns a class or a class array and defines alternative types for the class.<br>
Example:
```java
@AlternativeTypeDef("classes")
public @interface Example {
    Class<?>[] classes();
}
```
This will register the annotated classes with these defined types.

# Migration
## from 1.x.x to 2.0.0
- The usage of the annotations is not changed.<br>
- The most code in the injection Process was rewritten in this update.
The InjectionsBootstrap was replaced by an InjectionsBuilder. Look [here](#setup) how
to set up the manager now.
- The ProcessorAdapter is replaced completely. You have to update getters etc. everywhere
- All injectable instances are held by a DependencyRegistry. You have to update your code when
you manually register/resolved an object.
- **Summary:** The initialization of injections is completely changed. It is necessary to
recode that. The actual usage in form of annotations can be untouched. 
## from 2.x.x to 3.0.0
- `@Invoke` was renamed to `@Startup`. Its usage is the same.
- `@Instantiate` was renamed to `@Scoped`. Its usage is the same.