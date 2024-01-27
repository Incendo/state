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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractStatefulTest {

    private AbstractStateful<TestState, TestStateful> stateful;

    @BeforeEach
    void setup() {
        this.stateful = new TestStateful();
    }

    @Test
    void TransitionTo_HappyFlow_Success() {
        // Arrange
        final TestState nextState = TestState.INTERMEDIARY_STATE;

        // Act
        this.stateful.transitionTo(nextState);

        // Assert
        assertThat(this.stateful.state()).isEqualTo(TestState.INTERMEDIARY_STATE);
    }

    @Test
    void TransitionTo_IllegalTransition_ThrowsException() {
        // Arrange
        final TestState nextState = TestState.END_STATE;

        // Act & Assert
        assertThrows(
                IllegalStateTransitionException.class,
                () -> this.stateful.transitionTo(TestState.END_STATE)
        );
    }

    @Test
    void Transition_HappyFlow_Success() {
        // Arrange
        final TestState currentState = TestState.INITIAL_STATE;
        final TestState nextState = TestState.INTERMEDIARY_STATE;

        // Act
        this.stateful.transition(currentState, nextState);

        // Assert
        assertThat(this.stateful.state()).isEqualTo(TestState.INTERMEDIARY_STATE);
    }

    @Test
    void Transition_UnexpectedState_ThrowsException() {
        // Arrange
        final TestState currentState = TestState.INTERMEDIARY_STATE;
        final TestState nextState = TestState.END_STATE;

        // Act & Assert
        assertThrows(
                UnexpectedStateException.class,
                () -> this.stateful.transition(currentState, nextState)
        );
    }

    @Test
    void AsImmutable_StateIsUpdated_UpdateReflected() {
        // Arrange
        final Stateful<TestState, TestStateful> immutable = this.stateful.asImmutable();

        // Act
        this.stateful.transitionTo(TestState.INTERMEDIARY_STATE);

        // Assert
        assertThat(immutable.state()).isEqualTo(TestState.INTERMEDIARY_STATE);
    }

    @Test
    void ExpectState_HappyFlow_Success() {
        // Act
        final TestStateful result = this.stateful.expectState(TestState.INITIAL_STATE);

        // Assert
        assertThat(result).isEqualTo(this.stateful);
    }

    @Test
    void ExpectState_UnexpectedState_ThrowsException() {
        // Act & Assert
        assertThrows(
                UnexpectedStateException.class,
                () -> this.stateful.expectState(TestState.END_STATE)
        );
    }


    static final class TestState extends AbstractState<TestState> {

        static final TestState END_STATE = new TestState(States.of());
        static final TestState INTERMEDIARY_STATE = new TestState(States.of(END_STATE));
        static final TestState INITIAL_STATE = new TestState(States.of(INTERMEDIARY_STATE));

        private TestState(final @NonNull States<TestState> allowedTransitions) {
            super(allowedTransitions);
        }
    }

    static final class TestStateful extends AbstractStateful<TestState, TestStateful> {

        TestStateful() {
            super(TestState.INITIAL_STATE);
        }
    }
}
