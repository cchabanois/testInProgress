package org.jenkinsci.plugins.testinprogress;

import static org.jenkinsci.plugins.testinprogress.JenkinsAntJobProjectBuilder.aJenkinsAntJobProject;
import hudson.model.Run;
import hudson.slaves.DumbSlave;
import hudson.tasks.Ant;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class AntProjectWithOldClient13Test {

	@Rule
	public JenkinsRule jenkinsRule = new JenkinsRule() {
		protected void before() throws Throwable {
			contextPath = "/jenkins";
			super.before();
		};
	};

	

	@Test
	@Ignore("Can be used for manual testing")
	public void testWithOldClient13() throws Exception {
		System.out.println("Jenkins url :"+jenkinsRule.jenkins.getRootUrl());
		// Create a slave, install ant
		jenkinsRule.jenkins.setCrumbIssuer(null);
		Ant.AntInstallation antInstallation = jenkinsRule.configureDefaultAnt();
		DumbSlave slave = jenkinsRule.createOnlineSlave();
		
		// create our job
		JenkinsJob jenkinsJob = aJenkinsAntJobProject(jenkinsRule.jenkins,
				"antTestProjectWithClient13").withAntInstallation(antInstallation)
				.withProjectZipFile(new File("resources/antTestProjectWithClient13.zip"))
				.withAssignedNode(slave).withTargets("test-all").withProperties("numTests=10").create();
		
		// run it
		Run build = jenkinsJob.run();
		String s = FileUtils.readFileToString(build.getLogFile());
	}	
	
}
