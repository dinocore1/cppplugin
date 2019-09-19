package com.devsmart.cppplugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ToolchainsPluginFunctTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id 'com.devsmart.toolchains'
            }
        """
    }

    def "toolchains"() {

        given:
        buildFile << """
            toolchains {
                gcc {
                  platform 'linux', 'x86'
                  cppCompiler {
                    exePath '/usr/bin/c++'
                  }
                }
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
}
