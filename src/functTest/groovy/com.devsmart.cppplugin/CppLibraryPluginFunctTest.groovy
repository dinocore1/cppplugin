package com.devsmart.cppplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class CppLibraryPluginFunctTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.devsmart.cpplibrary'
            }
        """
    }

    def "dependencies resolve"() {

        buildFile << """
            model {
                toolChains {
                  linux_gcc(Gcc) {
                    target('linux_x86-64')
                  }
                }
            }
            repositories {
                maven {
                    url 'https://artifactory.videray.com/artifactory/native-libs'
                }
            }

            library {
              
            }
            
            dependencies {
                    implementation 'org.gradle.cpp-samples:googletest:1.9.0-gr4-SNAPSHOT'
                }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments('assemble')
            .withDebug(true)
            .build()

        then:
        result.task(":assemble").outcome == SUCCESS

    }


}
