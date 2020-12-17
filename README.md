# Ktor Global-Call-Data

## Description

This Ktor-Feature allows to access the `call` Object 
from any suspending function that was called by the route (e.g. `get()` or `post()`)
No passthrough of the variable required any more!

This Feature also allows specifying more attributes that should be bound to the rest call.
Examples are
* A Unique Call-ID for each call 
* A Call specific Logging-Object that should be added to the database after the request is done
* ...

## Disclaimer

If anything is unclear, feel free to 
* open an issue
* text me on twitter `@byCaelis`
* text me via email `github@maax.gr`


## Installation
### Add the jitpack repository to build.gradle

```groovy
repositories {
    ...
    maven { url "https://jitpack.io" }
}
```

### Add ktor-globalcalldata dependency to build.gradle.
```groovy
dependencies {
    ...
    implementation 'com.github.MaaxGr:ktor-globalcalldata:1.0.2'
}
```

### Install Feature
```kotlin
install(GlobalCallData)
```

## Example 1: Getting Started

### Configure routing block

Set up a route, that calls a suspending function!
The `test()` function can be in any class 

```kotlin
routing {
    get("/test") {
        test()
        call.respond("OK")
    }
}
````

### Access callData() to get call data

Call the `callData()` from any suspending function, 
that was called by a defined above.


```kotlin
suspend fun test() {
    val url = callData().call.request.uri
    println(url) // prints "/test"
}
```

If call is null test() method was probably called from the wrong coroutine! 

## Example 2: Define custom global data

### Define custom Key

Each CallData has an own key. 
In this example we create a key named `CallUUID` which value can be of type `UUID`.
You can specify a differt Key-Name or value in the generic.
It is important, that the Key is an `object`

```kotlin
object CallUUID : Key<UUID>
```

### Define class that inherits from CallData

Define a class that inherits from CallData and pass the CoroutineContext from the constructor.
Now add a delegated variable for each property you want to have globally accessible for the current call.


```kotlin

class CustomCallData(context: CoroutineContext) : CallData(context) {
    var callUUID: UUID by delegate.propNotNull(CallUUID)
}
```

### Create method to access CustomCallData

Declare the method in a file on the top level to access it globally!

```kotlin
suspend fun customCallData() = CustomCallData(coroutineContext)
```


Import the `coroutineContext` from `kotlin.coroutines.coroutineContext`

### Define route

```kotlin
get("/test") {
    a()
    b()
    call.respond("OK")
}
```

### Define method that stores the Call-UUID

```kotlin
suspend fun a() {
    customCallData().callUUID = UUID.randomUUID()
}
```

### Define a method that accesses the Call-UUID

```kotlin
suspend fun b() {
    val uuid = customCallData().callUUID
    println(uuid) // prints "b0518030-e85b-4991-9105-9520fea931cc"
}
```


## FAQ

To be continued!