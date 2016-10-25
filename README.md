# GraphBuilder 0.1.4

GraphBuilder will be an open-source piece of software which allows users to visually construct [graphs](https://en.wikipedia.org/wiki/Graph_(abstract_data_type)) on a "editor" panel.

Currently, GraphBuilder is written in Java 7+ (developed in Eclipse).

Please note that GraphBuilder is still in its very early stages!

## Goals

Ultimately, users will be able to load and save their graphs, run algorithms on them, and export them as images.

## Code Overview

An overview of the packages and what they contain.

### actions

This package contains implementations of actions. Each action is a procedure the user can carry out in the editor. Some actions are reversible, which allows them to be undone. These actions are all implemented using the javax.swing.AbstractAction abstract class, since key bindings require a javax.swing.Action. The actual "action" performed is specified in the overridden `actionPerformed` method. For reversible actions, there is a corresponding `undo` method which should reverse the changes made by `actionPerformed`.

### actions.file

This subpackage contains actions specific for file actions (such as those which would be found under the "File" menu).

### components

This package contains the implementations of various graph components. These contain only the logic for the components of the data structure. Currently, this contains implementations of nodes and edges (both directed and undirected), the most basic components of a graph.

### components.display

This package contains data required for each component's visual display on the editor panel.

### context

This package contains a single context class which will contain data important for just about every part of the program, held in one central class. This data includes the set of nodes and edges of the graph currently being built, and the history of (reversible) actions the user has performed.

### io

This package contains utility classes for saving graphs to files and loading graphs from files.

### keybindings

This package currently contains code for keyboard shortcuts and a hard-coded mapping of keyboard shortcuts and corresponding actions. These key bindings are bound to the root pane of GraphBuilder's main window frame.

### math

This package contains classes for additional math beyond what Java's distribution comes with.

### preferences

This tentative package contains a list of user preferences. Ultimately, these will be moved to a configuration file.

### tool

This package contains a list of all the tools that the user can use in GraphBuilder.

### ui

This package contains classes related to the UI components necessary for user interaction. This includes the main window which appears when the program starts, and the editor panel that appears within it.

### ui.dialogs

This subpackage contains dialogs, which will be used to view and modify various settings within GraphBuilder.

### ui.menus

This subpackage contains menu UI components.

### ui.tooloptions

This subpackage contains all the "option bars" for each of the tools.

### util

This package contains several utility classes whose methods will be used in several different places.
