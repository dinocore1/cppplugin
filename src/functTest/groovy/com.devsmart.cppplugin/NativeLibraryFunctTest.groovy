package com.devsmart.cppplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class NativeLibraryFunctTest extends Specification {

    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.devsmart.native-library'
            }
        """
    }

    def "library module source files found under src_main_cpp"() {

        buildFile << """
            library {
            }
        """

        File mainSrcDir = testProjectDir.newFolder("src", "main", "cpp");
        File srcFile = new File(mainSrcDir, "hello.cpp");
        srcFile << """
            #include <iostream>
            
            int main() {
              std::cout << "hello world" << std::endl;
              return 0;
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('tasks')
            .withPluginClasspath()
            .withDebug(true)
            .build()

        then:
        result.task(":tasks").outcome == SUCCESS

    }

    def "library module header files found under src_main_public"() {

    }
}
