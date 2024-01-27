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
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status = API.Status.INTERNAL, since = "1.0.0")
final class LazyStates<S extends State<S>> implements States<S> {

    private final Supplier<@NonNull States<S>> statesSupplier;
    private States<S> backingStates;

    LazyStates(final @NonNull Supplier<@NonNull States<S>> statesSupplier) {
        this.statesSupplier = Objects.requireNonNull(statesSupplier, "statesSupplier");
    }

    private synchronized @NonNull States<S> backingStates() {
        if (this.backingStates == null) {
            this.backingStates = Objects.requireNonNull(this.statesSupplier.get(), "backingStates");
        }
        return this.backingStates;
    }

    @Override
    public boolean contains(final @NonNull S state) {
        return this.backingStates().contains(state);
    }

    @Override
    public @NonNull States<S> withState(final @NonNull S state) {
        return this.backingStates().withState(state);
    }

    @Override
    public boolean empty() {
        return this.backingStates().empty();
    }
}
