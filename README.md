# Compose-TimeRangePicker

TimeRangePicker created in Jetpack Compose.

<img src="https://github.com/kako351/Compose-TimeRangePicker/blob/main/images/screenshot-v1.gif" width="300">

## Get Started

### Step1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### Step2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.kako351:Compose-TimeRangePicker:Tag'
}
```

## Usage

```kotlin
TimeRangePicker { startHour, startMinutes, endHour, endMinutes ->
    // do something
}
```

or 

```kotlin
TimeRangePicker { startTime, endTime ->
    // do something
}
```

### Customization

```kotlin
TimeRangePicker (
    startTime = Time(10, 0),
    endTime = Time(20, 0),
    rangeBarStyle = RangeBarStyle(color = MaterialTheme.colorScheme.primary)
) { startTime, endTime ->
    // do something
}
```
## License

[here!](https://github.com/kako351/Compose-TimeRangePicker/blob/main/LICENSE)
