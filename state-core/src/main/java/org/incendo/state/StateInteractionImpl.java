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

import java.util.concurrent.locks.Lock;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status = API.Status.STABLE, since = "1.0.0")
record StateInteractionImpl<U extends State<U>, V extends Stateful<U, V>>(
        @NonNull V instance,
        @NonNull States<U> incomingStates,
        @NonNull States<U> outgoingStates,
        @NonNull States<U> shortcircuitStates,
        @NonNull Interaction<U, V> interaction
) implements StateInteraction<U, V> {

    @Override
    public @NonNull InteractionResult<U, V> execute() {
        final Lock lock;
        if (this.instance instanceof AbstractLockableStateful<?, ?> lockableStateful) {
            lock = lockableStateful.lock().writeLock();
        } else {
            lock = null;
        }

        if (lock != null) {
            lock.lock();
        }

        try {
            final U currentState = this.instance.state();

            if (this.shortcircuitStates.contains(currentState)) {
                return new InteractionResult.ShortCircuited<>(this.instance);
            }

            if (!this.incomingStates.contains(currentState)) {
                return new InteractionResult.Failed.IllegalIncomingState<>(
                        this.instance,
                        new UnexpectedStateException(this.incomingStates, currentState, this.instance)
                );
            }

            final V result;

            try {
                result = this.interaction.interact(this.instance);
            } catch (final RuntimeException e) {
                throw e;
            } catch (final Throwable throwable) {
                throw new RuntimeException(throwable);
            }

            final U newState = result.state();
            if (!this.outgoingStates.contains(newState)) {
                return new InteractionResult.Failed.IllegalOutgoingState<>(
                        this.instance,
                        result,
                        new UnexpectedStateException(this.outgoingStates, newState, result)
                );
            }

            return new InteractionResult.Succeeded<>(this.instance, result);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
