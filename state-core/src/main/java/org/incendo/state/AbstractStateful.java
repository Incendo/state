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

import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A thread-safe implementation of {@link MutableStateful}.
 *
 * @param <S> state type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public abstract class AbstractStateful<S extends State<S>> implements MutableStateful<S> {

    private S state;

    /**
     * Creates a new instance.
     *
     * @param initialState initial state
     */
    protected AbstractStateful(final @NonNull S initialState) {
        this.state = Objects.requireNonNull(initialState, "initialState");
    }

    @Override
    public final synchronized @NonNull S state() {
        return this.state;
    }

    @Override
    public final synchronized void transitionTo(final @NonNull S state) throws IllegalStateTransitionException {
        Objects.requireNonNull(state, "state");
        if (!this.canTransitionTo(state)) {
            throw new IllegalStateTransitionException(this.state, state, this);
        }
        this.state = state;
    }

    @Override
    public final synchronized void transition(final @NonNull S currentState, final @NonNull S newState)
            throws UnexpectedStateException, IllegalStateTransitionException {
        Objects.requireNonNull(currentState, "currentState");
        if (!this.state.equals(newState)) {
            throw new UnexpectedStateException(currentState, newState, this);
        }
        this.transitionTo(newState);
    }
}
