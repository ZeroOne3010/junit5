/*
 * Copyright 2015-2022 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.api.condition;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Base class for OS-based {@link ExecutionCondition} implementations.
 *
 * @since 5.9
 */
abstract class AbstractOsBasedExecutionCondition<A extends Annotation> implements ExecutionCondition {
	private final Class<A> annotationType;

	AbstractOsBasedExecutionCondition(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		return findAnnotation(context.getElement(), annotationType) //
				.map(this::evaluateExecutionCondition) //
				.orElseGet(this::enabledByDefault);
	}

	abstract ConditionEvaluationResult evaluateExecutionCondition(A annotation);

	protected String createReason(boolean enabled, boolean osSpecified, boolean archSpecified) {
		StringBuilder reason = new StringBuilder() //
				.append(enabled ? "Enabled" : "Disabled") //
				.append(osSpecified ? " on operating system: " : " on architecture: ");

		if (osSpecified && archSpecified) {
			reason.append(String.format("%s (%s)", currentOS(), currentArchitecture()));
		}
		else if (osSpecified) {
			reason.append(currentOS());
		}
		else {
			reason.append(currentArchitecture());
		}

		return reason.toString();
	}

	private ConditionEvaluationResult enabledByDefault() {
		String reason = String.format("@%s is not present", annotationType.getSimpleName());
		return enabled(reason);
	}

	private String currentOS() {
		return System.getProperty("os.name");
	}

	protected String currentArchitecture() {
		return System.getProperty("os.arch");
	}

}
