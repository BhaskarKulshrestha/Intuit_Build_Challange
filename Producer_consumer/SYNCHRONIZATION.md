# Thread Synchronization Concepts

## Overview of Producer-Consumer Pattern

The Producer-Consumer pattern is a classic concurrency pattern where:
- **Producers** generate data and put it into a shared buffer
- **Consumers** take data from the shared buffer and process it
- The buffer has a fixed capacity (bounded buffer problem)

## Thread States and Transitions

```
┌─────────────────────────────────────────────────────────────────┐
│                    Producer-Consumer Flow                        │
└─────────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Source     │         │    Shared    │         │ Destination  │
│  Container   │────────▶│    Buffer    │────────▶│  Container   │
│              │         │  (Capacity)  │         │              │
└──────────────┘         └──────────────┘         └──────────────┘
       │                        │                         │
       │                        │                         │
   Producer                 Blocking                  Consumer
   Thread(s)                Mechanism                 Thread(s)
```

## Synchronization Mechanisms

### 1. Wait/Notify Mechanism

```
Producer Thread:                      Consumer Thread:
┌──────────────┐                     ┌──────────────┐
│              │                     │              │
│  Read Item   │                     │  Wait for    │
│  from Source │                     │    Item      │
│              │                     │              │
└──────┬───────┘                     └──────┬───────┘
       │                                    │
       ▼                                    ▼
┌──────────────┐                     ┌──────────────┐
│ Check Buffer │                     │ Check Buffer │
│   Is Full?   │                     │  Is Empty?   │
└──────┬───────┘                     └──────┬───────┘
       │                                    │
   Yes │  No                            Yes │  No
       │  │                                 │  │
   ┌───▼──▼────┐                       ┌───▼──▼────┐
   │   wait()  │                       │   wait()  │
   │  Release  │                       │  Release  │
   │   Lock    │                       │   Lock    │
   └────┬──────┘                       └────┬──────┘
        │                                   │
        │ notified                          │ notified
        │                                   │
   ┌────▼──────┐                       ┌────▼──────┐
   │   Add to  │                       │  Remove   │
   │   Buffer  │                       │   from    │
   │           │                       │  Buffer   │
   └────┬──────┘                       └────┬──────┘
        │                                   │
   ┌────▼──────┐                       ┌────▼──────┐
   │notifyAll()│                       │notifyAll()│
   │  Wake up  │                       │  Wake up  │
   │ Consumers │                       │ Producers │
   └───────────┘                       └───────────┘
```

### 2. SharedBuffer Internal State

```
Initial State (Empty):
┌─────┬─────┬─────┬─────┬─────┐
│     │     │     │     │     │  Capacity: 5
└─────┴─────┴─────┴─────┴─────┘  Size: 0

After Producer adds 3 items:
┌─────┬─────┬─────┬─────┬─────┐
│ A   │ B   │ C   │     │     │  Capacity: 5
└─────┴─────┴─────┴─────┴─────┘  Size: 3

Buffer Full (Producer must wait):
┌─────┬─────┬─────┬─────┬─────┐
│ A   │ B   │ C   │ D   │ E   │  Capacity: 5
└─────┴─────┴─────┴─────┴─────┘  Size: 5
                                   Producer: WAITING

Consumer removes 1 item:
┌─────┬─────┬─────┬─────┬─────┐
│     │ B   │ C   │ D   │ E   │  Capacity: 5
└─────┴─────┴─────┴─────┴─────┘  Size: 4
                                   Producer: NOTIFIED → RUNNABLE
```

## Thread Synchronization Details

### Synchronized Keyword

```java
public synchronized void produce(T item) {
    // Only ONE thread can execute this at a time
    // Other threads must wait for the lock
}
```

**What happens:**
1. Thread acquires the monitor lock on the object
2. Executes the method
3. Releases the lock when done
4. Other waiting threads compete for the lock

### Wait() Method

```java
while (buffer.size() == capacity) {
    wait();  // Release lock and wait
}
```

**What happens:**
1. Thread checks condition (buffer full)
2. Calls `wait()` which:
   - Releases the monitor lock
   - Moves thread to WAITING state
   - Thread waits until notified
3. Must be in synchronized context

### NotifyAll() Method

```java
notifyAll();  // Wake up all waiting threads
```

**What happens:**
1. All threads in WAITING state are moved to BLOCKED state
2. They compete for the lock
3. One thread acquires lock and continues
4. Others wait their turn

## Race Conditions and How They're Prevented

### Without Synchronization (Race Condition):

