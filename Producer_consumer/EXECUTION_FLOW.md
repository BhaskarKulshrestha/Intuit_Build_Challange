# Visual Execution Flow

This document provides a visual representation of how the Producer-Consumer pattern executes.

## Basic Demo Execution Timeline

```
Time    Producer-1                  SharedBuffer               Consumer-1
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

t=0ms   [START] Reading Item-1     [ EMPTY ]                 [START] Waiting...
        from Source                                          
        
t=100ms Lock acquired              [ EMPTY ]                 [WAITING]
        Check: Is Full? NO          
        Add Item-1                  
        
t=150ms notifyAll() → wake up      [ Item-1 ]               [NOTIFIED]
        Release lock                                         
        Sleep 300ms...              
        
t=200ms                            [ Item-1 ]                Lock acquired
                                                              Check: Empty? NO
                                                              Remove Item-1
                                                              
t=250ms                            [ EMPTY ]                 notifyAll()
                                                              Release lock
                                                              Add to Destination
                                                              Sleep 500ms...
                                                              
t=400ms Reading Item-2              [ EMPTY ]                [SLEEPING]
        Lock acquired               
        Add Item-2                  
        
t=450ms notifyAll()                [ Item-2 ]               [SLEEPING]
        Release lock                
        Sleep 300ms...              
        
t=700ms Reading Item-3              [ Item-2 ]               Lock acquired
        [WAITING for lock]                                    Remove Item-2
        
t=750ms Lock acquired              [ EMPTY ]                 notifyAll()
        Add Item-3                                            Release lock
                                                              Sleep 500ms...
        
...     [Pattern continues for remaining items]
```

## Buffer State Changes During Execution

```
Legend: [P] = Producer adds | [C] = Consumer removes | [X] = Occupied slot

Time  Action  Buffer State                                    Size
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
t=0ms START   [_][_][_][_][_]  (Empty, capacity = 5)        0/5

t=1ms [P] #1  [X][_][_][_][_]  Producer adds Item-1         1/5

t=2ms [C] #1  [_][_][_][_][_]  Consumer takes Item-1        0/5

t=3ms [P] #2  [X][_][_][_][_]  Producer adds Item-2         1/5

t=4ms [P] #3  [X][X][_][_][_]  Producer adds Item-3         2/5

t=5ms [C] #2  [_][X][_][_][_]  Consumer takes Item-2        1/5

t=6ms [P] #4  [_][X][X][_][_]  Producer adds Item-4         2/5

t=7ms [P] #5  [_][X][X][X][_]  Producer adds Item-5         3/5

t=8ms [P] #6  [_][X][X][X][X]  Producer adds Item-6         4/5

t=9ms [P] #7  [X][X][X][X][X]  Producer adds Item-7 (FULL!) 5/5

t=10  [P] #8  [X][X][X][X][X]  Producer WAITING (full)      5/5 ⚠️

t=11  [C] #3  [_][X][X][X][X]  Consumer takes Item-3        4/5 ✓

t=12  [P] #8  [X][X][X][X][X]  Producer adds Item-8 (woke)  5/5

t=13  [C] #4  [_][X][X][X][X]  Consumer takes Item-4        4/5
```

## Thread State Diagram During Execution

```
PRODUCER THREAD STATE:                CONSUMER THREAD STATE:

    NEW                                   NEW
     │                                     │
     │ start()                             │ start()
     ▼                                     ▼
  RUNNABLE ◄──────────┐               RUNNABLE ◄──────────┐
     │                │                   │                │
     │ Buffer Full?   │                   │ Buffer Empty?  │
     ▼                │                   ▼                │
  WAITING             │                WAITING             │
  (wait() called)     │              (wait() called)       │
     │                │                   │                │
     │ Consumer        │                   │ Producer        │
     │ calls notify()  │                   │ calls notify()  │
     └────────────────┘                   └────────────────┘
```

## Multiple Producers and Consumers Flow

