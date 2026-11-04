# Chat Log Instructions for Claude

Follow these instructions in any Claude chat that is given this file. Keep a Markdown log of the chat in the same folder as this file.

## Files

- Keep one log for each chat.
- Name the log `Claude-Chat-Log_YYYY-MM-DD_HHMM.md`, using the date and time the chat started.
- Never edit or overwrite another chat's log.
- Create the log on the first prompt after receiving this file, and include the whole chat so far.

## When to update

Update the log after every prompt, with no exceptions:

1. Finish the work the prompt asks for.
2. Add the prompt and your reply to the log as the last step before you answer.
3. Send the same reply text that you wrote in the log.

If an update is missed, add the missing entry during the next reply.

## Header

Start the log with this header:

```markdown
# Claude Chat Log

- **Model:** <model name and ID>
- **Date created:** <YYYY-MM-DD>
- **Time created:** <h:mm AM/PM (UTC offset)>
- **Marketplace plugin used:** <plugin name and version, or None>

## Information uploaded by user

| Prompt | Item | Type | Notes |
|---|---|---|---|
| <prompt number> | <file name only> | <File, Folder reference, or Pasted text> | <short note> |

**Layout:** USER is on the right. CLAUDE is on the left.
```

- Add a row to the table for every upload, including pasted code and text.
- If nothing has been uploaded, write `None` in the Item column.
- Add any plugin used later in the chat to the header.

## Entry format

Add each prompt and reply in this format. The `div` puts the prompt on the right side in a Markdown preview, and the reply stays on the left.

```markdown
---

## Prompt <number>

<div align="right">

**USER**

<the prompt, word for word>

</div>

**CLAUDE**

(Actions: <one-line summary of tool work, if any>)

<the reply, word for word>
```

- Keep the blank lines around `<div align="right">` and `</div>`, or the Markdown inside will not render.
- Put code in fenced code blocks, in both prompts and replies.
- Summarize tool work in the one `Actions` line. Do not copy raw tool output.
- Copy pasted content in full, then add a note such as `(Attachment: pasted code, shown above.)`.

## Do not edit files

Do not edit any of the user's files. Give code changes in your reply, for the user to apply.

There are 2 exceptions: this chat's log, and a specific file that the user tells you to change.

## Hide file paths

Never show the full path of an uploaded file or folder. Show only its name, everywhere in the log:

- In the upload table.
- In the copied prompt. Replace the path with the name, and change nothing else.
- In your replies.

For example, `C:\Users\Name\Documents\report.pdf` becomes `report.pdf`.
