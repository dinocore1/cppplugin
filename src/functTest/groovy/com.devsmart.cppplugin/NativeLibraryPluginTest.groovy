package com.devsmart.cppplugin

import com.devsmart.cppplugin.plugin.NativeLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class NativeLibraryPluginTest extends Specification {

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
                .build()


        when:
        project.pluginManager.apply(NativeLibraryPlugin)

        then:
        project.library instanceof NativeLibrary
        project.library.baseName.get() == "testLib"
        project.library.cppSource.files == [srcFile] as Set
        project.library.publicHeaderDirs.files == [publicHeaders] as Set
    }
}
