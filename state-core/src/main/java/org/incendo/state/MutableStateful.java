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
import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * Something that holds a {@link State} that can be mutated.
 *
 * <p>It is recommended to extend {@link AbstractStateful} when implementing this interface.</p>
 *
 * @param <U> state type
 * @param <V> self-referencing type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface MutableStateful<U extends State<U>, V extends MutableStateful<U, V>> extends Stateful<U, V> {

    /**
     * Creates a new mutable stateful instance with the given {@code initialState}.
     *
     * @param <U>          state type
     * @param initialState initial state
     * @return the instance
     */
    static <U extends State<U>> @NonNull MutableStateful<U, ?> of(final @NonNull U initialState) {
        return new StatefulImpl<>(initialState);
    }

    /**
     * Transitions to the given {@code state}.
     *
     * @param state new state
     * @throws IllegalStateTransitionException if the state transition is not possible
     * @return {@code this}
     */
    @This @NonNull V transitionTo(@NonNull U state) throws IllegalStateTransitionException;

    /**
     * Attempts to transition from the given {@code currentState} to the given {@code newState}.
     *
     * @param currentState expected current state, the state transition will fail if the current {@link #state()} is different
     * @param newState     new state
     * @return {@code this}
     * @throws UnexpectedStateException        if the actual {@link #state()} is different form the {@code currentState}
     * @throws IllegalStateTransitionException if the state transition is not possible
     */
    @This @NonNull V transition(@NonNull U currentState, @NonNull U newState) throws
            UnexpectedStateException, IllegalStateTransitionException;

    /**
     * Creates a new interaction builder.
     *
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    default StateInteraction.@NonNull Builder<U, V> interact() {
        return StateInteraction.on((V) this);
    }

    /**
     * Returns an immutable view of this instance.
     *
     * <p>Any updates to this instance will be reflected in the returned view.</p>
     *
     * @return immutable view
     */
    default @NonNull Stateful<U, V> asImmutable() {
        final Stateful<U, V> mutable = this;
        return new Stateful<U, V>() {

            @Override
            public @NonNull U state() {
                return mutable.state();
            }
        };
    }
}
