# Injections
Simple Library to use Annotations for injecting, invoking and instantiation.
It allows you to initialize whole projects annotation based.
You don't even have to worry about dependencies, this library supports circular dependencies.
## Table of Contents

- [Dependency](#dependency)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Setup](#setup)
- [Custom AnnotationProcessors](#custom-annotation-processors)
  - [for Fields](#fields)
  - [for Methods](#methods)
  - [for Classes](#fields)

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
    <version>1.0.0</version>
</dependency>
```
### Gradle
```
maven {
	url "https://repo.flammenfuchs.de/public"
}
```
```
implementation("de.flammenfuchs:injections:1.0.0")
```
## Setup
Example Setup:
```
//Initialize Manager with Bootstrap
//You can override some settings in the bootstrap, like shown below, but it is not needed.
InjectionsManager manager = new InjectionsManager(
    InjectionsBootstrap.create(this)
        .classLoader(Optional.of(getClass().getClassLoader()))
        .logLevel(LogLevel.EXTENDED)
    );
       
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
It is a void method and it is used to invoke Methods anywhere else.
For Example to run the method at an specific time.
### Classes
```
registry.registerClassAnnotation(ClassAnnotation.class, (class, o) -> true);
```
The Method `processClass(Class clazz)` is the lambda
expression used above. The Class is not instantiated. The return value is a
boolean. If it returns true, the class will be processed.
If it returns false, the class will not be processed.
