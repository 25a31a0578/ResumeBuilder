# Resume Builder (Java Swing)

## Run in VS Code
1. Install JDK 17 or newer and the "Extension Pack for Java" in VS Code.
2. File > Open Folder > select this ResumeBuilder folder (the one that contains `src`).
3. Open `src/Main.java` and click **Run** above `main`.
No external library is needed (the PDF writer is our own code).

## Run from a terminal
    javac -d out $(find src -name "*.java")      (Windows PowerShell: javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName)
    java -cp out Main

## How to use
Fill the 5 tabs (or press "Fill Sample Data") > Generate Preview > pick a template > Save as TXT / PDF.
"Back to Edit" keeps all your data.

## Packages
model    - data classes (Resume owns everything), Validator, Utils
template - ResumeTemplate (abstract) + 5 templates -> build a Page (what to show)
layout   - Layouter turns a Page into drawing commands for A4 pages (wrapping, tables, page breaks)
export   - TxtExporter, PdfExporter (hand-written PDF writer)
ui       - MainWindow, ResumeForm, EntryListPanel, EntryDialog, PreviewPanel
