package me.tatarka.inject.test

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import me.tatarka.inject.ProjectCompiler
import me.tatarka.inject.Target
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

class WarningTest {

    @TempDir
    lateinit var workingDir: File

    @ParameterizedTest
    @EnumSource(Target::class)
    fun warns_on_scope_on_provider_method_which_is_ignored(target: Target) {
        val projectCompiler = ProjectCompiler(target, workingDir)

        assertThat(
            projectCompiler.source(
                "MyComponent.kt",
                """
                import me.tatarka.inject.annotations.Component
                import me.tatarka.inject.annotations.Scope
                import me.tatarka.inject.annotations.Inject
                import me.tatarka.inject.annotations.Provides
                
                @Scope annotation class MyScope1
                @Scope annotation class MyScope2
                @Scope annotation class MyScope3
                
                @MyScope1 @Component abstract class MyComponent1 {
                    @get:MyScope1 abstract val foo: String
                    
                    @Provides fun str(): String = ""
                }
                
                @MyScope2 @Component abstract class MyComponent2 {
                    @MyScope2 abstract fun bar(): String
                    
                    @Provides fun str(): String = ""
                }
            
                @MyScope3 @Component abstract class MyComponent3 {
                    @MyScope3 abstract val foo: String

                    @Provides fun str(): String = ""
                }
                """.trimIndent()
            ).compile()
        ).warnings().all {
            contains("Scope: @MyScope1 has no effect. Place on @Provides function or @Inject constructor instead.")
            contains("Scope: @MyScope2 has no effect. Place on @Provides function or @Inject constructor instead.")
            contains("Scope: @MyScope3 has no effect. Place on @Provides function or @Inject constructor instead.")
        }
    }

    @ParameterizedTest
    @EnumSource(Target::class)
    fun warns_on_multiple_scope_on_provider_method_which_is_ignored(target: Target) {
        val projectCompiler = ProjectCompiler(target, workingDir)

        assertThat(
            projectCompiler.source(
                "MyComponent.kt",
                """
                import me.tatarka.inject.annotations.Component
                import me.tatarka.inject.annotations.Scope
                import me.tatarka.inject.annotations.Inject
                import me.tatarka.inject.annotations.Provides
                
                @Scope annotation class MyScope1
                @Scope annotation class MyScope2
                
                @MyScope1 @Component abstract class MyComponent1 {
                    @get:MyScope1 @get:MyScope2 abstract val foo: String
                    
                    @Provides fun str(): String = ""
                }
                
                @MyScope2 @Component abstract class MyComponent2 {
                    @MyScope2 @MyScope1 abstract fun bar(): String
                    
                    @Provides fun str(): String = ""
                }
                """.trimIndent()
            ).compile()
        ).warnings().all {
            contains("Scope: @MyScope1 has no effect. Place on @Provides function or @Inject constructor instead.")
            contains("Scope: @MyScope2 has no effect. Place on @Provides function or @Inject constructor instead.")
        }
    }
}
