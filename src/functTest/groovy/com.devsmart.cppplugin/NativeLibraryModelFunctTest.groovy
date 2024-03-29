package com.devsmart.cppplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class NativeLibraryModelFunctTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.devsmart.toolchains' 
                id 'com.devsmart.native-library' 
            }
            
            toolchains {
                gcc {
                  platform 'linux', 'x86_64'
                  cppCompiler {
                    exe '/usr/bin/c++'
                  }
                  
                  archiveTool {
                    exe '/usr/bin/ar'
                  }
                }
            }
        """
    }

    def "assemble task runs"() {

        buildFile << """
            library {
              cppStandard 'c++11'
            }
        """

        File mainSrcDir = testProjectDir.newFolder("src", "main", "cpp");
        File srcFile = new File(mainSrcDir, "hello.cpp");
        srcFile << """
            #include <iostream>
            
            void sayhello() {
              std::cout << "hello world" << std::endl;
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