```
Time    Producer-1    Producer-2    SharedBuffer    Consumer-1    Consumer-2
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

t=0ms   Add P1-Item-1  [WAITING]    [P1-1]         [WAITING]     [WAITING]
                                     size: 1

t=1ms   Sleep...       Add P2-Item-1 [P1-1,P2-1]   [WAITING]     [WAITING]
                                     size: 2

t=2ms   [SLEEPING]     Sleep...      [P1-1,P2-1]   Remove P1-1   [WAITING]
                                     size: 1

t=3ms   [SLEEPING]     [SLEEPING]    [P2-1]        Sleep...      Remove P2-1
                                     size: 0

t=4ms   Add P1-Item-2  [SLEEPING]    [P1-2]        [SLEEPING]    Sleep...
                                     size: 1

t=5ms   Sleep...       Add P2-Item-2 [P1-2,P2-2]   [SLEEPING]    [SLEEPING]
                                     size: 2

... (Pattern continues with interleaved operations)
```

## Wait/Notify Sequence Diagram

```
Producer Thread          SharedBuffer          Consumer Thread
     |                        |                      |
     |  1. produce(item)      |                      |
     |----------------------->|                      |
     |                        |                      |
     |  2. Check: buffer.size() == capacity?         |
     |                        |                      |
     |  3. NO → Add to buffer |                      |
     |  4. notifyAll()        |                      |
     |                        |------ notify ------->|
     |                        |                      |
     |  5. Return             |  6. consume()        |
     |<-----------------------|<---------------------|
     |                        |                      |
     |  7. produce(item)      |  8. Check: isEmpty()?|
     |----------------------->|                      |
     |                        |  9. NO → Remove item |
     |  10. Check: isFull()?  |                      |
     |  11. YES → wait()      |  10. notifyAll()     |
     |  [WAITING]             |------ notify ------->|
     |                        |                      |
     |                        |  11. Return          |
     |                        |--------------------->|
     |                        |                      |
     |  12. [NOTIFIED]        |  12. consume()       |
     |  13. Add to buffer     |<---------------------|
     |  14. notifyAll()       |                      |
     |----------------------->|                      |
```

## Lock Contention Visualization

```
Scenario: Multiple threads competing for SharedBuffer lock

Thread Priority Queue (waiting for lock):

    ┌─────────────────────────────────────┐
    │      SharedBuffer (LOCKED)          │
    │   Currently held by: Producer-1     │
    └─────────────────────────────────────┘
              ▲
              │ Waiting for lock
    ┌─────────┼─────────┐
    │         │         │
    │         │         │
┌───▼───┐ ┌──▼────┐ ┌──▼────┐
│ P2    │ │ C1    │ │ C2    │
│BLOCKED│ │BLOCKED│ │BLOCKED│
└───────┘ └───────┘ └───────┘

When Producer-1 releases lock:
1. All threads compete
2. One thread (e.g., Producer-2) acquires lock
3. Others continue waiting
```

## Blocking Queue vs Wait/Notify Comparison

```
CUSTOM WAIT/NOTIFY APPROACH:
═══════════════════════════════

Producer Code:
┌────────────────────────────────────────┐
│ synchronized void produce(item) {      │
│   while (buffer.isFull()) {            │
│     wait();  ← Explicit wait           │
│   }                                    │
│   buffer.add(item);                    │
│   notifyAll();  ← Explicit notify      │
│ }                                      │
└────────────────────────────────────────┘

Lines of Code: ~15
Complexity: Medium
Control: High


JAVA BLOCKINGQUEUE APPROACH:
═══════════════════════════════

Producer Code:
┌────────────────────────────────────────┐
│ void produce(item) {                   │
│   blockingQueue.put(item);             │
│   ← Automatic blocking & synchronization
│ }                                      │
└────────────────────────────────────────┘

Lines of Code: ~3
Complexity: Low
Control: Lower (abstracted)
```

## Performance Characteristics

```
Scenario Analysis: 10 Items, Buffer Size = 5

Producer Speed: 300ms/item (Faster)
Consumer Speed: 500ms/item (Slower)

Timeline:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

0-1500ms:   Producer adds items 1-5
            Buffer: [1][2][3][4][5] ← FULL
            Producer: WAITING

1500-2000ms: Consumer takes item 1
            Buffer: [ ][2][3][4][5]
            Producer: WAKES UP, adds item 6
            Buffer: [6][2][3][4][5] ← FULL again
            Producer: WAITING

2000-3000ms: Consumer takes items 2,3
            Buffer: [ ][ ][3][4][5][6]
            Producer: WAKES UP, adds items 7,8
            Buffer: [7][8][3][4][5][6] ← FULL

... Pattern continues

Total Time: ~5000ms
Producer Idle Time: ~2000ms (waiting)
Consumer Idle Time: ~500ms (waiting)

BOTTLENECK: Slow consumer
```

