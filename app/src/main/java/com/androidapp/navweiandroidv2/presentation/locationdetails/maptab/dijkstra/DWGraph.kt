package com.androidapp.navweiandroidv2.presentation.locationdetails.maptab.dijkstra

import com.androidapp.entity.models.NodeModel

/**
 * Created by S.Nur Uysal on 2019-12-23.
 */

interface Graph {
     val V: Int
     var E: Int
     fun adjacentVertices(from: Int): Collection<Int>

     fun vertices(): IntRange = 0 until V
}

class DWGraph( override val V: Int) :
    Graph {
    override var E: Int = 0
    private val adj: Array<Queue<Edge>> = Array(V) { Queue<Edge>() }
    private val indegree: IntArray = IntArray(V)

    class Edge( val fromIndex: Int,  val toIndex: Int,val node:NodeModel,  val weight: Double)

    fun addEdge( fromIndex: Int,   toIndex: Int, node: NodeModel, weight: Double) {
        val edge = Edge(
            fromIndex,
            toIndex,
            node,
            weight
        )
        adj[fromIndex].add(edge)
        indegree[toIndex]++
        E++
    }

    fun edges(): Collection<Edge> {
        val stack =
            Stack<Edge>()
        adj.flatMap { it }.forEach { stack.push(it) }
        return stack
    }

    fun adjacentEdges(from: Int): Collection<Edge> {
        return adj[from]
    }

    override fun adjacentVertices(from: Int): Collection<Int> {
        return adjacentEdges(from).map { it.toIndex }
    }

    fun outdegree(v: Int): Int {
        return adj[v].size
    }
}

