# Point in Polygon Tool

This program is meant to be an educational tool for demonstrating how to use the Kirkpatrick method for determining whether points are inside a given polygon in O(log n) query time. The program utilizes 4 phases in order to demonstrate this.

The first is the **Drawing** Stage, where users draw the polygon that queries about points will be from. The controls are simple, left-click to add a point to the poygon bewtween the last added and the first added points at the cursor, right-click to remove the nearest point to the cursor.

The second is the **Triangulation** Stage, where the program draws a triangle around the polygon and triangulates the combined figure for constructing the Directed Acyclical Graph (DAG) which is used for taking queries.

The third is the **DAG Construction** stage whereby a DAG is constructed, where each node represents a triangle made with vertices from the Triangulation Stage that the point may be inside.

The fourth is the **Search** Stage, where the DAG processes point queries. Click to see if the cursor is inside or outside the polygon.

Credit: The Kirkpatrick method itself obviously was not invented by me, I only implemented it here. The Geometric predicates and ear-clipping triangulation algorithm are adapted from those described in Joseph O'Rourke's "Computational Geometry in C".
