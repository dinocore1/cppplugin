package com.devsmart.cppplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class NativeBasePluginFunctTest extends Specification {
    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.devsmart.toolchains' 
                id 'com.devsmart.cpp-library' 
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

    def "dependencies resolve"() {

        buildFile << """
            library {
              cppStandard 'c++11'
            }
            
            repositories {
                maven {
                    url 'https://artifactory.videray.com/artifactory/native-libs'
                    if (project.hasProperty("gartifactoryUsername") 
                        && project.hasProperty("gartifactoryPassword")) {
                        credentials {
                            username = gartifactoryUsername
                            password = gartifactoryPassword
                        }
                    }
                }
            }
        
            dependencies {
                implementation 'com.videray:core:1.0.2-SNAPSHOT'
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
                .withArguments('assemble2')
                .withDebug(true)
                .build()

        then:
        result.task(":assemble2").outcome == SUCCESS

    }
}
