# UI test plan

## Test: Valid tasks remain correct after an unknown command

**Aim:** Verify that a rejected command does not alter tasks added before or after it.

### Input

```text
todo borrow book
abracadabra
list
deadline submit report /by Sunday 5pm
list
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
ORDER PROCESSED: [T][ ] BORROW BOOK
---
WELL, THAT IS NO LONGER A COMMAND.
---
1.[T][ ] borrow book
---
ORDER PROCESSED: [D][ ] SUBMIT REPORT (BY: SUNDAY 5PM)
---
1.[T][ ] borrow book
2.[D][ ] submit report (by: Sunday 5pm)
---
ORDER PROCESSED: [E][ ] PROJECT MEETING (FROM: MON 2PM TO: 4PM)
---
1.[T][ ] borrow book
2.[D][ ] submit report (by: Sunday 5pm)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Test: Invalid task details preserve the documented fallback state

**Aim:** Verify that an empty todo is rejected and malformed dated commands follow their existing todo fallback behavior.

### Input

```text
todo 
list
deadline revise notes
list
event orientation /from Monday
list
bye
```

### Expected output

```text
IT IS BARREN AND CANNOT BE CREATED.
---
BUT, THERE WAS NOTHING TO READ.
---
YOU MUST BE
MISTAKEN.

HERE.
ORDER PROCESSED: [T][ ] REVISE NOTES
---
1.[T][ ] revise notes
---
YOU MUST BE
MISTAKEN.

HERE.
ORDER PROCESSED: [T][ ] ORIENTATION /FROM MONDAY
---
1.[T][ ] revise notes
2.[T][ ] orientation /from Monday
```

## Test: Invalid marking leaves task state unchanged

**Aim:** Verify that an invalid mark command does not complete the task, while later valid mark and unmark commands still work.

### Input

```text
todo write tests
mark not-a-number
list
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
ORDER PROCESSED: [T][ ] WRITE TESTS
---
BUT, IT IS INVALID.
---
1.[T][ ] write tests
---
THEN, IT IS DONE.
[T][X] write tests
---
1.[T][X] write tests
---
THEN, IT WAS AS IF IT WAS NEVER DONE.
[T][ ] write tests
---
1.[T][ ] write tests
```

## Test: Missing mark argument leaves an empty list unchanged

**Aim:** Verify that a malformed mark command is rejected without creating or modifying a task.

### Input

```text
mark 
list
bye
```

### Expected output

```text
BUT, WHICH ITEM IS YOUR CHOICE?
---
BUT, THERE WAS NOTHING TO READ.
```
