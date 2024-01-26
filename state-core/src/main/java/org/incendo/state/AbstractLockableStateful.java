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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * Implementation of {@link AbstractStateful} which can be locked using a {@link ReentrantReadWriteLock}.
 *
 * @param <U> state type
 * @param <V> self-referencing type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public abstract class AbstractLockableStateful<U extends State<U>, V extends AbstractLockableStateful<U, V>>
        extends AbstractStateful<U, V> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = this.lock.readLock();
    private final Lock writeLock = this.lock.writeLock();

    /**
     * Creates a new instance.
     *
     * @param initialState initial state
     */
    protected AbstractLockableStateful(final @NonNull U initialState) {
        super(initialState);
    }

    @Override
    public @This @NonNull V transition(final @NonNull U currentState, final @NonNull U newState) throws UnexpectedStateException,
            IllegalStateTransitionException {
        this.writeLock.lock();
        try {
            return super.transition(currentState, newState);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public @This @NonNull V transitionTo(final @NonNull U state) throws IllegalStateTransitionException {
        this.writeLock.lock();
        try {
            return super.transitionTo(state);
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public @NonNull U state() {
        this.readLock.lock();
        try {
            return super.state();
        } finally {
            this.readLock.unlock();
        }
    }

    /**
     * Returns the lock.
     *
     * @return the lock
     */
    @NonNull ReentrantReadWriteLock lock() {
        return this.lock;
    }
}
