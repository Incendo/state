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
 * Something that holds a {@link State}.
 *
 * @param <U> state type
 * @param <V> self-referencing type
 * @since 1.0.0
 */
@FunctionalInterface
@API(status = API.Status.STABLE, since = "1.0.0")
public interface Stateful<U extends State<U>, V extends Stateful<U, V>> {

    /**
     * Returns the current state.
     *
     * @return current state
     */
    @NonNull U state();

    /**
     * Returns the transitions that are possible <i>from</i> the current {@link #state()}.
     *
     * @return allowed state transitions
     */
    default @NonNull States<U> allowedTransitions() {
        return this.state().allowedTransitions();
    }

    /**
     * Returns whether the state can be transition into the given {@code state}.
     *
     * @param state new state
     * @return {@code true} if the state transition is allowed, {@code false} if not
     */
    default boolean canTransitionTo(final @NonNull U state) {
        return this.allowedTransitions().contains(state);
    }

    /**
     * Fails exceptionally if the current {@link #state()} is different from the given {@code state}.
     *
     * @param state expected state
     * @return {@code this}
     * @throws UnexpectedStateException if the current {@link #state()} is different from the given {@code state}
     */
    @SuppressWarnings("unchecked")
    default @This @NonNull V expectState(final @NonNull U state) throws UnexpectedStateException {
        final U currentState = this.state();
        if (currentState.equals(state)) {
            return (V) this;
        }
        throw new UnexpectedStateException(States.of(state), currentState, this);
    }
}
