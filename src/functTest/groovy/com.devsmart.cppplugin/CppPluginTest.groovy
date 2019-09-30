package com.devsmart.cppplugin

import com.devsmart.cppplugin.plugin.CppLibraryPlugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class CppPluginTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    def projectDir
    def project

    def setup() {
        projectDir = testProjectDir.newFolder("project")
        project = ProjectBuilder.builder().withProjectDir(projectDir).withName("testComponent").build()
    }


    def "add maven publications"() {

        //def libraryComponent = Stub(CppLibrary)

        given:
        project.pluginManager.apply(CppLibraryPlugin)
        project.pluginManager.apply(MavenPublishPlugin)
        //project.components.add(libraryComponent)
        project.group = "my.group"
        project.version = "1.2"
        ((ProjectInternal) project).evaluate()

        expect:
        def publishing = project.publishing
        publishing.publications.size() > 1


    }
}
