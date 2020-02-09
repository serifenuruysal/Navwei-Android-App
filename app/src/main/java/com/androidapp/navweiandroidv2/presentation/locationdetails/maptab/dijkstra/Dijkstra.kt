package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.dijkstra


import android.util.Log
import java.lang.*

/**
 * Created by S.Nur Uysal on 2019-12-23.
 */




class Dijkstra(graph: DWGraph, val fromIndex: Int) {
    /**
     * distTo[v] = distance  of shortest s->v path
     */
    private val distTo: DoubleArray = DoubleArray(graph.V, { if (it == fromIndex) 0.0 else Double.POSITIVE_INFINITY })

    /**
     * edgeTo[v] = last edge on shortest s->v path
     */
    private val edgeTo: Array<DWGraph.Edge?> = arrayOfNulls(graph.V)

    /**
     * priority queue of vertices
     */
    private val pq: IndexedPriorityQueue<Double> =
        IndexedPriorityQueue(
            graph.V
        )

    init {
        if (graph.edges().any { it.weight < 0 }) {
            throw IllegalArgumentException("there is a negative weight edge")
        }

        // relax vertices in order of distance frhom s
        pq.insert(fromIndex, distTo[fromIndex])
        while (!pq.isEmpty()) {
            val v = pq.poll().first
            for (e in graph.adjacentEdges(v)) {
                relax(e)
            }
        }
    }

    // relax edge e and update pq if changed
    private fun relax(e: DWGraph.Edge) {
        val from = e.fromIndex
        val to = e.toIndex
        if (distTo[to] > distTo[from] + e.weight) {
            distTo[to] = distTo[from] + e.weight
            edgeTo[to] = e
            if (pq.contains(to)) {
                pq.decreaseKey(to, distTo[to])
            } else {
                pq.insert(to, distTo[to])
            }
        }
    }

    /**
     * Returns the length of a shortest path from the source vertex `s` to vertex `v`.
     * @param  v the destination vertex
     * @return the length of a shortest path from the source vertex `s` to vertex `v`;
     *         `Double.POSITIVE_INFINITY` if no such path
     */
    fun distTo(v: Int): Double {
        Log.d("MAP", "distTo[v]:${distTo[v]} ")

        return distTo[v]
    }

    /**
     * Returns true if there is a path from the source vertex `s` to vertex `v`.
     * @param  v the destination vertex
     * @return `true` if there is a path from the source vertex
     *         `s` to vertex `v`; `false` otherwise
     */
    fun hasPathTo(v: Int): Boolean {
        return distTo[v] < java.lang.Double.POSITIVE_INFINITY
    }

    /**
     * Returns a shortest path from the source vertex `s` to vertex `v`.
     * @param  v the destination vertex
     * @return a shortest path from the source vertex `s` to vertex `v`
     *         as an iterable of edges, and `null` if no such path
     */
    fun pathTo(v: Int): Iterable<DWGraph.Edge> {
        if (!hasPathTo(v)) throw NoSuchPathException(
            "There is no path from [$fromIndex] to [$v]"
        )
        val path =
            Stack<DWGraph.Edge>()
        var e = edgeTo[v]
        while (e != null) {
            path.push(e)
            e = edgeTo[e.fromIndex]
        }
        return path
    }
}