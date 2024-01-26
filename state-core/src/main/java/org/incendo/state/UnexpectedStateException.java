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
 * Exception thrown when a state of a {@link Stateful} instance is different from the expected state.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public class UnexpectedStateException extends IllegalArgumentException {

    private final States<?> expected;
    private final State<?> actual;
    private final Stateful<?, ?> stateful;

    /**
     * Creates a new instance.
     *
     * @param expected expected state
     * @param actual   actual state
     * @param stateful instance that holds the state
     */
    public UnexpectedStateException(
            final @NonNull States<?> expected,
            final @NonNull State<?> actual,
            final @NonNull Stateful<?, ?> stateful
    ) {
        super(String.format("Expected states '%s' but was '%s'", expected, actual));
        this.expected = expected;
        this.actual = actual;
        this.stateful = stateful;
    }

    /**
     * Returns the expected state.
     *
     * @return expected state
     */
    public @NonNull States<?> expected() {
        return this.expected;
    }

    /**
     * Returns the actual state
     *
     * @return actual state
     */
    public @NonNull State<?> actual() {
        return this.actual;
    }

    /**
     * Returns the stateful instance.
     *
     * @return stateful instance
     */
    public @NonNull Stateful<?, ?> stateful() {
        return this.stateful;
    }
}
