package org.jenkinsci.plugins.testinprogress;

import static org.jenkinsci.plugins.testinprogress.JenkinsAntJobProjectBuilder.aJenkinsAntJobProject;
import static org.junit.Assert.assertTrue;
import hudson.model.Run;
import hudson.slaves.DumbSlave;
import hudson.tasks.Ant;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class MyTest {

	@Rule
	public JenkinsRule j = new JenkinsRule();

	@Test
	public void test1() throws Exception {
		j.jenkins.setCrumbIssuer(null);
		Ant.AntInstallation antInstallation = j.configureDefaultAnt();
		DumbSlave slave = j.createOnlineSlave();
		JenkinsJob jenkinsJob = aJenkinsAntJobProject(j.jenkins,
				"antTestProject").withAntInstallation(antInstallation)
				.withProjectZipFile(new File("resources/antTestProject.zip"))
				.withAssignedNode(slave).withTargets("test-parallelSuite").create();
		Run build = jenkinsJob.run();
		String s = FileUtils.readFileToString(build.getLogFile());
		System.out.println(s);
//		assertTrue(s.contains("Test testproject.CalcTest FAILED"));
	}

}
