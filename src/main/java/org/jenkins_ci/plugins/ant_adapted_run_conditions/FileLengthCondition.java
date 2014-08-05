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
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.util.List;

import org.apache.tools.ant.taskdefs.Length;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.jenkins_ci.plugins.run_condition.common.BaseDirectory;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * This condition runs if the length of a file matches a given criteria.
 * 
 * @author Anthony Wat
 * @see Length
 */
public class FileLengthCondition extends RunCondition {

	@Extension
	public static class FileLengthConditionDescriptor extends
			RunConditionDescriptor {

		/**
		 * Validates the <code>file</code> field.
		 * 
		 * @return <code>FormValidation.ok()</code> if validation is successful;
		 *         <code>FormValidation.error()</code> with an error message
		 *         otherwise.
		 */
		public FormValidation doCheckFile(@QueryParameter String value) {
			return FormValidation.validateRequired(value);
		}

		/**
		 * Validates the <code>length</code> field.
		 * 
		 * @return <code>FormValidation.ok()</code> if validation is successful;
		 *         <code>FormValidation.error()</code> with an error message
		 *         otherwise.
		 */
		public FormValidation doCheckLength(@QueryParameter String value) {
			boolean ok;
			try {
				long longValue = Long.valueOf(value);
				ok = (longValue >= 0);
			} catch (NumberFormatException e) {
				ok = false;
			}
			return ok ? FormValidation.ok() : FormValidation.error(Messages
					.FileLengthCondition_doCheckLength_errMsg());
		}

		/**
		 * Returns the <code>ListBoxModel</code> object containing drop-down
		 * options for the <code>when</code> field.
		 * 
		 * @return The <code>ListBoxModel</code> object containing drop-down
		 *         options for the <code>when</code> field.
		 */
		public ListBoxModel doFillWhenItems() {
			ListBoxModel lbm = new ListBoxModel();
			for (When whenOption : When.values()) {
				lbm.add(whenOption.getDisplayName(), whenOption.getName());
			}
			return lbm;
		}

		/**
		 * Returns the list of base directories.
		 * 
		 * @return The list of base directories.
		 */
		public List<? extends Descriptor<? extends BaseDirectory>> getBaseDirectories() {
			return Hudson
					.getInstance()
					.<BaseDirectory, BaseDirectory.BaseDirectoryDescriptor> getDescriptorList(
							BaseDirectory.class);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return Messages.FileLengthCondition_displayName();
		}

	}

	/**
	 * An enum that models the options for the <code>when</code> field.
	 * 
	 * @author Anthony Wat
	 */
	enum When {

		/**
		 * Equal to.
		 */
		EQ("eq", Messages.FileLengthCondition_When_eq_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 == l2;
			}
		},

		/**
		 * Greater than or equal to.
		 */
		GE("ge", Messages.FileLengthCondition_When_ge_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 >= l2;
			}
		},

		/**
		 * Greater than.
		 */
		GT("gt", Messages.FileLengthCondition_When_gt_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 > l2;
			}
		},

		/**
		 * Less than or equal to.
		 */
		LE("le", Messages.FileLengthCondition_When_le_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 <= l2;
			}
		},

		/**
		 * Less than.
		 */
		LT("lt", Messages.FileLengthCondition_When_lt_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 < l2;
			}
		},

		/**
		 * Not equal to.
		 */
		NE("ne", Messages.FileLengthCondition_When_ne_displayName()) {
			@Override
			boolean compare(long l1, long l2) {
				return l1 != l2;
			}
		};

		/**
		 * The display name.
		 */
		private final String displayName;

		/**
		 * The name.
		 */
		private final String name;

		/**
		 * Constructs a <code>When</code> enum.
		 * 
		 * @param name
		 *            The name.
		 * @param displayName
		 *            The display name.
		 */
		When(String name, String displayName) {
			this.name = name;
			this.displayName = displayName;
		}

		/**
		 * Compares two longs and return whether the left long operand conforms
		 * with the comparison criteria represented by this enum.
		 * 
		 * @param l1
		 *            The left long operand.
		 * @param l2
		 *            The right long operand.
		 * @return <code>true</code> if the left long operand conforms with the
		 *         comparison criteria represented by this enum;
		 *         <code>false</code> otherwise.
		 */
		abstract boolean compare(long l1, long l2);

		/**
		 * Returns the display name.
		 * 
		 * @return The display name.
		 */
		String getDisplayName() {
			return displayName;
		}

		/**
		 * Returns the name.
		 * 
		 * @return Returns the name.
		 */
		String getName() {
			return name;
		}

	}

	/**
	 * The base directory of the file.
	 */
	final BaseDirectory baseDir;

	/**
	 * The file relative to the base directory to compare length on.
	 */
	final String file;

	/**
	 * The comparison length.
	 */
	final String length;

	/**
	 * The comparison type.
	 */
	final String when;

	@DataBoundConstructor
	public FileLengthCondition(BaseDirectory baseDir, String file,
			String length, String when) {
		this.baseDir = baseDir;
		this.file = file;
		this.length = length;
		this.when = when;
	}

	/**
	 * Returns the base directory of the file.
	 * 
	 * @return The base directory of the file.
	 */
	public BaseDirectory getBaseDir() {
		return this.baseDir;
	}

	/**
	 * Returns the file relative to the base directory to compare length on.
	 * 
	 * @return The file relative to the base directory to compare length on.
	 */
	public String getFile() {
		return this.file;
	}

	/**
	 * Returns the comparison length.
	 * 
	 * @return The comparison length.
	 */
	public String getLength() {
		return this.length;
	}

	/**
	 * Returns the comparison type.
	 * 
	 * @return The comparison type.
	 */
	public String getWhen() {
		return this.when;
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
		String expandedFile = TokenMacro.expandAll(build, listener, file);
		listener.getLogger().println(
				Messages.FileLengthCondition_console_args(baseDir, file,
						length, when));
		if (!baseDir.getBaseDirectory(build).child(expandedFile).exists()) {
			return false;
		}
		long fileLength = baseDir.getBaseDirectory(build).child(expandedFile)
				.length();
		long lengthValue = Long.valueOf(length);
		for (When whenEnumValue : When.values()) {
			if (whenEnumValue.getName().equals(when)) {
				return whenEnumValue.compare(fileLength, lengthValue);
			}
		}
		return false;
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
