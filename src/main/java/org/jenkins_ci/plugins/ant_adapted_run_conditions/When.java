package org.jenkins_ci.plugins.ant_adapted_run_conditions;

/**
 * An enum that models the options for the <code>when</code> field.
 * 
 * @author Anthony Wat
 */
public enum When {

	/**
	 * Equal to.
	 */
	EQ("eq", Messages.When_eq_displayName()) {
		@Override
		boolean compare(long l1, long l2) {
			return l1 == l2;
		}
	},

	/**
	 * Greater than or equal to.
	 */
	GE("ge", Messages.When_ge_displayName()) {
		@Override
		boolean compare(long l1, long l2) {
			return l1 >= l2;
		}
	},

	/**
	 * Greater than.
	 */
	GT("gt", Messages.When_gt_displayName()) {
		@Override
		boolean compare(long l1, long l2) {
			return l1 > l2;
		}
	},

	/**
	 * Less than or equal to.
	 */
	LE("le", Messages.When_le_displayName()) {
		@Override
		boolean compare(long l1, long l2) {
			return l1 <= l2;
		}
	},

	/**
	 * Less than.
	 */
	LT("lt", Messages.When_lt_displayName()) {
		@Override
		boolean compare(long l1, long l2) {
			return l1 < l2;
		}
	},

	/**
	 * Not equal to.
	 */
	NE("ne", Messages.When_ne_displayName()) {
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
	 * Compares two longs and return whether the left long operand conforms with
	 * the comparison criteria represented by this enum.
	 * 
	 * @param l1
	 *            The left long operand.
	 * @param l2
	 *            The right long operand.
	 * @return <code>true</code> if the left long operand conforms with the
	 *         comparison criteria represented by this enum; <code>false</code>
	 *         otherwise.
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
