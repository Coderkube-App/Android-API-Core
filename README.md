# Android API Core Library

[![](https://jitpack.io/v/Coderkube-App/Android-API-Core.svg)](https://jitpack.io/#Coderkube-App/Android-API-Core)

A robust, reusable Android network library built on top of **Retrofit**, **OkHttp**, and **Coroutines**, designed following Clean Architecture principles. `api-core` serves as a centralized networking framework providing automatic error handling, state wrapping, dynamic headers, Compose compatibility, and Hilt injection out of the box.

## Features

- **Seamless Compose Integration**: Converts API responses directly to Compose `UiState`.
- **Automatic Retries**: Configurable exponential backoff for network timeouts.
- **Dynamic Configuration**: Update Base URLs and headers on the fly without recreating Retrofit instances.
- **Built-in Result Mapping**: `safeApiCall` automatically wraps success, failure, and network exceptions into a clean `ApiResult`.
- **Coroutines & Flow**: Exposes data natively via Kotlin Flow.
- **Network Monitor**: Real-time connectivity status checker via Flow.
- **Hilt Ready**: Built-in dependency injection module.

## Setup via JitPack

Add it in your root `build.gradle.kts` or `settings.gradle.kts` at the end of repositories:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Add Jitpack
    }
}
```

Add the dependency in your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.Coderkube-App:Android-API-Core:1.0.2")
}
```

## Implementation Guide

### 1. Initialization

Initialize the core library in your `Application` class. If you are using Hilt, this will automatically set up the components.

```kotlin
@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        ApiCore.initialize(
            context = this,
            baseUrl = "https://api.yourservice.com/",
            enableLogging = true, // Enables HttpLoggingInterceptor
            connectTimeout = 30,
            readTimeout = 30
        )
        
        // Optional: Provide an authentication token dynamically
        ApiCore.setAuthTokenProvider {
            "your_bearer_token"
        }
        
        // Optional: Provide dynamic headers
        ApiCore.setHeaderProvider {
            mapOf("Custom-Header" to "Value")
        }
    }
}
```

### 2. Define your API Interfaces

Just write your Retrofit interfaces as usual. The library provides the Retrofit instance for you.

```kotlin
interface UserApi {
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
}
```

### 3. Setup the Repository

Use the provided `ApiExecutor` to safely execute your API calls. It automatically handles HTTP and network errors and converts them to `ApiResult`.

```kotlin
class UserRepository @Inject constructor(
    private val apiExecutor: ApiExecutor
) {
    // Generate your service
    private val api: UserApi by lazy {
        ApiCore.createService<UserApi>()
    }

    // Execute API calls with a clean wrapper
    suspend fun fetchUsers() = apiExecutor.execute {
        api.getUsers()
    }
    
    // Or execute with automatic retries for network failures
    suspend fun fetchUsersWithRetry() = ApiCore.executeWithRetry(retryCount = 3) {
        api.getUsers()
    }
}
```

### 4. ViewModel & Compose UI State

The library provides a built-in `UiState` mapping to seamlessly bridge between your Repository and Jetpack Compose UI.

**ViewModel:**
```kotlin
@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // Extension function mapped directly to UiState
            _uiState.value = repository.fetchUsers().toUiState()
        }
    }
}
```

**Compose UI:**
```kotlin
@Composable
fun UserScreen(viewModel: UserViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    
    when (state) {
        is UiState.Idle -> { /* Initial state */ }
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> {
            val users = (state as UiState.Success).data
            // Render your list...
        }
        is UiState.Error -> {
            Text("Error: ${(state as UiState.Error).message}")
        }
    }
}
```

### 5. Utilities

**Network Connectivity Flow**
Observe device internet connectivity changes instantly:
```kotlin
val isConnected by ApiCore.getNetworkStatusFlow().collectAsState(initial = true)
```

**Dynamic Base URL**
Switch environments (e.g., Staging to Production) seamlessly without restarting the app:
```kotlin
ApiCore.updateBaseUrl("https://api.production.com/")
```

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

```text
Copyright 2026

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
