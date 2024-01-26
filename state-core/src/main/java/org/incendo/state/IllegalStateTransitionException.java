//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.state;

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Exception thrown when an attempt is made to perform a state transition that is not allowed.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public class IllegalStateTransitionException extends IllegalArgumentException {

    private final State<?> from;
    private final State<?> to;
    private final MutableStateful<?> stateful;

    /**
     * Creates a new instance.
     *
     * @param from     current state
     * @param to       new state
     * @param stateful instance that the transition was attempted on
     */
    public IllegalStateTransitionException(
            final @NonNull State<?> from,
            final @NonNull State<?> to,
            final @NonNull MutableStateful<?> stateful
    ) {
        super(String.format("Cannot transition from state '%s' to state '%s'", from, to));
        this.from = from;
        this.to = to;
        this.stateful = stateful;
    }

    /**
     * Returns the original state.
     *
     * @return from state
     */
    public @NonNull State<?> from() {
        return this.from;
    }

    /**
     * Returns the attempted transition state.
     *
     * @return new state
     */
    public @NonNull State<?> to() {
        return this.to;
    }

    /**
     * Returns the stateful instance that the state transition was attempted on.
     *
     * @return stateful instance
     */
    public @NonNull MutableStateful<?> stateful() {
        return this.stateful;
    }
}
