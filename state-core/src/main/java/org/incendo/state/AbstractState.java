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

@API(status = API.Status.STABLE, since = "1.0.0")
public abstract class AbstractState<S extends AbstractState<S>> implements State<S> {

    private States<S> allowedTransitions;

    /**
     * Creates a new instance.
     *
     * <p>The allowed states will be configured using {@link #configureAllowedTransitions()}.</p>
     */
    protected AbstractState() {
    }

    /**
     * Creates a new instance and declares the {@code allowedTransitions}.
     *
     * @param allowedTransitions allowed transitions <i>from</i> this state
     */
    protected AbstractState(final @NonNull States<S> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    @Override
    public @NonNull States<S> allowedTransitions() {
        if (this.allowedTransitions == null) {
            synchronized (this) {
                this.allowedTransitions = Objects.requireNonNull(this.configureAllowedTransitions(), "allowedTransitions");
            }
        }
        return this.allowedTransitions;
    }

    /**
     * Returns the allowed transitions for this state.
     *
     * <p>This is invoked the first time {@link #allowedTransitions()} is invoked.</p>
     *
     * @return allowed transitions
     */
    protected @NonNull States<S> configureAllowedTransitions() {
        return States.of();
    }
}
