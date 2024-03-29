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
import java.util.function.Consumer;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

@API(status = API.Status.STABLE, since = "1.0.0")
public interface StateInteraction<U extends State<U>, V extends Stateful<U, V>> {

    /**
     * Creates a new interaction builder.
     *
     * @param <U>      state type
     * @param <V>      instance type
     * @param instance instance to run the interaction on
     * @return interaction builder
     */
    static <U extends State<U>, V extends Stateful<U, V>> @NonNull Builder<U, V> on(final @NonNull V instance) {
        return new Builder<>(instance);
    }

    /**
     * Executes the interaction.
     *
     * <p>Any checked exceptions will be wrapped in {@link RuntimeException}.</p>
     *
     * @return the result of the interaction
     */
    @NonNull InteractionResult<U, V> execute();

    final class Builder<U extends State<U>, V extends Stateful<U, V>> implements StateInteraction<U, V> {

        private final V instance;

        private States<U> incomingStates;
        private States<U> outgoingStates;
        private States<U> shortCircuitStates;
        private Interaction<U, V> interaction;

        private Builder(final @NonNull V instance) {
            this.instance = Objects.requireNonNull(instance, "instance");
            this.incomingStates = States.of(instance.state());
            this.outgoingStates = instance.allowedTransitions();
            this.shortCircuitStates = States.of();
            this.interaction = Interaction.identity();
        }

        /**
         * Sets the allowed incoming states of the interaction.
         *
         * @param states incoming states
         * @return {@code this}
         */
        public @This @NonNull Builder<U, V> incomingStates(final @NonNull States<U> states) {
            this.incomingStates = Objects.requireNonNull(states, "states");
            return this;
        }

        /**
         * Sets the allowed outgoing states of the interaction.
         *
         * @param states outgoing states
         * @return {@code this}
         */
        public @This @NonNull Builder<U, V> outgoingStates(final @NonNull States<U> states) {
            this.outgoingStates = Objects.requireNonNull(states, "states");
            return this;
        }

        /**
         * Sets the short-circuit states of the interaction.
         *
         * <p>If the instance has a short-circuit state then bot the incoming &amp; outgoing state validation will be
         * bypassed, and the incoming instance will be immediately returned.</p>
         *
         * @param states short-circuit states
         * @return {@code this}
         */
        public @This @NonNull Builder<U, V> shortCircuitStates(final @NonNull States<U> states) {
            this.shortCircuitStates = Objects.requireNonNull(states, "states");
            return this;
        }

        /**
         * Sets the interaction.
         *
         * @param interaction interaction
         * @return {@code this}
         */
        public @This @NonNull Builder<U, V> interaction(final @NonNull Interaction<U, V> interaction) {
            this.interaction = Objects.requireNonNull(interaction, "interaction");
            return this;
        }

        /**
         * Sets the interaction.
         *
         * @param interaction interaction
         * @return {@code this}
         */
        public @This @NonNull Builder<U, V> consumer(final @NonNull Consumer<V> interaction) {
            return this.interaction(stateful -> {
                interaction.accept(stateful);
                return stateful;
            });
        }

        /**
         * Builds the interaction.
         *
         * @return the interaction
         */
        public @NonNull StateInteraction<U, V> build() {
            return new StateInteractionImpl<>(
                    this.instance,
                    this.incomingStates,
                    this.outgoingStates,
                    this.shortCircuitStates,
                    this.interaction
            );
        }

        /**
         * {@link #build() Builds} and {@link StateInteraction#execute() executes} the interaction.
         *
         * @return the result of the interaction
         */
        @Override
        public @NonNull InteractionResult<U, V> execute() {
            return this.build().execute();
        }
    }

    @FunctionalInterface
    interface Interaction<U extends State<U>, V extends Stateful<U, V>> {

        /**
         * Returns an interaction that immediately returns the incoming stateful.
         *
         * @param <U> state type
         * @param <V> stateful type
         * @return identity interaction
         */
        static <U extends State<U>, V extends Stateful<U, V>> @NonNull Interaction<U, V> identity() {
            return interaction -> interaction;
        }

        /**
         * Performs the interaction.
         *
         * @param stateful stateful instance to perform interaction on
         * @return result of the interaction
         */
        @NonNull V interact(@NonNull V stateful) throws Throwable;
    }

    sealed interface InteractionResult<U extends State<U>, V extends Stateful<U, V>> {

        /**
         * Returns the stateful instance involved in the interaction.
         *
         * @return stateful instance
         */
        @NonNull V instance();

        /**
         * Unwraps the result.
         *
         * @return the result of the interaction
         */
        @NonNull V unwrap();

        record ShortCircuited<U extends State<U>, V extends Stateful<U, V>>(
                @NonNull V instance
        ) implements InteractionResult<U, V> {

            @Override
            public @NonNull V unwrap() {
                return this.instance;
            }
        }

        record Succeeded<U extends State<U>, V extends Stateful<U, V>>(
                @NonNull V instance,
                @NonNull V result
        ) implements InteractionResult<U, V> {

            @Override
            public @NonNull V unwrap() {
                return this.result;
            }
        }

        sealed interface Failed<U extends State<U>, V extends Stateful<U, V>> extends InteractionResult<U, V> {

            /**
             * Returns the exception produced by the interaction.
             *
             * @return interaction exception
             */
            @NonNull UnexpectedStateException exception();

            @Override
            default @NonNull V unwrap() {
                throw this.exception();
            }

            record IllegalIncomingState<U extends State<U>, V extends Stateful<U, V>>(
                    @NonNull V instance,
                    @NonNull UnexpectedStateException exception
            ) implements Failed<U, V> {
            }

            record IllegalOutgoingState<U extends State<U>, V extends Stateful<U, V>>(
                    @NonNull V instance,
                    @NonNull V result,
                    @NonNull UnexpectedStateException exception
            ) implements Failed<U, V> {

            }
        }
    }
}
