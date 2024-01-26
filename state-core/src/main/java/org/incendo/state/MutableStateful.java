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
 * Something that holds a {@link State} that can be mutated.
 *
 * <p>It is recommended to extend {@link AbstractStateful} when implementing this interface.</p>
 *
 * @param <S> state type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface MutableStateful<S extends State<S>> extends Stateful<S> {

    /**
     * Creates a new mutable stateful instance with the given {@code initialState}.
     *
     * @param <S>          state type
     * @param initialState initial state
     * @return the instance
     */
    static <S extends State<S>> @NonNull MutableStateful<S> of(final @NonNull S initialState) {
        return new StatefulImpl<>(initialState);
    }

    /**
     * Transitions to the given {@code state}.
     *
     * @param state new state
     * @throws IllegalStateTransitionException if the state transition is not possible
     */
    void transitionTo(@NonNull S state) throws IllegalStateTransitionException;

    /**
     * Attempts to transition from the given {@code currentState} to the given {@code newState}.
     *
     * @param currentState expected current state, the state transition will fail if the current {@link #state()} is different
     * @param newState     new state
     * @throws UnexpectedStateException        if the actual {@link #state()} is different form the {@code currentState}
     * @throws IllegalStateTransitionException if the state transition is not possible
     */
    void transition(@NonNull S currentState, @NonNull S newState) throws
            UnexpectedStateException, IllegalStateTransitionException;

    /**
     * Returns an immutable view of this instance.
     *
     * <p>Any updates to this instance will be reflected in the returned view.</p>
     *
     * @return immutable view
     */
    default @NonNull Stateful<S> asImmutable() {
        final Stateful<S> mutable = this;
        return new Stateful<S>() {

            @Override
            public @NonNull S state() {
                return mutable.state();
            }
        };
    }
}
