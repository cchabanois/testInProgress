package org.jenkinsci.plugins.testinprogress;

import hudson.model.TopLevelItem;
import hudson.model.FreeStyleProject;
import hudson.model.Node;
import hudson.tasks.Ant;
import hudson.tasks.Shell;

import java.io.File;

import jenkins.model.Jenkins;

import org.jvnet.hudson.test.ExtractResourceSCM;

/**
 * 
 * @author Cedric Chabanois (cchabanois at gmail.com)
 * 
 */
public class JenkinsAntJobProjectBuilder {
	private final Jenkins jenkins;
	private final String name;
	private File resource;
	private Ant.AntInstallation antInstallation;
	private String targets = "";
	private Node node;
	private String properties = null;
	
	private JenkinsAntJobProjectBuilder(Jenkins jenkins, String name) {
		this.jenkins = jenkins;
		this.name = name;
	}

	public static JenkinsAntJobProjectBuilder aJenkinsAntJobProject(
			Jenkins jenkins, String name) {
		return new JenkinsAntJobProjectBuilder(jenkins, name);
	}

	public JenkinsAntJobProjectBuilder withProjectZipFile(File resource) {
		this.resource = resource;
		return this;
	}

	public JenkinsAntJobProjectBuilder withTargets(String targets) {
		this.targets = targets;
		return this;
	}
	
	public JenkinsAntJobProjectBuilder withAntInstallation(
			Ant.AntInstallation antInstallation) {
		this.antInstallation = antInstallation;
		return this;
	}

	public JenkinsAntJobProjectBuilder withAssignedNode(Node node) {
		this.node = node;
		return this;
	}

	public JenkinsAntJobProjectBuilder withProperties(String properties) {
		this.properties = properties;
		return this;
	}
	
	public JenkinsJob create() throws Exception {
		TopLevelItem item = jenkins.getItem(name);
		if (item != null) {
			item.delete();
		}
		FreeStyleProject job = jenkins.createProject(FreeStyleProject.class,
				name);
		Ant ant = new Ant(targets, antInstallation.getName(), null, "build.xml",
				properties);
		job.setScm(new ExtractResourceSCM(resource.toURI().toURL()));
		if (node != null) {
			job.setAssignedNode(node);
		}
		job.getBuildWrappersList().add(new TestInProgressBuildWrapper());
		job.getBuildersList().add(ant);
		job.save();
		return new JenkinsJob(job);
	}
}
