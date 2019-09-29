package com.devsmart.cppplugin

import com.devsmart.cppplugin.tasks.CppCompileTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class CppCompileTaskTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    def "compile produces output"() {

        given:
        File mainSrcDir = testProjectDir.newFolder("src", "main", "cpp")
        File srcFile = new File(mainSrcDir, "main.cpp")
        srcFile << """
            #include <iostream>
            
            int main() {
              std::cout << "hello world" << std::endl;
              return 0;
            }
        """

        def project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .withName("testLib")
                .build()

        def gcctoolChain = project.objects.newInstance(DefaultGccToolchain)
        gcctoolChain.platform 'linux', 'x86'
        gcctoolChain.cppCompiler {
            exePath '/usr/bin/c++'
        }


        CppCompileTask compileTask = project.task('compile', type: CppCompileTask) {
            source.from(mainSrcDir)
            toolChain = gcctoolChain
            outputDir = project.layout.buildDirectory.dir('obj')
        }

        when:

        compileTask.compile(null)


        then:
        compileTask.toolChain != null


    }
}
