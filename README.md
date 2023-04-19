# Navwei Android V2 - Kotlin
Project web site: https://www.navwei.com/

[Product Demo Video](https://www.instagram.com/p/B9L0W41AJYG/?utm_source=ig_web_button_share_sheet)
<img width="1116" alt="Screenshot 2023-04-19 at 16 26 32" src="https://user-images.githubusercontent.com/3584359/233125880-19b8ae39-0560-46f0-a2aa-1f4e74295046.png">


This is Navwei Android app project Version 2

With the use of this app user can find his way through the biggest malls in the shopping center or airport. Rely on navigation to guide user to the hottest trends, events and happenings.


## Approach of Clean Architecture 
There are 4 layers in the project: Data, Domain, Entity and Presentation(app), and the UI layer contains view and viewModels.
And also [Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) algorithm applied for shortest path calculation.User navigation calculated between many store at different floors in the shopping center.


## Multi-threading
Base `UseCase` class handles the thread of Rx chains, it puts  whole chain on IO thread, and then changes back to Android main thread(UI thread) for the steps after use case execution. That means when you write on this project, then you don't have to worry about any multi-threading issue.

## Requirements &amp; configurations
#### Requirements
- JDK 8
- Android SDK API 29
- Kotlin Gradle plugin 1.3.50 *(it will be installed automatically when this project is synced)*

#### Configurations
- minSdkVersion=21
- targetSdkVersion=29

## Language
*   [Kotlin](https://kotlinlang.org/)

## Libraries
*   [AndroidX](https://developer.android.com/jetpack/androidx)
*   [Dagger 2](https://google.github.io/dagger/)
*   [RxJava2](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0)
*   [RxAndroid](https://github.com/ReactiveX/RxAndroid)
*   [OkHttp](http://square.github.io/okhttp/)
*   [Retrofit](http://square.github.io/retrofit/)
*   [Gson](https://github.com/google/gson)
*   [JUnit 4](https://junit.org/junit4/)


## More about The Clean Architecture

[The Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)


