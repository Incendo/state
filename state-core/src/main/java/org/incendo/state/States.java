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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A container of {@link State states}.
 *
 * @param <S> state type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface States<S extends State<S>> {

    /**
     * Returns an empty {@link States} instance.
     *
     * @param <S> state type
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    static <S extends State<S>> @NonNull States<S> of() {
        return (States<S>) EmptyStates.EMPTY;
    }

    /**
     * Creates a new states instance using the given values.
     *
     * @param <S>    state type
     * @param states states
     * @return the instance
     */
    @SafeVarargs
    static <S extends State<S>> @NonNull States<S> of(final @NonNull S @NonNull... states) {
        Objects.requireNonNull(states, "states");
        return new StatesImpl<>(List.copyOf(Arrays.asList(states)));
    }

    /**
     * Creates a new states instance using the given values.
     *
     * @param <S>    state type
     * @param states states
     * @return the instance
     */
    static <S extends State<S>> @NonNull States<S> of(final @NonNull Collection<S> states) {
        Objects.requireNonNull(states, "states");
        return new StatesImpl<>(Set.copyOf(states));
    }

    /**
     * Creates a new states instance using the given enum values.
     *
     * @param <S>    state type
     * @param state  first state, may not be null
     * @param states remaining states
     * @return the instance
     */
    @SafeVarargs
    static <S extends Enum<S> & State<S>> @NonNull States<S> ofEnum(final @NonNull S state, final @NonNull S @NonNull... states) {
        Objects.requireNonNull(state, "state");
        return new StatesImpl<>(Collections.unmodifiableCollection(EnumSet.of(state, states)));
    }

    /**
     * Creates a new states instance that will lazily evaluate the given {@code statesSupplier} the first time
     * the states are requested.
     *
     * @param <S>            state type
     * @param statesSupplier supplier of the states
     * @return the instance
     */
    static <S extends State<S>> @NonNull States<S> lazy(final @NonNull Supplier<@NonNull States<S>> statesSupplier) {
        return new LazyStates<>(statesSupplier);
    }

    /**
     * Returns whether this instance contains the given {@code state}.
     *
     * @param state state to check for
     * @return {@code true} if the instance contains the state, {@code false} if not
     */
    boolean contains(@NonNull S state);

    /**
     * Returns a <b>new</b> instance with the given {@code state} added.
     *
     * @param state state to add
     * @return the new instance
     */
    @NonNull States<S> withState(@NonNull S state);

    /**
     * Returns whether the state container is empty.
     *
     * @return {@code true} if this instance contains no states, else {@code false}
     */
    boolean empty();

    /**
     * Returns a stream of the stateCollection contained in this instance
     *
     * @return the stateCollection
     */
    @NonNull Stream<S> states();
}
