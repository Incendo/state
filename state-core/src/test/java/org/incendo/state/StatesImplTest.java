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
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class StatesImplTest {

    @Test
    void Contains_ContainsState_ReturnsTrue() {
        // Arrange
        final States<TestState> states = States.of(TestState.INITIAL_STATE);

        // Act
        final boolean result = states.contains(TestState.INITIAL_STATE);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void Contains_DoesNotContainState_ReturnsFalse() {
        // Arrange
        final States<TestState> states = States.of(TestState.INITIAL_STATE);

        // Act
        final boolean result = states.contains(TestState.END_STATE);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void WithState_HappyFlow_Success() {
        // Arrange
        final States<TestState> states = States.of();

        // Act
        final States<TestState> result = states.withState(TestState.INITIAL_STATE);

        // Assert
        assertThat(result).isEqualTo(States.of(TestState.INITIAL_STATE));
    }

    @Test
    void Empty_EmptyStates_ReturnsTrue() {
        // Arrange
        final States<TestState> states = States.of();

        // Act
        final boolean result = states.empty();

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void Empty_NonEmptyStates_ReturnsFalse() {
        // Arrange
        final States<TestState> states = States.of(TestState.INITIAL_STATE);

        // Act
        final boolean result = states.empty();

        // Assert
        assertThat(result).isFalse();
    }


    static final class TestState extends AbstractState<TestState> {

        static final TestState END_STATE = new TestState(States.of());
        static final TestState INTERMEDIARY_STATE = new TestState(States.of(END_STATE));
        static final TestState INITIAL_STATE = new TestState(States.of(INTERMEDIARY_STATE));

        private TestState(final @NonNull States<TestState> allowedTransitions) {
            super(allowedTransitions);
        }
    }
}