```
Time  Producer Thread        Consumer Thread       Buffer State
----  -----------------      -----------------     ------------
t1    Read size (0)          Read size (0)         []
t2    Check if < capacity    Check if empty        []
t3    Add item A             ❌ CRASH or CORRUPT   [A] or ERROR
```

### With Synchronization (Safe):

```
Time  Producer Thread        Consumer Thread       Buffer State
----  -----------------      -----------------     ------------
t1    Acquire lock          Wait for lock          []
t2    Read size (0)         [BLOCKED]              []
t3    Add item A            [BLOCKED]              [A]
t4    Release lock          [BLOCKED]              [A]
t5    [continues]           Acquire lock           [A]
t6    [continues]           Read size (1)          [A]
t7    [continues]           Remove item A          []
t8    [continues]           Release lock           []
```

## BlockingQueue vs Wait/Notify

### Wait/Notify (Low-Level):
```java
// Producer
synchronized (buffer) {
    while (buffer.isFull()) {
        buffer.wait();
    }
    buffer.add(item);
    buffer.notifyAll();
}

// Consumer
synchronized (buffer) {
    while (buffer.isEmpty()) {
        buffer.wait();
    }
    Item item = buffer.remove();
    buffer.notifyAll();
}
```

### BlockingQueue (High-Level):
```java
// Producer
blockingQueue.put(item);  // Blocks if full

// Consumer
Item item = blockingQueue.take();  // Blocks if empty
```

**Key Differences:**
- BlockingQueue handles synchronization internally
- No need for explicit synchronized blocks
- No need for wait/notify calls
- Less error-prone
- Simpler code

## Thread States

```
┌─────────┐
│   NEW   │  Thread created but not started
└────┬────┘
     │ start()
     ▼
┌─────────┐  Lock acquired
│RUNNABLE │◄──────────────┐
└────┬────┘               │
     │ Lock not          │ Lock
     │ available         │ available
     ▼                   │
┌─────────┐              │
│ BLOCKED │──────────────┘
└─────────┘

┌─────────┐  notify/notifyAll
│ WAITING │──────────────┐
└────▲────┘              │
     │ wait()            ▼
     │            ┌─────────┐
     └────────────│RUNNABLE │
                  └─────────┘
```

## Multiple Producers and Consumers

```
Producer-1 ──┐
             │
Producer-2 ──┤     ┌──────────────┐     ┌─── Consumer-1
             ├────▶│    Shared    │────▶│
Producer-3 ──┤     │    Buffer    │     └─── Consumer-2
             │     │  (Thread-Safe)│     
Producer-N ──┘     └──────────────┘     └─── Consumer-M

All threads coordinate through the shared buffer using:
- Synchronized access
- Wait/Notify for coordination
- FIFO ordering (First In First Out)
```

## Testing Scenarios

### 1. Buffer Full Scenario
```
1. Fill buffer to capacity
2. Producer tries to add → WAITING
3. Consumer removes item → notify
4. Producer wakes up → adds item
```

### 2. Buffer Empty Scenario
```
1. Start with empty buffer
2. Consumer tries to remove → WAITING
3. Producer adds item → notify
4. Consumer wakes up → removes item
```

### 3. Multiple Threads
```
P1 adds → P2 adds → P3 waits (full) → C1 removes → P3 wakes → P3 adds
```

## Best Practices

1. **Always use while loop for wait conditions**
   ```java
   while (condition) {
       wait();  // NOT if (condition) { wait(); }
   }
   ```
   Prevents spurious wakeups

2. **Use notifyAll() instead of notify()**
   - notify() wakes only one thread (may not be the right one)
   - notifyAll() wakes all threads (safer)

3. **Keep synchronized blocks small**
   - Only protect critical sections
   - Don't perform I/O in synchronized blocks

4. **Prefer high-level utilities when possible**
   - BlockingQueue over wait/notify
   - Executor framework over raw threads
   - Concurrent collections over synchronized collections

## Performance Considerations

- **Context Switching**: Thread switching has overhead
- **Lock Contention**: Multiple threads competing for lock
- **Buffer Size**: Larger buffer = less blocking but more memory
- **Thread Count**: More threads ≠ better performance (overhead increases)

## Common Pitfalls

1. **Nested Locks**: Can cause deadlock
2. **Forgetting to notifyAll()**: Threads wait forever
3. **Not checking condition in loop**: Spurious wakeups
4. **Synchronizing on wrong object**: No mutual exclusion
5. **Interruption not handled**: Threads don't stop cleanly

---

This document provides a comprehensive understanding of the synchronization mechanisms used in the Producer-Consumer implementation.
