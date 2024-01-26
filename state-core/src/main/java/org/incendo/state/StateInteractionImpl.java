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

import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status = API.Status.STABLE, since = "1.0.0")
record StateInteractionImpl<U extends State<U>, V extends Stateful<U, V>>(
        @NonNull V instance,
        @NonNull States<U> incomingStates,
        @NonNull States<U> outgoingStates,
        @NonNull States<U> shortcircuitStates,
        @NonNull Function<V, V> interaction
) implements StateInteraction<U, V> {

    @Override
    public @NonNull V execute() {
        final U currentState = this.instance.state();
        if (!this.incomingStates.contains(currentState)) {
            throw new UnexpectedStateException(this.incomingStates, currentState, this.instance);
        }

        if (this.shortcircuitStates.contains(currentState)) {
            return this.instance;
        }

        final V result = this.interaction.apply(this.instance);

        final U newState = result.state();
        if (!this.outgoingStates.contains(newState)) {
            throw new UnexpectedStateException(this.outgoingStates, newState, result);
        }

        return result;
    }
}
