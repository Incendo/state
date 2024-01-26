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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status = API.Status.INTERNAL, since = "1.0.0")
record StatesImpl<S extends State<S>>(@NonNull Collection<@NonNull S> states) implements States<S> {

    @Override
    public boolean contains(final @NonNull S state) {
        return this.states.contains(state);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public @NonNull States<S> withState(final @NonNull S state) {
        Objects.requireNonNull(state, "state");
        if (state instanceof Enum<?>) {
            // Special case so that we preserve the EnumSet.
            final EnumSet enumSet = EnumSet.copyOf((Collection) this.states);
            enumSet.add(state);
            return new StatesImpl<>(Collections.unmodifiableCollection(enumSet));
        }
        final List<S> states = new ArrayList<>(this.states);
        states.add(state);
        return new StatesImpl<>(Collections.unmodifiableCollection(states));
    }

    @Override
    public String toString() {
        return this.states.stream().map(S::toString).collect(Collectors.joining(", ", "(", ")"));
    }
}
