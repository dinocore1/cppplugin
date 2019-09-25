package com.devsmart.cppplugin

import com.devsmart.cppplugin.plugin.CppLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class NativeLibraryModelPluginTest extends Specification {

    //see: https://github.com/gradle/gradle/blob/master/subprojects/language-native/src/test/groovy/org/gradle/language/cpp/plugins/CppLibraryPluginTest.groovy

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    def "adds extension with convention for source layout and base name"() {
        given:
        File mainSrcDir = testProjectDir.newFolder("src", "main", "cpp")
        File srcFile = new File(mainSrcDir, "hello.cpp");
        srcFile << """
            #include <iostream>
            
            int main() {
              std::cout << "hello world" << std::endl;
              return 0;
            }
        """
        File publicHeaders = testProjectDir.newFolder("src", "main", "public")

        def project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .withName("testLib")
                .withGradleUserHomeDir(new File("/home/paul/.gradle"))
                .build()


        when:
        project.pluginManager.apply(CppLibraryPlugin)

        then:
        project.library instanceof CppLibrary
        project.library.baseName.get() == "testLib"
        project.library.cppSource.files == [srcFile] as Set
        project.library.publicHeaderDirs.files == [publicHeaders] as Set
    }
}