## Race Condition Example (WITHOUT Synchronization)

```
⚠️ DANGER: This is what happens WITHOUT proper synchronization!

Time  Thread-1 (Producer)     Thread-2 (Consumer)     Buffer State
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

t=0   Read size: 3             Read size: 3            [A][B][C]

t=1   Check: size < 5? YES     Check: size > 0? YES    [A][B][C]

t=2   Add item D at index 3    Remove item from index 0 [A][B][C]

t=3   Write buffer[3] = D      Read buffer[0] = A       ??? ERROR!
                                                         Corrupted!

RESULT: ❌ Race Condition! Data Corruption! Undefined Behavior!
```

## WITH Synchronization (Safe)

```
✅ SAFE: With synchronized keyword

Time  Thread-1 (Producer)     Thread-2 (Consumer)     Buffer State
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

t=0   Acquire LOCK             Wait for LOCK           [A][B][C]
      Read size: 3             [BLOCKED]               [A][B][C]

t=1   Check: size < 5? YES     [BLOCKED]              [A][B][C]

t=2   Add item D               [BLOCKED]              [A][B][C][D]

t=3   Release LOCK             Acquire LOCK           [A][B][C][D]
      [continues...]           Read size: 4            [A][B][C][D]

t=4                            Check: size > 0? YES    [A][B][C][D]

t=5                            Remove item A           [ ][B][C][D]

t=6                            Release LOCK            [ ][B][C][D]

RESULT: ✅ No Race Condition! Data Integrity Maintained!
```

## Memory Layout Visualization

```
JVM Heap Memory Layout:

┌────────────────────────────────────────────────────────────┐
│                      HEAP MEMORY                           │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────────────────┐                                │
│  │  Container "Source"  │                                │
│  │  ┌────────────────┐  │                                │
│  │  │ [Item-1]       │  │                                │
│  │  │ [Item-2]       │  │                                │
│  │  │ [...]          │  │                                │
│  │  └────────────────┘  │                                │
│  └──────────────────────┘                                │
│                                                            │
│  ┌──────────────────────┐                                │
│  │   SharedBuffer       │ ◄─── Monitor Lock               │
│  │   capacity: 5        │                                │
│  │  ┌────────────────┐  │                                │
│  │  │ Queue:         │  │                                │
│  │  │ [Item-1]       │  │                                │
│  │  │ [Item-2]       │  │                                │
│  │  │ [Item-3]       │  │                                │
│  │  └────────────────┘  │                                │
│  └──────────────────────┘                                │
│                                                            │
│  ┌──────────────────────┐                                │
│  │Container "Destination│                                │
│  │  ┌────────────────┐  │                                │
│  │  │ [Item-1]       │  │                                │
│  │  │ [Item-2]       │  │                                │
│  │  └────────────────┘  │                                │
│  └──────────────────────┘                                │
│                                                            │
└────────────────────────────────────────────────────────────┘

JVM Thread Stack:

┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ Producer-1  │  │ Consumer-1  │  │   Main      │
│   Stack     │  │   Stack     │  │   Stack     │
├─────────────┤  ├─────────────┤  ├─────────────┤
│ run()       │  │ run()       │  │ main()      │
│ produce()   │  │ consume()   │  │ ...         │
│ ...         │  │ ...         │  │             │
└─────────────┘  └─────────────┘  └─────────────┘
```

---

## Summary

This visual guide demonstrates:
- ✅ Execution timeline of producer and consumer threads
- ✅ Buffer state changes over time
- ✅ Thread state transitions
- ✅ Wait/notify sequence
- ✅ Lock contention scenarios
- ✅ Race condition prevention
- ✅ Memory layout

All visualizations show how proper thread synchronization ensures safe, correct concurrent execution.
