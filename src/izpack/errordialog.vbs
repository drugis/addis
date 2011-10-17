dim msg
msg = "Could not find Java " + WScript.Arguments(0) + " (or newer). Please install Java to run ADDIS."
dim title
title = "Java not found!"
x = MsgBox(msg, 16, title)
