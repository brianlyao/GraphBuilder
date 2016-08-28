# GraphBuilder 0.0.3

GraphBuilder will be an open-source piece of software which allows users to visually construct [graphs](https://en.wikipedia.org/wiki/Graph_(abstract_data_type)) on a "editor" panel.

Currently, GraphBuilder is written in Java 7+ (developed in Eclipse).

Please note that GraphBuilder is still in its very early stages!

## Goals

Ultimately, users will be able to load and save their graphs, run algorithms on them, and export them as images.

## Code Overview

An over view of the packages and what they contain.

### components

This package contains the implementations of various graph components. These are essential for holding the data necessary to draw them on the editor panel. Currently, this contains implementations of nodes (circles) and edges (lines and arrows), the most basic components of a graph.

### math

This package contains classes for additional math beyond what Java's distribution comes with.

### preferences

This tentative package contains a list of user preferences. Ultimately, these will be moved to a configuration file.

### tool

This package contains a list of all the tools that the user can use in GraphBuilder.

### uielements

This package contains classes related to the UI components necessary for user interaction. This includes the main GUI which appears when the program starts, and the editor panel that appears within it.