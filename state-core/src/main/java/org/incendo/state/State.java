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
 * Represents a state of a {@link Stateful} object.
 *
 * <p>A state is {@link Stateful} and always returns itself when {@link #state()} is invoked.</p>
 *
 * @param <S> self-referencing type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface State<S extends State<S>> extends Stateful<S, S> {

    /**
     * Returns the transitions that are possible <i>from</i> this state.
     *
     * @return allowed state transitions
     */
    @Override
    default @NonNull States<S> allowedTransitions() {
        return States.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull S state() {
        return (S) this;
    }
}
