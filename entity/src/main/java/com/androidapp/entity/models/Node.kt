package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Node(
    val category: NodeCategory?,
    val coord: Coord?,
    val externalLink: List<ExternalLink>?,
    val logo_url: String?,
    val name: String?,

    val id: String?,
    val from_id: String?,
    var to_id: String?,
    val type: NodeType?
) : Parcelable {
    constructor(id: String, type: NodeType, to_id: String, from_id: String?) :
            this(null, null, null, null, null, id, from_id, to_id, type)
}

enum class NodeType {
    point,
    link
}

enum class NodeCategory {
    slot,
    unset,
    entrance,
    exit
}