/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Anthony Wat
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins_ci.plugins.ant_adapted_run_conditions;

import hudson.Extension;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * This condition runs if the current operating system is of a given type.
 * 
 * @author Anthony Wat
 * @see Os
 */
public class OSCondition extends RunCondition {

	@Extension
	public static class OSConditionDescriptor extends RunConditionDescriptor {

		/**
		 * The prefix of the names of the constants for OS family in the Ant
		 * <code>Os</code> class.
		 */
		public static final String FAMILY_CONST_PREFIX = "FAMILY_";

		/**
		 * Validates whether at least one of the four fields are specified.
		 * 
		 * @param value
		 *            The value of the family field.
		 * @param name
		 *            The value of the name field.
		 * @param arch
		 *            The value of the architecture field.
		 * @param version
		 *            The value of the version field.
		 * @return <code>FormValidation.ok()</code> if validation is successful;
		 *         <code>FormValidation.error()</code> with an error message
		 *         otherwise.
		 */
		public FormValidation doCheckFamily(@QueryParameter String value,
				@QueryParameter String name, @QueryParameter String arch,
				@QueryParameter String version) {
			// Empty fields are not passed in as null values but empty strings
			// from Jenkins
			if (value.length() == 0 && name.length() == 0 && arch.length() == 0
					&& version.length() == 0) {
				return FormValidation.error(Messages
						.OSCondition_doCheckFamily_errMsg());
			}
			return FormValidation.ok();
		}

		/**
		 * Returns the <code>ListBoxModel</code> object containing drop-down
		 * options for the family field.
		 * 
		 * @return The <code>ListBoxModel</code> object containing drop-down
		 *         options for the family field.
		 * @throws IllegalAccessException
		 */
		public ListBoxModel doFillFamilyItems() throws IllegalAccessException {
			ListBoxModel lbm = new ListBoxModel();

			// Add empty option to the top
			lbm.add("");

			// Introspects the Os class for supported OS family values and
			// dynamically populate the drop-down list options for the family
			// field
			Set<String> familyOptions = new TreeSet<String>();
			Field[] fields = Os.class.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isPublic(field.getModifiers())
						&& Modifier.isStatic(field.getModifiers())
						&& field.getType().equals(String.class)
						&& field.getName().startsWith(FAMILY_CONST_PREFIX)) {
					familyOptions.add((String) field.get(String.class));
				}
			}
			for (String familyOption : familyOptions) {
				lbm.add(familyOption);
			}
			return lbm;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return Messages.OSCondition_displayName();
		}

	}

	/**
	 * The architecture of the operating system family to expect.
	 */
	final String arch;

	/**
	 * The family of the operating system family to expect.
	 */
	final String family;

	/**
	 * The name of the operating system family to expect.
	 */
	final String name;

	/**
	 * The version of the operating system family to expect.
	 */
	final String version;

	@DataBoundConstructor
	public OSCondition(String family, String name, String arch, String version) {
		this.family = family;
		this.name = name;
		this.arch = arch;
		this.version = version;
	}

	/**
	 * The architecture of the operating system family to expect.
	 * 
	 * @return The architecture of the operating system family to expect.
	 */
	public String getArch() {
		return this.arch;
	}

	/**
	 * Returns the family of the operating system family to expect.
	 * 
	 * @return The family of the operating system family to expect.
	 */
	public String getFamily() {
		return this.family;
	}

	/**
	 * Return the name of the operating system family to expect.
	 * 
	 * @return The name of the operating system family to expect.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * The version of the operating system family to expect.
	 * 
	 * @return The version of the operating system family to expect.
	 */
	public String getVersion() {
		return this.version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkins_ci.plugins.run_condition.RunCondition#runPerform(hudson.model
	 * .AbstractBuild, hudson.model.BuildListener)
	 */
	@Override
	public boolean runPerform(AbstractBuild<?, ?> build, BuildListener listener)
			throws Exception {
		// Treat empty values as null
		String expandedFamily = (family.length() == 0) ? null : TokenMacro
				.expandAll(build, listener, family);
		String expandedName = (name.length() == 0) ? null : TokenMacro
				.expandAll(build, listener, name);
		String expandedArch = (arch.length() == 0) ? null : TokenMacro
				.expandAll(build, listener, arch);
		String expandedVersion = (version.length() == 0) ? null : TokenMacro
				.expandAll(build, listener, version);

		listener.getLogger().println(
				Messages.OSCondition_console_args(expandedFamily, expandedName,
						expandedArch, expandedVersion));
		Os osCondition = new Os();
		if (expandedFamily != null) {
			osCondition.setFamily(expandedFamily);
		}
		if (expandedName != null) {
			osCondition.setName(expandedName);
		}
		if (expandedArch != null) {
			osCondition.setArch(expandedArch);
		}
		if (expandedVersion != null) {
			osCondition.setVersion(expandedVersion);
		}
		return osCondition.eval();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkins_ci.plugins.run_condition.RunCondition#runPrebuild(hudson.
	 * model.AbstractBuild, hudson.model.BuildListener)
	 */
	@Override
	public boolean runPrebuild(AbstractBuild<?, ?> build, BuildListener listener)
			throws Exception {
		return true;
	}

}
