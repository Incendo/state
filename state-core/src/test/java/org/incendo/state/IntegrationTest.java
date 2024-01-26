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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Test of multiple different components at this. This is not structured
 * as a proper test, and instead serves as an example.
 */
class IntegrationTest {

    @Test
    void test() {
        // Create the stateful instance.
        final TestObject object = new TestObject();

        // Transition to foo.
        this.foo(object);
        // Transition to foo again.
        this.foo(object);

        // We should only have foo'd once!
        assertThat(object.fooCounter()).isEqualTo(1);

        // Attempting to end prematurely fails exceptionally.
        assertThrows(
                IllegalStateTransitionException.class,
                () -> this.end(object)
        );

        // But we can transition into a state that can be ended.
        object.transition(TestState.INTERMEDIARY_FOO, TestState.INTERMEDIARY_BAR);

        // ... and then we end!
        assertThat(this.end(object).state()).isEqualTo(TestState.END_STATE);
    }

    private @NonNull TestObject foo(final @NonNull TestObject object) {
        return object.interact()
                .incomingStates(States.of(TestState.START_STATE, TestState.INTERMEDIARY_FOO))
                .outgoingStates(States.of(TestState.INTERMEDIARY_FOO))
                .shortCircuitStates(States.of(TestState.INTERMEDIARY_FOO))
                .interaction(instance -> instance.foo().transitionTo(TestState.INTERMEDIARY_FOO))
                .execute();
    }

    private @NonNull TestObject end(final @NonNull TestObject object) {
        return object.interact()
                .outgoingStates(States.of(TestState.END_STATE))
                .shortCircuitStates(States.of(TestState.END_STATE))
                .interaction(instance -> instance.transitionTo(TestState.END_STATE))
                .execute();
    }

    enum TestState implements State<TestState> {
        START_STATE,
        INTERMEDIARY_FOO,
        INTERMEDIARY_BAR,
        INTERMEDIARY_BAZ,
        END_STATE;

        static {
            // We cannot create these in the enum constructor because of illegal forward referencing.
            START_STATE.allowedTransitions = States.ofEnum(INTERMEDIARY_FOO, END_STATE);
            INTERMEDIARY_FOO.allowedTransitions = States.ofEnum(INTERMEDIARY_FOO, INTERMEDIARY_BAR);
            INTERMEDIARY_BAR.allowedTransitions = States.of(INTERMEDIARY_BAZ, END_STATE);
            INTERMEDIARY_BAZ.allowedTransitions = States.of(END_STATE);
            END_STATE.allowedTransitions = States.empty();
        }

        private States<TestState> allowedTransitions;

        @Override
        public @NonNull States<TestState> allowedTransitions() {
            return States.lazy(() -> this.allowedTransitions);
        }
    }

    static final class TestObject extends AbstractStateful<TestState, TestObject> {

        private int fooCounter = 0;

        TestObject() {
            super(TestState.START_STATE);
        }

        @This @NonNull TestObject foo() {
            this.fooCounter++;
            return this;
        }

        int fooCounter() {
            return this.fooCounter;
        }
    }
}
